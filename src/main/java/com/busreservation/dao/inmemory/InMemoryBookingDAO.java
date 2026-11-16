package com.busreservation.dao.inmemory;

import com.busreservation.dao.BookingDAO;
import com.busreservation.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Thread-safe In-Memory implementation of BookingDAO.
 * Simulates ACID transactions and seat locking with synchronized blocks.
 */
public class InMemoryBookingDAO implements BookingDAO {
    private final Map<Integer, Booking> bookingStorage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(100);
    private final InMemorySeatDAO seatDAO;
    private final InMemoryPassengerDAO passengerDAO;

    public InMemoryBookingDAO(InMemorySeatDAO seatDAO, InMemoryPassengerDAO passengerDAO) {
        this.seatDAO = seatDAO;
        this.passengerDAO = passengerDAO;
    }

    public void addBookingDirectly(Booking b) {
        if (b != null) {
            bookingStorage.put(b.getBookingId(), b);
            if (b.getBookingId() >= idGenerator.get()) {
                idGenerator.set(b.getBookingId() + 1);
            }
        }
    }

    @Override
    public Booking findById(int bookingId) {
        return bookingStorage.get(bookingId);
    }

    @Override
    public Booking findByPNR(String pnr) {
        if (pnr == null) return null;
        return bookingStorage.values().stream()
                .filter(b -> b.getPnrNumber().equalsIgnoreCase(pnr.trim()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Booking> findAll() {
        return bookingStorage.values().stream()
                .sorted(Comparator.comparing(Booking::getBookingDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByUserId(int userId) {
        return bookingStorage.values().stream()
                .filter(b -> b.getUserId() == userId)
                .sorted(Comparator.comparing(Booking::getBookingDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findRecent(int limit) {
        return bookingStorage.values().stream()
                .sorted(Comparator.comparing(Booking::getBookingDate).reversed())
                .limit(limit > 0 ? limit : 5)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized boolean createBookingTransaction(Booking booking, List<BookingPassenger> bookingPassengers) {
        if (booking == null || bookingPassengers == null || bookingPassengers.isEmpty()) {
            return false;
        }

        // STEP 1: Verify all seats are available (Atomic check)
        for (BookingPassenger bp : bookingPassengers) {
            Seat s = seatDAO.getById(bp.getSeat().getSeatId());
            if (s == null || s.getSeatStatus() != SeatStatus.AVAILABLE) {
                System.err.println("Seat " + (s != null ? s.getSeatNumber() : "null") + " is unavailable! Transaction rolled back.");
                return false;
            }
        }

        // STEP 2: Allocate Booking ID and save booking
        int newBookingId = idGenerator.getAndIncrement();
        booking.setBookingId(newBookingId);

        // STEP 3: Save passengers and update seats
        for (BookingPassenger bp : bookingPassengers) {
            Passenger p = bp.getPassenger();
            if (p.getPassengerId() <= 0) {
                passengerDAO.insert(p);
            }
            // Mark seat BOOKED
            Seat s = seatDAO.getById(bp.getSeat().getSeatId());
            if (s != null) {
                s.setSeatStatus(SeatStatus.BOOKED);
            }
            booking.addBookingPassenger(bp);
        }

        bookingStorage.put(newBookingId, booking);
        return true;
    }

    @Override
    public synchronized boolean cancelBooking(String pnr) {
        Booking b = findByPNR(pnr);
        if (b == null || b.isCancelled()) {
            return false;
        }

        // 1. Mark status CANCELLED
        b.setBookingStatus(BookingStatus.CANCELLED);

        // 2. Mark seats back to AVAILABLE
        for (BookingPassenger bp : b.getBookingPassengers()) {
            if (bp.getSeat() != null) {
                Seat s = seatDAO.getById(bp.getSeat().getSeatId());
                if (s != null) {
                    s.setSeatStatus(SeatStatus.AVAILABLE);
                }
            }
        }
        return true;
    }
}
