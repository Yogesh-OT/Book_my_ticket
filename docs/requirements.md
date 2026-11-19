# Requirements Specification: Book My Ticket

**Course:** 22IT321 Object Oriented Programming  
**Project Title:** Book My Ticket – Movie Ticket Booking System  
**Architecture:** Desktop GUI (Java Swing)  

---

## 1. Project Overview
"Book My Ticket" is a lightweight desktop movie ticket booking application developed in pure Java (JDK 17+) using Swing. It provides intuitive, real-time ticket booking for cinema patrons and a comprehensive movie and reservation management suite for theater administrators. The application persists state locally using flat file storage (CSV format) without requiring any external relational databases or web servers.

---

## 2. Actors
1. **Customer (Patron):**
   - Authenticates by providing Name and a 10-digit Phone Number.
   - Browses current movies and available screening times.
   - Selects theater seats from an interactive grid (Regular vs. Premium).
   - Confirms ticket reservations and receives formatted booking confirmation slips.
   - Views personal booking history and cancels existing reservations.
2. **Administrator:**
   - Authenticates using fixed administrative credentials (`admin123`).
   - Adds new movies along with metadata, base pricing, and show schedules.
   - Removes obsolete or discontinued movies.
   - Audits and monitors all customer bookings system-wide.

---

## 3. Functional Requirements (FR)

| Req ID | Requirement Description | Actor |
|---|---|---|
| **FR-01** | **User Authentication & Role Selection:** The system shall present a unified login interface allowing selection between Customer and Admin modes. Customers provide Name and a valid 10-digit Phone Number. Admins provide a password. | Customer / Admin |
| **FR-02** | **Movie Catalog Display:** The system shall display all available movies with Title, Genre, Duration (minutes), Language, Base Price, and Screening Times in a clean tabular view. | Customer |
| **FR-03** | **Interactive Seat Matrix:** The system shall render a 5x8 grid (Rows A–E, Columns 1–8) representing theater seats. Rows A–C are classified as Regular seats, and Rows D–E as Premium seats. Booked seats must be visually distinct and disabled. | Customer |
| **FR-04** | **Ticket Price Calculation:** The system shall dynamically compute total booking charges based on seat tiers (Regular: Base Price; Premium: Base Price + Rs. 50 surcharge) using polymorphic price evaluation. | Customer |
| **FR-05** | **Thread-Safe Reservation & Confirmation:** The system shall validate availability, prevent race conditions through synchronized methods, generate a unique Booking ID (e.g. `BMT-1001`), display a ticket dialog, and persist booking records asynchronously via background worker threads (`SwingWorker`). | Customer |
| **FR-06** | **Booking Cancellation & Seat Deallocation:** The system shall allow customers to view personal bookings and cancel any confirmed ticket, immediately restoring the released seats to the available pool. | Customer |
| **FR-07** | **Movie Catalog Management:** The system shall allow administrators to dynamically create new movies with multiple show timings and remove existing movies from the active schedule. | Administrator |
| **FR-08** | **System-Wide Booking Audit:** The system shall provide administrators with a consolidated table of all ticket bookings, including customer details, seat lists, total amounts, and statuses. | Administrator |
| **FR-09** | **Data Persistence & Auto-Seeding:** The system shall automatically seed initial catalog data if missing, and persist movies (`data/movies.txt`) and bookings (`data/bookings.txt`) across application sessions. | System |

---

## 4. Non-Functional Requirements (NFR)

| Req ID | Category | Specification |
|---|---|---|
| **NFR-01** | **Platform Independence** | Pure Java SE (JDK 17+) implementation with zero third-party dependencies or external libraries; executable across Windows, Linux, and macOS. |
| **NFR-02** | **Responsiveness & Threading** | Asynchronous disk persistence via `SwingWorker` ensures that file I/O operations never block the Swing Event Dispatch Thread (EDT) or cause UI stutter. |
| **NFR-03** | **Data Integrity & Concurrency** | Synchronized reservation logic guarantees that multiple threads or rapid concurrent actions cannot double-book the same seat. |
| **NFR-04** | **Fault Tolerance & Validation** | Robust input verification with custom checked exceptions (`InvalidInputException`, `SeatAlreadyBookedException`) and friendly graphical alerts (`JOptionPane`). |
| **NFR-05** | **Persistence & Portability** | Human-readable CSV flat files located in the `data/` directory ensure effortless backup, inspection, and manual verification. |
| **NFR-06** | **Usability & Aesthetics** | Consistent corporate typography (Segoe UI), polished color palette (Navy header, Slate body, Emerald confirm), clear visual hierarchy, and intuitive button states. |

