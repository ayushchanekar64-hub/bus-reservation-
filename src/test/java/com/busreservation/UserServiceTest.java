package com.busreservation;

import com.busreservation.dao.DAOFactory;
import com.busreservation.dao.UserDAO;
import com.busreservation.model.User;
import com.busreservation.service.AuthService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private static UserDAO userDAO;
    private static AuthService authService;

    @BeforeAll
    public static void setUp() {
        DAOFactory.initialize();
        userDAO = DAOFactory.getUserDAO();
        authService = new AuthService(userDAO);
    }

    @Test
    @DisplayName("Verify existing user authentication from Database")
    public void testAuthenticateExistingUser() {
        User user = authService.login("aarav@example.com", "password123");
        assertNotNull(user, "User should be found and authenticated in DB");
        assertEquals("Aarav Sharma", user.getFullName());
    }

    @Test
    @DisplayName("Verify user login with mobile number from Database")
    public void testAuthenticateWithMobile() {
        User user = authService.login("9876543210", "password123");
        assertNotNull(user, "User should authenticate with mobile");
        assertEquals("aarav@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Verify failed login with incorrect password")
    public void testFailedLoginWrongPassword() {
        User user = authService.login("aarav@example.com", "wrongpass");
        assertNull(user, "User should not authenticate with wrong password");
    }

    @Test
    @DisplayName("Verify user registration persists to Database")
    public void testRegisterNewUser() {
        String testEmail = "testuser" + System.currentTimeMillis() + "@example.com";
        String testMobile = "9" + (long) (Math.random() * 900000000L + 100000000L);
        User registered = authService.register("New Test User", testEmail, testMobile, "secret123");

        assertNotNull(registered);
        assertTrue(registered.getUserId() > 0);

        // Verify login with newly registered user
        User loginUser = authService.login(testEmail, "secret123");
        assertNotNull(loginUser);
        assertEquals("New Test User", loginUser.getFullName());
    }
}
