package com.busreservation.dao.postgres;

import com.busreservation.config.DBConnection;
import com.busreservation.dao.PassengerDAO;
import com.busreservation.model.Passenger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL JDBC implementation for PassengerDAO.
 */
public class PostgresPassengerDAO implements PassengerDAO {

    @Override
    public Passenger findById(int passengerId) {
        String sql = "SELECT * FROM passengers WHERE passenger_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPassenger(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding passenger by id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Passenger findByMobile(String mobileNumber) {
        String sql = "SELECT * FROM passengers WHERE mobile_number = ? ORDER BY passenger_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mobileNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPassenger(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding passenger by mobile: " + e.getMessage());
        }
        return null;
    }

    @Override
    public int insert(Passenger passenger) {
        try (Connection conn = DBConnection.getConnection()) {
            return insert(passenger, conn);
        } catch (SQLException e) {
            System.err.println("Error inserting passenger: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public int insert(Passenger passenger, Connection conn) {
        String sql = "INSERT INTO passengers (full_name, age, gender, mobile_number, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, passenger.getFullName());
            ps.setInt(2, passenger.getAge());
            ps.setString(3, passenger.getGender());
            ps.setString(4, passenger.getMobileNumber());
            ps.setString(5, passenger.getEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    passenger.setPassengerId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting passenger in transaction: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public List<Passenger> findAll() {
        List<Passenger> list = new ArrayList<>();
        String sql = "SELECT * FROM passengers ORDER BY passenger_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPassenger(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all passengers: " + e.getMessage());
        }
        return list;
    }

    public static Passenger mapResultSetToPassenger(ResultSet rs) throws SQLException {
        return new Passenger(
                rs.getInt("passenger_id"),
                rs.getString("full_name"),
                rs.getInt("age"),
                rs.getString("gender"),
                rs.getString("mobile_number"),
                rs.getString("email")
        );
    }
}