---

## 5. Software Engineering Use Cases

### Use Case 1: UC-01 User Authentication (Customer / Admin Login)
- **Use Case ID:** UC-01
- **Name:** Authenticate User
- **Primary Actor:** Customer / Administrator
- **Preconditions:** Application is launched and `LoginFrame` is visible.
- **Main Flow (Customer):**
  1. User selects "Customer" account type.
  2. User enters Full Name (e.g., "Alex Johnson") and 10-digit Phone Number (e.g., "9876543210").
  3. User clicks "Continue to Book My Ticket".
  4. System validates inputs.
  5. System initializes `Customer` session and opens `MovieListFrame`.
  6. System disposes `LoginFrame`.
- **Main Flow (Admin):**
  1. User selects "Admin" account type.
  2. User enters the Administrator Password (`admin123`).
  3. User clicks "Continue to Book My Ticket".
  4. System validates password against `Admin.DEFAULT_ADMIN_PASSWORD`.
  5. System opens `AdminFrame`.
  6. System disposes `LoginFrame`.
- **Alternate Flow (A1 - Blank Customer Fields):**
  - If name or phone number is empty, system throws `InvalidInputException` and presents an error dialog: *"Customer name cannot be empty"* or *"Phone number cannot be empty"*. Flow returns to Step 2.
- **Alternate Flow (A2 - Invalid Phone Format):**
  - If phone number is not exactly 10 digits, system throws `InvalidInputException` and displays: *"Invalid phone number. Must be exactly 10 digits"*.
- **Alternate Flow (A3 - Incorrect Admin Password):**
  - If password does not match `admin123`, system displays: *"Incorrect admin password! Check README for default credentials"*.
- **Postconditions:** User is authenticated and granted access to the corresponding portal.

---

### Use Case 2: UC-02 Browse Movies and Select Show
- **Use Case ID:** UC-02
- **Name:** Browse Movies and Select Show
- **Primary Actor:** Customer
- **Preconditions:** Customer is authenticated and `MovieListFrame` is open.
- **Main Flow:**
  1. Customer views the movie catalog table displaying Title, Genre, Duration, Language, Base Price, and available Shows.
  2. Customer clicks on a desired movie row (triggered via `ListSelectionListener`).
  3. System updates the show timing dropdown combo box with the screening times for the selected movie.
  4. Customer chooses a preferred show timing from the dropdown.
  5. Customer clicks "Select Seats ->".
  6. System opens `SeatSelectionFrame` initialized with the chosen movie and show.
- **Alternate Flow (A1 - No Movie Selected):**
  - If customer clicks "Select Seats ->" without selecting a row, system displays a warning: *"Please select a movie from the table above."*
- **Postconditions:** `SeatSelectionFrame` is displayed with current seating availability.

---

### Use Case 3: UC-03 Select Seats and Confirm Ticket Booking
- **Use Case ID:** UC-03
- **Name:** Reserve Seats and Generate Ticket
- **Primary Actor:** Customer
- **Preconditions:** `SeatSelectionFrame` is open for a valid movie and show.
- **Main Flow:**
  1. Customer reviews the 5x8 seating layout and screen guide.
  2. Customer clicks available seat buttons (Rows A–C for Regular, Rows D–E for Premium).
  3. System toggles button colors to Emerald Green (Selected) and updates total payable amount dynamically using overloaded `calculateTotal(selectedSeats)`.
  4. Customer clicks "Confirm Booking".
  5. System displays a confirmation dialog with complete booking breakdown (Movie, Time, Seats, Total Price).
  6. Customer clicks "Yes" to confirm.
  7. System invokes synchronized `bookingService.bookSeats(...)`.
  8. System marks seats as booked in the show object.
  9. System generates unique Booking ID (e.g. `BMT-1001`), creates `Booking` entity, and queues asynchronous disk persistence via `SwingWorker`.
  10. System presents a graphical Ticket dialog showing full booking details.
  11. Customer closes ticket dialog and returns to `MovieListFrame`.
