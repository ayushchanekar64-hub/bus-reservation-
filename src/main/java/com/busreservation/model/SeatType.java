package com.busreservation.model;

/**
 * Enumeration representing different types of bus seats.
 */
public enum SeatType {
    WINDOW("Window"),
    AISLE("Aisle"),
    MIDDLE("Middle"),
    SLEEPER_LOWER("Lower Sleeper"),
    SLEEPER_UPPER("Upper Sleeper");

    private final String displayName;

    SeatType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
