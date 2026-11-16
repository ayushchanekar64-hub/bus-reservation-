package com.busreservation.model;

import java.time.LocalTime;

/**
 * Concrete subclass representing a High-End Volvo Multi-Axle Bus.
 * Demonstrates Multi-level Inheritance and Specialized Polymorphic Behavior.
 */
public class LuxuryVolvoBus extends SeaterBus {

    public LuxuryVolvoBus() {
        super();
        setBusType("Volvo Multi-Axle 2+2");
    }

    public LuxuryVolvoBus(int busId, String busNumber, String busName, String busType,
                          String source, String destination, LocalTime departureTime,
                          LocalTime arrivalTime, String journeyDuration, double baseFare,
                          int totalSeats, boolean active) {
        super(busId, busNumber, busName, busType, source, destination, departureTime,
              arrivalTime, journeyDuration, baseFare, totalSeats, active);
    }

    @Override
    protected void initDefaultAmenities() {
        super.initDefaultAmenities();
        addAmenity("High-Speed WiFi");
        addAmenity("Mineral Water Bottle");
        addAmenity("Blanket & Pillow");
        addAmenity("Live Bus Tracking");
        addAmenity("Emergency SOS");
    }

    @Override
    public String getSeatingCategory() {
        return "Volvo Luxury Class (2+2 Multi-Axle)";
    }

    @Override
    public double calculateSeatFare(Seat seat) {
        // Base seater fare + luxury service surcharge
        double standard = super.calculateSeatFare(seat);
        return Math.round(standard * 1.08 * 100.0) / 100.0; // 8% comfort and amenity enhancement
    }
}
