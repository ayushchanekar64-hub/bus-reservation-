# TOP 30 VIVA & INTERVIEW QUESTIONS WITH MODEL ANSWERS
## Master of Computer Applications (MCA) – Bus Reservation System

---

### PART 1: CORE JAVA & OOP CONCEPTS

#### Q1. How is Polymorphism demonstrated in your project?
**Answer:** Polymorphism is primarily demonstrated through the abstract method `calculateSeatFare(Seat seat)` defined in the abstract base class `Bus`. The subclasses `SeaterBus`, `LuxuryVolvoBus`, and `SleeperBus` each provide their own implementation of this method (e.g. `SeaterBus` adds a 5% premium for window seats, `LuxuryVolvoBus` adds an 8% luxury comfort enhancement, and `SleeperBus` adds a 10% premium for lower berths). At runtime, `FareService` calls `bus.calculateSeatFare(seat)` through an abstract `Bus` reference, and JVM dynamic method dispatch automatically executes the appropriate subclass implementation.

#### Q2. What is the difference between Method Overloading and Method Overriding in your project?
**Answer:**
- **Method Overloading (Compile-Time Polymorphism)** occurs when methods share the same name but have different parameter lists. In our project, `BusDAO` overloads `searchBuses(source, destination)` and `searchBuses(source, destination, journeyDate)`.
- **Method Overriding (Runtime Polymorphism)** occurs when a subclass provides a specific implementation of a method declared in its parent class with the exact same name, return type, and parameter list (e.g., `calculateSeatFare()` in `SeaterBus` and `SleeperBus`).

#### Q3. Why did you make `Bus` an abstract class instead of an interface or concrete class?
**Answer:** `Bus` represents a conceptual entity that should never be directly instantiated on its own (a bus in real life is always of a specific physical type, like a Seater or Sleeper). Making it an abstract class allows it to share common state and concrete implementations (like `busId`, `busNumber`, `source`, `destination`, and getters/setters) while enforcing that every subclass must implement mandatory polymorphic methods like `calculateSeatFare()` and `getSeatingCategory()`.

#### Q4. How have you implemented Encapsulation in your entities?
**Answer:** All field attributes in `Bus`, `Seat`, `Passenger`, and `Booking` are declared `private`. Access is restricted via public getters and setters. Setters contain validation logic (such as checking that passenger age is between 1 and 120, and base fare is not negative). In addition, `Collections.unmodifiableList()` is used to return defensive read-only copies of collections like `amenities` and `bookingPassengers`.

#### Q5. What is the difference between Composition and Aggregation, and which one is used here?
**Answer:** Both represent "HAS-A" relationships. In Aggregation, child objects can exist independently of the parent. In Composition, child objects are strongly owned by the parent and their lifecycle depends on it. In our project, `Booking` uses **Composition**: it contains `List<BookingPassenger>`, which binds passengers and seats specifically for that reservation. When a booking is cancelled or removed, its specific passenger-seat allocation instances are directly affected.

#### Q6. What is the DAO Pattern and why is it used?
**Answer:** The Data Access Object (DAO) pattern isolates the application/business layer from the persistence layer (PostgreSQL or In-Memory) using interfaces. It ensures separation of concerns. If the database schema or database provider changes in the future, only the DAO classes need to be updated; no business service or UI code is impacted.

#### Q7. What design pattern did you use to instantiate your DAOs?
**Answer:** We used the **Abstract Factory Pattern** in `DAOFactory.java`. At startup, `DAOFactory` checks whether PostgreSQL is online. If yes, it creates PostgreSQL JDBC DAOs; if not, it gracefully creates In-Memory DAOs populated with realistic sample data.

---

### PART 2: DATABASE, SQL & JDBC

#### Q8. How do you prevent double-booking when two users attempt to book the exact same seat simultaneously?
**Answer:** We use **ACID Transactions** with **Pessimistic Row Locking**:
1. Disable auto-commit: `conn.setAutoCommit(false)`.
2. Query candidate seats with row-level locks: `SELECT seat_id, seat_status FROM seats WHERE seat_id = ? FOR UPDATE`.
3. In PostgreSQL, `FOR UPDATE` locks those specific seat rows exclusively. If another transaction tries to access them, it must wait.
4. If our transaction discovers that the seat status is already `BOOKED`, we immediately call `conn.rollback()` and notify the user.
5. If all seats are available, we insert the booking and passenger records, update the seat status to `BOOKED`, and invoke `conn.commit()`.

#### Q9. Why did you use `PreparedStatement` instead of `Statement`?
**Answer:**
1. **Security**: `PreparedStatement` automatically sanitizes input parameters, completely preventing SQL Injection attacks.
2. **Performance**: The SQL statement is pre-compiled by the database engine, and the execution plan is cached and reused.
3. **Type Safety**: Parameters are mapped to strong Java SQL types (`setInt`, `setString`, `setDate`, `setTime`).

