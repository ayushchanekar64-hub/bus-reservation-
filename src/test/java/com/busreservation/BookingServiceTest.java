package com.busreservation;

import com.busreservation.dao.inmemory.InMemoryBookingDAO;
import com.busreservation.dao.inmemory.InMemoryPassengerDAO;
import com.busreservation.dao.inmemory.InMemorySeatDAO;
import com.busreservation.model.*;
import com.busreservation.service.BookingService;
import com.busreservation.service.FareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Booking Transaction, Prevention of Double Booking, & Cancellation Tests")
class BookingServiceTest {

    private BookingService bookingService;
    private InMemorySeatDAO seatDAO;
    private Bus bus;
    private Seat seat1;
    private Seat seat2;

    @BeforeEach
    void setUp() {
        seatDAO = new InMemorySeatDAO();
        InMemoryPassengerDAO passengerDAO = new InMemoryPassengerDAO();
        InMemoryBookingDAO bookingDAO = new InMemoryBookingDAO(seatDAO, passengerDAO);
        FareService fareService = new FareService();

        bookingService = new BookingService(bookingDAO, fareService);

        bus = new SeaterBus(1, "DL-01-AB-1234", "Royal Express", "AC Seater 2+2",
                "Delhi", "Jaipur", LocalTime.of(6, 0), LocalTime.of(11, 30), "05h 30m", 500.0, 40, true);

        seat1 = new Seat(101, 1, "1A", SeatType.AISLE, SeatStatus.AVAILABLE, "LOWER", 1, 2, 1.0);
        seat2 = new Seat(102, 1, "1W", SeatType.WINDOW, SeatStatus.AVAILABLE, "LOWER", 1, 1, 1.0);

        seatDAO.addSeatDirectly(seat1);
        seatDAO.addSeatDirectly(seat2);
    }

    @Test
    @DisplayName("Successful booking transaction marks seats BOOKED and generates PNR")
    void testSuccessfulBooking() {
        LocalDate journeyDate = LocalDate.now().plusDays(2);
        Passenger p1 = new Passenger("Aarav Sharma", 25, "Male", "9876543210");
        Passenger p2 = new Passenger("Priya Sharma", 23, "Female", "9876543210");

        Booking booking = bookingService.processBooking(
                bus, journeyDate, List.of(p1, p2), List.of(seat1, seat2),
                "9876543210", "aarav@example.com"
        );

        assertNotNull(booking);
        assertNotNull(booking.getPnrNumber());
        assertTrue(booking.getPnrNumber().startsWith("PNR-"));
        assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());
        assertEquals(2, booking.getSeatCount());

        // Verify seats are now BOOKED in the store
        assertEquals(SeatStatus.BOOKED, seatDAO.getById(101).getSeatStatus());
        assertEquals(SeatStatus.BOOKED, seatDAO.getById(102).getSeatStatus());
    }

    @Test
    @DisplayName("Duplicate seat booking fails atomically (Concurrency Protection)")
    void testDuplicateSeatBookingPrevention() {
        LocalDate journeyDate = LocalDate.now().plusDays(2);
        Passenger p1 = new Passenger("User One", 30, "Male", "9876543210");
        Passenger p2 = new Passenger("User Two", 28, "Female", "9123456780");

        // First user books seat1
        Booking b1 = bookingService.processBooking(
                bus, journeyDate, List.of(p1), List.of(seat1), "9876543210", "user1@example.com"
        );
        assertNotNull(b1);
        assertEquals(SeatStatus.BOOKED, seatDAO.getById(101).getSeatStatus());

        // Second user attempts to book the SAME seat1
        assertThrows(IllegalStateException.class, () -> {
            bookingService.processBooking(
                    bus, journeyDate, List.of(p2), List.of(seat1), "9123456780", "user2@example.com"
            );
        }, "Should throw IllegalStateException when trying to book an already reserved seat");
    }

    @Test
    @DisplayName("Booking cancellation updates status and restores seat availability")
    void testBookingCancellationAndSeatRestoration() {
        LocalDate journeyDate = LocalDate.now().plusDays(2);
        Passenger p1 = new Passenger("Aarav Sharma", 25, "Male", "9876543210");

        Booking booking = bookingService.processBooking(
                bus, journeyDate, List.of(p1), List.of(seat1), "9876543210", "aarav@example.com"
        );

        String pnr = booking.getPnrNumber();
        assertEquals(SeatStatus.BOOKED, seatDAO.getById(101).getSeatStatus());

        // Cancel the booking
        boolean cancelled = bookingService.cancelBooking(pnr);
        assertTrue(cancelled, "Booking cancellation should succeed");

        // Verify status changed and seat restored to AVAILABLE
        Booking updated = bookingService.getBookingByPNR(pnr);
        assertNotNull(updated);
        assertEquals(BookingStatus.CANCELLED, updated.getBookingStatus());
        assertEquals(SeatStatus.AVAILABLE, seatDAO.getById(101).getSeatStatus(), "Seat must be restored to AVAILABLE");
    }
}
