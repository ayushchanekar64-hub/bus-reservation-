# SYSTEM TEST SPECIFICATION & EXECUTION REPORT
## Bus Reservation System – MCA 1st Semester Mini-Project

---

## 1. TESTING OVERVIEW

- **Testing Framework**: JUnit 5 (Jupiter 5.10.2)
- **Execution Engine**: Maven Surefire Plugin 3.2.5
- **JDK Environment**: OpenJDK 21.0.8 (64-Bit)
- **Total Test Cases**: 41
- **Pass Rate**: 100% (41 Passed, 0 Failed, 0 Skipped, 0 Errors)
- **Execution Date**: 16 November 2026

---

## 2. DETAILED TEST MATRIX

| Test ID | Module / Class | Test Case Description | Input Data | Expected Result | Actual Result | Status |
| :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-01** | `DatabaseConnectionTest` | AppConfig loads default database parameters | `database.properties` | Properties not null, timeout > 0 | Matches configuration | **PASS** |
| **TC-02** | `DatabaseConnectionTest` | DBConnection handles offline PostgreSQL gracefully | `DBConnection.isAvailable()` | Returns `false` safely without unhandled exception | Handled without crashing | **PASS** |
| **TC-03** | `DatabaseConnectionTest` | In-memory fallback flag toggles cleanly | `setInMemoryFallback(true)` | `isInMemoryFallback() == true` | State preserved | **PASS** |
| **TC-04** | `BusServiceTest` | Search buses with valid route | Source: "Delhi", Dest: "Jaipur", Tomorrow | Returns list of 2 active buses sorted by time | 2 buses returned in order | **PASS** |
| **TC-05** | `BusServiceTest` | Route search is case-insensitive | Source: "delhi", Dest: "JAIPUR" | Matches "Delhi" -> "Jaipur" | Correctly matched | **PASS** |
| **TC-06** | `BusServiceTest` | Search with identical source and destination throws error | Source: "Delhi", Dest: "Delhi" | Throws `IllegalArgumentException` | Exception thrown | **PASS** |
| **TC-07** | `BusServiceTest` | Search with blank or null city throws error | Source: "", Dest: "Jaipur" | Throws `IllegalArgumentException` | Exception thrown | **PASS** |
| **TC-08** | `BusServiceTest` | Search with past journey date throws error | Date: Yesterday | Throws `IllegalArgumentException` | Exception thrown | **PASS** |
| **TC-09** | `BusServiceTest` | Distinct origin & destination lists retrieval | DAO query | Returns non-empty, distinct lists | Distinct cities returned | **PASS** |
| **TC-10** | `SeatServiceTest` | Retrieve all physical seats for a bus | `busId = 1` | Returns all mapped seats | 3 seats returned | **PASS** |
| **TC-11** | `SeatServiceTest` | Count available seats excluding booked ones | `busId = 1` (2 available, 1 booked) | Count == 2 | Count == 2 | **PASS** |
| **TC-12** | `SeatServiceTest` | Validate availability when all selected seats are free | Seats: "1A", "1W" | Returns `true` | Returns `true` | **PASS** |
| **TC-13** | `SeatServiceTest` | Validate availability fails if any seat is already booked | Seats: "1A", "2A" (booked) | Returns `false` | Returns `false` | **PASS** |
| **TC-14** | `FareServiceTest` | SeaterBus standard aisle seat fare | Base: 500, Seat: Aisle | Fare == 500.00 | Fare == 500.00 | **PASS** |
| **TC-15** | `FareServiceTest` | SeaterBus window seat 5% premium | Base: 500, Seat: Window | Fare == 525.00 | Fare == 525.00 | **PASS** |
| **TC-16** | `FareServiceTest` | LuxuryVolvoBus 8% comfort surcharge | Base: 600, Seat: Aisle | Fare == 648.00 | Fare == 648.00 | **PASS** |
| **TC-17** | `FareServiceTest` | SleeperBus lower berth 10% premium | Base: 1000, Seat: Lower | Fare == 1100.00 | Fare == 1100.00 | **PASS** |
| **TC-18** | `FareServiceTest` | SleeperBus upper berth standard pricing | Base: 1000, Seat: Upper | Fare == 1000.00 | Fare == 1000.00 | **PASS** |
| **TC-19** | `FareServiceTest` | 5% GST computation and grand total | Subtotal: 1000.00 | GST == 50.00, Total == 1050.00 | GST: 50.0, Total: 1050.0 | **PASS** |
| **TC-20** | `PNRGeneratorTest` | PNR pattern format verification | `generatePNR()` | Starts with "PNR-", length 11 | Pattern matches `PNR-\d{7}` | **PASS** |
| **TC-21** | `PNRGeneratorTest` | PNR collision & uniqueness test | 1,000 continuous generations | Set size == 1,000 | 1,000 unique values | **PASS** |
| **TC-22** | `BookingServiceTest` | Atomic booking marks seats BOOKED | 2 passengers, Seats 101, 102 | Status CONFIRMED, Seats BOOKED | Seats updated in store | **PASS** |
| **TC-23** | `BookingServiceTest` | Concurrency check prevents duplicate seat booking | 2 concurrent requests on Seat 101 | 2nd request throws `IllegalStateException` | Rolled back, exception thrown | **PASS** |
| **TC-24** | `BookingServiceTest` | Booking cancellation updates status | Valid PNR | Status changed to CANCELLED | Status == CANCELLED | **PASS** |
| **TC-25** | `BookingServiceTest` | Booking cancellation restores seats to AVAILABLE | Valid PNR | Associated seats reset to AVAILABLE | Status == AVAILABLE | **PASS** |
| **TC-26** | `ValidationUtilTest` | Valid Indian mobile number format | "9876543210", "8123456789" | Returns `true` | Returns `true` | **PASS** |
| **TC-27** | `ValidationUtilTest` | Invalid mobile numbers rejected | "1234567890", "98765", "abc" | Returns `false` | Returns `false` | **PASS** |
| **TC-28** | `ValidationUtilTest` | Valid passenger full name | "Aarav Sharma", "John Doe" | Returns `true` | Returns `true` | **PASS** |
| **TC-29** | `ValidationUtilTest` | Numeric/short names rejected | "", "123", "A" | Returns `false` | Returns `false` | **PASS** |
| **TC-30** | `ValidationUtilTest` | Valid passenger ages | 1, 25, 120 | Returns `true` | Returns `true` | **PASS** |
| **TC-31** | `ValidationUtilTest` | Out of bounds passenger ages | 0, -5, 121 | Returns `false` | Returns `false` | **PASS** |
| **TC-32** | `ValidationUtilTest` | Valid email formats | "user@example.com" | Returns `true` | Returns `true` | **PASS** |
| **TC-33** | `ValidationUtilTest` | Empty or null email is accepted (optional field) | "", null | Returns `true` | Returns `true` | **PASS** |
| **TC-34** | `ValidationUtilTest` | Malformed email rejected | "user@", "user.com" | Returns `false` | Returns `false` | **PASS** |
| **TC-35** | `ValidationUtilTest` | Valid future journey date | `LocalDate.now().plusDays(5)` | Returns `true` | Returns `true` | **PASS** |
| **TC-36** | `ValidationUtilTest` | Past journey date rejected | `LocalDate.now().minusDays(1)` | Returns `false` | Returns `false` | **PASS** |
| **TC-37** | `ValidationUtilTest` | Null journey date rejected | `null` | Returns `false` | Returns `false` | **PASS** |
| **TC-38** | `ValidationUtilTest` | Distinct route validated | "Delhi" -> "Jaipur" | Returns `true` | Returns `true` | **PASS** |
| **TC-39** | `ValidationUtilTest` | Same city route rejected | "Pune" -> "Pune" | Returns `false` | Returns `false` | **PASS** |
| **TC-40** | `ValidationUtilTest` | Null route arguments rejected | null -> "Goa" | Returns `false` | Returns `false` | **PASS** |
| **TC-41** | `ValidationUtilTest` | Empty route arguments rejected | "" -> "Goa" | Returns `false` | Returns `false` | **PASS** |

---

## 3. TEST EXECUTION COMMANDS

To execute the complete test suite at any time from terminal or command prompt:

```powershell
# Using the bundled Maven Wrapper
.\mvnw.cmd test

# Using standard Maven
mvn test
```
