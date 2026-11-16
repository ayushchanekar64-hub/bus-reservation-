package com.busreservation.service;

import com.busreservation.dao.DAOFactory;
import com.busreservation.dao.SeatDAO;
import com.busreservation.model.Seat;
import com.busreservation.model.SeatStatus;

import java.util.List;

/**
 * Business Service handling Seat availability, seat map retrieval, and status queries.
 */
public class SeatService {
    private final SeatDAO seatDAO;

    public SeatService() {
        this(DAOFactory.getSeatDAO());
    }

    public SeatService(SeatDAO seatDAO) {
        this.seatDAO = seatDAO;
    }

    public List<Seat> getSeatsForBus(int busId) {
        return seatDAO.findByBusId(busId);
    }

    public Seat getSeatByNumber(int busId, String seatNumber) {
        return seatDAO.findByBusIdAndSeatNumber(busId, seatNumber);
    }

    public int getAvailableSeatCount(int busId) {
        return seatDAO.countAvailableSeats(busId);
    }

    public boolean areAllSeatsAvailable(int busId, List<Seat> requestedSeats) {
        if (requestedSeats == null || requestedSeats.isEmpty()) return false;
        for (Seat requested : requestedSeats) {
            Seat current = seatDAO.findByBusIdAndSeatNumber(busId, requested.getSeatNumber());
            if (current == null || current.getSeatStatus() != SeatStatus.AVAILABLE) {
                return false;
            }
        }
        return true;
    }
}
