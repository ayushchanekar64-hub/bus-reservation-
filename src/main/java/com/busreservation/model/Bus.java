package com.busreservation.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class representing a commercial bus.
 * Demonstrates:
 * - Abstraction (abstract template methods for layout and fare)
 * - Encapsulation (protected/private fields with validation)
 * - Polymorphism (extended by SeaterBus, SleeperBus, LuxuryVolvoBus)
 */
public abstract class Bus {
    private int busId;
    private String busNumber;
    private String busName;
    private String busType;
    private String source;
    private String destination;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String journeyDuration;
    private double baseFare;
    private int totalSeats;
    private boolean active;
    private List<String> amenities = new ArrayList<>();

    // Default constructor
    public Bus() {
        this.active = true;
    }

    // Parameterized constructor
    public Bus(int busId, String busNumber, String busName, String busType,
               String source, String destination, LocalTime departureTime,
               LocalTime arrivalTime, String journeyDuration, double baseFare,
               int totalSeats, boolean active) {
        this.busId = busId;
        this.busNumber = busNumber;
        this.busName = busName;
        this.busType = busType;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.journeyDuration = journeyDuration;
        this.baseFare = baseFare;
        this.totalSeats = totalSeats;
        this.active = active;
        initDefaultAmenities();
    }

    protected void initDefaultAmenities() {
        amenities.add("Air Conditioning");
        amenities.add("USB Charging Port");
        amenities.add("GPS Tracking");
    }

    // Abstract polymorphic methods to be implemented by specialized bus types
    public abstract String getSeatingCategory();
    public abstract double calculateSeatFare(Seat seat);
    public abstract int getLayoutColumns();
    public abstract int getAisleAfterColumn();

    // Concrete shared logic
    public String getRoute() {
        return source + " \u2192 " + destination;
    }

    // Getters and Setters with Encapsulation
    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getJourneyDuration() {
        return journeyDuration;
    }

    public void setJourneyDuration(String journeyDuration) {
        this.journeyDuration = journeyDuration;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        if (baseFare < 0) {
            throw new IllegalArgumentException("Base fare cannot be negative");
        }
        this.baseFare = baseFare;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getAmenities() {
        return Collections.unmodifiableList(amenities);
    }

    public void addAmenity(String amenity) {
        if (amenity != null && !amenities.contains(amenity)) {
            amenities.add(amenity);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bus bus)) return false;
        return busId == bus.busId && Objects.equals(busNumber, bus.busNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(busId, busNumber);
    }

    @Override
    public String toString() {
        return busName + " (" + busNumber + ") [" + getRoute() + "]";
    }
}
