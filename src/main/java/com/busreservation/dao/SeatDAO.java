package com.busreservation.dao;

import com.busreservation.model.Seat;
import com.busreservation.model.SeatStatus;
import java.sql.Connection;
import java.util.List;

/**
 * Data Access Object Interface for Seat entities.
 */
public interface SeatDAO {
    List<Seat> findByBusId(int busId);
    Seat findByBusIdAndSeatNumber(int busId, String seatNumber);
    boolean updateSeatStatus(int seatId, SeatStatus status);
    boolean updateSeatStatuses(List<Integer> seatIds, SeatStatus status, Connection conn);
    int countAvailableSeats(int busId);
    void bulkInsertSeats(List<Seat> seats);
}
