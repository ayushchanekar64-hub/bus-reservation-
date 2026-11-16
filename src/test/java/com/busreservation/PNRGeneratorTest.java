package com.busreservation;

import com.busreservation.util.PNRGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PNR Generator Unit Tests")
class PNRGeneratorTest {

    @Test
    @DisplayName("Generated PNR must match PNR-XXXXXXX pattern")
    void testPNRFormat() {
        String pnr = PNRGenerator.generatePNR();
        assertNotNull(pnr);
        assertTrue(pnr.startsWith("PNR-"));
        assertEquals(11, pnr.length(), "PNR format must be 4 prefix chars + 7 digits");
        assertTrue(pnr.substring(4).matches("\\d{7}"), "Remaining characters must be digits");
    }

    @Test
    @DisplayName("Generated PNRs must be unique across bulk runs")
    void testPNRUniqueness() {
        Set<String> generated = new HashSet<>();
        int count = 1000;
        for (int i = 0; i < count; i++) {
            generated.add(PNRGenerator.generatePNR());
        }
        assertEquals(count, generated.size(), "All 1000 generated PNRs should be distinct");
    }
}
