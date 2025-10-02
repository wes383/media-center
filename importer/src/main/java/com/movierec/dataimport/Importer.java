package com.movierec.dataimport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * IMDb Dataset Importer.
 */
public class Importer {

    // ================== Configuration ==================
    private static final int BATCH_SIZE = 5000;
    private static final int MIN_VOTES_THRESHOLD = 1000;
    // ===========================================

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: Please provide the folder path for the IMDb datasets as a command-line argument.");
            System.err.println("Usage: java -jar importer.jar \"C:\\path\\to\\your\\datasets\"");
            return;
        }
        String basePath = args[0];
        // Ensure the path ends with a file separator for easy concatenation
        if (!basePath.endsWith(System.getProperty("file.separator"))) {
            basePath += System.getProperty("file.separator");
        }

        System.out.println("Loading datasets from path: " + basePath);

        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.println("Database connection successful!");
            conn.setAutoCommit(false);

            // Step 1: Create a "seed list" of popular titles (movies/series)
            Map<String, Rating> popularTitles = loadPopularTitles(basePath + "title.ratings.tsv.gz");
            System.out.printf("Seed List: Found %d titles with more than %d votes%n", popularTitles.size(), MIN_VOTES_THRESHOLD);

            // Step 2: Expand the list to include all episodes of popular series
            expandPopularTitlesWithEpisodes(popularTitles, basePath + "title.episode.tsv.gz");
            Set<String> finalPopularTconsts = popularTitles.keySet();
            System.out.printf("Expanded List: A total of %d titles (including all episodes of popular series) will be imported%n", finalPopularTconsts.size());

            // Step 3: Create an "intent list" of cast/crew related to the popular titles/episodes
            Set<String> intendedPopularNconsts = loadPopularPeople(
                    basePath + "title.principals.tsv.gz",
                    basePath + "title.crew.tsv.gz",
                    finalPopularTconsts
            );
            System.out.printf("Intent List: A total of %d cast/crew members are associated with these popular titles%n", intendedPopularNconsts.size());

            // Step 4: Import popular cast/crew and return an "authoritative list"
            Set<String> actualPopularNconsts = importNamesAndProfessions(conn, basePath + "name.basics.tsv.gz", intendedPopularNconsts);
            System.out.printf("Authoritative List: Successfully found and imported %d cast/crew members from name.basics%n", actualPopularNconsts.size());

            // Step 5: Import all titles from the expanded popular list
            importTitlesAndGenres(conn, basePath + "title.basics.tsv.gz", popularTitles);

            // Step 6: Import known_for using the authoritative lists
            importKnownFor(conn, basePath + "name.basics.tsv.gz", finalPopularTconsts, actualPopularNconsts);

            // Step 7: Import crew using the authoritative lists
            importCrew(conn, basePath + "title.crew.tsv.gz", finalPopularTconsts, actualPopularNconsts);

            // Step 8: Import principals using the authoritative lists
            importPrincipals(conn, basePath + "title.principals.tsv.gz", finalPopularTconsts, actualPopularNconsts);

            // Step 9: Import episodes
            importEpisodes(conn, basePath + "title.episode.tsv.gz", finalPopularTconsts);

            System.out.println("All data imported successfully!");

        } catch (Exception e) {
            System.err.println("A critical error occurred during the import process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Map<String, Rating> loadPopularTitles(String filePath) throws Exception {
        System.out.println("Step 1: Starting to create the popular titles seed list...");
        Map<String, Rating> popularTitles = new HashMap<>();
        CSVFormat format = CSVFormat.TDF.withFirstRecordAsHeader().withQuote(null);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filePath))));
             CSVParser parser = new CSVParser(reader, format)) {
            for (CSVRecord record : parser) {
                Integer numVotes = parseInt(record.get("numVotes"));
                if (numVotes != null && numVotes >= MIN_VOTES_THRESHOLD) {
                    popularTitles.put(record.get("tconst"), new Rating(parseDecimal(record.get("averageRating")), numVotes));
                }
            }
        }
        return popularTitles;
    }

    /**
     * Scan the episodes file to add all episodes of popular series to the processing list.
     */
    public static void expandPopularTitlesWithEpisodes(Map<String, Rating> popularTitles, String episodeFilePath) throws Exception {
        System.out.println("Step 2: Expanding list to include all episodes of popular series...");
        Set<String> episodesToAdd = new HashSet<>();
        CSVFormat format = CSVFormat.TDF.withFirstRecordAsHeader().withQuote(null);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(episodeFilePath))));
             CSVParser parser = new CSVParser(reader, format)) {
            for (CSVRecord record : parser) {
                // If the episode's parent series is in our popular list
                if (popularTitles.containsKey(record.get("parentTconst"))) {
                    // record this episode's tconst
                    episodesToAdd.add(record.get("tconst"));
                }
            }
        }
        // Add all found episodes to the main list (giving them an empty Rating, as they may not be popular themselves)
        for(String episodeTconst : episodesToAdd) {
            popularTitles.putIfAbsent(episodeTconst, new Rating(null, null));
        }
    }

    public static Set<String> loadPopularPeople(String principalsPath, String crewPath, Set<String> popularTconsts) throws Exception {
        System.out.println("Step 3: Starting to create the intent list for popular cast/crew...");
        Set<String> popularNconsts = new HashSet<>();
        CSVFormat format = CSVFormat.TDF.withFirstRecordAsHeader().withQuote(null);

        System.out.println("  Scanning principals file...");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(principalsPath))));
             CSVParser parser = new CSVParser(reader, format)) {
            for (CSVRecord record : parser) {
                if (popularTconsts.contains(record.get("tconst"))) {
                    popularNconsts.add(record.get("nconst"));
                }
            }
        }

        System.out.println("  Scanning crew file...");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(crewPath))));
             CSVParser parser = new CSVParser(reader, format)) {
            for (CSVRecord record : parser) {
                if (popularTconsts.contains(record.get("tconst"))) {
                    String directors = record.get("directors");
                    if (!"\\N".equals(directors)) {
                        for (String nconst : directors.split(",")) popularNconsts.add(nconst);
                    }
                    String writers = record.get("writers");
                    if (!"\\N".equals(writers)) {
                        for (String nconst : writers.split(",")) popularNconsts.add(nconst);
                    }
                }
            }
        }
        return popularNconsts;
    }

    public static Set<String> importNamesAndProfessions(Connection conn, String filePath, Set<String> intendedPopularNconsts) throws Exception {
        System.out.println("Step 4: Starting to import names and professions, and creating the authoritative list...");
        Set<String> actualPopularNconsts = new HashSet<>();
        String insertNameSQL = "INSERT INTO names (nconst, primaryName, birthYear, deathYear) VALUES (?, ?, ?, ?);";
        String insertProfessionLinkSQL = "INSERT INTO name_professions (nconst, profession_id) VALUES (?, ?);";
        Map<String, Integer> professionCache = new HashMap<>();
        long nameCount = 0;
        CSVFormat format = CSVFormat.TDF.withFirstRecordAsHeader().withQuote(null);

        try (PreparedStatement nameStmt = conn.prepareStatement(insertNameSQL);
             PreparedStatement professionLinkStmt = conn.prepareStatement(insertProfessionLinkSQL);
             BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filePath))));
             CSVParser parser = new CSVParser(reader, format)) {

            for (CSVRecord record : parser) {
                String nconst = record.get("nconst");
                if (!intendedPopularNconsts.contains(nconst)) continue;

                nameStmt.setString(1, nconst);
                nameStmt.setString(2, record.get("primaryName"));
                nameStmt.setObject(3, parseShort(record.get("birthYear")));
                nameStmt.setObject(4, parseShort(record.get("deathYear")));
                nameStmt.addBatch();
                actualPopularNconsts.add(nconst);
                nameCount++;

                String professions = record.get("primaryProfession");
                if (!"\\N".equals(professions)) {
                    for (String profName : professions.split(",")) {
                        int profId = getOrCreateId(conn, professionCache, "professions", "name", profName);
                        professionLinkStmt.setString(1, nconst);
                        professionLinkStmt.setInt(2, profId);
                        professionLinkStmt.addBatch();
                    }
                }

                if (nameCount % BATCH_SIZE == 0) {
                    System.out.printf("  Committing %d name records and related data...%n", nameCount);
                    nameStmt.executeBatch();
                    professionLinkStmt.executeBatch();
                    conn.commit();
                }
            }
            System.out.println("  Committing the final batch of name records...");
            nameStmt.executeBatch();
            professionLinkStmt.executeBatch();
            conn.commit();
            System.out.println("Names and professions import complete!");
        }
        return actualPopularNconsts;
    }

    public static void importTitlesAndGenres(Connection conn, String filePath, Map<String, Rating> popularTitles) throws Exception {
        System.out.println("Step 5: Starting to import titles and genres...");
        String sql = "INSERT INTO titles (tconst, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, averageRating, numVotes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        String linkSql = "INSERT INTO title_genres (tconst, genre_id) VALUES (?, ?);";
        Map<String, Integer> genreCache = new HashMap<>();
        long titleCount = 0;
        CSVFormat format = CSVFormat.TDF.withFirstRecordAsHeader().withQuote(null);

        try (PreparedStatement titleStmt = conn.prepareStatement(sql);
             PreparedStatement genreLinkStmt = conn.prepareStatement(linkSql);
             BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filePath))));
             CSVParser parser = new CSVParser(reader, format)) {

            for (CSVRecord record : parser) {
                String tconst = record.get("tconst");
                if (!popularTitles.containsKey(tconst)) continue;

                Rating rating = popularTitles.get(tconst);
                titleStmt.setString(1, tconst);
                titleStmt.setString(2, record.get("titleType"));
                titleStmt.setString(3, record.get("primaryTitle"));
                titleStmt.setString(4, record.get("originalTitle"));
                titleStmt.setBoolean(5, "1".equals(record.get("isAdult")));
                titleStmt.setObject(6, parseShort(record.get("startYear")));
                titleStmt.setObject(7, parseShort(record.get("endYear")));
                titleStmt.setObject(8, parseInt(record.get("runtimeMinutes")));
                // For episodes added manually, rating might be null
                if (rating != null) {
                    titleStmt.setBigDecimal(9, rating.getAverageRating());
                    titleStmt.setObject(10, rating.getNumVotes());
                } else {
                    titleStmt.setNull(9, java.sql.Types.DECIMAL);
                    titleStmt.setNull(10, java.sql.Types.INTEGER);
                }
                titleStmt.addBatch();
                titleCount++;

                String genres = record.get("genres");
                if (!"\\N".equals(genres)) {
                    for (String genreName : genres.split(",")) {
                        int genreId = getOrCreateId(conn, genreCache, "genres", "name", genreName);
                        genreLinkStmt.setString(1, tconst);
                        genreLinkStmt.setInt(2, genreId);
                        genreLinkStmt.addBatch();
                    }
                }

                if (titleCount % BATCH_SIZE == 0) {
                    System.out.printf("  Committing %d title records and related data...%n", titleCount);
                    titleStmt.executeBatch();
                    genreLinkStmt.executeBatch();
                    conn.commit();
                }
            }
            System.out.println("  Committing the final batch of title records...");
            titleStmt.executeBatch();
            genreLinkStmt.executeBatch();
            conn.commit();
            System.out.println("Titles and Genres import complete!");
        }
    }

    public static void importKnownFor(Connection conn, String filePath, Set<String> popularTconsts, Set<String> actualPopularNconsts) throws Exception {
        System.out.println("Step 6: Starting to import known_for...");
        String sql = "INSERT INTO known_for (nconst, tconst) VALUES (?, ?);";
        long count = 0;
        CSVFormat format = CSVFormat.TDF.withFirstRecordAsHeader().withQuote(null);

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filePath))));
             CSVParser parser = new CSVParser(reader, format)) {

            for (CSVRecord record : parser) {
                String nconst = record.get("nconst");
                if (!actualPopularNconsts.contains(nconst)) continue;

                String knownForTitles = record.get("knownForTitles");
                if (!"\\N".equals(knownForTitles)) {
                    for (String tconst : knownForTitles.split(",")) {
                        if (popularTconsts.contains(tconst)) {
                            stmt.setString(1, nconst);
                            stmt.setString(2, tconst);
                            stmt.addBatch();
                            if (++count % BATCH_SIZE == 0) {
                                System.out.printf("  Committing %d known_for records...%n", count);
                                stmt.executeBatch();
                                conn.commit();
                            }
                        }
                    }
                }
            }
            System.out.println("  Committing the final batch of known_for records...");
            stmt.executeBatch();
            conn.commit();
            System.out.println("Known_for import complete!");
        }
    }

    public static void importCrew(Connection conn, String filePath, Set<String> popularTconsts, Set<String> actualPopularNconsts) throws Exception {
        System.out.println("Step 7: Starting to import crew (directors, writers)...");
        String dirSql = "INSERT INTO title_directors (tconst, nconst) VALUES (?, ?);";
        String wriSql = "INSERT INTO title_writers (tconst, nconst) VALUES (?, ?);";
        long count = 0;
        CSVFormat format = CSVFormat.TDF.withFirstRecordAsHeader().withQuote(null);

        try (PreparedStatement dirStmt = conn.prepareStatement(dirSql);
             PreparedStatement wriStmt = conn.prepareStatement(wriSql);
             BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filePath))));
             CSVParser parser = new CSVParser(reader, format)) {

            for (CSVRecord record : parser) {
                String tconst = record.get("tconst");
                if (!popularTconsts.contains(tconst)) continue;

                String directors = record.get("directors");
                if (!"\\N".equals(directors)) {
                    for (String nconst : directors.split(",")) {
                        if (actualPopularNconsts.contains(nconst)) {
                            dirStmt.setString(1, tconst);
                            dirStmt.setString(2, nconst);
                            dirStmt.addBatch();
                        }
                    }
                }

                String writers = record.get("writers");
                if (!"\\N".equals(writers)) {
                    for (String nconst : writers.split(",")) {
                        if (actualPopularNconsts.contains(nconst)) {
                            wriStmt.setString(1, tconst);
                            wriStmt.setString(2, nconst);
                            wriStmt.addBatch();
                        }
                    }
                }

                if (++count % BATCH_SIZE == 0) {
                    System.out.printf("  Processed crew records for %d titles...%n", count);
                    dirStmt.executeBatch();
                    wriStmt.executeBatch();
                    conn.commit();
                }
            }
            System.out.println("  Committing the final batch of crew records...");
            dirStmt.executeBatch();
            wriStmt.executeBatch();
            conn.commit();
            System.out.println("Crew import complete!");
        }
    }

    public static void importPrincipals(Connection conn, String filePath, Set<String> popularTconsts, Set<String> actualPopularNconsts) throws Exception {
        System.out.println("Step 8: Starting to import principals...");
        String sql = "INSERT INTO principals (tconst, ordering, nconst, category, job, characters) VALUES (?, ?, ?, ?, ?, ?);";
        long count = 0;
        CSVFormat format = CSVFormat.TDF.withFirstRecordAsHeader().withQuote(null);

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filePath))));
             CSVParser parser = new CSVParser(reader, format)) {

            for (CSVRecord record : parser) {
                String tconst = record.get("tconst");
                String nconst = record.get("nconst");

                if (!popularTconsts.contains(tconst) || !actualPopularNconsts.contains(nconst)) continue;

                stmt.setString(1, tconst);
                stmt.setInt(2, Integer.parseInt(record.get("ordering")));
                stmt.setString(3, nconst);
                stmt.setString(4, record.get("category"));
                stmt.setString(5, record.get("job"));
                stmt.setString(6, record.get("characters"));
                stmt.addBatch();

                if (++count % BATCH_SIZE == 0) {
                    System.out.printf("  Committing %d principal records...%n", count);
                    stmt.executeBatch();
                    conn.commit();
                }
            }
            System.out.println("  Committing the final batch of principal records...");
            stmt.executeBatch();
            conn.commit();
            System.out.println("Principals import complete!");
        }
    }

    public static void importEpisodes(Connection conn, String filePath, Set<String> popularTconsts) throws Exception {
        System.out.println("Step 9: Starting to import episodes...");
        String sql = "INSERT INTO episodes (tconst, parentTconst, seasonNumber, episodeNumber) VALUES (?, ?, ?, ?);";
        long count = 0;
        CSVFormat format = CSVFormat.TDF.withFirstRecordAsHeader().withQuote(null);

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filePath))));
             CSVParser parser = new CSVParser(reader, format)) {

            for (CSVRecord record : parser) {
                // We only need to import episodes whose parent series is in our list,
                // because the episode tconst itself is now guaranteed to be in the titles table.
                if (!popularTconsts.contains(record.get("parentTconst"))) continue;

                stmt.setString(1, record.get("tconst"));
                stmt.setString(2, record.get("parentTconst"));
                stmt.setObject(3, parseInt(record.get("seasonNumber")));
                stmt.setObject(4, parseInt(record.get("episodeNumber")));
                stmt.addBatch();

                if (++count % BATCH_SIZE == 0) {
                    System.out.printf("  Committing %d episode records...%n", count);
                    stmt.executeBatch();
                    conn.commit();
                }
            }
            System.out.println("  Committing the final batch of episode records...");
            stmt.executeBatch();
            conn.commit();
            System.out.println("Episodes import complete!");
        }
    }

    // ================== Helper Methods ==================
    private static Integer parseInt(String value) {
        return "\\N".equals(value) || value == null ? null : Integer.parseInt(value);
    }
    private static Short parseShort(String value) {
        return "\\N".equals(value) || value == null ? null : Short.parseShort(value);
    }
    private static BigDecimal parseDecimal(String value) {
        return "\\N".equals(value) || value == null ? null : new BigDecimal(value);
    }
    private static int getOrCreateId(Connection conn, Map<String, Integer> cache, String tableName, String columnName, String value) throws SQLException {
        if (cache.containsKey(value)) return cache.get(value);
        String selectSQL = "SELECT id FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
            selectStmt.setString(1, value);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    cache.put(value, id);
                    return id;
                }
            }
        }
        String insertSQL = "INSERT INTO " + tableName + " (" + columnName + ") VALUES (?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, value);
            insertStmt.executeUpdate();
            try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    cache.put(value, newId);
                    return newId;
                }
            }
        }
        throw new SQLException("Failed to create new " + tableName + ": " + value);
    }
}