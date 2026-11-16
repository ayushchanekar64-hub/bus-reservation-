package com.busreservation.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized Application Configuration Manager.
 * Loads settings from database.properties and environment variables.
 * Allows graceful fallback to in-memory mode when PostgreSQL is not running.
 */
public class AppConfig {
    private static final Properties properties = new Properties();
    private static boolean inMemoryFallback = false;

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        try (InputStream input = AppConfig.class.getResourceAsStream("/config/database.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not load /config/database.properties, using built-in defaults: " + e.getMessage());
        }
    }

    public static String getDbUrl() {
        String env = System.getenv("DB_URL");
        if (env != null && !env.isBlank()) return env;
        return properties.getProperty("db.url", "jdbc:postgresql://localhost:5432/bus_reservation_db");
    }

    public static String getDbUser() {
        String env = System.getenv("DB_USER");
        if (env != null && !env.isBlank()) return env;
        return properties.getProperty("db.user", "postgres");
    }

    public static String getDbPassword() {
        String env = System.getenv("DB_PASSWORD");
        if (env != null) return env;
        return properties.getProperty("db.password", "postgres");
    }

    public static String getDbDriver() {
        return properties.getProperty("db.driver", "org.postgresql.Driver");
    }

    public static int getDbTimeoutSeconds() {
        try {
            return Integer.parseInt(properties.getProperty("db.timeout", "3"));
        } catch (NumberFormatException e) {
            return 3;
        }
    }

    public static String getDbMode() {
        return properties.getProperty("db.mode", "auto").trim().toLowerCase();
    }

    public static boolean isInMemoryFallback() {
        return inMemoryFallback;
    }

    public static void setInMemoryFallback(boolean fallback) {
        inMemoryFallback = fallback;
    }
}
