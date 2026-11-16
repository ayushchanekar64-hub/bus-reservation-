package com.busreservation.util;

import java.security.SecureRandom;

/**
 * Utility for generating cryptographically randomized, human-readable PNRs.
 * Format: PNR-XXXXXXX (e.g., PNR-7824901)
 */
public final class PNRGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String DIGITS = "0123456789";

    private PNRGenerator() {
    }

    public static String generatePNR() {
        StringBuilder sb = new StringBuilder("PNR-");
        // 7 numeric digits
        for (int i = 0; i < 7; i++) {
            sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }
        return sb.toString();
    }
}
