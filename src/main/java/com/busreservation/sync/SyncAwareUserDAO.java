package com.busreservation.sync;

import com.busreservation.dao.UserDAO;
import com.busreservation.dao.inmemory.InMemoryUserDAO;
import com.busreservation.dao.postgres.PostgresUserDAO;
import com.busreservation.config.DBConnection;
import com.busreservation.model.User;

import java.util.List;

/**
 * Sync-Aware UserDAO implementation.
 * Guarantees zero downtime by writing immediately to local project storage
 * and automatically syncing changes to PostgreSQL in the background.
 */
public class SyncAwareUserDAO implements UserDAO {

    private final InMemoryUserDAO localDAO;
    private final PostgresUserDAO postgresDAO = new PostgresUserDAO();
    private final DatabaseSyncEngine syncEngine = DatabaseSyncEngine.getInstance();

    public SyncAwareUserDAO(InMemoryUserDAO localDAO) {
        this.localDAO = localDAO;
    }

    @Override
    public User findById(int userId) {
        User u = localDAO.findById(userId);
        if (u == null && DBConnection.isAvailable()) {
            u = postgresDAO.findById(userId);
            if (u != null) localDAO.addUserDirectly(u);
        }
        return u;
    }

    @Override
    public User findByEmail(String email) {
        User u = localDAO.findByEmail(email);
        if (u == null && DBConnection.isAvailable()) {
            u = postgresDAO.findByEmail(email);
            if (u != null) localDAO.addUserDirectly(u);
        }
        return u;
    }

    @Override
    public User findByMobile(String mobile) {
        User u = localDAO.findByMobile(mobile);
        if (u == null && DBConnection.isAvailable()) {
            u = postgresDAO.findByMobile(mobile);
            if (u != null) localDAO.addUserDirectly(u);
        }
        return u;
    }

    @Override
    public boolean register(User user) {
        // 1. Save immediately in Local Project Database
        boolean localSuccess = localDAO.register(user);
        if (localSuccess) {
            // 2. Queue for automatic background sync to PostgreSQL
            syncEngine.queueUser(user);
        }
        return localSuccess;
    }

    @Override
    public User authenticate(String emailOrMobile, String password) {
        // Check local store first
        User user = localDAO.authenticate(emailOrMobile, password);
        if (user != null) return user;

        // If not in local cache, query PostgreSQL and cache locally
        if (DBConnection.isAvailable()) {
            user = postgresDAO.authenticate(emailOrMobile, password);
            if (user != null) {
                localDAO.addUserDirectly(user);
            }
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return localDAO.findAll();
    }
}
