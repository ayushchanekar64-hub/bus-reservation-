package com.busreservation.dao.postgres;

import com.busreservation.config.DBConnection;
import com.busreservation.dao.SeatDAO;
import com.busreservation.model.Seat;
import com.busreservation.model.SeatStatus;
import com.busreservation.model.SeatType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL JDBC implementation for SeatDAO.
 */
public class PostgresSeatDAO implements SeatDAO {

    @Override
    public List<Seat> findByBusId(int busId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE bus_id = ? ORDER BY deck ASC, row_num ASC, col_num ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, busId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapResultSetToSeat(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching seats for bus " + busId + ": " + e.getMessage());
        }
        return seats;
    }

    @Override
    public Seat findByBusIdAndSeatNumber(int busId, String seatNumber) {
        String sql = "SELECT * FROM seats WHERE bus_id = ? AND seat_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, busId);
            ps.setString(2, seatNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSeat(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching seat " + seatNumber + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateSeatStatus(int seatId, SeatStatus status) {
        String sql = "UPDATE seats SET seat_status = ? WHERE seat_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, seatId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating seat status: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateSeatStatuses(List<Integer> seatIds, SeatStatus status, Connection conn) {
        if (seatIds == null || seatIds.isEmpty()) return true;
        String sql = "UPDATE seats SET seat_status = ? WHERE seat_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int id : seatIds) {
                ps.setString(1, status.name());
                ps.setInt(2, id);
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            for (int res : results) {
                if (res <= 0 && res != PreparedStatement.SUCCESS_NO_INFO) return false;
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error batch updating seat statuses: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int countAvailableSeats(int busId) {
        String sql = "SELECT COUNT(*) FROM seats WHERE bus_id = ? AND seat_status = 'AVAILABLE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, busId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting available seats: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public void bulkInsertSeats(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) return;
        String sql = "INSERT INTO seats (bus_id, seat_number, seat_type, seat_status, deck, row_num, col_num, fare_modifier) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (bus_id, seat_number) DO NOTHING";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Seat s : seats) {
                ps.setInt(1, s.getBusId());
                ps.setString(2, s.getSeatNumber());
                ps.setString(3, s.getSeatType().name());
                ps.setString(4, s.getSeatStatus().name());
                ps.setString(5, s.getDeck());
                ps.setInt(6, s.getRowNum());
                ps.setInt(7, s.getColNum());
                ps.setDouble(8, s.getFareModifier());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error bulk inserting seats: " + e.getMessage());
        }
    }

    public static Seat mapResultSetToSeat(ResultSet rs) throws SQLException {
        int id = rs.getInt("seat_id");
        int busId = rs.getInt("bus_id");
        String number = rs.getString("seat_number");
        SeatType type = SeatType.valueOf(rs.getString("seat_type"));
        SeatStatus status = SeatStatus.valueOf(rs.getString("seat_status"));
        String deck = rs.getString("deck");
        int row = rs.getInt("row_num");
        int col = rs.getInt("col_num");
        double modifier = rs.getDouble("fare_modifier");

        return new Seat(id, busId, number, type, status, deck, row, col, modifier);
    }
}
