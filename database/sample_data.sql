-- ============================================================================
-- BUS RESERVATION SYSTEM - SAMPLE DATA SCRIPT (PostgreSQL)
-- MCA 1st Semester Object-Oriented Programming Mini-Project
-- ============================================================================

-- Clear existing data
TRUNCATE TABLE booking_passengers RESTART IDENTITY CASCADE;
TRUNCATE TABLE bookings RESTART IDENTITY CASCADE;
TRUNCATE TABLE seats RESTART IDENTITY CASCADE;
TRUNCATE TABLE passengers RESTART IDENTITY CASCADE;
TRUNCATE TABLE buses RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- ----------------------------------------------------------------------------
-- 1. INSERT BUSES (Diverse Types & Major Intercity Routes)
-- ----------------------------------------------------------------------------
INSERT INTO buses (bus_id, bus_number, bus_name, bus_type, source, destination, departure_time, arrival_time, journey_duration, fare, total_seats, is_active) VALUES
(1, 'DL-01-AB-1234', 'Zingbus Royal Class', 'Volvo Multi-Axle 2+2', 'Delhi', 'Jaipur', '06:00:00', '11:30:00', '05h 30m', 650.00, 40, true),
(2, 'RJ-14-PA-5678', 'IntrCity SmartBus', 'AC Seater 2+2', 'Delhi', 'Jaipur', '14:30:00', '20:15:00', '05h 45m', 550.00, 40, true),
(3, 'DL-01-EE-9012', 'NueGo Green Electric', 'Electric AC Seater 2+2', 'Delhi', 'Jaipur', '18:00:00', '23:30:00', '05h 30m', 580.00, 40, true),
(4, 'MH-02-CD-4321', 'Shivneri Volvo Executive', 'Volvo Multi-Axle 2+2', 'Mumbai', 'Pune', '07:00:00', '10:30:00', '03h 30m', 450.00, 40, true),
(5, 'MH-12-EF-8765', 'Purple Travels Gold', 'AC Luxury 2+1', 'Mumbai', 'Pune', '16:00:00', '19:45:00', '03h 45m', 520.00, 30, true),
(6, 'KA-01-JK-3456', 'KSRTC Airavat Club Class', 'Volvo Multi-Axle 2+2', 'Bangalore', 'Goa', '20:30:00', '08:30:00', '12h 00m', 1250.00, 40, true),
(7, 'GA-03-MN-7890', 'VRL Travels Grand Sleeper', 'AC Sleeper 2+1', 'Bangalore', 'Goa', '21:15:00', '09:00:00', '11h 45m', 1450.00, 30, true),
(8, 'TS-09-UB-1122', 'Orange Tours & Travels', 'Volvo Multi-Axle 2+2', 'Hyderabad', 'Bangalore', '21:00:00', '06:30:00', '09h 30m', 950.00, 40, true),
(9, 'KA-05-ZZ-3344', 'SRS Travels Premium', 'AC Sleeper 2+1', 'Hyderabad', 'Bangalore', '22:00:00', '07:15:00', '09h 15m', 1100.00, 30, true),
(10, 'TN-01-AL-9988', 'Parveen Travels Platinum', 'Volvo Multi-Axle 2+2', 'Chennai', 'Coimbatore', '22:30:00', '06:00:00', '07h 30m', 850.00, 40, true),
(11, 'MH-31-CA-1001', 'Purple Grand Multi-Axle', 'Volvo Multi-Axle 2+2', 'Nagpur', 'Pune', '19:00:00', '07:30:00', '12h 30m', 950.00, 40, true),
(12, 'MH-31-TR-2002', 'Royal Chintamani Sleeper', 'AC Sleeper 2+1', 'Nagpur', 'Pune', '20:30:00', '08:45:00', '12h 15m', 1150.00, 30, true),
(13, 'MH-31-SM-3003', 'Samruddhi Express Superfast', 'Volvo Multi-Axle 2+2', 'Nagpur', 'Mumbai', '18:00:00', '05:00:00', '11h 00m', 1250.00, 40, true),
(14, 'MH-31-OR-4004', 'Orange Tours & Travels', 'Volvo Multi-Axle 2+2', 'Nagpur', 'Hyderabad', '21:00:00', '06:00:00', '09h 00m', 850.00, 40, true),
(15, 'MH-31-PY-5005', 'Payal Travels Intercity', 'AC Seater 2+2', 'Nagpur', 'Raipur', '06:30:00', '12:00:00', '05h 30m', 450.00, 40, true),
(16, 'MP-04-VR-6006', 'Verma Travels Express', 'AC Sleeper 2+1', 'Nagpur', 'Bhopal', '22:00:00', '05:30:00', '07h 30m', 750.00, 30, true),
(17, 'MP-09-HN-7007', 'Hans Travels Platinum', 'AC Sleeper 2+1', 'Nagpur', 'Indore', '20:00:00', '06:30:00', '10h 30m', 900.00, 30, true),
(18, 'MH-12-NG-8008', 'Shivshahi Executive', 'AC Seater 2+2', 'Pune', 'Nagpur', '18:30:00', '07:00:00', '12h 30m', 920.00, 40, true),
(19, 'MH-01-NG-9009', 'Neeta Volvo Multi-Axle', 'Volvo Multi-Axle 2+2', 'Mumbai', 'Nagpur', '17:30:00', '04:30:00', '11h 00m', 1250.00, 40, true),
(20, 'TS-09-NG-1111', 'Kaveri Travels Express', 'AC Sleeper 2+1', 'Hyderabad', 'Nagpur', '20:30:00', '05:30:00', '09h 00m', 880.00, 30, true),
(21, 'CG-04-NG-2222', 'Mahendra Travels', 'AC Seater 2+2', 'Raipur', 'Nagpur', '14:00:00', '19:30:00', '05h 30m', 450.00, 40, true);

