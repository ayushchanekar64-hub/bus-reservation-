package com.busreservation.dao.inmemory;

import com.busreservation.dao.SeatDAO;
import com.busreservation.model.Seat;
import com.busreservation.model.SeatStatus;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Thread-safe In-Memory implementation of SeatDAO.
 */
public class InMemorySeatDAO implements SeatDAO {
    private final Map<Integer, Seat> seatStorage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1000);

    public void addSeatDirectly(Seat seat) {
        if (seat != null) {
            seatStorage.put(seat.getSeatId(), seat);
            if (seat.getSeatId() >= idGenerator.get()) {
                idGenerator.set(seat.getSeatId() + 1);
            }
        }
    }

    @Override
    public List<Seat> findByBusId(int busId) {
        return seatStorage.values().stream()
                .filter(s -> s.getBusId() == busId)
                .sorted((s1, s2) -> {
                    int c = s1.getDeck().compareTo(s2.getDeck());
                    if (c != 0) return c;
                    c = Integer.compare(s1.getRowNum(), s2.getRowNum());
                    if (c != 0) return c;
                    return Integer.compare(s1.getColNum(), s2.getColNum());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Seat findByBusIdAndSeatNumber(int busId, String seatNumber) {
        return seatStorage.values().stream()
                .filter(s -> s.getBusId() == busId && s.getSeatNumber().equalsIgnoreCase(seatNumber))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean updateSeatStatus(int seatId, SeatStatus status) {
        Seat s = seatStorage.get(seatId);
        if (s != null) {
            s.setSeatStatus(status);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSeatStatuses(List<Integer> seatIds, SeatStatus status, Connection conn) {
        if (seatIds == null) return false;
        for (int id : seatIds) {
            updateSeatStatus(id, status);
        }
        return true;
    }

    @Override
    public int countAvailableSeats(int busId) {
        return (int) seatStorage.values().stream()
                .filter(s -> s.getBusId() == busId && s.getSeatStatus() == SeatStatus.AVAILABLE)
                .count();
    }

    @Override
    public void bulkInsertSeats(List<Seat> seats) {
        if (seats != null) {
            for (Seat s : seats) {
                if (s.getSeatId() <= 0) {
                    s.setSeatId(idGenerator.getAndIncrement());
                }
                seatStorage.put(s.getSeatId(), s);
            }
        }
    }

    public Seat getById(int seatId) {
        return seatStorage.get(seatId);
    }
}
