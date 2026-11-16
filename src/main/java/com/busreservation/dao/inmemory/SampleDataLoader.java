package com.busreservation.dao.inmemory;

import com.busreservation.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads rich, realistic domain data into in-memory storage.
 * Synchronized with database/sample_data.sql.
 */
public class SampleDataLoader {

    public static void load(InMemoryBusDAO busDAO, InMemorySeatDAO seatDAO,
                            InMemoryPassengerDAO passengerDAO, InMemoryBookingDAO bookingDAO) {
        load(busDAO, seatDAO, passengerDAO, bookingDAO, new InMemoryUserDAO());
    }

    public static void load(InMemoryBusDAO busDAO, InMemorySeatDAO seatDAO,
                            InMemoryPassengerDAO passengerDAO, InMemoryBookingDAO bookingDAO,
                            InMemoryUserDAO userDAO) {

        // 0. Create Default Sample Users
        User u1 = new User(1, "Aarav Sharma", "aarav@example.com", "9876543210", "password123");
        User u2 = new User(2, "Rajesh Patel", "rajesh@example.com", "9123456789", "password123");
        User u3 = new User(3, "Rahul Verma", "rahul@example.com", "9988776655", "password123");
        userDAO.addUserDirectly(u1);
        userDAO.addUserDirectly(u2);
        userDAO.addUserDirectly(u3);

        // 1. Create Buses
        Bus bus1 = new LuxuryVolvoBus(1, "DL-01-AB-1234", "Zingbus Royal Class", "Volvo Multi-Axle 2+2",
                "Delhi", "Jaipur", LocalTime.of(6, 0), LocalTime.of(11, 30), "05h 30m", 650.00, 40, true);

        Bus bus2 = new SeaterBus(2, "RJ-14-PA-5678", "IntrCity SmartBus", "AC Seater 2+2",
                "Delhi", "Jaipur", LocalTime.of(14, 30), LocalTime.of(20, 15), "05h 45m", 550.00, 40, true);

        Bus bus3 = new SeaterBus(3, "DL-01-EE-9012", "NueGo Green Electric", "Electric AC Seater 2+2",
                "Delhi", "Jaipur", LocalTime.of(18, 0), LocalTime.of(23, 30), "05h 30m", 580.00, 40, true);

        Bus bus4 = new LuxuryVolvoBus(4, "MH-02-CD-4321", "Shivneri Executive", "Volvo Multi-Axle 2+2",
                "Mumbai", "Pune", LocalTime.of(7, 0), LocalTime.of(10, 30), "03h 30m", 450.00, 40, true);

        Bus bus5 = new SleeperBus(5, "MH-12-EF-8765", "Purple Travels Gold", "AC Sleeper 2+1",
                "Mumbai", "Pune", LocalTime.of(16, 0), LocalTime.of(19, 45), "03h 45m", 520.00, 30, true);

        Bus bus6 = new LuxuryVolvoBus(6, "KA-01-JK-3456", "KSRTC Airavat Club Class", "Volvo Multi-Axle 2+2",
                "Bangalore", "Goa", LocalTime.of(20, 30), LocalTime.of(8, 30), "12h 00m", 1250.00, 40, true);

        Bus bus7 = new SleeperBus(7, "GA-03-MN-7890", "VRL Travels Grand Sleeper", "AC Sleeper 2+1",
                "Bangalore", "Goa", LocalTime.of(21, 15), LocalTime.of(9, 0), "11h 45m", 1450.00, 30, true);

        Bus bus8 = new LuxuryVolvoBus(8, "TS-09-UB-1122", "Orange Tours & Travels", "Volvo Multi-Axle 2+2",
                "Hyderabad", "Bangalore", LocalTime.of(21, 0), LocalTime.of(6, 30), "09h 30m", 950.00, 40, true);

        Bus bus9 = new SleeperBus(9, "KA-05-ZZ-3344", "SRS Travels Premium", "AC Sleeper 2+1",
                "Hyderabad", "Bangalore", LocalTime.of(22, 0), LocalTime.of(7, 15), "09h 15m", 1100.00, 30, true);

        Bus bus10 = new LuxuryVolvoBus(10, "TN-01-AL-9988", "Parveen Platinum", "Volvo Multi-Axle 2+2",
                "Chennai", "Coimbatore", LocalTime.of(22, 30), LocalTime.of(6, 0), "07h 30m", 850.00, 40, true);

        // NAGPUR ROUTES (Nagpur to various locations & return routes)
        Bus bus11 = new LuxuryVolvoBus(11, "MH-31-CA-1001", "Purple Grand Multi-Axle", "Volvo Multi-Axle 2+2",
                "Nagpur", "Pune", LocalTime.of(19, 0), LocalTime.of(7, 30), "12h 30m", 950.00, 40, true);

        Bus bus12 = new SleeperBus(12, "MH-31-TR-2002", "Royal Chintamani Sleeper", "AC Sleeper 2+1",
                "Nagpur", "Pune", LocalTime.of(20, 30), LocalTime.of(8, 45), "12h 15m", 1150.00, 30, true);

        Bus bus13 = new LuxuryVolvoBus(13, "MH-31-SM-3003", "Samruddhi Express Superfast", "Volvo Multi-Axle 2+2",
                "Nagpur", "Mumbai", LocalTime.of(18, 0), LocalTime.of(5, 0), "11h 00m", 1250.00, 40, true);

        Bus bus14 = new LuxuryVolvoBus(14, "MH-31-OR-4004", "Orange Tours & Travels", "Volvo Multi-Axle 2+2",
                "Nagpur", "Hyderabad", LocalTime.of(21, 0), LocalTime.of(6, 0), "09h 00m", 850.00, 40, true);

        Bus bus15 = new SeaterBus(15, "MH-31-PY-5005", "Payal Travels Intercity", "AC Seater 2+2",
                "Nagpur", "Raipur", LocalTime.of(6, 30), LocalTime.of(12, 0), "05h 30m", 450.00, 40, true);

        Bus bus16 = new SleeperBus(16, "MP-04-VR-6006", "Verma Travels Express", "AC Sleeper 2+1",
                "Nagpur", "Bhopal", LocalTime.of(22, 0), LocalTime.of(5, 30), "07h 30m", 750.00, 30, true);

        Bus bus17 = new SleeperBus(17, "MP-09-HN-7007", "Hans Travels Platinum", "AC Sleeper 2+1",
                "Nagpur", "Indore", LocalTime.of(20, 0), LocalTime.of(6, 30), "10h 30m", 900.00, 30, true);

        Bus bus18 = new SeaterBus(18, "MH-12-NG-8008", "Shivshahi Executive", "AC Seater 2+2",
                "Pune", "Nagpur", LocalTime.of(18, 30), LocalTime.of(7, 0), "12h 30m", 920.00, 40, true);

        Bus bus19 = new LuxuryVolvoBus(19, "MH-01-NG-9009", "Neeta Volvo Multi-Axle", "Volvo Multi-Axle 2+2",
                "Mumbai", "Nagpur", LocalTime.of(17, 30), LocalTime.of(4, 30), "11h 00m", 1250.00, 40, true);

        Bus bus20 = new SleeperBus(20, "TS-09-NG-1111", "Kaveri Travels Express", "AC Sleeper 2+1",
                "Hyderabad", "Nagpur", LocalTime.of(20, 30), LocalTime.of(5, 30), "09h 00m", 880.00, 30, true);

        Bus bus21 = new SeaterBus(21, "CG-04-NG-2222", "Mahendra Travels", "AC Seater 2+2",
                "Raipur", "Nagpur", LocalTime.of(14, 0), LocalTime.of(19, 30), "05h 30m", 450.00, 40, true);

        Bus bus22 = new LuxuryVolvoBus(22, "MH-01-MJ-9911", "Rajdhani Super Luxury Multi-Axle", "Volvo Multi-Axle 2+2",
                "Mumbai", "Jaipur", LocalTime.of(17, 0), LocalTime.of(9, 30), "16h 30m", 1550.00, 40, true);

        Bus bus23 = new SleeperBus(23, "RJ-14-MJ-9922", "Shreenath Royal AC Sleeper", "AC Sleeper 2+1",
                "Jaipur", "Mumbai", LocalTime.of(16, 30), LocalTime.of(9, 0), "16h 30m", 1450.00, 30, true);

        busDAO.addBusDirectly(bus1);
        busDAO.addBusDirectly(bus2);
        busDAO.addBusDirectly(bus3);
        busDAO.addBusDirectly(bus4);
        busDAO.addBusDirectly(bus5);
        busDAO.addBusDirectly(bus6);
        busDAO.addBusDirectly(bus7);
        busDAO.addBusDirectly(bus8);
        busDAO.addBusDirectly(bus9);
        busDAO.addBusDirectly(bus10);
        busDAO.addBusDirectly(bus11);
        busDAO.addBusDirectly(bus12);
        busDAO.addBusDirectly(bus13);
        busDAO.addBusDirectly(bus14);
        busDAO.addBusDirectly(bus15);
        busDAO.addBusDirectly(bus16);
        busDAO.addBusDirectly(bus17);
        busDAO.addBusDirectly(bus18);
        busDAO.addBusDirectly(bus19);
        busDAO.addBusDirectly(bus20);
        busDAO.addBusDirectly(bus21);
        busDAO.addBusDirectly(bus22);
        busDAO.addBusDirectly(bus23);

        // 2. Generate Seats for all buses
        generateSeatsForBus(seatDAO, bus1, 10, 4, false);
        generateSeatsForBus(seatDAO, bus2, 10, 4, false);
        generateSeatsForBus(seatDAO, bus3, 10, 4, false);
        generateSeatsForBus(seatDAO, bus4, 10, 4, false);
        generateSeatsForBus(seatDAO, bus5, 10, 3, true);  // 2+1 Sleeper
        generateSeatsForBus(seatDAO, bus6, 10, 4, false);
        generateSeatsForBus(seatDAO, bus7, 10, 3, true);  // 2+1 Sleeper
        generateSeatsForBus(seatDAO, bus8, 10, 4, false);
        generateSeatsForBus(seatDAO, bus9, 10, 3, true);  // 2+1 Sleeper
        generateSeatsForBus(seatDAO, bus10, 10, 4, false);
        generateSeatsForBus(seatDAO, bus11, 10, 4, false); // Nagpur -> Pune 2+2
        generateSeatsForBus(seatDAO, bus12, 10, 3, true);  // Nagpur -> Pune Sleeper
        generateSeatsForBus(seatDAO, bus13, 10, 4, false); // Nagpur -> Mumbai 2+2
        generateSeatsForBus(seatDAO, bus14, 10, 4, false); // Nagpur -> Hyderabad 2+2
        generateSeatsForBus(seatDAO, bus15, 10, 4, false); // Nagpur -> Raipur 2+2
        generateSeatsForBus(seatDAO, bus16, 10, 3, true);  // Nagpur -> Bhopal Sleeper
        generateSeatsForBus(seatDAO, bus17, 10, 3, true);  // Nagpur -> Indore Sleeper
        generateSeatsForBus(seatDAO, bus18, 10, 4, false); // Pune -> Nagpur 2+2
        generateSeatsForBus(seatDAO, bus19, 10, 4, false); // Mumbai -> Nagpur 2+2
        generateSeatsForBus(seatDAO, bus20, 10, 3, true);  // Hyderabad -> Nagpur Sleeper
        generateSeatsForBus(seatDAO, bus21, 10, 4, false); // Raipur -> Nagpur 2+2
        generateSeatsForBus(seatDAO, bus22, 10, 4, false); // Mumbai -> Jaipur 2+2
        generateSeatsForBus(seatDAO, bus23, 10, 3, true);  // Jaipur -> Mumbai Sleeper
        generateSeatsForBus(seatDAO, bus20, 10, 3, true);  // Hyderabad -> Nagpur Sleeper
        generateSeatsForBus(seatDAO, bus21, 10, 4, false); // Raipur -> Nagpur 2+2

        // 3. Mark a few seats as booked on Bus 1 for visual verification
        markBooked(seatDAO, 1, "2A");
        markBooked(seatDAO, 1, "2W");
        markBooked(seatDAO, 1, "5W");
        markBooked(seatDAO, 1, "5A");
        markBooked(seatDAO, 1, "10A");

        // 4. Create sample passengers
        Passenger p1 = new Passenger(1, "Aarav Sharma", 26, "Male", "9876543210", "aarav.sharma@example.com");
        Passenger p2 = new Passenger(2, "Priya Verma", 24, "Female", "9876543211", "priya.verma@example.com");
        Passenger p3 = new Passenger(3, "Rajesh Patel", 45, "Male", "9123456789", "rajesh.patel@example.com");
        passengerDAO.addPassengerDirectly(p1);
        passengerDAO.addPassengerDirectly(p2);
        passengerDAO.addPassengerDirectly(p3);

        // 5. Create initial sample bookings
        Booking booking1 = new Booking(1, "PNR-8849201", bus1, LocalDate.now().plusDays(1),
                LocalDateTime.now().minusHours(2), 1404.00, BookingStatus.CONFIRMED, "9876543210", "aarav.sharma@example.com");
        booking1.setUserId(1); // Belongs to user Aarav Sharma

        Seat s2A = seatDAO.findByBusIdAndSeatNumber(1, "2A");
        Seat s2W = seatDAO.findByBusIdAndSeatNumber(1, "2W");
        if (s2A != null && s2W != null) {
            booking1.addBookingPassenger(new BookingPassenger(1, 1, p1, s2A, bus1.calculateSeatFare(s2A)));
            booking1.addBookingPassenger(new BookingPassenger(2, 1, p2, s2W, bus1.calculateSeatFare(s2W)));
        }

        Booking booking2 = new Booking(2, "PNR-7731945", bus1, LocalDate.now().plusDays(1),
                LocalDateTime.now().minusHours(5), 702.00, BookingStatus.CONFIRMED, "9123456789", "rajesh.patel@example.com");
        booking2.setUserId(2); // Belongs to user Rajesh Patel
        Seat s5W = seatDAO.findByBusIdAndSeatNumber(1, "5W");
        if (s5W != null) {
            booking2.addBookingPassenger(new BookingPassenger(3, 2, p3, s5W, bus1.calculateSeatFare(s5W)));
        }

        bookingDAO.addBookingDirectly(booking1);
        bookingDAO.addBookingDirectly(booking2);
    }

