CREATE TABLE titles (
    tconst VARCHAR(15) NOT NULL PRIMARY KEY,
    titleType VARCHAR(50),
    primaryTitle TEXT,
    originalTitle TEXT,
    isAdult BOOLEAN,
    startYear SMALLINT UNSIGNED,
    endYear SMALLINT UNSIGNED,
    runtimeMinutes INT,
    INDEX (startYear),
    INDEX (titleType)
);

CREATE TABLE names (
    nconst VARCHAR(15) NOT NULL PRIMARY KEY,
    primaryName VARCHAR(255),
    birthYear SMALLINT UNSIGNED,
    deathYear SMALLINT UNSIGNED,
    INDEX (primaryName)
);

CREATE TABLE ratings (
    tconst VARCHAR(15) NOT NULL PRIMARY KEY,
    averageRating DECIMAL(3, 1),
    numVotes INT,
    FOREIGN KEY (tconst) REFERENCES titles(tconst) ON DELETE CASCADE
);

CREATE TABLE episodes (
    tconst VARCHAR(15) NOT NULL PRIMARY KEY,
    parentTconst VARCHAR(15),
    seasonNumber INT,
    episodeNumber INT,
    FOREIGN KEY (tconst) REFERENCES titles(tconst) ON DELETE CASCADE,
    FOREIGN KEY (parentTconst) REFERENCES titles(tconst) ON DELETE CASCADE,
    INDEX (parentTconst)
);

CREATE TABLE principals (
    tconst VARCHAR(15) NOT NULL,
    ordering INT NOT NULL,
    nconst VARCHAR(15) NOT NULL,
    category VARCHAR(100),
    job TEXT,
    characters TEXT,
    PRIMARY KEY (tconst, ordering),
    FOREIGN KEY (tconst) REFERENCES titles(tconst) ON DELETE CASCADE,
    FOREIGN KEY (nconst) REFERENCES names(nconst) ON DELETE CASCADE,
    INDEX (nconst)
);

CREATE TABLE akas (
    titleId VARCHAR(15) NOT NULL,
    ordering INT NOT NULL,
    title TEXT,
    region VARCHAR(10),
    language VARCHAR(10),
    types VARCHAR(255),
    attributes VARCHAR(255),
    isOriginalTitle BOOLEAN,
    PRIMARY KEY (titleId, ordering),
    FOREIGN KEY (titleId) REFERENCES titles(tconst) ON DELETE CASCADE
);

CREATE TABLE genres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE title_genres (
    tconst VARCHAR(15) NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (tconst, genre_id),
    FOREIGN KEY (tconst) REFERENCES titles(tconst) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

CREATE TABLE title_directors (
    tconst VARCHAR(15) NOT NULL,
    nconst VARCHAR(15) NOT NULL,
    PRIMARY KEY (tconst, nconst),
    FOREIGN KEY (tconst) REFERENCES titles(tconst) ON DELETE CASCADE,
    FOREIGN KEY (nconst) REFERENCES names(nconst) ON DELETE CASCADE
);

CREATE TABLE title_writers (
    tconst VARCHAR(15) NOT NULL,
    nconst VARCHAR(15) NOT NULL,
    PRIMARY KEY (tconst, nconst),
    FOREIGN KEY (tconst) REFERENCES titles(tconst) ON DELETE CASCADE,
    FOREIGN KEY (nconst) REFERENCES names(nconst) ON DELETE CASCADE
);

CREATE TABLE professions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE name_professions (
    nconst VARCHAR(15) NOT NULL,
    profession_id INT NOT NULL,
    PRIMARY KEY (nconst, profession_id),
    FOREIGN KEY (nconst) REFERENCES names(nconst) ON DELETE CASCADE,
    FOREIGN KEY (profession_id) REFERENCES professions(id) ON DELETE CASCADE
);

CREATE TABLE known_for (
    nconst VARCHAR(15) NOT NULL,
    tconst VARCHAR(15) NOT NULL,
    PRIMARY KEY (nconst, tconst),
    FOREIGN KEY (nconst) REFERENCES names(nconst) ON DELETE CASCADE,
    FOREIGN KEY (tconst) REFERENCES titles(tconst) ON DELETE CASCADE
);

CREATE TABLE my_watch_history (
    tconst VARCHAR(15) NOT NULL PRIMARY KEY,
    rating TINYINT UNSIGNED,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tconst) REFERENCES titles(tconst) ON DELETE CASCADE
);