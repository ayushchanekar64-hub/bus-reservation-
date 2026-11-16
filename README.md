# Bus Reservation System
### Enterprise Desktop Bus Reservation System in Java, JavaFX & PostgreSQL
*MCA 1st Semester Object-Oriented Programming (OOP) Mini-Project*

---

## 📌 Project Overview

The **Bus Reservation System** is a professional desktop application developed in Java using JavaFX for the user interface, PostgreSQL for relational persistence, and Apache Maven for build orchestration. It allows travelers to search intercity buses across corridors, inspect a realistic **top-view bus interior floor plan**, select seats interactively in real time, register passenger profiles, process concurrency-safe bookings, download official PDF boarding passes, and manage or cancel reservations.

The application is engineered using clean **Object-Oriented Programming (OOP)** principles, the **Data Access Object (DAO)** pattern, and a **Dual-Mode Persistence Architecture** that allows the entire application to run seamlessly out of the box with zero external dependencies, while offering full PostgreSQL enterprise connectivity when ready.

---

## 🌟 Key Features

1. **Realistic Top-View Interactive Bus Seat Selection (Core Feature)**:
   - Complete top-view bus floor plan featuring an aerodynamic bus hull, curved windshield, side mirrors, driver cabin with steering wheel, front entrance door, central walking aisle, and rear emergency exit.
   - Distinct color-coded seat states:
     - 🟩 **Green**: Available for booking. Hover displays seat number, type, and fare tooltip.
     - ⬜ **Grey**: Already booked by another passenger (disabled).
     - 🟦 **Blue with Glow**: Selected by active user.
   - Supports diverse fleet configurations: **2+2 Seater**, **2+1 Luxury Seater**, and **2+1 Sleeper Berths**.
   - Instant live calculation: Selected seats count, seat number list, base fare, 5% statutory GST, and grand total update in real-time on every click.
   - Enforces a maximum limit of 6 seats per transaction with clear validation messages.

2. **Search & Fleet Exploration**:
   - Filter by Origin, Destination, and Journey Date with date validation (past dates disabled).
   - Instant Route Swap button ($\rightleftharpoons$).
   - Quick route selector chips for major corridors (Delhi $\rightarrow$ Jaipur, Mumbai $\rightarrow$ Pune, Bangalore $\rightarrow$ Goa, Hyderabad $\rightarrow$ Bangalore, Chennai $\rightarrow$ Coimbatore).
   - Rich bus cards displaying operator name, registration plate, bus category badge, timings, duration, available seats indicator, and amenities tags.

3. **Passenger Profiling & Input Validation**:
   - Dynamic passenger entry rows matching the exact count and identifiers of reserved seats.
   - Strict validation for 10-digit Indian mobile numbers (`^[6-9]\d{9}$`), alphabetic names, and ages (1–120).

4. **ACID Transaction-Safe Booking Engine**:
   - Concurrency protection using pessimistic row-level locking (`SELECT ... FOR UPDATE`).
   - Atomic revalidation before commitment: completely prevents double-booking race conditions.
   - Cryptographically randomized, unique human-readable PNR generation (`PNR-XXXXXXX`).

5. **Official Electronic Ticket & Document Issuance**:
   - Boarding-pass styled e-ticket with PNR badge, route vector, passenger details, and simulated barcode.
   - **Vector-rendered PDF Ticket Export** powered by Apache PDFBox 3.0.
   - Direct operating system print routing via JavaFX `PrinterJob`.

6. **Booking Management & Real-time Cancellation**:
   - Lookup and search bookings by PNR.
   - One-click cancellation with immediate database restoration of seats back to `AVAILABLE`.

7. **Dual-Mode Persistence (Graceful Auto-Fallback)**:
   - Connects to PostgreSQL when online.
   - If PostgreSQL is offline or not yet configured, it automatically activates a thread-safe, high-fidelity In-Memory engine preloaded with identical realistic data from `sample_data.sql`, ensuring 100% functionality right away!

---

## 🛠️ Technology Stack

| Layer | Technology | Details |
| :--- | :--- | :--- |
| **Language** | Java 21 (LTS) | Modern Java with records, pattern matching, dynamic method dispatch |
| **GUI Framework** | JavaFX 21.0.2 | Controls, Graphics, FXML, Custom Shapes (`Arc`, `Rectangle`, `Line`) |
| **Database** | PostgreSQL 14+ | Relational persistence, constraints, indexes, row locking |
| **Database Driver**| PostgreSQL JDBC 42.7.2 | PreparedStatements, Connection pooling, ACID transaction management |
| **PDF Generation** | Apache PDFBox 3.0.1 | Vector PDF document generation |
| **Build Tool** | Apache Maven 3.9+ | Standard Maven layout with bundled `mvnw.cmd` wrapper |
| **Testing** | JUnit 5 (Jupiter 5.10.2) | 41 unit & integration tests with 100% pass rate |

---

## 📁 Project Folder Structure

