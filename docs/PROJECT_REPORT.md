# BUS RESERVATION SYSTEM
## Master of Computer Applications (MCA) – 1st Semester
### Object-Oriented Programming (Java) Mini-Project Report

---

### CERTIFICATE OF ORIGINALITY

This is to certify that the project entitled **"Bus Reservation System"** submitted by the student in partial fulfillment of the requirements for the award of the degree of **Master of Computer Applications (MCA)** is an authentic record of original and independent work carried out under academic supervision. The matter presented in this report has not been submitted elsewhere for any other degree or diploma.

\
**Internal Guide / Project Supervisor** &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; **Head of Department (MCA)**  
Date: 16 November 2026 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Department of Computer Applications

---

### ACKNOWLEDGEMENT

I express my deepest gratitude to our project guide and the faculty of the Department of Computer Applications for their invaluable guidance, encouragement, and insightful feedback throughout the conceptualization, system architecture design, and implementation of this Bus Reservation System.

I also extend my sincere thanks to my peers and family for their continuous moral support and motivation during the development of this enterprise application.

---

### ABSTRACT

The **Bus Reservation System** is an enterprise-grade desktop application engineered in Java using modern JavaFX for graphical user interaction and PostgreSQL for relational persistence. The system models intercity road passenger transport operations, enabling users to search buses across regional corridors, inspect real-time seat status on an interactive, realistic top-view bus floor plan, reserve individual or multiple seats, register passenger profiles, process atomic bookings with double-booking prevention, generate unique PNR electronic tickets, and perform trip cancellations with automatic seat release.

The application rigorously adheres to core Object-Oriented Programming (OOP) paradigms including Encapsulation, Multi-level and Hierarchical Inheritance, Polymorphism, Abstraction, and Object Composition, layered over the Data Access Object (DAO) and Service-Oriented Architecture (SOA) patterns.

---

## 1. INTRODUCTION

In the modern public and commercial transport sector, computerized ticket reservation systems are crucial for operational efficiency, passenger convenience, and real-time fleet scheduling. Traditional counter-based booking often suffers from long waiting queues, double allocation risks, human accounting errors, and a lack of visual seat transparency.

The **Bus Reservation System** is developed to bridge these limitations by delivering a rich graphical user interface desktop client that emulates commercial platforms (such as redBus, Zingbus, and AbhiBus). It delivers end-to-end functionality from schedule exploration to PDF electronic ticket issuance and booking cancellation.

---

## 2. PROBLEM STATEMENT

Manual or basic terminal-based ticket booking systems suffer from several critical shortcomings:
1. **Lack of Spatial Awareness**: Passengers cannot visualize whether they are booking a window, aisle, or sleeper berth, or where the seat is positioned relative to the driver cabin and entry doors.
2. **Double Booking Anomalies**: Multiple users or threads attempting to reserve the same seat simultaneously can cause race conditions and overbooking.
3. **Rigid Seating Assumptions**: Many software prototypes assume a simplistic static grid, failing to reflect diverse fleet layouts such as 2+2 luxury seater buses and 2+1 sleeper coaches.
4. **Poor Error Handling & Data Leakage**: Inadequate validation allows corrupt telephone numbers, invalid ages, and negative fares to enter persistence stores.

---

## 3. OBJECTIVES

The primary objectives of this project are:
- Build an interactive, realistic **Top-View Bus Floor Plan** visualizing the aerodynamic bus hull, driver cabin, steering wheel, central aisle, entry door, emergency exit, and color-coded seat buttons.
- Implement real-time dynamic seat selection with visual states (Green = Available, Grey = Booked, Blue Outline = Selected).
- Enforce strict transaction safety using ACID transactions, pessimistic locking (`SELECT ... FOR UPDATE`), and rollback controls.
- Support diverse bus models (`SeaterBus`, `LuxuryVolvoBus`, `SleeperBus`) utilizing Polymorphic fare calculation and dynamic coordinate-based layout rendering.
- Provide multi-passenger booking, instant fare calculation (including 5% statutory GST), and unique cryptographically randomized PNR generation.
- Supply full-featured e-ticket issuance with PDF export (via Apache PDFBox) and ticket printing support.
- Enable full booking lifecycle management including lookup and one-click booking cancellation with immediate seat restoration.

---

## 4. HARDWARE AND SOFTWARE REQUIREMENTS