- **Alternate Flow (A1 - Zero Seats Selected):**
  - If customer clicks "Confirm Booking" without selecting any seat, system throws `InvalidInputException` and shows warning dialog: *"No seats selected! Please select at least one seat before booking."*
- **Alternate Flow (A2 - Seat Already Booked / Race Condition):**
  - If a selected seat is already occupied, system catches `SeatAlreadyBookedException`, presents an error alert, re-renders the seat matrix with updated disabled states, and clears invalid selections.
- **Postconditions:** Seats are reserved, booking is persistently written to `data/bookings.txt`, and confirmation ticket is generated.

---

### Use Case 4: UC-04 View Personal Bookings and Cancel Reservation
- **Use Case ID:** UC-04
- **Name:** View Bookings and Cancel Ticket
- **Primary Actor:** Customer
- **Preconditions:** Customer is on `MovieListFrame` and clicks "My Bookings".
- **Main Flow:**
  1. System displays `MyBookingsFrame` listing all bookings matching the customer's phone number.
  2. Customer selects a booking row in the table.
  3. Customer clicks "Cancel Selected Booking".
  4. System prompts confirmation dialog: *"Are you sure you want to cancel booking #BMT-XXXX?"*
  5. Customer selects "Yes".
  6. System executes `bookingService.cancelBooking(...)`, which sets status to `CANCELLED` and marks all associated seats in the movie show as available (`seat.cancel()`).
  7. System writes update asynchronously to `data/bookings.txt`.
  8. System updates the table status to `CANCELLED` and displays success notification.
- **Alternate Flow (A1 - No Booking Selected):**
  - Customer clicks cancel without selecting a row; system displays: *"Please select a booking from the table to cancel."*
- **Alternate Flow (A2 - Booking Already Cancelled):**
  - Customer attempts to cancel a previously cancelled ticket; system displays: *"Booking #BMT-XXXX has already been cancelled."*
- **Postconditions:** Booking status is cancelled and seats are immediately released for other customers.

---

### Use Case 5: UC-05 Admin Manage Movies (Add and Remove Movie)
- **Use Case ID:** UC-05
- **Name:** Add and Remove Movies
- **Primary Actor:** Administrator
- **Preconditions:** Administrator is logged in and `AdminFrame` is open on the "Manage Movies" tab.
- **Main Flow (Add Movie):**
  1. Administrator clicks "+ Add New Movie".
  2. System opens the modal movie creation dialog.
  3. Administrator enters Title, Genre, Duration (minutes), Language, Base Ticket Price, and comma-separated Show Timings.
  4. Administrator clicks "Save Movie".
  5. System validates inputs (non-empty strings, positive numeric values for duration and price).
  6. System generates unique Movie ID (e.g., `M6`), creates `Movie` object with nested `Show` instances, adds to `MovieService`, and saves to `data/movies.txt`.
  7. System refreshes the movie management table and displays success notification.
- **Main Flow (Remove Movie):**
  1. Administrator selects a movie row from the table.
  2. Administrator clicks "Remove Selected Movie".
  3. System prompts confirmation dialog: *"Are you sure you want to remove the movie...?"*
  4. Administrator confirms deletion.
  5. System removes movie from memory and updates `data/movies.txt`.
  6. System refreshes the movie list table.
- **Alternate Flow (A1 - Validation Failure on Add):**
  - If any field is empty or numbers are non-positive, system throws `InvalidInputException` and displays specific error message.
- **Postconditions:** Active movie catalog file `data/movies.txt` is updated.

---

### Use Case 6: UC-06 Admin Audit All Bookings
- **Use Case ID:** UC-06
- **Name:** View All System Bookings
- **Primary Actor:** Administrator
- **Preconditions:** Administrator is logged in and selects the "View All Bookings" tab in `AdminFrame`.
- **Main Flow:**
  1. System queries `BookingService.getAllBookings()`.
  2. System populates `JTable` with all recorded bookings across all patrons and shows (Booking ID, Customer Name, Phone, Movie, Show, Seats, Total, Status, Date).
  3. Administrator reviews reservations and can click "Refresh Bookings" to fetch real-time updates.
- **Postconditions:** Full audit table of reservations is displayed.
