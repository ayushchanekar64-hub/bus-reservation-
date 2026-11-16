package com.busreservation.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utility class providing validation methods for user inputs,
 * passengers, routes, and dates.
 */
public final class ValidationUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s.]{2,60}$");

    private ValidationUtil() {
    }

    public static boolean isValidFullName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidMobile(String mobile) {
        if (mobile == null) return false;
        String clean = mobile.replaceAll("[^0-9]", "");
        return PHONE_PATTERN.matcher(clean).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return true; // Optional field
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidAge(int age) {
        return age >= 1 && age <= 120;
    }

    public static boolean isValidRoute(String source, String destination) {
        if (source == null || destination == null) return false;
        String s = source.trim();
        String d = destination.trim();
        return !s.isEmpty() && !d.isEmpty() && !s.equalsIgnoreCase(d);
    }

    public static boolean isValidJourneyDate(LocalDate date) {
        if (date == null) return false;
        return !date.isBefore(LocalDate.now());
    }
}
