package com.busreservation.dao;

import com.busreservation.model.User;
import java.util.List;

/**
 * Data Access Object Interface for User authentication and profiles.
 */
public interface UserDAO {
    User findById(int userId);
    User findByEmail(String email);
    User findByMobile(String mobile);
    boolean register(User user);
    User authenticate(String emailOrMobile, String password);
    List<User> findAll();
}