    private static void markBooked(InMemorySeatDAO seatDAO, int busId, String seatNum) {
        Seat s = seatDAO.findByBusIdAndSeatNumber(busId, seatNum);
        if (s != null) {
            s.setSeatStatus(SeatStatus.BOOKED);
        }
    }

    private static void generateSeatsForBus(InMemorySeatDAO seatDAO, Bus bus, int rows, int cols, boolean isSleeper) {
        List<Seat> seats = new ArrayList<>();
        int seatCounter = 1;

        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                String seatNum;
                SeatType type;

                if (cols == 4) { // 2+2 layout
                    boolean isWindow = (c == 1 || c == 4);
                    type = isWindow ? SeatType.WINDOW : SeatType.AISLE;
                    seatNum = seatCounter + (isWindow ? "W" : "A");
                } else { // 2+1 layout
                    if (c == 1) {
                        type = SeatType.WINDOW;
                        seatNum = seatCounter + "W";
                    } else if (c == 2) {
                        type = SeatType.AISLE;
                        seatNum = seatCounter + "A";
                    } else {
                        type = isSleeper ? SeatType.SLEEPER_LOWER : SeatType.WINDOW;
                        seatNum = seatCounter + "S";
                    }
                }

                Seat seat = new Seat(
                        (bus.getBusId() * 100) + seatCounter,
                        bus.getBusId(),
                        seatNum,
                        type,
                        SeatStatus.AVAILABLE,
                        "LOWER",
                        r,
                        c,
                        1.0
                );
                seats.add(seat);
                seatCounter++;
            }
        }
        seatDAO.bulkInsertSeats(seats);
    }
}
