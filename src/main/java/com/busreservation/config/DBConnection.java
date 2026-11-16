package com.busreservation.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages JDBC Database Connections to PostgreSQL.
 * Features:
 * - Centralized configuration from AppConfig
 * - Dynamic driver loading
 * - Connection health checking
 * - Safe resource cleanup and transaction support
 */
public class DBConnection {

    static {
        try {
            Class.forName(AppConfig.getDbDriver());
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    private DBConnection() {
        // Prevent direct instantiation
    }

    /**
     * Obtains a new JDBC connection to PostgreSQL.
     * @return active java.sql.Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        DriverManager.setLoginTimeout(AppConfig.getDbTimeoutSeconds());
        return DriverManager.getConnection(
                AppConfig.getDbUrl(),
                AppConfig.getDbUser(),
                AppConfig.getDbPassword()
        );
    }

    /**
     * Tests if the PostgreSQL database is online and reachable.
     * @return true if connection succeeds and a ping query executes; false otherwise
     */
    public static boolean isAvailable() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.execute("SELECT 1");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Safely closes a Connection without throwing checked exceptions.
     */
    public static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Safely rolls back an ongoing transaction.
     */
    public static void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }
}
