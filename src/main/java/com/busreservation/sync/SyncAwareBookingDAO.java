package com.busreservation.sync;

import com.busreservation.dao.BookingDAO;
import com.busreservation.dao.inmemory.InMemoryBookingDAO;
import com.busreservation.dao.postgres.PostgresBookingDAO;
import com.busreservation.config.DBConnection;
import com.busreservation.model.Booking;
import com.busreservation.model.BookingPassenger;

import java.util.List;

/**
 * Sync-Aware BookingDAO implementation.
 * Commits bookings immediately to the local project store and
 * automatically syncs transactions to PostgreSQL via DatabaseSyncEngine.
 */
public class SyncAwareBookingDAO implements BookingDAO {

    private final InMemoryBookingDAO localDAO;
    private final PostgresBookingDAO postgresDAO = new PostgresBookingDAO();
    private final DatabaseSyncEngine syncEngine = DatabaseSyncEngine.getInstance();

    public SyncAwareBookingDAO(InMemoryBookingDAO localDAO) {
        this.localDAO = localDAO;
    }

    @Override
    public Booking findById(int bookingId) {
        Booking b = localDAO.findById(bookingId);
        if (b == null && DBConnection.isAvailable()) {
            b = postgresDAO.findById(bookingId);
            if (b != null) localDAO.addBookingDirectly(b);
        }
        return b;
    }

    @Override
    public Booking findByPNR(String pnr) {
        Booking b = localDAO.findByPNR(pnr);
        if (b == null && DBConnection.isAvailable()) {
            b = postgresDAO.findByPNR(pnr);
            if (b != null) localDAO.addBookingDirectly(b);
        }
        return b;
    }

    @Override
    public List<Booking> findAll() {
        return localDAO.findAll();
    }

    @Override
    public List<Booking> findByUserId(int userId) {
        return localDAO.findByUserId(userId);
    }

    @Override
    public List<Booking> findRecent(int limit) {
        return localDAO.findRecent(limit);
    }

    @Override
    public boolean createBookingTransaction(Booking booking, List<BookingPassenger> bookingPassengers) {
        // 1. Commit immediately to local store (guarantees zero lag and offline safety)
        boolean localSuccess = localDAO.createBookingTransaction(booking, bookingPassengers);
        if (localSuccess) {
            // 2. Queue for automatic background sync to PostgreSQL
            syncEngine.queueBooking(booking, bookingPassengers);
        }
        return localSuccess;
    }

    @Override
    public boolean cancelBooking(String pnr) {
        boolean localCancelled = localDAO.cancelBooking(pnr);
        if (localCancelled && DBConnection.isAvailable()) {
            try {
                postgresDAO.cancelBooking(pnr);
            } catch (Exception ignored) {}
        }
        return localCancelled;
    }
}
