# OBJECT-ORIENTED PROGRAMMING (OOP) IMPLEMENTATION GUIDE
## College Viva & Project Defense Reference

This document provides direct mapping between academic Object-Oriented Programming (OOP) concepts and the actual codebase implementations in the **Bus Reservation System**.

---

## 1. ENCAPSULATION

### Concept:
Encapsulation is the mechanism of wrapping data (variables) and code acting on the data (methods) together as a single unit, restricting direct outside access to prevent unintended state modification.

### In Our Project:
- All entity attributes in `model.Bus`, `model.Seat`, `model.Passenger`, and `model.Booking` are strictly declared `private`.
- Public getter and setter methods enforce business validation rules before updating state.
- Defensive unmodifiable collections are returned to prevent external tampering of internal lists.

```java
// Example from Passenger.java
public class Passenger {
    private int passengerId;
    private String fullName;
    private int age;
    private String mobileNumber;

    // Mutator with validation
    public void setAge(int age) {
        if (age < 1 || age > 120) {
            throw new IllegalArgumentException("Age must be between 1 and 120");
        }
        this.age = age;
    }

    public int getAge() {
        return this.age;
    }
}
```

---

## 2. INHERITANCE

### Concept:
Inheritance is the mechanism by which one class acquires the properties and behaviors (fields and methods) of a parent class, promoting code reusability and establishing an "IS-A" relationship.

### In Our Project:
We utilize both **Hierarchical Inheritance** and **Multilevel Inheritance**:

```
                  +---------------------------+
                  |         abstract          |
                  |         Bus.java          |
                  +---------------------------+
                                |
               +----------------+----------------+
               |                                 |
               v                                 v
    +--------------------+             +--------------------+
    |   SeaterBus.java   |             |   SleeperBus.java  |
    +--------------------+             +--------------------+
               |
               v
    +--------------------+
    | LuxuryVolvoBus.java|
    +--------------------+
```

1. **`Bus` (Abstract Base Class)**: Defines core fleet attributes (`busId`, `busNumber`, `source`, `destination`, `departureTime`, `baseFare`, `totalSeats`).
2. **`SeaterBus` extends `Bus`**: Implements 2+2 layout specifications and seater fare logic.
3. **`LuxuryVolvoBus` extends `SeaterBus`**: Multilevel inheritance adding premium amenities (WiFi, water, blanket, GPS) and luxury comfort adjustments.
4. **`SleeperBus` extends `Bus`**: Implements 2+1 layout specifications and upper/lower berth pricing.

---

## 3. POLYMORPHISM (DYNAMIC METHOD DISPATCH)

### Concept:
Polymorphism allows one interface or method signature to have multiple distinct implementations, resolved dynamically at runtime based on the actual object instance.

### In Our Project:
The abstract method `calculateSeatFare(Seat seat)` in `Bus` is polymorphically overridden:

```java
// In Bus.java (Abstract Template)
public abstract double calculateSeatFare(Seat seat);

// In SeaterBus.java: 5% premium on Window seats
@Override
public double calculateSeatFare(Seat seat) {
    double mult = seat.getFareModifier();
    if (seat.getSeatType() == SeatType.WINDOW) {
        mult *= 1.05;
    }
    return Math.round(getBaseFare() * mult * 100.0) / 100.0;
}

// In LuxuryVolvoBus.java: 8% luxury service and amenity enhancement
@Override
public double calculateSeatFare(Seat seat) {
    double standard = super.calculateSeatFare(seat);
    return Math.round(standard * 1.08 * 100.0) / 100.0;
}

// In SleeperBus.java: 10% premium on Lower Berths
@Override
public double calculateSeatFare(Seat seat) {
    double mult = seat.getFareModifier();
    if (seat.getSeatType() == SeatType.SLEEPER_LOWER) {
        mult *= 1.10;
    }
    return Math.round(getBaseFare() * mult * 100.0) / 100.0;
}
```

### Polymorphic Invocation in FareService:
```java
// FareService does not need to know the specific type of bus!
public double calculateSeatFare(Bus bus, Seat seat) {
    return bus.calculateSeatFare(seat); // Runtime Dynamic Dispatch
}
```

---

## 4. ABSTRACTION

### Concept:
Abstraction is the process of hiding implementation details and showing only essential features to the user or calling code.

### In Our Project:
1. **Abstract Classes**: `Bus.java` cannot be instantiated directly; it serves as a conceptual blueprint defining template methods.
2. **Interface Contracts**: All data access is abstracted behind pure interfaces:
   - `BusDAO`, `SeatDAO`, `PassengerDAO`, `BookingDAO`
   Calling services (`BusService`, `SeatService`, `BookingService`) interact purely with the interfaces. The implementation can switch between PostgreSQL JDBC (`PostgresBusDAO`) and In-Memory collections (`InMemoryBusDAO`) without requiring any change in business logic.

---

## 5. METHOD OVERLOADING (COMPILE-TIME POLYMORPHISM)

### Concept:
Multiple methods having the exact same name within the same class but differing in their parameter list (number, order, or types of parameters).

### In Our Project:
```java
// In BusDAO.java & BusService.java:
List<Bus> searchBuses(String source, String destination);
List<Bus> searchBuses(String source, String destination, LocalDate journeyDate);

// Overloaded Constructors in Passenger.java:
public Passenger(String fullName, int age, String gender, String mobileNumber);
public Passenger(int id, String fullName, int age, String gender, String mobileNumber, String email);
```

---

## 6. COMPOSITION (HAS-A RELATIONSHIP)

### Concept:
Composition is a strong form of association where a container object owns one or more component objects, and the component objects cannot logically exist without their container.

### In Our Project:
A `Booking` object is composed of:
- An associated `Bus` reference
- A collection of `BookingPassenger` objects, each binding a `Passenger` profile with their specific `Seat`.

```java
public class Booking {
    private String pnrNumber;
    private Bus bus; // HAS-A Bus
    private final List<BookingPassenger> bookingPassengers = new ArrayList<>(); // HAS-MANY BookingPassengers

    public void addBookingPassenger(BookingPassenger bp) {
        this.bookingPassengers.add(bp);
    }
}
```

---

## 7. DESIGN PATTERNS EMPLOYED

| Design Pattern | Category | Application in Bus Reservation System |
| :--- | :--- | :--- |
| **DAO Pattern** | Architectural | Decouples persistence operations from domain and UI logic (`BusDAO`, `SeatDAO`, etc.) |
| **Factory Pattern** | Creational | `DAOFactory` dynamically instantiates PostgreSQL or In-Memory DAOs |
| **Polymorphic Factory**| Creational | `PostgresBusDAO.mapResultSetToBus()` dynamically constructs `SeaterBus`, `LuxuryVolvoBus`, or `SleeperBus` based on database column values |
| **MVC Pattern** | Architectural | Separation of Model (`com.busreservation.model`), View (`com.busreservation.view`), and Controller/Service (`com.busreservation.service`) |
| **Observer Pattern** | Behavioral | Reactive event handlers in JavaFX for instant UI re-computation on seat click |
