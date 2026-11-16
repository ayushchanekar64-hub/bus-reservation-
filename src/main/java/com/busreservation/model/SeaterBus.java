package com.busreservation.model;

import java.time.LocalTime;

/**
 * Concrete subclass representing a standard 2+2 Seater Bus.
 * Demonstrates Inheritance and Polymorphic Method Overriding.
 */
public class SeaterBus extends Bus {

    public SeaterBus() {
        super();
        setBusType("AC Seater 2+2");
    }

    public SeaterBus(int busId, String busNumber, String busName, String busType,
                     String source, String destination, LocalTime departureTime,
                     LocalTime arrivalTime, String journeyDuration, double baseFare,
                     int totalSeats, boolean active) {
        super(busId, busNumber, busName, busType, source, destination, departureTime,
              arrivalTime, journeyDuration, baseFare, totalSeats, active);
    }

    @Override
    public String getSeatingCategory() {
        return "Seater (2+2 Layout)";
    }

    @Override
    public double calculateSeatFare(Seat seat) {
        if (seat == null) return getBaseFare();
        double multiplier = seat.getFareModifier();
        // Window seats carry a modest 5% premium for panoramic view
        if (seat.getSeatType() == SeatType.WINDOW) {
            multiplier *= 1.05;
        }
        return Math.round(getBaseFare() * multiplier * 100.0) / 100.0;
    }

    @Override
    public int getLayoutColumns() {
        return 4; // 2 on left, 2 on right
    }

    @Override
    public int getAisleAfterColumn() {
        return 2; // Central aisle between col 2 and 3
    }
}
