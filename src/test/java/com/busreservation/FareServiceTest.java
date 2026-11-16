package com.busreservation;

import com.busreservation.model.*;
import com.busreservation.service.FareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Fare Calculation & Polymorphism Unit Tests")
class FareServiceTest {

    private FareService fareService;

    @BeforeEach
    void setUp() {
        fareService = new FareService();
    }

    @Test
    @DisplayName("Polymorphic fare calculation for SeaterBus (Aisle vs Window)")
    void testSeaterBusFare() {
        SeaterBus bus = new SeaterBus(1, "BUS-01", "Express Seater", "AC Seater 2+2",
                "Delhi", "Jaipur", LocalTime.of(8, 0), LocalTime.of(13, 0), "5h", 500.0, 40, true);

        Seat aisleSeat = new Seat(1, 1, "1A", SeatType.AISLE, SeatStatus.AVAILABLE, "LOWER", 1, 2, 1.0);
        Seat windowSeat = new Seat(2, 1, "1W", SeatType.WINDOW, SeatStatus.AVAILABLE, "LOWER", 1, 1, 1.0);

        double aisleFare = fareService.calculateSeatFare(bus, aisleSeat);
        double windowFare = fareService.calculateSeatFare(bus, windowSeat);

        assertEquals(500.0, aisleFare, 0.01, "Aisle seat should match base fare");
        assertEquals(525.0, windowFare, 0.01, "Window seat should include 5% preference premium");
    }

    @Test
    @DisplayName("Polymorphic fare calculation for LuxuryVolvoBus")
    void testLuxuryVolvoFare() {
        LuxuryVolvoBus volvo = new LuxuryVolvoBus(2, "VOL-01", "Club Class", "Volvo Multi-Axle 2+2",
                "Delhi", "Jaipur", LocalTime.of(9, 0), LocalTime.of(14, 0), "5h", 600.0, 40, true);

        Seat seat = new Seat(3, 2, "2A", SeatType.AISLE, SeatStatus.AVAILABLE, "LOWER", 1, 2, 1.0);
        double fare = fareService.calculateSeatFare(volvo, seat);

        // 600 * 1.08 = 648
        assertEquals(648.0, fare, 0.01, "Volvo bus should include 8% comfort surcharge");
    }

    @Test
    @DisplayName("Polymorphic fare calculation for SleeperBus")
    void testSleeperBusFare() {
        SleeperBus sleeper = new SleeperBus(3, "SLP-01", "Grand Sleeper", "AC Sleeper 2+1",
                "Bangalore", "Goa", LocalTime.of(21, 0), LocalTime.of(9, 0), "12h", 1000.0, 30, true);

        Seat lowerBerth = new Seat(4, 3, "L1", SeatType.SLEEPER_LOWER, SeatStatus.AVAILABLE, "LOWER", 1, 1, 1.0);
        Seat upperBerth = new Seat(5, 3, "U1", SeatType.SLEEPER_UPPER, SeatStatus.AVAILABLE, "UPPER", 1, 1, 1.0);

        double lowerFare = fareService.calculateSeatFare(sleeper, lowerBerth);
        double upperFare = fareService.calculateSeatFare(sleeper, upperBerth);

        assertEquals(1100.0, lowerFare, 0.01, "Lower berth should include 10% preference premium");
        assertEquals(1000.0, upperFare, 0.01, "Upper berth matches standard base berth fare");
    }

    @Test
    @DisplayName("Grand total includes base total and 5% GST")
    void testGrandTotalWithGst() {
        SeaterBus bus = new SeaterBus(1, "BUS-01", "Express Seater", "AC Seater 2+2",
                "Delhi", "Jaipur", LocalTime.of(8, 0), LocalTime.of(13, 0), "5h", 500.0, 40, true);

        Seat s1 = new Seat(1, 1, "1A", SeatType.AISLE, SeatStatus.AVAILABLE, "LOWER", 1, 2, 1.0);
        Seat s2 = new Seat(2, 1, "2A", SeatType.AISLE, SeatStatus.AVAILABLE, "LOWER", 2, 2, 1.0);

        double baseTotal = fareService.calculateBaseTotal(bus, List.of(s1, s2)); // 1000.0
        double gst = fareService.calculateGst(baseTotal);                        // 50.0
        double grandTotal = fareService.calculateGrandTotal(bus, List.of(s1, s2)); // 1050.0

        assertEquals(1000.0, baseTotal, 0.01);
        assertEquals(50.0, gst, 0.01);
        assertEquals(1050.0, grandTotal, 0.01);
    }
}
