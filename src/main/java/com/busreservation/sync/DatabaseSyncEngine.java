package com.busreservation.sync;

import com.busreservation.config.DBConnection;
import com.busreservation.dao.postgres.PostgresBookingDAO;
import com.busreservation.dao.postgres.PostgresUserDAO;
import com.busreservation.model.Booking;
import com.busreservation.model.BookingPassenger;
import com.busreservation.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Intelligent Local-to-PostgreSQL Auto-Sync Engine.
 * 
 * Provides an Offline-First, Write-Behind Persistence Architecture:
 * 1. Transactions are stored temporarily in a fast local database/journal.
 * 2. A background worker periodically checks PostgreSQL availability.
 * 3. As soon as PostgreSQL is available, un-synced data is automatically
 *    migrated and committed to PostgreSQL tables.
 */
public class DatabaseSyncEngine {

    private static final Path JOURNAL_FILE = Path.of("data", "pending_sync.log");
    private static final DatabaseSyncEngine INSTANCE = new DatabaseSyncEngine();

    private final List<User> pendingUsers = new CopyOnWriteArrayList<>();
    private final List<SyncBookingTask> pendingBookings = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "SwiftBus-AutoSync-Worker");
        t.setDaemon(true);
        return t;
    });

    private final PostgresUserDAO postgresUserDAO = new PostgresUserDAO();
    private final PostgresBookingDAO postgresBookingDAO = new PostgresBookingDAO();

    public static DatabaseSyncEngine getInstance() {
        return INSTANCE;
    }

    private DatabaseSyncEngine() {
        loadJournal();
        // Periodically check and auto-sync every 4 seconds
        scheduler.scheduleWithFixedDelay(this::syncPendingToPostgres, 2, 4, TimeUnit.SECONDS);
    }

    public static class SyncBookingTask {
        public final Booking booking;
        public final List<BookingPassenger> passengers;

        public SyncBookingTask(Booking booking, List<BookingPassenger> passengers) {
            this.booking = booking;
            this.passengers = passengers;
        }
    }

    /**
     * Enqueue a new user registration for auto-sync.
     */
    public void queueUser(User user) {
        if (user != null && !pendingUsers.contains(user)) {
            pendingUsers.add(user);
            saveJournal();
            triggerSyncAsync();
        }
    }

    /**
     * Enqueue a new booking transaction for auto-sync.
     */
    public void queueBooking(Booking booking, List<BookingPassenger> passengers) {
        if (booking != null && passengers != null) {
            pendingBookings.add(new SyncBookingTask(booking, passengers));
            saveJournal();
            triggerSyncAsync();
        }
    }

    public void triggerSyncAsync() {
        CompletableFuture.runAsync(this::syncPendingToPostgres);
    }

    /**
     * Core Sync Worker: Pushes local pending records to PostgreSQL.
     */
    public synchronized void syncPendingToPostgres() {
        if (pendingUsers.isEmpty() && pendingBookings.isEmpty()) {
            return;
        }

        if (!DBConnection.isAvailable()) {
            // PostgreSQL is currently offline; data remains safe locally
            return;
        }

        int syncedUsers = 0;
        int syncedBookings = 0;

        // 1. Sync Pending Users
        List<User> usersToSync = new ArrayList<>(pendingUsers);
        for (User user : usersToSync) {
            try {
                if (syncUserToPostgres(user)) {
                    pendingUsers.remove(user);
                    syncedUsers++;
                }
            } catch (Exception e) {
                System.err.println("[Auto-Sync] Error syncing user " + user.getEmail() + ": " + e.getMessage());
            }
        }

        // 2. Sync Pending Bookings
        List<SyncBookingTask> bookingsToSync = new ArrayList<>(pendingBookings);
        for (SyncBookingTask task : bookingsToSync) {
            try {
                if (syncBookingToPostgres(task)) {
                    pendingBookings.remove(task);
                    syncedBookings++;
                }
            } catch (Exception e) {
                System.err.println("[Auto-Sync] Error syncing booking " + task.booking.getPnrNumber() + ": " + e.getMessage());
            }
        }

        if (syncedUsers > 0 || syncedBookings > 0) {
            System.out.println("==================================================================");
            System.out.println(" [Auto-Sync Engine] Local Data Successfully Synced to PostgreSQL!");
            if (syncedUsers > 0) System.out.println("  -> Synced " + syncedUsers + " User(s) to 'users' table");
            if (syncedBookings > 0) System.out.println("  -> Synced " + syncedBookings + " Booking(s) to 'bookings' & 'seats' tables");
            System.out.println("==================================================================");
            saveJournal();
        }
    }

    private boolean syncUserToPostgres(User user) {
        String sql = "INSERT INTO users (full_name, email, mobile_number, password_hash) " +
                     "VALUES (?, ?, ?, ?) ON CONFLICT (email) DO NOTHING";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail().toLowerCase().trim());
            ps.setString(3, user.getMobileNumber().trim());
            ps.setString(4, user.getPassword());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[Auto-Sync] Failed to insert user into PostgreSQL: " + e.getMessage());
            return false;
        }
    }

    private boolean syncBookingToPostgres(SyncBookingTask task) {
        // Check if booking already exists in PostgreSQL
        String checkSql = "SELECT booking_id FROM bookings WHERE LOWER(pnr_number) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, task.booking.getPnrNumber());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Already in PostgreSQL
                    return true;
                }
            }
        } catch (SQLException e) {
            return false;
        }

        // Delegate to atomic PostgreSQL transaction
        return postgresBookingDAO.createBookingTransaction(task.booking, task.passengers);
    }

    private void saveJournal() {
        try {
            if (JOURNAL_FILE.getParent() != null) {
                Files.createDirectories(JOURNAL_FILE.getParent());
            }
            List<String> lines = new ArrayList<>();
            lines.add("# SwiftBus Local Sync Journal");
            lines.add("pending_users=" + pendingUsers.size());
            lines.add("pending_bookings=" + pendingBookings.size());
            for (User u : pendingUsers) {
                lines.add("USER:" + u.getFullName() + "|" + u.getEmail() + "|" + u.getMobileNumber() + "|" + u.getPassword());
            }
            for (SyncBookingTask b : pendingBookings) {
                lines.add("BOOKING:" + b.booking.getPnrNumber() + "|" + b.booking.getTotalFare());
            }
            Files.write(JOURNAL_FILE, lines);
        } catch (IOException ignored) {}
    }

    private void loadJournal() {
        if (!Files.exists(JOURNAL_FILE)) return;
        try {
            List<String> lines = Files.readAllLines(JOURNAL_FILE);
            for (String line : lines) {
                if (line.startsWith("USER:")) {
                    String[] parts = line.substring(5).split("\\|");
                    if (parts.length >= 4) {
                        pendingUsers.add(new User(parts[0], parts[1], parts[2], parts[3]));
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    public int getPendingCount() {
        return pendingUsers.size() + pendingBookings.size();
    }
}
