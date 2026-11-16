package com.busreservation.model;

/**
 * Enumeration representing the reservation status of a seat.
 */
public enum SeatStatus {
    AVAILABLE("Available", "#22C55E"),  // Modern vibrant green
    BOOKED("Booked", "#9CA3AF"),        // Neutral grey
    SELECTED("Selected", "#2563EB"),    // Bold royal blue
    BLOCKED("Blocked", "#EF4444");      // Soft red

    private final String label;
    private final String hexColor;

    SeatStatus(String label, String hexColor) {
        this.label = label;
        this.hexColor = hexColor;
    }

    public String getLabel() {
        return label;
    }

    public String getHexColor() {
        return hexColor;
    }
}