```
Bus-Reservation-System/
├── pom.xml                                 # Maven build and dependencies
├── mvnw.cmd                                # Windows Maven wrapper script
├── README.md                               # Project documentation
│
├── database/                               # PostgreSQL database assets
│   ├── schema.sql                          # Complete DDL tables, constraints, indexes
│   ├── sample_data.sql                     # Rich intercity routes, buses, and seats
│   └── queries.sql                         # Operational and analytical reports
│
├── docs/                                   # College submission documentation
│   ├── PROJECT_REPORT.md                   # Full 22-section MCA project report
│   ├── DATABASE_DESIGN.md                  # Schema dictionary & ER diagrams
│   ├── OOP_CONCEPTS.md                     # Code-to-concept viva mapping
│   ├── TEST_CASES.md                       # Comprehensive 41-case test specification
│   ├── VIVA_QUESTIONS.md                   # Top 30 viva questions with model answers
│   └── USER_GUIDE.md                       # Operating manual
│
├── src/main/java/com/busreservation/
│   ├── Main.java                           # JavaFX Application entrypoint
│   ├── config/
│   │   ├── AppConfig.java                  # Centralized configuration & mode detection
│   │   └── DBConnection.java               # JDBC Connection provider & transaction helpers
│   ├── model/                              # OOP Domain Models
│   │   ├── Bus.java                        # Abstract base class
│   │   ├── SeaterBus.java                  # 2+2 layout subclass
│   │   ├── LuxuryVolvoBus.java             # Multi-level inheritance luxury subclass
│   │   ├── SleeperBus.java                 # 2+1 sleeper berth subclass
│   │   ├── Seat.java                       # Physical seat model
│   │   ├── SeatType.java                   # Window, Aisle, Sleeper enums
│   │   ├── SeatStatus.java                 # Available, Booked, Selected enums
│   │   ├── Passenger.java                  # Passenger profile model
│   │   ├── Booking.java                    # Composite reservation model
│   │   ├── BookingStatus.java              # Confirmed, Cancelled enums
│   │   └── BookingPassenger.java           # Junction entity
│   ├── dao/                                # Data Access Layer
│   │   ├── BusDAO.java                     # Bus DAO interface
│   │   ├── SeatDAO.java                    # Seat DAO interface
│   │   ├── PassengerDAO.java               # Passenger DAO interface
│   │   ├── BookingDAO.java                 # Booking DAO interface
│   │   ├── DAOFactory.java                 # Abstract factory pattern
│   │   ├── postgres/                       # PostgreSQL JDBC implementations
│   │   │   ├── PostgresBusDAO.java
│   │   │   ├── PostgresSeatDAO.java
│   │   │   ├── PostgresPassengerDAO.java
│   │   │   └── PostgresBookingDAO.java     # SELECT FOR UPDATE transaction engine
│   │   └── inmemory/                       # Standalone in-memory implementations
│   │       ├── InMemoryBusDAO.java
│   │       ├── InMemorySeatDAO.java
│   │       ├── InMemoryPassengerDAO.java
│   │       ├── InMemoryBookingDAO.java
│   │       └── SampleDataLoader.java       # Preloads realistic sample dataset
│   ├── service/                            # Business Logic Layer
│   │   ├── BusService.java                 # Route search and catalog queries
│   │   ├── SeatService.java                # Seat availability & coordinate queries
│   │   ├── FareService.java                # Polymorphic fare & GST calculations
│   │   └── BookingService.java             # Atomic reservations and cancellations
│   ├── util/                               # Utilities
│   │   ├── ValidationUtil.java             # Regex mobile, email, age, route validation
│   │   ├── PNRGenerator.java               # Secure randomized PNR generator
│   │   ├── DateTimeUtil.java               # Formatter for dates and times
│   │   └── PDFGenerator.java               # Apache PDFBox PDF ticket generator
│   └── view/                               # Presentation Layer
│       ├── ViewManager.java                # Central Page Router & state coordinator
│       ├── DashboardView.java              # Search console & KPI stats
│       ├── BusSearchView.java              # Filtered bus cards listing
│       ├── SeatSelectionView.java          # Interactive top-view layout screen
│       ├── PassengerDetailsView.java       # Dynamic passenger input forms
│       ├── BookingSummaryView.java         # Review & pre-confirmation card
│       ├── TicketView.java                 # E-ticket boarding pass screen
│       ├── MyBookingsView.java             # PNR search & cancellation screen
│       └── component/
│           ├── NavbarComponent.java        # Navigation bar with DB mode indicator
│           ├── BusCardComponent.java       # Reusable bus result card
│           ├── BusLayoutPane.java          # Custom top-view bus floor plan visualizer
│           ├── SeatButton.java             # Custom realistic seat component
│           └── ToastNotification.java      # User feedback toast banner
│
├── src/main/resources/
│   ├── css/style.css                       # Modern CSS stylesheet
│   └── config/database.properties          # Database connection settings
│
└── src/test/java/com/busreservation/       # Unit & Integration Tests (JUnit 5)
    ├── ValidationUtilTest.java             # 20 tests
    ├── PNRGeneratorTest.java               # 2 tests
    ├── FareServiceTest.java                # 4 tests
    ├── BusServiceTest.java                 # 5 tests
    ├── SeatServiceTest.java                # 4 tests
    ├── BookingServiceTest.java             # 3 tests
    └── DatabaseConnectionTest.java         # 3 tests
```

