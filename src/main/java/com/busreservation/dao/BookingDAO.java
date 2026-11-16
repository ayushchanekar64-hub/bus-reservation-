package com.busreservation.dao;

import com.busreservation.model.Booking;
import com.busreservation.model.BookingPassenger;
import java.util.List;

/**
 * Data Access Object Interface for Booking entities.
 * Enforces transactional execution and lifecycle controls.
 */
public interface BookingDAO {
    Booking findById(int bookingId);
    Booking findByPNR(String pnr);
    List<Booking> findAll();
    List<Booking> findByUserId(int userId);
    List<Booking> findRecent(int limit);
    
    /**
     * Executes atomic reservation transaction with seat locking.
     */
    boolean createBookingTransaction(Booking booking, List<BookingPassenger> bookingPassengers);

    /**
     * Cancels an existing booking and restores seat availability.
     */
    boolean cancelBooking(String pnr);
}
