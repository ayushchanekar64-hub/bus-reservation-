package com.busreservation.dao;

import com.busreservation.config.AppConfig;
import com.busreservation.config.DBConnection;
import com.busreservation.dao.inmemory.*;
import com.busreservation.dao.postgres.*;
import com.busreservation.sync.DatabaseSyncEngine;
import com.busreservation.sync.SyncAwareBookingDAO;
import com.busreservation.sync.SyncAwareUserDAO;

/**
 * Abstract Factory Pattern Implementation for Data Access Objects.
 * Gracefully provides:
 * 1. Intelligent Sync Engine (Local Persistent Store + Background Auto-Sync to PostgreSQL)
 * 2. Direct PostgreSQL DAOs
 * 3. Standalone In-Memory DAOs
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

        if ("postgres_direct".equalsIgnoreCase(mode)) {
            try {
                busDAO = new PostgresBusDAO();
                seatDAO = new PostgresSeatDAO();
                passengerDAO = new PostgresPassengerDAO();
                bookingDAO = new PostgresBookingDAO();
                userDAO = new PostgresUserDAO();
                System.out.println("[Success] Connected directly to PostgreSQL database.");
            } catch (Exception e) {
                System.err.println("[Warning] Failed direct PostgreSQL connection: " + e.getMessage() + ". Switching to Auto-Sync Engine.");
                initSyncEngine();
            }
        } else {
            // Default & Recommended: Auto-Sync Engine (Local Storage + Auto-Sync to PostgreSQL)
            initSyncEngine();
        }

        isInitialized = true;
    }

    private static void initSyncEngine() {
        InMemoryBusDAO inBusDAO = new InMemoryBusDAO();
        InMemorySeatDAO inSeatDAO = new InMemorySeatDAO();
        InMemoryPassengerDAO inPassengerDAO = new InMemoryPassengerDAO();
        InMemoryBookingDAO inBookingDAO = new InMemoryBookingDAO(inSeatDAO, inPassengerDAO);
        InMemoryUserDAO inUserDAO = new InMemoryUserDAO();

        SampleDataLoader.load(inBusDAO, inSeatDAO, inPassengerDAO, inBookingDAO, inUserDAO);

        busDAO = inBusDAO;
        seatDAO = inSeatDAO;
        passengerDAO = inPassengerDAO;
        bookingDAO = new SyncAwareBookingDAO(inBookingDAO);
        userDAO = new SyncAwareUserDAO(inUserDAO);

        DatabaseSyncEngine.getInstance(); // Spin up background sync daemon

        System.out.println("==================================================================");
        System.out.println(" [Sync Engine] Dual-Tier Auto-Sync Database Active");
        System.out.println(" - Local Store: Zero-downtime temporary local persistence");
        System.out.println(" - Cloud/PostgreSQL: Auto-syncs transactions automatically");
        System.out.println("==================================================================");
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
