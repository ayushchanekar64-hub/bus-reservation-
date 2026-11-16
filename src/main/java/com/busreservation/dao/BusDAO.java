package com.busreservation.dao;

import com.busreservation.model.Bus;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object Interface for Bus entities.
 * Demonstrates Abstraction and Method Overloading.
 */
public interface BusDAO {
    Bus findById(int busId);
    List<Bus> findAll();
    
    // Method Overloading for flexible search capabilities
    List<Bus> searchBuses(String source, String destination);
    List<Bus> searchBuses(String source, String destination, LocalDate journeyDate);

    List<String> getAllSources();
    List<String> getAllDestinations();
    boolean insert(Bus bus);
    boolean update(Bus bus);
}
