-- ============================================================================
-- BUS RESERVATION SYSTEM - DATABASE SCHEMA (PostgreSQL)
-- MCA 1st Semester Object-Oriented Programming Mini-Project
-- ============================================================================

-- Create Database (Run separately if needed in pgAdmin 4 or psql)
-- CREATE DATABASE bus_reservation_db;
-- \c bus_reservation_db;

-- Drop existing tables in reverse dependency order
DROP TABLE IF EXISTS booking_passengers CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS seats CASCADE;
DROP TABLE IF EXISTS passengers CASCADE;
DROP TABLE IF EXISTS buses CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop existing types if recreating
DROP TYPE IF EXISTS bus_type_enum CASCADE;
DROP TYPE IF EXISTS seat_type_enum CASCADE;
DROP TYPE IF EXISTS seat_status_enum CASCADE;
DROP TYPE IF EXISTS booking_status_enum CASCADE;

-- ----------------------------------------------------------------------------
-- 0. USERS TABLE
-- Stores authenticated user accounts and credential hashes.
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    user_id              SERIAL PRIMARY KEY,
    full_name            VARCHAR(120) NOT NULL,
    email                VARCHAR(150) NOT NULL UNIQUE,
    mobile_number        VARCHAR(15) NOT NULL,
    password_hash        VARCHAR(255) NOT NULL,
    created_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users (LOWER(email));
CREATE INDEX idx_users_mobile ON users (mobile_number);

-- ----------------------------------------------------------------------------
-- 1. BUSES TABLE
-- Stores master data of buses operating across different intercity routes.
-- ----------------------------------------------------------------------------
CREATE TABLE buses (
    bus_id              SERIAL PRIMARY KEY,
    bus_number          VARCHAR(30) NOT NULL UNIQUE,
    bus_name            VARCHAR(100) NOT NULL,
    bus_type            VARCHAR(50) NOT NULL, -- e.g., 'AC Sleeper 2+1', 'Volvo Multi-Axle 2+2', 'Electric AC Seater 2+2'
    source              VARCHAR(100) NOT NULL,
    destination         VARCHAR(100) NOT NULL,
    departure_time      TIME NOT NULL,
    arrival_time        TIME NOT NULL,
    journey_duration    VARCHAR(30) NOT NULL, -- e.g., '05h 30m'
    fare                NUMERIC(10, 2) NOT NULL CHECK (fare > 0),
    total_seats         INT NOT NULL CHECK (total_seats > 0),
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_source_dest CHECK (LOWER(source) <> LOWER(destination))
);

-- Index for rapid search on route pairs
CREATE INDEX idx_buses_route ON buses (LOWER(source), LOWER(destination), is_active);

-- ----------------------------------------------------------------------------
-- 2. PASSENGERS TABLE
-- Stores passenger personal details for reservations.
-- ----------------------------------------------------------------------------
CREATE TABLE passengers (
    passenger_id        SERIAL PRIMARY KEY,
    full_name           VARCHAR(120) NOT NULL,
    age                 INT NOT NULL CHECK (age >= 1 AND age <= 120),
    gender              VARCHAR(15) NOT NULL CHECK (gender IN ('Male', 'Female', 'Other')),
    mobile_number       VARCHAR(15) NOT NULL,
    email               VARCHAR(150),
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_passengers_mobile ON passengers (mobile_number);

-- ----------------------------------------------------------------------------
-- 3. SEATS TABLE
-- Stores individual seats associated with each bus, seat type, and layout info.
-- ----------------------------------------------------------------------------
CREATE TABLE seats (
    seat_id             SERIAL PRIMARY KEY,
    bus_id              INT NOT NULL REFERENCES buses(bus_id) ON DELETE CASCADE,
    seat_number         VARCHAR(10) NOT NULL, -- e.g., 'A1', 'A2', 'B1', 'L1', 'U1'
    seat_type           VARCHAR(30) NOT NULL, -- 'WINDOW', 'AISLE', 'MIDDLE', 'SLEEPER_LOWER', 'SLEEPER_UPPER'
    seat_status         VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', -- 'AVAILABLE', 'BOOKED', 'BLOCKED'
    deck                VARCHAR(10) NOT NULL DEFAULT 'LOWER', -- 'LOWER', 'UPPER'
    row_num             INT NOT NULL,
    col_num             INT NOT NULL,
    fare_modifier       NUMERIC(5, 2) NOT NULL DEFAULT 1.00,
    CONSTRAINT uq_bus_seat UNIQUE (bus_id, seat_number),
    CONSTRAINT chk_seat_status CHECK (seat_status IN ('AVAILABLE', 'BOOKED', 'BLOCKED'))
);

CREATE INDEX idx_seats_bus_status ON seats (bus_id, seat_status);

-- ----------------------------------------------------------------------------
-- 4. BOOKINGS TABLE
-- Stores overall booking transactions, journey dates, total fare, and PNR.
-- ----------------------------------------------------------------------------
CREATE TABLE bookings (
    booking_id          SERIAL PRIMARY KEY,
    user_id             INT REFERENCES users(user_id) ON DELETE SET NULL,
    pnr_number          VARCHAR(16) NOT NULL UNIQUE,
    bus_id              INT NOT NULL REFERENCES buses(bus_id) ON DELETE RESTRICT,
    journey_date        DATE NOT NULL,
    booking_date        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    total_fare          NUMERIC(10, 2) NOT NULL CHECK (total_fare >= 0),
    booking_status      VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED', -- 'CONFIRMED', 'CANCELLED'
    contact_phone       VARCHAR(15) NOT NULL,
    contact_email       VARCHAR(150),
    CONSTRAINT chk_booking_status CHECK (booking_status IN ('CONFIRMED', 'CANCELLED'))
);

CREATE INDEX idx_bookings_user ON bookings (user_id);
CREATE INDEX idx_bookings_pnr ON bookings (pnr_number);
CREATE INDEX idx_bookings_bus_date ON bookings (bus_id, journey_date, booking_status);

-- ----------------------------------------------------------------------------
-- 5. BOOKING_PASSENGERS TABLE
-- Junction table linking bookings, passengers, and allocated seats.
-- ----------------------------------------------------------------------------
CREATE TABLE booking_passengers (
    booking_passenger_id SERIAL PRIMARY KEY,
    booking_id           INT NOT NULL REFERENCES bookings(booking_id) ON DELETE CASCADE,
    passenger_id         INT NOT NULL REFERENCES passengers(passenger_id) ON DELETE RESTRICT,
    seat_id              INT NOT NULL REFERENCES seats(seat_id) ON DELETE RESTRICT,
    seat_fare            NUMERIC(10, 2) NOT NULL CHECK (seat_fare >= 0),
    CONSTRAINT uq_booking_seat UNIQUE (booking_id, seat_id)
);

CREATE INDEX idx_bp_booking ON booking_passengers (booking_id);
CREATE INDEX idx_bp_seat ON booking_passengers (seat_id);

-- ----------------------------------------------------------------------------
-- Comments for academic review / documentation
-- ----------------------------------------------------------------------------
COMMENT ON TABLE buses IS 'Master catalog of buses with route, timings, capacity and pricing';
COMMENT ON TABLE passengers IS 'Passenger profiles created during booking';
COMMENT ON TABLE seats IS 'Physical bus seats with top-view coordinate and layout mapping';
COMMENT ON TABLE bookings IS 'Master reservations with PNR, journey date, and audit trail';
COMMENT ON TABLE booking_passengers IS 'Detailed passenger-to-seat allocation per booking';
