package com.busreservation.service;

import com.busreservation.dao.BusDAO;
import com.busreservation.dao.DAOFactory;
import com.busreservation.model.Bus;
import com.busreservation.util.ValidationUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Business Service handling Bus searching, catalog queries, and route lookups.
 */
public class BusService {
    private final BusDAO busDAO;

    public BusService() {
        this(DAOFactory.getBusDAO());
    }

    public BusService(BusDAO busDAO) {
        this.busDAO = busDAO;
    }

    public List<Bus> searchBuses(String source, String destination, LocalDate journeyDate) {
        if (!ValidationUtil.isValidRoute(source, destination)) {
            throw new IllegalArgumentException("Source and destination must be valid and distinct.");
        }
        if (!ValidationUtil.isValidJourneyDate(journeyDate)) {
            throw new IllegalArgumentException("Journey date cannot be in the past.");
        }
        return busDAO.searchBuses(source, destination, journeyDate);
    }

    public List<Bus> getAllActiveBuses() {
        return busDAO.findAll();
    }

    public Bus getBusById(int busId) {
        return busDAO.findById(busId);
    }

    public List<String> getAvailableSources() {
        return busDAO.getAllSources();
    }

    public List<String> getAvailableDestinations() {
        return busDAO.getAllDestinations();
    }
}