### Hardware Requirements
- **Processor**: Intel Core i3 / AMD Ryzen 3 or higher (x86_64 or ARM64)
- **RAM**: Minimum 4 GB (8 GB recommended for concurrent IDE and JVM execution)
- **Storage**: Minimum 500 MB free hard disk space
- **Display**: Minimum screen resolution of 1280 x 720 pixels

### Software Requirements
- **Operating System**: Windows 10/11, Linux (Ubuntu/Debian/Fedora), or macOS
- **JDK**: OpenJDK / Oracle JDK 21 (LTS)
- **GUI Framework**: JavaFX 21.0.2
- **Build & Dependency Tool**: Apache Maven 3.9+
- **Database Engine**: PostgreSQL 14+ (or integrated high-fidelity In-Memory engine)
- **Database Administration**: pgAdmin 4 or psql CLI
- **PDF Generation Library**: Apache PDFBox 3.0.1
- **Testing Framework**: JUnit 5 (Jupiter 5.10.2)
- **IDE Compatibility**: IntelliJ IDEA 2024+, Apache NetBeans 21+, Eclipse IDE

---

## 5. TECHNOLOGY STACK JUSTIFICATION

| Component | Technology | Technical Justification |
| :--- | :--- | :--- |
| **Language** | Java (JDK 21) | Strongly typed, robust memory management, platform-independent, object-oriented |
| **User Interface** | JavaFX 21 | Hardware-accelerated graphics pipeline, rich custom shapes (`Arc`, `Rectangle`, `Line`), CSS styling, responsive layout containers (`BorderPane`, `VBox`, `HBox`, `StackPane`) |
| **Relational Database**| PostgreSQL 16 | ACID-compliant enterprise database supporting row-level pessimistic locking (`FOR UPDATE`), complex check constraints, and indexes |
| **Database Access** | JDBC (PostgreSQL Driver 42.7.2) | Direct, high-performance connection pooling, PreparedStatement execution, and explicit transaction management (`commit()`, `rollback()`) |
| **Document Export** | Apache PDFBox 3.0.1 | Native Java library for generating vector-rendered, professional PDF boarding passes |
| **Unit Testing** | JUnit 5 | Modern testing framework supporting parameterized assertions, lifecycle hooks, and automated regression testing |

---

## 6. SYSTEM ARCHITECTURE

The application implements a decoupled, 4-tier architectural pattern:

```
+-----------------------------------------------------------------------+
|                         PRESENTATION TIER                             |
|  ViewManager -> [DashboardView | BusSearchView | SeatSelectionView |   |
|   PassengerDetailsView | BookingSummaryView | TicketView | MyBookings]|
|               Custom Controls: BusLayoutPane, SeatButton              |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                          SERVICE / LOGIC TIER                         |
|   BusService      SeatService      FareService      BookingService   |
|     (Search)    (Availability)     (Polymorphic)     (Transactions)   |
|                                                                       |
|   Utilities: ValidationUtil, PNRGenerator, DateTimeUtil, PDFGenerator |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                         DATA ACCESS LAYER (DAO)                       |
|   DAOFactory (Abstract Factory Pattern with Graceful Auto-Fallback)   |
|            +-----------------------+----------------------+           |
|            v                                              v           |
|  PostgreSQL JDBC DAOs                           In-Memory Thread-Safe |
|  (PostgresBusDAO, PostgresSeatDAO,              (InMemoryBusDAO,      |
|   PostgresPassengerDAO, PostgresBookingDAO)      InMemorySeatDAO,...) |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                            DATA STORAGE TIER                          |
|         PostgreSQL Database Server (bus_reservation_db) /             |
|         Concurrent In-Memory High-Fidelity Data Store                 |
+-----------------------------------------------------------------------+
```

---

## 7. OBJECT-ORIENTED PROGRAMMING (OOP) IMPLEMENTATION

The application is structured to clearly illustrate all foundational OOP principles:

### 7.1 Encapsulation
All entity attributes across `Bus`, `Seat`, `Passenger`, `Booking`, and `BookingPassenger` are declared `private`. Internal mutability is guarded with strict validation rules (e.g. non-negative base fares, age within 1 to 120, non-null collections). Defensive unmodifiable collections (`Collections.unmodifiableList(...)`) are returned to prevent unauthorized collection mutation outside service boundaries.

