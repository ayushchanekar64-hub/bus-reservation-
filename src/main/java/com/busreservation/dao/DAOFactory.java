package com.busreservation.dao;

import com.busreservation.config.AppConfig;
import com.busreservation.config.DBConnection;
import com.busreservation.dao.inmemory.*;
import com.busreservation.dao.postgres.*;

/**
 * Abstract Factory Pattern Implementation for Data Access Objects.
 * Gracefully provides PostgreSQL DAOs or In-Memory DAOs based on runtime configuration
 * and database availability.
 */
public class DAOFactory {
    private static BusDAO busDAO;
    private static SeatDAO seatDAO;
    private static PassengerDAO passengerDAO;
    private static BookingDAO bookingDAO;
    private static UserDAO userDAO;
    private static boolean isInitialized = false;

    public static synchronized void initialize() {
        if (isInitialized) return;

        String mode = AppConfig.getDbMode();
        boolean usePostgres = false;

        if ("postgres".equalsIgnoreCase(mode)) {
            usePostgres = true;
        } else if ("auto".equalsIgnoreCase(mode)) {
            // Check if PostgreSQL is actively listening
            if (DBConnection.isAvailable()) {
                usePostgres = true;
            } else {
                System.out.println("\n[Notice] PostgreSQL server is not currently reachable at " + AppConfig.getDbUrl());
                System.out.println("[Notice] Activating high-fidelity Standalone In-Memory mode with full sample data.\n");
                AppConfig.setInMemoryFallback(true);
            }
        } else {
            AppConfig.setInMemoryFallback(true);
        }

        if (usePostgres) {
            try {
                busDAO = new PostgresBusDAO();
                seatDAO = new PostgresSeatDAO();
                passengerDAO = new PostgresPassengerDAO();
                bookingDAO = new PostgresBookingDAO();
                userDAO = new PostgresUserDAO();
                System.out.println("[Success] Connected to PostgreSQL database successfully.");
            } catch (Exception e) {
                System.err.println("[Warning] Failed to initialize PostgreSQL DAOs: " + e.getMessage() + ". Switching to In-Memory fallback.");
                AppConfig.setInMemoryFallback(true);
                initInMemory();
            }
        } else {
            initInMemory();
        }

        isInitialized = true;
    }

    private static void initInMemory() {
        InMemoryBusDAO inBusDAO = new InMemoryBusDAO();
        InMemorySeatDAO inSeatDAO = new InMemorySeatDAO();
        InMemoryPassengerDAO inPassengerDAO = new InMemoryPassengerDAO();
        InMemoryBookingDAO inBookingDAO = new InMemoryBookingDAO(inSeatDAO, inPassengerDAO);
        InMemoryUserDAO inUserDAO = new InMemoryUserDAO();

        SampleDataLoader.load(inBusDAO, inSeatDAO, inPassengerDAO, inBookingDAO, inUserDAO);

        busDAO = inBusDAO;
        seatDAO = inSeatDAO;
        passengerDAO = inPassengerDAO;
        bookingDAO = inBookingDAO;
        userDAO = inUserDAO;
    }

    public static UserDAO getUserDAO() {
        if (!isInitialized) initialize();
        return userDAO;
    }

    public static BusDAO getBusDAO() {
        if (!isInitialized) initialize();
        return busDAO;
    }

    public static SeatDAO getSeatDAO() {
        if (!isInitialized) initialize();
        return seatDAO;
    }

    public static PassengerDAO getPassengerDAO() {
        if (!isInitialized) initialize();
        return passengerDAO;
    }

    public static BookingDAO getBookingDAO() {
        if (!isInitialized) initialize();
        return bookingDAO;
    }
}