---

## 🚀 Execution Instructions

### 1. Run the Application
You can launch the application immediately using the included Maven wrapper:

```powershell
# Using the bundled Maven Wrapper
.\mvnw.cmd javafx:run

# Or using standard Maven
mvn javafx:run
```

The application window will open automatically.

### 2. Run the Automated Test Suite
To execute all 41 JUnit 5 unit and integration tests:

```powershell
.\mvnw.cmd test
```

Expected output:
```text
[INFO] Tests run: 41, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 🗄️ PostgreSQL Database Setup (When Ready)

The application includes complete PostgreSQL integration. When you wish to connect PostgreSQL:

### Step 1: Create Database & Tables in pgAdmin 4 or psql
Open pgAdmin 4 Query Tool or `psql` and run:

```sql
-- 1. Create database
CREATE DATABASE bus_reservation_db;

-- 2. Connect to the database
\c bus_reservation_db;
```

Execute the scripts located in the `database/` folder in order:
1. `database/schema.sql` (Creates all tables, constraints, sequences, and indexes)
2. `database/sample_data.sql` (Inserts realistic fleet, seats, and sample reservations)

### Step 2: Configure Credentials
Edit `src/main/resources/config/database.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/bus_reservation_db
db.user=postgres
db.password=your_password_here
db.mode=auto
```

*(Alternatively, set the environment variables `DB_URL`, `DB_USER`, and `DB_PASSWORD` without changing source code).*

When you launch the app, `DAOFactory` will automatically detect PostgreSQL and display:
```
[Success] Connected to PostgreSQL database successfully.
```
And the navbar badge will display: `Mode: PostgreSQL Connected`.

---

## 🎓 Academic Viva Reference

A complete, detailed Viva guide is provided in `docs/VIVA_QUESTIONS.md` and `docs/OOP_CONCEPTS.md`. Here is a quick reference of the core OOP concepts implemented:

- **Encapsulation**: Private fields across all models with validation in setters (e.g. `Passenger.setAge(1..120)`). Defensive copies returned using `Collections.unmodifiableList()`.
- **Inheritance**: `Bus` $\rightarrow$ `SeaterBus` $\rightarrow$ `LuxuryVolvoBus` (Multilevel) and `Bus` $\rightarrow$ `SleeperBus` (Hierarchical).
- **Polymorphism**: `calculateSeatFare(Seat seat)` dynamically overridden across bus subclasses (Window seat premium, luxury comfort surcharge, lower berth multiplier).
- **Abstraction**: Data access decoupled behind `BusDAO`, `SeatDAO`, `PassengerDAO`, and `BookingDAO` interfaces.
- **Composition**: `Booking` owns a list of `BookingPassenger` junction objects.
- **Method Overloading**: Overloaded search methods (`searchBuses(src, dest)` and `searchBuses(src, dest, date)`) and multiple constructors.

---

## 📋 Implemented Feature Checklist

- [x] JavaFX desktop application with light background, white cards, subtle shadows, and modern styling
- [x] Welcome / Dashboard Screen with Search console, Swap button, and KPI metrics
- [x] Available Buses Screen with realistic bus cards and amenities chips
- [x] **Interactive Bus Seat Selection Screen** with realistic TOP-VIEW bus floor plan (curved windshield, side mirrors, driver cabin, steering wheel, entrance door, central aisle, rear emergency exit, and cushioned seats)
- [x] Reactive seat selection: Green (Available), Grey (Booked), Blue Outline with Glow (Selected)
- [x] Real-time live fare calculation with 5% GST breakdown
- [x] Multi-passenger form dynamically matching selected seats
- [x] Comprehensive input validation (Mobile, Email, Age, Name, Route, Date)
- [x] Booking Summary Screen with trip and passenger review
- [x] Transaction-safe booking engine with pessimistic row locking (`SELECT FOR UPDATE`)
- [x] Unique PNR generation (`PNR-XXXXXXX`)
- [x] Electronic Boarding Pass ticket screen with simulated barcode
- [x] **Save Ticket as PDF** using Apache PDFBox 3.0
- [x] **Print Ticket** using JavaFX `PrinterJob`
- [x] My Bookings screen with PNR lookup
- [x] Booking cancellation with immediate seat availability restoration
- [x] PostgreSQL database scripts (`schema.sql`, `sample_data.sql`, `queries.sql`)
- [x] Dual-mode architecture (Seamless fallback to In-Memory demo mode when Postgres is offline)
- [x] 41 automated JUnit 5 tests covering all services, validations, transactions, and concurrency
- [x] Full academic project documentation pack (`PROJECT_REPORT.md`, `DATABASE_DESIGN.md`, `TEST_CASES.md`, `OOP_CONCEPTS.md`, `VIVA_QUESTIONS.md`, `USER_GUIDE.md`)
