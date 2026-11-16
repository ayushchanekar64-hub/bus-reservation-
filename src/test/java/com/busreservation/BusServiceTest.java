package com.busreservation;

import com.busreservation.dao.inmemory.InMemoryBusDAO;
import com.busreservation.model.Bus;
import com.busreservation.model.LuxuryVolvoBus;
import com.busreservation.model.SeaterBus;
import com.busreservation.service.BusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bus Search & Service Unit Tests")
class BusServiceTest {

    private BusService busService;
    private InMemoryBusDAO busDAO;

    @BeforeEach
    void setUp() {
        busDAO = new InMemoryBusDAO();
        busService = new BusService(busDAO);

        Bus b1 = new LuxuryVolvoBus(1, "DL-01-A", "Zingbus Royal", "Volvo Multi-Axle 2+2",
                "Delhi", "Jaipur", LocalTime.of(6, 0), LocalTime.of(11, 30), "5h 30m", 650.0, 40, true);
        Bus b2 = new SeaterBus(2, "RJ-14-B", "SmartBus", "AC Seater 2+2",
                "Delhi", "Jaipur", LocalTime.of(14, 0), LocalTime.of(19, 30), "5h 30m", 550.0, 40, true);
        Bus b3 = new SeaterBus(3, "MH-01-C", "Shivneri", "Volvo 2+2",
                "Mumbai", "Pune", LocalTime.of(7, 0), LocalTime.of(10, 30), "3h 30m", 450.0, 40, true);

        busDAO.addBusDirectly(b1);
        busDAO.addBusDirectly(b2);
        busDAO.addBusDirectly(b3);
    }

    @Test
    @DisplayName("Valid route search returns matching active buses sorted by departure time")
    void testValidRouteSearch() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Bus> results = busService.searchBuses("Delhi", "Jaipur", tomorrow);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("DL-01-A", results.get(0).getBusNumber());
        assertEquals("RJ-14-B", results.get(1).getBusNumber());
    }

    @Test
    @DisplayName("Route search is case-insensitive")
    void testCaseInsensitiveSearch() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Bus> results = busService.searchBuses("delhi", "JAIPUR", tomorrow);
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Search with invalid route throws IllegalArgumentException")
    void testInvalidRouteThrows() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class, () ->
                busService.searchBuses("Delhi", "Delhi", tomorrow));
        assertThrows(IllegalArgumentException.class, () ->
                busService.searchBuses("", "Jaipur", tomorrow));
        assertThrows(IllegalArgumentException.class, () ->
                busService.searchBuses(null, "Jaipur", tomorrow));
    }

    @Test
    @DisplayName("Search with past journey date throws IllegalArgumentException")
    void testPastDateThrows() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertThrows(IllegalArgumentException.class, () ->
                busService.searchBuses("Delhi", "Jaipur", yesterday));
    }

    @Test
    @DisplayName("All sources and destinations are retrieved correctly")
    void testSourcesAndDestinations() {
        List<String> sources = busService.getAvailableSources();
        List<String> dests = busService.getAvailableDestinations();

        assertTrue(sources.contains("Delhi"));
        assertTrue(sources.contains("Mumbai"));
        assertTrue(dests.contains("Jaipur"));
        assertTrue(dests.contains("Pune"));
    }
}