SELECT setval('buses_bus_id_seq', (SELECT MAX(bus_id) FROM buses));

-- ----------------------------------------------------------------------------
-- 2. INSERT SEATS FOR BUS 1 (Delhi -> Jaipur, 40 Seats, 2+2 Layout)
-- Rows 1 to 10. Columns 1, 2 (Left), 3, 4 (Right). Seat Aisle between col 2 & 3.
-- ----------------------------------------------------------------------------
-- Row 1
INSERT INTO seats (bus_id, seat_number, seat_type, seat_status, deck, row_num, col_num) VALUES
(1, '1W', 'WINDOW', 'AVAILABLE', 'LOWER', 1, 1),
(1, '1A', 'AISLE',  'AVAILABLE', 'LOWER', 1, 2),
(1, '2A', 'AISLE',  'BOOKED',    'LOWER', 1, 3),
(1, '2W', 'WINDOW', 'BOOKED',    'LOWER', 1, 4),
-- Row 2
(1, '3W', 'WINDOW', 'AVAILABLE', 'LOWER', 2, 1),
(1, '3A', 'AISLE',  'AVAILABLE', 'LOWER', 2, 2),
(1, '4A', 'AISLE',  'AVAILABLE', 'LOWER', 2, 3),
(1, '4W', 'WINDOW', 'AVAILABLE', 'LOWER', 2, 4),
-- Row 3
(1, '5W', 'WINDOW', 'BOOKED',    'LOWER', 3, 1),
(1, '5A', 'AISLE',  'BOOKED',    'LOWER', 3, 2),
(1, '6A', 'AISLE',  'AVAILABLE', 'LOWER', 3, 3),
(1, '6W', 'WINDOW', 'AVAILABLE', 'LOWER', 3, 4),
-- Row 4
(1, '7W', 'WINDOW', 'AVAILABLE', 'LOWER', 4, 1),
(1, '7A', 'AISLE',  'AVAILABLE', 'LOWER', 4, 2),
(1, '8A', 'AISLE',  'AVAILABLE', 'LOWER', 4, 3),
(1, '8W', 'WINDOW', 'AVAILABLE', 'LOWER', 4, 4),
-- Row 5
(1, '9W', 'WINDOW', 'AVAILABLE', 'LOWER', 5, 1),
(1, '9A', 'AISLE',  'AVAILABLE', 'LOWER', 5, 2),
(1, '10A', 'AISLE', 'BOOKED',    'LOWER', 5, 3),
(1, '10W', 'WINDOW', 'AVAILABLE', 'LOWER', 5, 4),
-- Row 6
(1, '11W', 'WINDOW', 'AVAILABLE', 'LOWER', 6, 1),
(1, '11A', 'AISLE',  'AVAILABLE', 'LOWER', 6, 2),
(1, '12A', 'AISLE',  'AVAILABLE', 'LOWER', 6, 3),
(1, '12W', 'WINDOW', 'AVAILABLE', 'LOWER', 6, 4),
-- Row 7
(1, '13W', 'WINDOW', 'AVAILABLE', 'LOWER', 7, 1),
(1, '13A', 'AISLE',  'AVAILABLE', 'LOWER', 7, 2),
(1, '14A', 'AISLE',  'AVAILABLE', 'LOWER', 7, 3),
(1, '14W', 'WINDOW', 'AVAILABLE', 'LOWER', 7, 4),
-- Row 8
(1, '15W', 'WINDOW', 'AVAILABLE', 'LOWER', 8, 1),
(1, '15A', 'AISLE',  'AVAILABLE', 'LOWER', 8, 2),
(1, '16A', 'AISLE',  'AVAILABLE', 'LOWER', 8, 3),
(1, '16W', 'WINDOW', 'AVAILABLE', 'LOWER', 8, 4),
-- Row 9
(1, '17W', 'WINDOW', 'AVAILABLE', 'LOWER', 9, 1),
(1, '17A', 'AISLE',  'AVAILABLE', 'LOWER', 9, 2),
(1, '18A', 'AISLE',  'AVAILABLE', 'LOWER', 9, 3),
(1, '18W', 'WINDOW', 'AVAILABLE', 'LOWER', 9, 4),
-- Row 10 (Rear)
(1, '19W', 'WINDOW', 'AVAILABLE', 'LOWER', 10, 1),
(1, '19A', 'AISLE',  'AVAILABLE', 'LOWER', 10, 2),
(1, '20A', 'AISLE',  'AVAILABLE', 'LOWER', 10, 3),
(1, '20W', 'WINDOW', 'AVAILABLE', 'LOWER', 10, 4);

