package com.busreservation.service;

import com.busreservation.dao.DAOFactory;
import com.busreservation.dao.UserDAO;
import com.busreservation.model.User;
import com.busreservation.util.ValidationUtil;

/**
 * Service managing user authentication, registration, and active session state.
 */
public class AuthService {
    private final UserDAO userDAO;
    private static User currentUser = null; // Active logged-in session user

    public AuthService() {
        this(DAOFactory.getUserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String emailOrMobile, String password) {
        if (emailOrMobile == null || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email/Mobile and Password are required.");
        }
        User user = userDAO.authenticate(emailOrMobile.trim(), password);
        if (user != null) {
            currentUser = user;
            return user;
        }
        return null;
    }

    public User register(String fullName, String email, String mobileNumber, String password) {
        if (!ValidationUtil.isValidFullName(fullName)) {
            throw new IllegalArgumentException("Invalid full name. Use letters and spaces only.");
        }
        if (!ValidationUtil.isValidEmail(email) || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Please provide a valid email address.");
        }
        if (!ValidationUtil.isValidMobile(mobileNumber)) {
            throw new IllegalArgumentException("Please provide a valid 10-digit mobile number.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }

        if (userDAO.findByEmail(email) != null) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User newUser = new User(fullName.trim(), email.trim().toLowerCase(), mobileNumber.trim(), password);
        boolean success = userDAO.register(newUser);
        if (success) {
            currentUser = newUser;
            return newUser;
        } else {
            throw new IllegalStateException("Registration failed. Please try again.");
        }
    }

    public void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
