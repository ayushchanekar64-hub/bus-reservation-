package com.busreservation.dao.inmemory;

import com.busreservation.dao.PassengerDAO;
import com.busreservation.model.Passenger;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe In-Memory implementation of PassengerDAO.
 */
public class InMemoryPassengerDAO implements PassengerDAO {
    private final Map<Integer, Passenger> passengerStorage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(100);

    public void addPassengerDirectly(Passenger p) {
        if (p != null) {
            passengerStorage.put(p.getPassengerId(), p);
            if (p.getPassengerId() >= idGenerator.get()) {
                idGenerator.set(p.getPassengerId() + 1);
            }
        }
    }

    @Override
    public Passenger findById(int passengerId) {
        return passengerStorage.get(passengerId);
    }

    @Override
    public Passenger findByMobile(String mobileNumber) {
        return passengerStorage.values().stream()
                .filter(p -> p.getMobileNumber() != null && p.getMobileNumber().equals(mobileNumber))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int insert(Passenger passenger) {
        return insert(passenger, null);
    }

    @Override
    public int insert(Passenger passenger, Connection conn) {
        if (passenger == null) return -1;
        int id = idGenerator.getAndIncrement();
        passenger.setPassengerId(id);
        passengerStorage.put(id, passenger);
        return id;
    }

    @Override
    public List<Passenger> findAll() {
        return new ArrayList<>(passengerStorage.values());
    }
}