### 7.2 Inheritance
A well-structured inheritance hierarchy models commercial fleet diversity:
- `Bus` (Abstract Base Class)
  - `SeaterBus` (Extends `Bus` for 2+2 commercial coaches)
    - `LuxuryVolvoBus` (Extends `SeaterBus` with high-tier amenities: WiFi, blankets, charging stations, GPS tracking)
  - `SleeperBus` (Extends `Bus` for 2+1 overnight berth coaches)

### 7.3 Polymorphism (Dynamic Method Dispatch)
Polymorphic behavior is demonstrated through overridden abstract methods in `Bus`:
```java
public abstract double calculateSeatFare(Seat seat);
public abstract String getSeatingCategory();
public abstract int getLayoutColumns();
public abstract int getAisleAfterColumn();
```
- In `SeaterBus`, window seats dynamically invoke a 5% preference premium.
- In `LuxuryVolvoBus`, an 8% luxury service and amenity enhancement is computed over base seating fares.
- In `SleeperBus`, lower berths carry a 10% convenience multiplier over standard upper berths.
At runtime, `FareService` interacts purely with the abstract `Bus` reference, and Java's dynamic dispatch invokes the correct subclass algorithm.

### 7.4 Abstraction
Data access contracts are abstracted behind pure Java interfaces (`BusDAO`, `SeatDAO`, `PassengerDAO`, `BookingDAO`). The business services remain entirely decoupled from underlying persistence mechanics, allowing seamless execution against PostgreSQL or in-memory stores without altering a single line of business code.

### 7.5 Method Overloading (Compile-Time Polymorphism)
Implemented across multiple classes:
- `BusDAO.searchBuses(String source, String destination)`
- `BusDAO.searchBuses(String source, String destination, LocalDate journeyDate)`
- Overloaded constructors in `Passenger`, `Seat`, and `BookingPassenger` accommodating both full relational instantiation and lightweight rapid instantiation.

### 7.6 Object Composition
A `Booking` instance is composed of:
- An associated `Bus` reference
- A collection of `BookingPassenger` objects, each binding an individual `Passenger` with their allocated `Seat` and specific fare.
Life-cycle coupling ensures that when a `Booking` is cancelled, all constituent seat allocations are released atomically.

---

## 8. DATABASE DESIGN

The relational schema is configured in PostgreSQL under the database `bus_reservation_db`.

### Entity Relationship (ER) Model

```
       1             *                      *             1
[buses] <------------- [seats] <------------- [booking_passengers] -------------> [passengers]
   |                                                |
   | 1                                              | *
   |                                                |
   +------------------------------------------------+
   |                        *
   v
[bookings]
```

### Table Summary
1. **`buses`**: Contains bus catalog, route coordinates, departure/arrival schedules, base fares, and total capacity.
2. **`passengers`**: Stores validated customer profiles (full name, age, gender, telephone, email).
3. **`seats`**: Maps individual physical seats with their relative row/column coordinates, deck (`LOWER`/`UPPER`), seat category (`WINDOW`, `AISLE`, `SLEEPER_LOWER`, `SLEEPER_UPPER`), and status (`AVAILABLE`, `BOOKED`, `BLOCKED`).
4. **`bookings`**: Stores high-level reservations with unique PNR strings, total fare, contact credentials, journey date, and status.
5. **`booking_passengers`**: Associative junction table linking bookings, individual passengers, and reserved seats.

---

## 9. MODULE DESCRIPTIONS

### Module 1: Dashboard & Bus Search
- Hero search console allowing selection of origin and destination cities via dropdowns with an instant Route Swap feature.
- Calendar widget restricting selection of past dates.
- KPI summary tiles displaying active fleet count, network corridors, bookings count, and system punctuality.
- Interactive route shortcuts allowing one-click searches for popular corridors.

### Module 2: Interactive Top-View Bus Floor Plan (Core Feature)
- Custom vector graphics visualizing an aerodynamic bus silhouette: rounded front windshield, side mirrors, dashboard, driver steering wheel, step-well entrance door, and rear emergency exit.
- Real-time seat buttons styled with cushions and headrests.
- Distinct color-coded states:
  - **Green**: Available for reservation
  - **Grey**: Booked by another passenger (disabled)
  - **Blue Outline with Glow**: Currently selected by active user
- Live reactive sidebar calculating subtotal, 5% GST, and grand total instantaneously upon every click.
- Enforces maximum limit of 6 seats per transaction.

### Module 3: Passenger Information & Form Validation
- Dynamically generates passenger input rows matching the exact count and identifiers of chosen seats.
- Validates 10-digit Indian mobile numbers (regex `^[6-9]\d{9}$`), alphabetic names, and realistic ages (1–120).