#### Q10. What happens in the database when a booking is cancelled?
**Answer:** In an atomic transaction, the status of the booking in the `bookings` table is updated to `'CANCELLED'`, and a subquery restores the associated seat statuses in the `seats` table back to `'AVAILABLE'` (`UPDATE seats SET seat_status = 'AVAILABLE' WHERE seat_id IN (SELECT seat_id FROM booking_passengers WHERE booking_id = ?)`).

#### Q11. What constraints are applied on your database tables?
**Answer:**
- **Primary Keys**: `bus_id`, `seat_id`, `passenger_id`, `booking_id`, `booking_passenger_id`.
- **Foreign Keys**: `seats.bus_id -> buses.bus_id`, `bookings.bus_id -> buses.bus_id`, `booking_passengers.seat_id -> seats.seat_id`.
- **Unique Constraints**: `buses.bus_number`, `bookings.pnr_number`, and composite unique `(bus_id, seat_number)` on `seats`.
- **Check Constraints**: `chk_source_dest` (source != destination), `age BETWEEN 1 AND 120`, `fare > 0`, and status enumerations.

#### Q12. Why did you create indexes on `(source, destination)` and `pnr_number`?
**Answer:** Without indexes, searches by route or PNR would perform full table scans ($O(N)$). B-Tree indexes on `(LOWER(source), LOWER(destination))` and `pnr_number` reduce search lookup time to $O(\log N)$, allowing instant response times even with millions of records.

---

### PART 3: JAVAFX & USER INTERFACE

#### Q13. How did you create the realistic top-view bus floor plan without using static rectangular grids?
**Answer:** In `BusLayoutPane.java`, we combined JavaFX visual primitives:
- An aerodynamic bus hull with rounded corners (`-fx-border-radius: 36px 36px 16px 16px`).
- An `Arc` shape with a translucent blue fill representing the curved front windshield.
- Side mirrors modeled with `Rectangle` shapes on the exterior sides.
- A driver cabin with an entrance step-well and a steering wheel constructed from concentric `Circle` and `Line` spoke shapes.
- A dashed `Line` representing the central aisle.
- An emergency exit indicator at the rear.
- Custom `SeatButton` components with separate headrest and cushion rectangles.

#### Q14. What are the three visual color states of a seat in your application?
**Answer:**
- **Green (`#22C55E` / `#DCFCE7`)**: Available seat.
- **Grey (`#9CA3AF` / `#E2E8F0`)**: Already booked seat (disabled, cannot be clicked).
- **Blue Outline with Glow (`#1D4ED8` / `#3B82F6`)**: Currently selected by the user.

#### Q15. How does the total fare update immediately when a seat is clicked?
**Answer:** The `SeatButton` registers an `onMouseClicked` event handler. When clicked, it toggles its selection state, updates its styling, and triggers a callback consumer in `SeatSelectionView`. The view updates its `selectedSeats` list and invokes `FareService.calculateGrandTotal(bus, selectedSeats)`, which instantly re-renders the subtotal, 5% GST, and grand total labels.

#### Q16. How does your application navigate between screens?
**Answer:** We implemented a centralized `ViewManager.java` class following the Page Router pattern. The primary scene has a `BorderPane` with a persistent top `NavbarComponent`. When navigating (e.g. `showDashboard()`, `showBusSearch()`, `showSeatSelection()`), `ViewManager` replaces the center node of the `BorderPane` with the requested view while carrying forward journey and selection state.

---

### PART 4: TESTING & MISCELLANEOUS

#### Q17. How did you generate the unique PNR?
**Answer:** In `PNRGenerator.java`, we use `java.security.SecureRandom` to generate a 7-digit random sequence prefixed with `PNR-` (e.g. `PNR-8849201`). In our unit test suite `PNRGeneratorTest`, 1,000 continuous generations were verified with 0 collisions.

#### Q18. How is the PDF ticket generated?
**Answer:** We integrated **Apache PDFBox 3.0**. In `PDFGenerator.java`, a new `PDDocument` is instantiated with an A4 `PDPage`. Using `PDPageContentStream`, vector rectangles and text elements (header banner, PNR, route, passenger details table, fare summary, and boarding terms) are drawn and written directly to a `.pdf` file.

#### Q19. How did you test your project?
**Answer:** We wrote 41 unit and integration tests using **JUnit 5**, covering input validation, PNR format & uniqueness, bus searching, seat status queries, polymorphic fare calculations, atomic booking transactions, duplicate booking prevention, and booking cancellation. All 41 tests pass with a 100% success rate.

#### Q20. What is your fallback mechanism if PostgreSQL is not installed or running?
**Answer:** In `AppConfig.java`, `db.mode` is set to `auto`. At startup, `DBConnection.isAvailable()` pings the database. If unreachable, `DAOFactory` activates the thread-safe `InMemory*DAO` suite preloaded with identical realistic routes, buses, and seats. A visual status badge on the top navbar informs the user ("Mode: Standalone / In-Memory Demo").
