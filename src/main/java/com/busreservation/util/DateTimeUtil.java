package com.busreservation.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility for formatting dates and times across views and tickets.
 */
public final class DateTimeUtil {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private DateTimeUtil() {
    }

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FMT) : "N/A";
    }

    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FMT) : "N/A";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FMT) : "N/A";
    }
}
