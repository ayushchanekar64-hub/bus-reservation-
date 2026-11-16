package com.busreservation.dao.inmemory;

import com.busreservation.dao.UserDAO;
import com.busreservation.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe In-Memory implementation of UserDAO.
 */
public class InMemoryUserDAO implements UserDAO {
    private final Map<Integer, User> userStorage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public void addUserDirectly(User user) {
        if (user != null) {
            userStorage.put(user.getUserId(), user);
            if (user.getUserId() >= idGenerator.get()) {
                idGenerator.set(user.getUserId() + 1);
            }
        }
    }

    @Override
    public User findById(int userId) {
        return userStorage.get(userId);
    }

    @Override
    public User findByEmail(String email) {
        if (email == null) return null;
        return userStorage.values().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findByMobile(String mobile) {
        if (mobile == null) return null;
        return userStorage.values().stream()
                .filter(u -> u.getMobileNumber() != null && u.getMobileNumber().equals(mobile.trim()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean register(User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) return false;
        if (findByEmail(user.getEmail()) != null) return false; // Duplicate email
        if (user.getUserId() <= 0) {
            user.setUserId(idGenerator.getAndIncrement());
        }
        userStorage.put(user.getUserId(), user);
        return true;
    }

    @Override
    public User authenticate(String emailOrMobile, String password) {
        if (emailOrMobile == null || password == null) return null;
        String query = emailOrMobile.trim();
        return userStorage.values().stream()
                .filter(u -> (u.getEmail().equalsIgnoreCase(query) || u.getMobileNumber().equals(query)) &&
                             u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userStorage.values());
    }
}
