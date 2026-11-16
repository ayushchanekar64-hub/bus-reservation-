# USER GUIDE & OPERATING MANUAL
## Bus Reservation System (Desktop Client)

---

## 1. GETTING STARTED

### 1.1 Prerequisites
- **Java Development Kit (JDK)**: Version 21 or higher
- **Maven**: Version 3.9+ (or use the included `mvnw.cmd` wrapper)

### 1.2 Quick Launch
Run the application using the Maven JavaFX plugin:

```powershell
# Open terminal in the project root directory
.\mvnw.cmd javafx:run
```

The application window will open automatically in high resolution (1180 x 800) with modern styling.

---

## 2. APPLICATION WORKFLOW & STEP-BY-STEP GUIDE

### Step 1: Welcome & Search Buses
1. On the **Dashboard**, choose your origin city from the **From** dropdown (e.g. *Delhi*).
2. Choose your destination city from the **To** dropdown (e.g. *Jaipur*).
   - Use the **Swap (\u21C4)** button to quickly switch origin and destination.
3. Select your travel date using the **Journey Date** calendar picker. Past dates are automatically disabled.
4. Click **Search Buses \u2192**. Alternatively, click any popular route card (e.g. *Mumbai \u2192 Pune*).

### Step 2: Available Buses Screen
1. Browse matching intercity buses. Each bus card displays:
   - Bus Operator name and Registration Number
   - Category badge (*Volvo Multi-Axle*, *AC Sleeper*, *Electric AC*)
   - Departure and Arrival times with journey duration
   - Base fare starting price
   - Available seats count
   - Included onboard amenities (WiFi, Charging ports, Water, Blanket)
2. Click **Select Seats \u2192** on your preferred bus.

### Step 3: Interactive Top-View Bus Seat Selection
1. The screen displays the realistic bus interior from a **top view**:
   - Aerodynamic curved front windshield and side mirrors
   - Driver cabin with steering wheel and front entrance door
   - Central aisle dividing left and right seat rows
   - Rear emergency exit
2. **Seat Color Legend**:
   - **Green**: Available for selection. Hover shows seat number, type, and fare tooltip.
   - **Grey**: Booked by someone else. Cannot be clicked.
   - **Blue with Glow**: Selected by you.
3. Click on any green seat to select it. Click again to deselect.
   - Notice that the **Fare Breakdown** in the right sidebar updates instantaneously (Base Fare + 5% GST = Total Payable).
4. You can select up to **6 seats** per booking transaction.
5. Once satisfied, click **Proceed to Passenger Details \u2192**.

### Step 4: Passenger Information
1. The form dynamically displays one entry card for each seat you selected.
2. For each passenger, enter:
   - **Full Name** (alphabetical letters)
   - **Age** (between 1 and 120)
   - **Gender** (Male, Female, or Other)
3. Enter your **10-digit primary mobile number** and optional email for SMS and ticket confirmation.
4. Click **Review & Confirm Booking \u2192**.

### Step 5: Booking Review & Confirmation
1. Review the trip overview, bus number, departure time, and passenger roster.
2. Review the final fare breakdown.
3. Click **Confirm Booking & Issue Ticket \u2192**.
4. The system executes an atomic transaction, reserving your seats and generating a unique PNR.

### Step 6: Boarding Pass & Electronic Ticket
1. An official boarding-pass style ticket appears on screen with:
   - Unique PNR (e.g. `PNR-8849201`)
   - Route and schedule overview
   - Passenger seat allocations
   - Simulated barcode
2. **Actions available**:
   - **Download Ticket (PDF)**: Saves a vector-rendered PDF ticket to your computer using Apache PDFBox.
   - **Print Ticket**: Sends the ticket to your physical printer or Microsoft Print to PDF.
   - **Return to Dashboard**: Navigates back to the main console.

### Step 7: Manage & Cancel Bookings
1. Click **My Bookings** on the top navigation bar.
2. Browse your past and current trips or search by PNR number.
3. Click **View E-Ticket** to review and reprint any confirmed ticket.
4. To cancel a trip, click **Cancel Booking**.
   - A confirmation dialog appears.
   - Upon confirming, the status changes to `CANCELLED` and all booked seats are immediately released back to `AVAILABLE`.
