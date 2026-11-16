package com.busreservation.dao;

import com.busreservation.model.Passenger;
import java.sql.Connection;
import java.util.List;

/**
 * Data Access Object Interface for Passenger entities.
 */
public interface PassengerDAO {
    Passenger findById(int passengerId);
    Passenger findByMobile(String mobileNumber);
    int insert(Passenger passenger);
    int insert(Passenger passenger, Connection conn);
    List<Passenger> findAll();
}