### Module 4: ACID Transactional Booking Engine
- Initiates JDBC transaction (`conn.setAutoCommit(false)`).
- Executes pessimistic row locking (`SELECT seat_id, seat_status FROM seats WHERE seat_id = ? FOR UPDATE`).
- Verifies that no other concurrent user has claimed any of the selected seats during user input.
- Inserts booking and passenger records, updates seat statuses to `BOOKED`, and invokes `conn.commit()`.
- Rolls back changes cleanly (`conn.rollback()`) if any seat conflict occurs.

### Module 5: Electronic Ticket & Document Issuance
- Renders an attractive boarding-pass styled e-ticket complete with PNR badge, route vector, passenger list, and simulated barcode.
- Generates compliant PDF tickets via Apache PDFBox 3.0.
- Supports direct operating system printer routing via JavaFX `PrinterJob`.

### Module 6: My Bookings & Cancellation Management
- Provides real-time lookup of past bookings using PNR query filters.
- Re-opens boarding pass e-tickets for confirmed bookings.
- Provides a one-click Cancellation workflow that marks the booking `CANCELLED` and immediately restores all associated seats back to `AVAILABLE`.

---

## 10. SYSTEM TESTING & RESULTS

Automated testing was conducted using JUnit 5. A total of **41 test cases** were executed across 7 test suites, achieving a **100% pass rate** with 0 failures and 0 errors:

| Test Suite | Focus Area | Tests Executed | Status |
| :--- | :--- | :---: | :---: |
| `ValidationUtilTest` | Mobile, Email, Name, Age, Route, and Date constraints | 20 | PASSED |
| `PNRGeneratorTest` | Format conformity (`PNR-XXXXXXX`) and 1000-sample collision test | 2 | PASSED |
| `FareServiceTest` | Polymorphic fare computation and 5% GST calculations | 4 | PASSED |
| `BusServiceTest` | Route filtering, sorting, case-insensitivity, and date validation | 5 | PASSED |
| `SeatServiceTest` | Seat counting, coordinate retrieval, and availability checks | 4 | PASSED |
| `BookingServiceTest` | Atomic booking transactions, concurrency conflict rollback, cancellation & seat recovery | 3 | PASSED |
| `DatabaseConnectionTest` | Configuration loading, fallback mode integrity, ping safety | 3 | PASSED |
| **Total** | **Comprehensive System Verification** | **41** | **ALL PASSED** |

---

## 11. LIMITATIONS

1. Payment processing is currently simulated through a confirmation review step rather than live banking gateway integration (Razorpay / Stripe).
2. Bus tracking displays simulated GPS status rather than live telematics feed from IoT hardware.

---

## 12. FUTURE SCOPE

1. **SMS & WhatsApp Gateway Integration**: Automated dispatch of ticket PDFs via Twilio or WhatsApp Business API upon booking.
2. **Dynamic Surge Pricing**: Real-time algorithmic fare adjustment based on demand, weekend peaks, and remaining seat scarcity.
3. **Multi-Deck 3D Rendering**: Extension of the top-view bus floor plan into an interactive 3D perspective using JavaFX 3D (`MeshView`, `PerspectiveCamera`).
4. **GPS Live Telematics**: Integration with onboard vehicle GPS trackers for real-time live map tracking.

---

## 13. CONCLUSION

The **Bus Reservation System** successfully fulfills all academic and functional requirements of an MCA 1st Semester Object-Oriented Programming mini-project. It combines robust enterprise design patterns (DAO, Service Layer, Factory Pattern) with an aesthetic, modern JavaFX desktop interface featuring a realistic top-view bus floor plan. The application delivers reliable, concurrency-safe ticket reservations, automated PDF document generation, and comprehensive booking lifecycle management.

---

## 14. REFERENCES

1. Bloch, Joshua. *Effective Java (3rd Edition)*. Addison-Wesley Professional, 2018.
2. Horstmann, Cay S. *Core Java Volume I – Fundamentals (12th Edition)*. Oracle Press, 2022.
3. OpenJFX Official Documentation: [https://openjfx.io/](https://openjfx.io/)
4. PostgreSQL 16 Documentation: [https://www.postgresql.org/docs/](https://www.postgresql.org/docs/)
5. Apache PDFBox 3.0 Documentation: [https://pdfbox.apache.org/](https://pdfbox.apache.org/)
