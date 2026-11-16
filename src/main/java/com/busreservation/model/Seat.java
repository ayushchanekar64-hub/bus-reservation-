package com.busreservation.model;

import java.util.Objects;

/**
 * Encapsulates the properties and state of an individual seat on a bus.
 * Demonstrates Encapsulation with private attributes and accessor methods.
 */
public class Seat {
    private int seatId;
    private int busId;
    private String seatNumber;
    private SeatType seatType;
    private SeatStatus seatStatus;
    private String deck; // "LOWER" or "UPPER"
    private int rowNum;
    private int colNum;
    private double fareModifier;

    // Default constructor
    public Seat() {
        this.seatStatus = SeatStatus.AVAILABLE;
        this.deck = "LOWER";
        this.fareModifier = 1.0;
    }

    // Parameterized constructor
    public Seat(int seatId, int busId, String seatNumber, SeatType seatType, SeatStatus seatStatus,
                String deck, int rowNum, int colNum, double fareModifier) {
        this.seatId = seatId;
        this.busId = busId;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.seatStatus = seatStatus;
        this.deck = deck != null ? deck : "LOWER";
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.fareModifier = fareModifier > 0 ? fareModifier : 1.0;
    }

    // Overloaded convenience constructor
    public Seat(int busId, String seatNumber, SeatType seatType, int rowNum, int colNum) {
        this(0, busId, seatNumber, seatType, SeatStatus.AVAILABLE, "LOWER", rowNum, colNum, 1.0);
    }

    public boolean isAvailable() {
        return this.seatStatus == SeatStatus.AVAILABLE;
    }

    public boolean isBooked() {
        return this.seatStatus == SeatStatus.BOOKED;
    }

    public boolean isSelected() {
        return this.seatStatus == SeatStatus.SELECTED;
    }

    // Getters and Setters
    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public SeatStatus getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(SeatStatus seatStatus) {
        this.seatStatus = seatStatus;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    public double getFareModifier() {
        return fareModifier;
    }

    public void setFareModifier(double fareModifier) {
        this.fareModifier = fareModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seat seat)) return false;
        return busId == seat.busId && Objects.equals(seatNumber, seat.seatNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(busId, seatNumber);
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatNumber='" + seatNumber + '\'' +
                ", type=" + seatType +
                ", status=" + seatStatus +
                ", deck='" + deck + '\'' +
                ", row=" + rowNum +
                ", col=" + colNum +
                '}';
    }
}
