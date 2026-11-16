package com.busreservation;

import com.busreservation.dao.inmemory.InMemorySeatDAO;
import com.busreservation.model.Seat;
import com.busreservation.model.SeatStatus;
import com.busreservation.model.SeatType;
import com.busreservation.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Seat Availability & Selection Unit Tests")
class SeatServiceTest {

    private SeatService seatService;
    private InMemorySeatDAO seatDAO;

    @BeforeEach
    void setUp() {
        seatDAO = new InMemorySeatDAO();
        seatService = new SeatService(seatDAO);

        Seat s1 = new Seat(101, 1, "1A", SeatType.AISLE, SeatStatus.AVAILABLE, "LOWER", 1, 2, 1.0);
        Seat s2 = new Seat(102, 1, "1W", SeatType.WINDOW, SeatStatus.AVAILABLE, "LOWER", 1, 1, 1.0);
        Seat s3 = new Seat(103, 1, "2A", SeatType.AISLE, SeatStatus.BOOKED, "LOWER", 2, 2, 1.0);

        seatDAO.addSeatDirectly(s1);
        seatDAO.addSeatDirectly(s2);
        seatDAO.addSeatDirectly(s3);
    }

    @Test
    @DisplayName("Retrieve seats for a specific bus")
    void testGetSeatsForBus() {
        List<Seat> seats = seatService.getSeatsForBus(1);
        assertEquals(3, seats.size());
    }

    @Test
    @DisplayName("Count only available seats")
    void testCountAvailableSeats() {
        int available = seatService.getAvailableSeatCount(1);
        assertEquals(2, available, "Should count 2 available seats (s1, s2)");
    }

    @Test
    @DisplayName("Seat availability validation returns true when all requested seats are available")
    void testValidateSeatAvailabilitySuccess() {
        Seat s1 = seatService.getSeatByNumber(1, "1A");
        Seat s2 = seatService.getSeatByNumber(1, "1W");

        boolean ok = seatService.areAllSeatsAvailable(1, List.of(s1, s2));
        assertTrue(ok);
    }

    @Test
    @DisplayName("Seat availability validation returns false if any requested seat is booked")
    void testValidateSeatAvailabilityFailure() {
        Seat s1 = seatService.getSeatByNumber(1, "1A");
        Seat s3 = seatService.getSeatByNumber(1, "2A"); // Booked

        boolean ok = seatService.areAllSeatsAvailable(1, List.of(s1, s3));
        assertFalse(ok, "Validation must fail when any seat is already booked");
    }
}
