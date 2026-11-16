-- ============================================================================
-- BUS RESERVATION SYSTEM - USEFUL QUERIES & VIVA REFERENCES (PostgreSQL)
-- ============================================================================

-- 1. Search Active Buses between Source and Destination
SELECT 
    bus_id,
    bus_number,
    bus_name,
    bus_type,
    source,
    destination,
    departure_time,
    arrival_time,
    journey_duration,
    fare,
    total_seats
FROM buses
WHERE LOWER(source) = LOWER('Delhi') 
  AND LOWER(destination) = LOWER('Jaipur') 
  AND is_active = true
ORDER BY departure_time ASC;

-- 2. Count Available vs Booked Seats for a Specific Bus
SELECT 
    seat_status,
    COUNT(*) AS total_count
FROM seats
WHERE bus_id = 1
GROUP BY seat_status;

-- 3. Retrieve Complete Passenger Boarding List for a Specific PNR
SELECT 
    b.pnr_number,
    b.journey_date,
    b.booking_status,
    bu.bus_name,
    bu.bus_number,
    bu.source,
    bu.destination,
    bu.departure_time,
    p.full_name AS passenger_name,
    p.age,
    p.gender,
    s.seat_number,
    s.seat_type,
    bp.seat_fare
FROM bookings b
JOIN buses bu ON b.bus_id = bu.bus_id
JOIN booking_passengers bp ON b.booking_id = bp.booking_id
JOIN passengers p ON bp.passenger_id = p.passenger_id
JOIN seats s ON bp.seat_id = s.seat_id
WHERE b.pnr_number = 'PNR-8849201';

-- 4. Pessimistic Locking query used inside JDBC Transaction before Booking
-- This prevents race conditions / double booking by concurrent threads.
-- (Executed within a transaction block: BEGIN -> SELECT FOR UPDATE -> UPDATE -> COMMIT)
SELECT seat_id, seat_number, seat_status 
FROM seats 
WHERE bus_id = 1 AND seat_number IN ('1W', '1A')
FOR UPDATE;

-- 5. Cancel Booking Transaction
-- Updates status to CANCELLED and marks corresponding seats back to AVAILABLE.
UPDATE bookings 
SET booking_status = 'CANCELLED' 
WHERE pnr_number = 'PNR-8849201';

UPDATE seats 
SET seat_status = 'AVAILABLE' 
WHERE seat_id IN (
    SELECT bp.seat_id 
    FROM booking_passengers bp
    JOIN bookings b ON bp.booking_id = b.booking_id
    WHERE b.pnr_number = 'PNR-8849201'
);

-- 6. Revenue and Occupancy Report by Bus Route
SELECT 
    bu.source || ' -> ' || bu.destination AS route,
    COUNT(DISTINCT b.booking_id) AS total_bookings,
    COUNT(bp.booking_passenger_id) AS total_passengers,
    COALESCE(SUM(bp.seat_fare), 0) AS total_revenue
FROM buses bu
LEFT JOIN bookings b ON bu.bus_id = b.bus_id AND b.booking_status = 'CONFIRMED'
LEFT JOIN booking_passengers bp ON b.booking_id = bp.booking_id
GROUP BY bu.source, bu.destination
ORDER BY total_revenue DESC;
