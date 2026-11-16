package com.busreservation.model;

/**
 * Junction entity linking a Booking with a Passenger and their reserved Seat.
 * Demonstrates Object Composition.
 */
public class BookingPassenger {
    private int bookingPassengerId;
    private int bookingId;
    private Passenger passenger;
    private Seat seat;
    private double seatFare;

    public BookingPassenger() {
    }

    public BookingPassenger(int bookingPassengerId, int bookingId, Passenger passenger, Seat seat, double seatFare) {
        this.bookingPassengerId = bookingPassengerId;
        this.bookingId = bookingId;
        this.passenger = passenger;
        this.seat = seat;
        this.seatFare = seatFare;
    }

    public BookingPassenger(Passenger passenger, Seat seat, double seatFare) {
        this(0, 0, passenger, seat, seatFare);
    }

    public int getBookingPassengerId() {
        return bookingPassengerId;
    }

    public void setBookingPassengerId(int bookingPassengerId) {
        this.bookingPassengerId = bookingPassengerId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public double getSeatFare() {
        return seatFare;
    }

    public void setSeatFare(double seatFare) {
        this.seatFare = seatFare;
    }

    @Override
    public String toString() {
        return "Passenger: " + (passenger != null ? passenger.getFullName() : "N/A") +
                " | Seat: " + (seat != null ? seat.getSeatNumber() : "N/A") +
                " | Fare: \u20B9" + seatFare;
    }
}
