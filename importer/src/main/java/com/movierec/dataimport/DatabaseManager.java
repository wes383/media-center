package com.movierec.dataimport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // --- Configure your database connection details here ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/imdb_db?rewriteBatchedStatements=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Fatal Error: MySQL JDBC Driver not found. Please check your pom.xml dependencies!");
            throw new SQLException("Driver loading failed", e);
        }
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}