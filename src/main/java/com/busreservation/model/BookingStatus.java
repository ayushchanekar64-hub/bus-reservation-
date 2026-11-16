package com.busreservation.model;

/**
 * Enumeration representing the lifecycle state of a booking reservation.
 */
public enum BookingStatus {
    CONFIRMED("Confirmed", "#16A34A"),
    CANCELLED("Cancelled", "#DC2626");

    private final String displayName;
    private final String colorHex;

    BookingStatus(String displayName, String colorHex) {
        this.displayName = displayName;
        this.colorHex = colorHex;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorHex() {
        return colorHex;
    }
}
