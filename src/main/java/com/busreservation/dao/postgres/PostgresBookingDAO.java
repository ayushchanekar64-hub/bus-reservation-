package com.busreservation.dao.postgres;

import com.busreservation.config.DBConnection;
import com.busreservation.dao.BookingDAO;
import com.busreservation.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL JDBC implementation for BookingDAO.
 * Implements transaction management with pessimistic row locking (SELECT FOR UPDATE)
 * to guarantee ACID compliance and completely prevent double bookings.
 */
public class PostgresBookingDAO implements BookingDAO {

    @Override
    public Booking findById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking b = mapResultSetToBooking(rs, conn);
                    loadBookingPassengers(b, conn);
                    return b;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding booking by id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Booking findByPNR(String pnr) {
        String sql = "SELECT * FROM bookings WHERE LOWER(pnr_number) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr != null ? pnr.trim() : "");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking b = mapResultSetToBooking(rs, conn);
                    loadBookingPassengers(b, conn);
                    return b;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding booking by PNR: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY booking_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Booking b = mapResultSetToBooking(rs, conn);
                loadBookingPassengers(b, conn);
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all bookings: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Booking> findByUserId(int userId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapResultSetToBooking(rs, conn);
                    loadBookingPassengers(b, conn);
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bookings for user: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Booking> findRecent(int limit) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY booking_date DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 5);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapResultSetToBooking(rs, conn);
                    loadBookingPassengers(b, conn);
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recent bookings: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean createBookingTransaction(Booking booking, List<BookingPassenger> bookingPassengers) {
        if (booking == null || bookingPassengers == null || bookingPassengers.isEmpty()) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin ACID Transaction

            // STEP 1: Pessimistic Row Locking & Re-validation of Seats
            String lockSql = "SELECT seat_id, seat_number, seat_status FROM seats WHERE seat_id = ? FOR UPDATE";
            for (BookingPassenger bp : bookingPassengers) {
                int seatId = bp.getSeat().getSeatId();
                try (PreparedStatement psLock = conn.prepareStatement(lockSql)) {
                    psLock.setInt(1, seatId);
                    try (ResultSet rs = psLock.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            System.err.println("Seat ID " + seatId + " not found!");
                            return false;
                        }
                        String currentStatus = rs.getString("seat_status");
                        if (!"AVAILABLE".equalsIgnoreCase(currentStatus)) {
                            // Seat was taken concurrently by another transaction
                            conn.rollback();
                            System.err.println("Seat " + rs.getString("seat_number") + " is already booked! Transaction rolled back.");
                            return false;
                        }
                    }
                }
            }

            // STEP 2: Insert into bookings table
            String bookingSql = "INSERT INTO bookings (user_id, pnr_number, bus_id, journey_date, booking_date, " +
                    "total_fare, booking_status, contact_phone, contact_email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int generatedBookingId;
            try (PreparedStatement psBooking = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS)) {
                if (booking.getUserId() > 0) {
                    psBooking.setInt(1, booking.getUserId());
                } else {
                    psBooking.setNull(1, Types.INTEGER);
                }
                psBooking.setString(2, booking.getPnrNumber());
                psBooking.setInt(3, booking.getBus().getBusId());
                psBooking.setDate(4, Date.valueOf(booking.getJourneyDate()));
                psBooking.setTimestamp(5, Timestamp.valueOf(booking.getBookingDate()));
                psBooking.setDouble(6, booking.getTotalFare());
                psBooking.setString(7, booking.getBookingStatus().name());
                psBooking.setString(8, booking.getContactPhone());
                psBooking.setString(9, booking.getContactEmail());
                psBooking.executeUpdate();

                try (ResultSet rsKey = psBooking.getGeneratedKeys()) {
                    if (rsKey.next()) {
                        generatedBookingId = rsKey.getInt(1);
                        booking.setBookingId(generatedBookingId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // STEP 3: Insert Passenger records & Link via booking_passengers
            String passSql = "INSERT INTO passengers (full_name, age, gender, mobile_number, email) VALUES (?, ?, ?, ?, ?)";
            String bpSql = "INSERT INTO booking_passengers (booking_id, passenger_id, seat_id, seat_fare) VALUES (?, ?, ?, ?)";
            String seatUpdateSql = "UPDATE seats SET seat_status = 'BOOKED' WHERE seat_id = ?";

            for (BookingPassenger bp : bookingPassengers) {
                Passenger p = bp.getPassenger();
                int passId;

                // Insert passenger
                try (PreparedStatement psPass = conn.prepareStatement(passSql, Statement.RETURN_GENERATED_KEYS)) {
                    psPass.setString(1, p.getFullName());
                    psPass.setInt(2, p.getAge());
                    psPass.setString(3, p.getGender());
                    psPass.setString(4, p.getMobileNumber());
                    psPass.setString(5, p.getEmail());
                    psPass.executeUpdate();
                    try (ResultSet rsPassKey = psPass.getGeneratedKeys()) {
                        if (rsPassKey.next()) {
                            passId = rsPassKey.getInt(1);
                            p.setPassengerId(passId);
                        } else {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                // Insert booking_passenger junction
                try (PreparedStatement psBp = conn.prepareStatement(bpSql)) {
                    psBp.setInt(1, generatedBookingId);
                    psBp.setInt(2, passId);
                    psBp.setInt(3, bp.getSeat().getSeatId());
                    psBp.setDouble(4, bp.getSeatFare());
                    psBp.executeUpdate();
                }

                // STEP 4: Update seat status to BOOKED
                try (PreparedStatement psSeat = conn.prepareStatement(seatUpdateSql)) {
                    psSeat.setInt(1, bp.getSeat().getSeatId());
                    psSeat.executeUpdate();
                }
            }

            // STEP 5: Commit Transaction atomically
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Transaction failed, rolling back: " + e.getMessage());
            DBConnection.rollbackQuietly(conn);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    @Override
    public boolean cancelBooking(String pnr) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Get booking details and seat IDs
            String getBookingSql = "SELECT booking_id, booking_status FROM bookings WHERE LOWER(pnr_number) = LOWER(?) FOR UPDATE";
            int bookingId;
            try (PreparedStatement ps = conn.prepareStatement(getBookingSql)) {
                ps.setString(1, pnr.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    if ("CANCELLED".equalsIgnoreCase(rs.getString("booking_status"))) {
                        conn.rollback();
                        return false; // Already cancelled
                    }
                    bookingId = rs.getInt("booking_id");
                }
            }

            // 2. Mark booking as CANCELLED
            String cancelSql = "UPDATE bookings SET booking_status = 'CANCELLED' WHERE booking_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(cancelSql)) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            // 3. Mark all booked seats back to AVAILABLE
            String releaseSeatsSql = "UPDATE seats SET seat_status = 'AVAILABLE' WHERE seat_id IN " +
                    "(SELECT seat_id FROM booking_passengers WHERE booking_id = ?)";
            try (PreparedStatement ps = conn.prepareStatement(releaseSeatsSql)) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
            DBConnection.rollbackQuietly(conn);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private Booking mapResultSetToBooking(ResultSet rs, Connection conn) throws SQLException {
        int id = rs.getInt("booking_id");
        String pnr = rs.getString("pnr_number");
        int busId = rs.getInt("bus_id");
        LocalDate jDate = rs.getDate("journey_date").toLocalDate();
        LocalDateTime bDate = rs.getTimestamp("booking_date").toLocalDateTime();
        double fare = rs.getDouble("total_fare");
        BookingStatus status = BookingStatus.valueOf(rs.getString("booking_status"));
        String phone = rs.getString("contact_phone");
        String email = rs.getString("contact_email");

        // Load bus
        Bus bus = null;
        String busSql = "SELECT * FROM buses WHERE bus_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(busSql)) {
            ps.setInt(1, busId);
            try (ResultSet rsBus = ps.executeQuery()) {
                if (rsBus.next()) {
                    bus = PostgresBusDAO.mapResultSetToBus(rsBus);
                }
            }
        }

        Booking booking = new Booking(id, pnr, bus, jDate, bDate, fare, status, phone, email);
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            booking.setUserId(userId);
        }
        return booking;
    }

    private void loadBookingPassengers(Booking booking, Connection conn) throws SQLException {
        String sql = "SELECT bp.*, p.*, s.* FROM booking_passengers bp " +
                "JOIN passengers p ON bp.passenger_id = p.passenger_id " +
                "JOIN seats s ON bp.seat_id = s.seat_id " +
                "WHERE bp.booking_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, booking.getBookingId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Passenger p = PostgresPassengerDAO.mapResultSetToPassenger(rs);
                    Seat s = PostgresSeatDAO.mapResultSetToSeat(rs);
                    double seatFare = rs.getDouble("seat_fare");
                    booking.addBookingPassenger(new BookingPassenger(
                            rs.getInt("booking_passenger_id"),
                            booking.getBookingId(),
                            p, s, seatFare
                    ));
                }
            }
        }
    }
}