-- ----------------------------------------------------------------------------
-- INSERT SEATS FOR BUS 5 (Mumbai -> Pune, 30 Seats, 2+1 Luxury Layout)
-- ----------------------------------------------------------------------------
INSERT INTO seats (bus_id, seat_number, seat_type, seat_status, deck, row_num, col_num)
SELECT 
    5,
    (r*3 - 2)::text || 'W',
    'WINDOW',
    CASE WHEN r IN (1, 4) THEN 'BOOKED' ELSE 'AVAILABLE' END,
    'LOWER',
    r,
    1
FROM generate_series(1, 10) AS r
UNION ALL
SELECT 
    5,
    (r*3 - 1)::text || 'A',
    'AISLE',
    CASE WHEN r = 1 THEN 'BOOKED' ELSE 'AVAILABLE' END,
    'LOWER',
    r,
    2
FROM generate_series(1, 10) AS r
UNION ALL
SELECT 
    5,
    (r*3)::text || 'S',
    'WINDOW',
    CASE WHEN r = 7 THEN 'BOOKED' ELSE 'AVAILABLE' END,
    'LOWER',
    r,
    3
FROM generate_series(1, 10) AS r;

-- ----------------------------------------------------------------------------
-- 3. INSERT SAMPLE PASSENGERS
-- ----------------------------------------------------------------------------
INSERT INTO passengers (passenger_id, full_name, age, gender, mobile_number, email) VALUES
(1, 'Aarav Sharma', 26, 'Male', '9876543210', 'aarav.sharma@example.com'),
(2, 'Priya Verma', 24, 'Female', '9876543211', 'priya.verma@example.com'),
(3, 'Rajesh Patel', 45, 'Male', '9123456789', 'rajesh.patel@example.com');

SELECT setval('passengers_passenger_id_seq', (SELECT MAX(passenger_id) FROM passengers));

-- ----------------------------------------------------------------------------
-- 0. INSERT SAMPLE USERS
-- ----------------------------------------------------------------------------
INSERT INTO users (user_id, full_name, email, mobile_number, password_hash) VALUES
(1, 'Aarav Sharma', 'aarav@example.com', '9876543210', 'password123'),
(2, 'Priya Verma', 'priya@example.com', '9876543211', 'password123'),
(3, 'Rajesh Patel', 'rajesh@example.com', '9123456789', 'password123'),
(4, 'Ayush', 'ayush@example.com', '9876543212', 'password123');

SELECT setval('users_user_id_seq', (SELECT MAX(user_id) FROM users));

-- ----------------------------------------------------------------------------
-- 4. INSERT SAMPLE BOOKINGS
-- ----------------------------------------------------------------------------
INSERT INTO bookings (booking_id, user_id, pnr_number, bus_id, journey_date, booking_date, total_fare, booking_status, contact_phone, contact_email) VALUES
(1, 1, 'PNR-8849201', 1, CURRENT_DATE + INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '2 hours', 1300.00, 'CONFIRMED', '9876543210', 'aarav.sharma@example.com'),
(2, 1, 'PNR-7731945', 1, CURRENT_DATE + INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '5 hours', 1300.00, 'CONFIRMED', '9123456789', 'rajesh.patel@example.com');

SELECT setval('bookings_booking_id_seq', (SELECT MAX(booking_id) FROM bookings));

-- ----------------------------------------------------------------------------
-- 5. INSERT BOOKING PASSENGERS
-- ----------------------------------------------------------------------------
INSERT INTO booking_passengers (booking_id, passenger_id, seat_id, seat_fare) VALUES
(1, 1, (SELECT seat_id FROM seats WHERE bus_id = 1 AND seat_number = '2A'), 650.00),
(1, 2, (SELECT seat_id FROM seats WHERE bus_id = 1 AND seat_number = '2W'), 650.00),
(2, 3, (SELECT seat_id FROM seats WHERE bus_id = 1 AND seat_number = '5W'), 650.00);
