package com.busreservation.util;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility for generating cryptographically randomized, human-readable, unique PNRs.
 * Format: PNR-XXXXXXX (e.g., PNR-7824901)
 */
public final class PNRGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Set<String> ISSUED_PNRS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private PNRGenerator() {
    }

    public static String generatePNR() {
        while (true) {
            int randomNum = 1000000 + RANDOM.nextInt(9000000); // 7 digit guarantee
            String pnr = "PNR-" + randomNum;
            if (ISSUED_PNRS.add(pnr)) {
                return pnr;
            }
        }
    }
}
