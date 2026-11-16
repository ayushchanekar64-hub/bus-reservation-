package com.busreservation.dao.postgres;

import com.busreservation.config.DBConnection;
import com.busreservation.dao.BusDAO;
import com.busreservation.model.Bus;
import com.busreservation.model.LuxuryVolvoBus;
import com.busreservation.model.SeaterBus;
import com.busreservation.model.SleeperBus;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL JDBC implementation for BusDAO.
 * Uses PreparedStatements and Polymorphic Bus Factory mapping.
 */
public class PostgresBusDAO implements BusDAO {

    @Override
    public Bus findById(int busId) {
        String sql = "SELECT * FROM buses WHERE bus_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, busId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBus(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bus by id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Bus> findAll() {
        List<Bus> list = new ArrayList<>();
        String sql = "SELECT * FROM buses WHERE is_active = true ORDER BY bus_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToBus(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all buses: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Bus> searchBuses(String source, String destination) {
        List<Bus> list = new ArrayList<>();
        String sql = "SELECT * FROM buses WHERE LOWER(source) = LOWER(?) AND LOWER(destination) = LOWER(?) AND is_active = true ORDER BY departure_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, source != null ? source.trim() : "");
            ps.setString(2, destination != null ? destination.trim() : "");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBus(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching buses: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Bus> searchBuses(String source, String destination, LocalDate journeyDate) {
        // In PostgreSQL, returns active buses matching route; journeyDate can filter schedules or seasonal operations
        return searchBuses(source, destination);
    }

    @Override
    public List<String> getAllSources() {
        List<String> sources = new ArrayList<>();
        String sql = "SELECT DISTINCT source FROM buses WHERE is_active = true ORDER BY source ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sources.add(rs.getString("source"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading bus sources: " + e.getMessage());
        }
        return sources;
    }

    @Override
    public List<String> getAllDestinations() {
        List<String> dests = new ArrayList<>();
        String sql = "SELECT DISTINCT destination FROM buses WHERE is_active = true ORDER BY destination ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dests.add(rs.getString("destination"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading bus destinations: " + e.getMessage());
        }
        return dests;
    }

    @Override
    public boolean insert(Bus bus) {
        String sql = "INSERT INTO buses (bus_number, bus_name, bus_type, source, destination, " +
                "departure_time, arrival_time, journey_duration, fare, total_seats, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setBusParameters(ps, bus);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bus.setBusId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting bus: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Bus bus) {
        String sql = "UPDATE buses SET bus_number=?, bus_name=?, bus_type=?, source=?, destination=?, " +
                "departure_time=?, arrival_time=?, journey_duration=?, fare=?, total_seats=?, is_active=? WHERE bus_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setBusParameters(ps, bus);
            ps.setInt(12, bus.getBusId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bus: " + e.getMessage());
        }
        return false;
    }

    private void setBusParameters(PreparedStatement ps, Bus bus) throws SQLException {
        ps.setString(1, bus.getBusNumber());
        ps.setString(2, bus.getBusName());
        ps.setString(3, bus.getBusType());
        ps.setString(4, bus.getSource());
        ps.setString(5, bus.getDestination());
        ps.setTime(6, Time.valueOf(bus.getDepartureTime()));
        ps.setTime(7, Time.valueOf(bus.getArrivalTime()));
        ps.setString(8, bus.getJourneyDuration());
        ps.setDouble(9, bus.getBaseFare());
        ps.setInt(10, bus.getTotalSeats());
        ps.setBoolean(11, bus.isActive());
    }

    /**
     * Polymorphic Factory method instantiate specialized Bus subclass based on bus_type.
     */
    public static Bus mapResultSetToBus(ResultSet rs) throws SQLException {
        int id = rs.getInt("bus_id");
        String number = rs.getString("bus_number");
        String name = rs.getString("bus_name");
        String type = rs.getString("bus_type");
        String source = rs.getString("source");
        String destination = rs.getString("destination");
        LocalTime dep = rs.getTime("departure_time").toLocalTime();
        LocalTime arr = rs.getTime("arrival_time").toLocalTime();
        String duration = rs.getString("journey_duration");
        double fare = rs.getDouble("fare");
        int totalSeats = rs.getInt("total_seats");
        boolean active = rs.getBoolean("is_active");

        String lowerType = type != null ? type.toLowerCase() : "";
        if (lowerType.contains("sleeper")) {
            return new SleeperBus(id, number, name, type, source, destination, dep, arr, duration, fare, totalSeats, active);
        } else if (lowerType.contains("volvo") || lowerType.contains("multi-axle")) {
            return new LuxuryVolvoBus(id, number, name, type, source, destination, dep, arr, duration, fare, totalSeats, active);
        } else {
            return new SeaterBus(id, number, name, type, source, destination, dep, arr, duration, fare, totalSeats, active);
        }
    }
}
