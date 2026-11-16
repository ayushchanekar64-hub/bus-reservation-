package com.busreservation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Core entity representing a completed or active bus booking reservation.
 * Demonstrates:
 * - Composition (contains Bus and List<BookingPassenger>)
 * - Encapsulation (managed collection access, defensive getters)
 */
public class Booking {
    private int bookingId;
    private int userId; // ID of the authenticated user who placed the booking
    private String pnrNumber;
    private Bus bus;
    private LocalDate journeyDate;
    private LocalDateTime bookingDate;
    private double totalFare;
    private BookingStatus bookingStatus;
    private String contactPhone;
    private String contactEmail;
    private final List<BookingPassenger> bookingPassengers = new ArrayList<>();

    public Booking() {
        this.bookingStatus = BookingStatus.CONFIRMED;
        this.bookingDate = LocalDateTime.now();
    }

    public Booking(int bookingId, String pnrNumber, Bus bus, LocalDate journeyDate,
                   LocalDateTime bookingDate, double totalFare, BookingStatus bookingStatus,
                   String contactPhone, String contactEmail) {
        this.bookingId = bookingId;
        this.pnrNumber = pnrNumber;
        this.bus = bus;
        this.journeyDate = journeyDate;
        this.bookingDate = bookingDate != null ? bookingDate : LocalDateTime.now();
        this.totalFare = totalFare;
        this.bookingStatus = bookingStatus != null ? bookingStatus : BookingStatus.CONFIRMED;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    public void addBookingPassenger(BookingPassenger bp) {
        if (bp != null) {
            bookingPassengers.add(bp);
        }
    }

    public void addBookingPassengers(List<BookingPassenger> list) {
        if (list != null) {
            bookingPassengers.addAll(list);
        }
    }

    public List<BookingPassenger> getBookingPassengers() {
        return Collections.unmodifiableList(bookingPassengers);
    }

    public String getSeatNumbersFormatted() {
        if (bookingPassengers.isEmpty()) return "None";
        return bookingPassengers.stream()
                .map(bp -> bp.getSeat() != null ? bp.getSeat().getSeatNumber() : "")
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));
    }

    public String getPassengerNamesFormatted() {
        if (bookingPassengers.isEmpty()) return "None";
        return bookingPassengers.stream()
                .map(bp -> bp.getPassenger() != null ? bp.getPassenger().getFullName() : "")
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));
    }

    public int getSeatCount() {
        return bookingPassengers.size();
    }

    public boolean isConfirmed() {
        return bookingStatus == BookingStatus.CONFIRMED;
    }

    public boolean isCancelled() {
        return bookingStatus == BookingStatus.CANCELLED;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        return Objects.equals(pnrNumber, booking.pnrNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pnrNumber);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "pnr='" + pnrNumber + '\'' +
                ", bus=" + (bus != null ? bus.getBusName() : "N/A") +
                ", journeyDate=" + journeyDate +
                ", seats=" + getSeatNumbersFormatted() +
                ", status=" + bookingStatus +
                '}';
    }
}
