package com.busreservation.service;

import com.busreservation.dao.BookingDAO;
import com.busreservation.dao.DAOFactory;
import com.busreservation.model.*;
import com.busreservation.util.PNRGenerator;
import com.busreservation.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service managing transactional reservations, ticket generation,
 * PNR lookups, and cancellations.
 */
public class BookingService {
    private final BookingDAO bookingDAO;
    private final FareService fareService;

    public BookingService() {
        this(DAOFactory.getBookingDAO(), new FareService());
    }

    public BookingService(BookingDAO bookingDAO, FareService fareService) {
        this.bookingDAO = bookingDAO;
        this.fareService = fareService;
    }

    /**
     * Completes an atomic booking transaction for the specified passengers and seats.
     */
    public Booking processBooking(Bus bus, LocalDate journeyDate,
                                  List<Passenger> passengers, List<Seat> seats,
                                  String contactPhone, String contactEmail) {
        int userId = AuthService.getCurrentUser() != null ? AuthService.getCurrentUser().getUserId() : 0;
        return processBooking(userId, bus, journeyDate, passengers, seats, contactPhone, contactEmail);
    }

    public Booking processBooking(int userId, Bus bus, LocalDate journeyDate,
                                  List<Passenger> passengers, List<Seat> seats,
                                  String contactPhone, String contactEmail) {
        if (bus == null) {
            throw new IllegalArgumentException("Bus cannot be null");
        }
        if (!ValidationUtil.isValidJourneyDate(journeyDate)) {
            throw new IllegalArgumentException("Journey date is invalid or in the past.");
        }
        if (passengers == null || seats == null || passengers.size() != seats.size() || passengers.isEmpty()) {
            throw new IllegalArgumentException("Passenger count must match selected seat count.");
        }
        if (!ValidationUtil.isValidMobile(contactPhone)) {
            throw new IllegalArgumentException("Invalid contact mobile number.");
        }
        if (!ValidationUtil.isValidEmail(contactEmail)) {
            throw new IllegalArgumentException("Invalid contact email format.");
        }

        // Validate each passenger
        for (Passenger p : passengers) {
            if (!ValidationUtil.isValidFullName(p.getFullName())) {
                throw new IllegalArgumentException("Invalid passenger name: " + p.getFullName());
            }
            if (!ValidationUtil.isValidAge(p.getAge())) {
                throw new IllegalArgumentException("Invalid age for passenger: " + p.getFullName());
            }
        }

        // Calculate total fare with taxes
        double totalFare = fareService.calculateGrandTotal(bus, seats);

        // Generate Unique PNR
        String pnr = PNRGenerator.generatePNR();

        // Assemble Booking Entity
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setPnrNumber(pnr);
        booking.setBus(bus);
        booking.setJourneyDate(journeyDate);
        booking.setBookingDate(LocalDateTime.now());
        booking.setTotalFare(totalFare);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setContactPhone(contactPhone);
        booking.setContactEmail(contactEmail);

        // Assemble BookingPassengers
        List<BookingPassenger> bpList = new ArrayList<>();
        for (int i = 0; i < passengers.size(); i++) {
            Passenger p = passengers.get(i);
            Seat s = seats.get(i);
            double seatFare = fareService.calculateSeatFare(bus, s);
            // Apply GST proportion
            double seatFareWithGst = Math.round(seatFare * (1.0 + FareService.GST_RATE) * 100.0) / 100.0;
            bpList.add(new BookingPassenger(p, s, seatFareWithGst));
        }

        // Execute transaction
        boolean success = bookingDAO.createBookingTransaction(booking, bpList);
        if (!success) {
            throw new IllegalStateException("Booking failed! One or more seats could not be reserved or are already taken.");
        }

        return booking;
    }

    public Booking getBookingByPNR(String pnr) {
        if (pnr == null || pnr.isBlank()) return null;
        return bookingDAO.findByPNR(pnr.trim());
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.findAll();
    }

    public List<Booking> getBookingsForUser(int userId) {
        if (userId <= 0) return new ArrayList<>();
        return bookingDAO.findByUserId(userId);
    }

    public List<Booking> getRecentBookings(int limit) {
        return bookingDAO.findRecent(limit);
    }

    public boolean cancelBooking(String pnr) {
        if (pnr == null || pnr.isBlank()) return false;
        return bookingDAO.cancelBooking(pnr.trim());
    }
}
