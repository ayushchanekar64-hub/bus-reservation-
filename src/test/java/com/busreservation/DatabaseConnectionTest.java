package com.busreservation;

import com.busreservation.config.AppConfig;
import com.busreservation.config.DBConnection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Database Configuration & Connectivity Tests")
class DatabaseConnectionTest {

    @Test
    @DisplayName("AppConfig loads default parameters accurately")
    void testAppConfigDefaults() {
        assertNotNull(AppConfig.getDbUrl());
        assertTrue(AppConfig.getDbUrl().contains("bus_reservation_db"));
        assertNotNull(AppConfig.getDbUser());
        assertEquals("org.postgresql.Driver", AppConfig.getDbDriver());
        assertTrue(AppConfig.getDbTimeoutSeconds() > 0);
    }

    @Test
    @DisplayName("DBConnection isAvailable safely returns boolean without unhandled exceptions")
    void testDbAvailabilityCheck() {
        // When PostgreSQL is not running, isAvailable() should gracefully return false rather than crashing
        assertDoesNotThrow(() -> {
            boolean available = DBConnection.isAvailable();
            System.out.println("PostgreSQL server online status: " + available);
        });
    }

    @Test
    @DisplayName("In-Memory fallback flag can be set and queried")
    void testFallbackFlag() {
        AppConfig.setInMemoryFallback(true);
        assertTrue(AppConfig.isInMemoryFallback());
        AppConfig.setInMemoryFallback(false);
        assertFalse(AppConfig.isInMemoryFallback());
    }
}
