# DATABASE DESIGN SPECIFICATION
## Bus Reservation System (PostgreSQL)

---

## 1. DATABASE METADATA
- **Database Engine**: PostgreSQL 14+
- **Database Name**: `bus_reservation_db`
- **Default Port**: 5432
- **Character Set**: UTF8
- **Collation**: C / en_US.utf8

---

## 2. RELATIONAL DATA DICTIONARY

### 2.1 Table: `buses`
Primary catalog of intercity buses with route, scheduling, pricing, and active status.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `bus_id` | SERIAL | PRIMARY KEY | Unique surrogate identifier for each bus |
| `bus_number` | VARCHAR(30) | NOT NULL, UNIQUE | Vehicle registration plate (e.g., 'DL-01-AB-1234') |
| `bus_name` | VARCHAR(100) | NOT NULL | Commercial operator name (e.g., 'Zingbus Royal Class') |
| `bus_type` | VARCHAR(50) | NOT NULL | Bus classification (e.g., 'Volvo Multi-Axle 2+2') |
| `source` | VARCHAR(100) | NOT NULL | Origin city |
| `destination` | VARCHAR(100) | NOT NULL | Destination city |
| `departure_time` | TIME | NOT NULL | Scheduled daily departure time |
| `arrival_time` | TIME | NOT NULL | Scheduled arrival time |
| `journey_duration` | VARCHAR(30) | NOT NULL | Formatted duration string (e.g., '05h 30m') |
| `fare` | NUMERIC(10,2) | NOT NULL, CHECK (fare > 0) | Standard base fare in INR |
| `total_seats` | INT | NOT NULL, CHECK (total_seats > 0)| Total passenger capacity |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT true | Operational state flag |
| `created_at` | TIMESTAMPTZ | DEFAULT CURRENT_TIMESTAMP | Audit creation timestamp |

**Table Constraints:**
- `chk_source_dest`: `CHECK (LOWER(source) <> LOWER(destination))` ensures origin and destination cannot be identical.
- **Index**: `CREATE INDEX idx_buses_route ON buses (LOWER(source), LOWER(destination), is_active);`

---

### 2.2 Table: `passengers`
Stores verified passenger personal data registered during booking.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `passenger_id` | SERIAL | PRIMARY KEY | Unique identifier for passenger |
| `full_name` | VARCHAR(120) | NOT NULL | Passenger's legal name |
| `age` | INT | NOT NULL, CHECK (age BETWEEN 1 AND 120) | Passenger's age |
| `gender` | VARCHAR(15) | NOT NULL, CHECK (gender IN ('Male', 'Female', 'Other')) | Gender |
| `mobile_number` | VARCHAR(15) | NOT NULL | 10-digit primary telephone number |
| `email` | VARCHAR(150) | NULL | Optional electronic mail contact |
| `created_at` | TIMESTAMPTZ | DEFAULT CURRENT_TIMESTAMP | Audit timestamp |

**Index:**
- `CREATE INDEX idx_passengers_mobile ON passengers (mobile_number);`

---

### 2.3 Table: `seats`
Stores individual physical seats associated with each bus, coordinate mapping for the top-view UI, and live status.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `seat_id` | SERIAL | PRIMARY KEY | Unique seat identifier |
| `bus_id` | INT | NOT NULL, REFERENCES buses(bus_id) ON DELETE CASCADE | Parent bus foreign key |
| `seat_number` | VARCHAR(10) | NOT NULL | Alphanumeric seat label (e.g., '1W', '1A', '2W') |
| `seat_type` | VARCHAR(30) | NOT NULL | Category ('WINDOW', 'AISLE', 'SLEEPER_LOWER', etc.) |
| `seat_status` | VARCHAR(20) | NOT NULL, DEFAULT 'AVAILABLE' | Current state ('AVAILABLE', 'BOOKED', 'BLOCKED') |
| `deck` | VARCHAR(10) | NOT NULL, DEFAULT 'LOWER' | 'LOWER' or 'UPPER' deck |
| `row_num` | INT | NOT NULL | Longitudinal row position (1..10) |
| `col_num` | INT | NOT NULL | Lateral column position (1..4) |
| `fare_modifier` | NUMERIC(5,2) | NOT NULL, DEFAULT 1.00 | Multiplier for specific seat preference |

**Table Constraints:**
- `uq_bus_seat`: `UNIQUE (bus_id, seat_number)` guarantees no duplicate seat labels on the same bus.
- `chk_seat_status`: `CHECK (seat_status IN ('AVAILABLE', 'BOOKED', 'BLOCKED'))`.
- **Index**: `CREATE INDEX idx_seats_bus_status ON seats (bus_id, seat_status);`

