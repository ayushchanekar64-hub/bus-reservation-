package com.busreservation.service;

import com.busreservation.model.Bus;
import com.busreservation.model.Seat;

import java.util.List;

/**
 * Service for calculating dynamic fares, taxes, and service charges.
 * Leverages polymorphic Bus.calculateSeatFare(seat) methods.
 */
public class FareService {
    public static final double GST_RATE = 0.05; // 5% GST for commercial passenger road transport

    /**
     * Calculates fare for an individual seat on a bus.
     */
    public double calculateSeatFare(Bus bus, Seat seat) {
        if (bus == null) return 0.0;
        return bus.calculateSeatFare(seat);
    }

    /**
     * Calculates base subtotal for a collection of selected seats.
     */
    public double calculateBaseTotal(Bus bus, List<Seat> seats) {
        if (bus == null || seats == null || seats.isEmpty()) return 0.0;
        double sum = 0.0;
        for (Seat s : seats) {
            sum += bus.calculateSeatFare(s);
        }
        return Math.round(sum * 100.0) / 100.0;
    }

    /**
     * Calculates applicable GST on base subtotal.
     */
    public double calculateGst(double baseTotal) {
        return Math.round(baseTotal * GST_RATE * 100.0) / 100.0;
    }

    /**
     * Calculates grand total payable including GST.
     */
    public double calculateGrandTotal(Bus bus, List<Seat> seats) {
        double base = calculateBaseTotal(bus, seats);
        double gst = calculateGst(base);
        return Math.round((base + gst) * 100.0) / 100.0;
    }
}
