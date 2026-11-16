package com.busreservation.model;

import java.time.LocalTime;

/**
 * Concrete subclass representing a 2+1 Sleeper Bus with Upper and Lower Berths.
 * Demonstrates Inheritance, Polymorphic Layout Definition, and Custom Pricing.
 */
public class SleeperBus extends Bus {

    public SleeperBus() {
        super();
        setBusType("AC Sleeper 2+1");
    }

    public SleeperBus(int busId, String busNumber, String busName, String busType,
                      String source, String destination, LocalTime departureTime,
                      LocalTime arrivalTime, String journeyDuration, double baseFare,
                      int totalSeats, boolean active) {
        super(busId, busNumber, busName, busType, source, destination, departureTime,
              arrivalTime, journeyDuration, baseFare, totalSeats, active);
    }

    @Override
    protected void initDefaultAmenities() {
        super.initDefaultAmenities();
        addAmenity("Full Reclining Berth (180\u00b0)");
        addAmenity("Personal Reading Light");
        addAmenity("Curtains for Privacy");
        addAmenity("Bed Sheet & Blanket");
    }

    @Override
    public String getSeatingCategory() {
        return "Sleeper (2+1 Layout)";
    }

    @Override
    public double calculateSeatFare(Seat seat) {
        if (seat == null) return getBaseFare();
        double multiplier = seat.getFareModifier();
        // Lower berths are slightly more desirable and priced with 10% premium
        if (seat.getSeatType() == SeatType.SLEEPER_LOWER || "LOWER".equalsIgnoreCase(seat.getDeck())) {
            multiplier *= 1.10;
        }
        return Math.round(getBaseFare() * multiplier * 100.0) / 100.0;
    }

    @Override
    public int getLayoutColumns() {
        return 3; // 2 berths on left, 1 single berth on right
    }

    @Override
    public int getAisleAfterColumn() {
        return 2; // Central aisle between double berth and single berth
    }
}
