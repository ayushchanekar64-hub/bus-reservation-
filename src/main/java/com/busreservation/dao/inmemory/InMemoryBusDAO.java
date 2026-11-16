package com.busreservation.dao.inmemory;

import com.busreservation.dao.BusDAO;
import com.busreservation.model.Bus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Thread-safe In-Memory implementation of BusDAO.
 * Used for rapid local development, unit testing, and standalone evaluation.
 */
public class InMemoryBusDAO implements BusDAO {
    private final Map<Integer, Bus> busStorage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(100);

    public InMemoryBusDAO() {
    }

    public void addBusDirectly(Bus bus) {
        if (bus != null) {
            busStorage.put(bus.getBusId(), bus);
            if (bus.getBusId() >= idGenerator.get()) {
                idGenerator.set(bus.getBusId() + 1);
            }
        }
    }

    @Override
    public Bus findById(int busId) {
        return busStorage.get(busId);
    }

    @Override
    public List<Bus> findAll() {
        return busStorage.values().stream()
                .filter(Bus::isActive)
                .sorted((b1, b2) -> Integer.compare(b1.getBusId(), b2.getBusId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Bus> searchBuses(String source, String destination) {
        if (source == null || destination == null) return new ArrayList<>();
        return busStorage.values().stream()
                .filter(Bus::isActive)
                .filter(b -> b.getSource().equalsIgnoreCase(source.trim()) &&
                             b.getDestination().equalsIgnoreCase(destination.trim()))
                .sorted((b1, b2) -> b1.getDepartureTime().compareTo(b2.getDepartureTime()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Bus> searchBuses(String source, String destination, LocalDate journeyDate) {
        return searchBuses(source, destination);
    }

    @Override
    public List<String> getAllSources() {
        return busStorage.values().stream()
                .filter(Bus::isActive)
                .map(Bus::getSource)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllDestinations() {
        return busStorage.values().stream()
                .filter(Bus::isActive)
                .map(Bus::getDestination)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public boolean insert(Bus bus) {
        if (bus == null) return false;
        if (bus.getBusId() <= 0) {
            bus.setBusId(idGenerator.getAndIncrement());
        }
        busStorage.put(bus.getBusId(), bus);
        return true;
    }

    @Override
    public boolean update(Bus bus) {
        if (bus == null || !busStorage.containsKey(bus.getBusId())) return false;
        busStorage.put(bus.getBusId(), bus);
        return true;
    }
}