---

### 2.4 Table: `bookings`
Master record for reservation transactions.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `booking_id` | SERIAL | PRIMARY KEY | Unique primary key |
| `pnr_number` | VARCHAR(16) | NOT NULL, UNIQUE | 11-char human-readable PNR (e.g. 'PNR-8849201') |
| `bus_id` | INT | NOT NULL, REFERENCES buses(bus_id) ON DELETE RESTRICT | Reserved bus foreign key |
| `journey_date` | DATE | NOT NULL | Date of travel |
| `booking_date` | TIMESTAMPTZ | DEFAULT CURRENT_TIMESTAMP | Transaction timestamp |
| `total_fare` | NUMERIC(10,2) | NOT NULL, CHECK (total_fare >= 0) | Grand total including GST in INR |
| `booking_status`| VARCHAR(20) | NOT NULL, DEFAULT 'CONFIRMED' | 'CONFIRMED' or 'CANCELLED' |
| `contact_phone` | VARCHAR(15) | NOT NULL | Contact telephone for notification |
| `contact_email` | VARCHAR(150) | NULL | Contact email for ticket delivery |

**Table Constraints:**
- `chk_booking_status`: `CHECK (booking_status IN ('CONFIRMED', 'CANCELLED'))`.
- **Indexes**:
  - `CREATE INDEX idx_bookings_pnr ON bookings (pnr_number);`
  - `CREATE INDEX idx_bookings_bus_date ON bookings (bus_id, journey_date, booking_status);`

---

### 2.5 Table: `booking_passengers`
Junction table linking bookings, individual passengers, and their designated seats.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `booking_passenger_id` | SERIAL | PRIMARY KEY | Unique identifier |
| `booking_id` | INT | NOT NULL, REFERENCES bookings(booking_id) ON DELETE CASCADE | Parent booking reference |
| `passenger_id` | INT | NOT NULL, REFERENCES passengers(passenger_id) ON DELETE RESTRICT | Passenger reference |
| `seat_id` | INT | NOT NULL, REFERENCES seats(seat_id) ON DELETE RESTRICT | Reserved seat reference |
| `seat_fare` | NUMERIC(10,2) | NOT NULL, CHECK (seat_fare >= 0) | Specific fare charged for this seat |

**Table Constraints:**
- `uq_booking_seat`: `UNIQUE (booking_id, seat_id)` prevents the same seat from being added twice in one booking.
- **Indexes**:
  - `CREATE INDEX idx_bp_booking ON booking_passengers (booking_id);`
  - `CREATE INDEX idx_bp_seat ON booking_passengers (seat_id);`

---

## 3. CONCURRENCY & TRANSACTION ISOLATION

To ensure complete ACID compliance and prevent two simultaneous users from booking the same seat:

```sql
-- 1. Begin JDBC Transaction
BEGIN;

-- 2. Acquire exclusive pessimistic row locks on candidate seats
SELECT seat_id, seat_number, seat_status 
FROM seats 
WHERE seat_id IN (101, 102) 
FOR UPDATE;

-- 3. In application code, verify seat_status == 'AVAILABLE' for all rows.
-- If any seat is already 'BOOKED', execute ROLLBACK;

-- 4. Insert booking
INSERT INTO bookings (...) VALUES (...);

-- 5. Insert passengers and booking_passengers junction rows
INSERT INTO booking_passengers (...) VALUES (...);

-- 6. Update seat status to BOOKED
UPDATE seats SET seat_status = 'BOOKED' WHERE seat_id IN (101, 102);

-- 7. Atomically commit the transaction
COMMIT;
```

---

## 4. RESTORING SEATS ON CANCELLATION

When a user cancels a booking:

```sql
BEGIN;

-- 1. Verify booking is confirmed and lock record
SELECT booking_id, booking_status FROM bookings WHERE pnr_number = 'PNR-8849201' FOR UPDATE;

-- 2. Mark booking CANCELLED
UPDATE bookings SET booking_status = 'CANCELLED' WHERE pnr_number = 'PNR-8849201';

-- 3. Release reserved seats back to AVAILABLE
UPDATE seats 
SET seat_status = 'AVAILABLE' 
WHERE seat_id IN (
    SELECT bp.seat_id 
    FROM booking_passengers bp
    JOIN bookings b ON bp.booking_id = b.booking_id
    WHERE b.pnr_number = 'PNR-8849201'
);

COMMIT;
```
