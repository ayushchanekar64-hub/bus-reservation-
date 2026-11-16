package com.busreservation;

import com.busreservation.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validation Utility Unit Tests")
class ValidationUtilTest {

    @Test
    @DisplayName("Valid Full Name should pass validation")
    void testValidFullName() {
        assertTrue(ValidationUtil.isValidFullName("John Doe"));
        assertTrue(ValidationUtil.isValidFullName("Aarav Sharma"));
        assertTrue(ValidationUtil.isValidFullName("Dr. A. P. J. Kalam"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "123", "A", "Name@123", "TooManySpecialChars#$%^"})
    @DisplayName("Invalid Full Name should fail validation")
    void testInvalidFullName(String name) {
        assertFalse(ValidationUtil.isValidFullName(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {"9876543210", "8123456789", "7000000000", "6999999999"})
    @DisplayName("Valid Indian 10-digit Mobile Number should pass")
    void testValidMobile(String mobile) {
        assertTrue(ValidationUtil.isValidMobile(mobile));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567890", "98765", "9876543210123", "abcdefghij", ""})
    @DisplayName("Invalid Mobile Numbers should fail")
    void testInvalidMobile(String mobile) {
        assertFalse(ValidationUtil.isValidMobile(mobile));
    }

    @Test
    @DisplayName("Email validation for standard and optional inputs")
    void testEmailValidation() {
        assertTrue(ValidationUtil.isValidEmail("user@example.com"));
        assertTrue(ValidationUtil.isValidEmail("")); // Optional
        assertTrue(ValidationUtil.isValidEmail(null)); // Optional
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail("user@.com"));
    }

    @Test
    @DisplayName("Age boundary validation (1 to 120)")
    void testAgeValidation() {
        assertTrue(ValidationUtil.isValidAge(1));
        assertTrue(ValidationUtil.isValidAge(25));
        assertTrue(ValidationUtil.isValidAge(120));
        assertFalse(ValidationUtil.isValidAge(0));
        assertFalse(ValidationUtil.isValidAge(-5));
        assertFalse(ValidationUtil.isValidAge(121));
    }

    @Test
    @DisplayName("Route validation: source and destination must differ")
    void testRouteValidation() {
        assertTrue(ValidationUtil.isValidRoute("Delhi", "Jaipur"));
        assertFalse(ValidationUtil.isValidRoute("Delhi", "delhi"));
        assertFalse(ValidationUtil.isValidRoute("", "Jaipur"));
        assertFalse(ValidationUtil.isValidRoute(null, "Jaipur"));
    }

    @Test
    @DisplayName("Journey Date must be today or in the future")
    void testJourneyDateValidation() {
        assertTrue(ValidationUtil.isValidJourneyDate(LocalDate.now()));
        assertTrue(ValidationUtil.isValidJourneyDate(LocalDate.now().plusDays(10)));
        assertFalse(ValidationUtil.isValidJourneyDate(LocalDate.now().minusDays(1)));
        assertFalse(ValidationUtil.isValidJourneyDate(null));
    }
}
