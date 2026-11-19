import os
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml import parse_xml
from docx.oxml.ns import nsdecls

def create_report():
    template_path = r"C:\Users\syoge\Downloads\OOP_MiniProject_Report_Template.docx"
    output_dir = r"d:\Book my ticket\report"
    output_path = os.path.join(output_dir, "BookMyTicket_Report.docx")
    screenshots_dir = r"d:\Book my ticket\docs\screenshots"

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    doc = docx.Document(template_path)

    # 1. Update cover page TITLE and Bonafide text
    for p in doc.paragraphs:
        if p.text.strip() == "TITLE":
            p.text = "BOOK MY TICKET – MOVIE TICKET BOOKING SYSTEM"
        elif "Certified that this project report TITLE" in p.text:
            p.text = p.text.replace("TITLE", "BOOK MY TICKET – MOVIE TICKET BOOKING SYSTEM")

    # Helper function to insert a paragraph right after a cursor paragraph
    def insert_after(cursor, text="", bold_prefix=None, space_after=6):
        new_p = cursor._parent.add_paragraph()
        cursor._p.addnext(new_p._p)
        new_p.paragraph_format.line_spacing = 1.15
        new_p.paragraph_format.space_after = Pt(space_after)

        if bold_prefix:
            r_bold = new_p.add_run(bold_prefix)
            r_bold.bold = True
            r_bold.font.size = Pt(11)

        if text:
            r_text = new_p.add_run(text)
            r_text.font.size = Pt(11)

        return new_p

    def insert_bullet(cursor, bold_prefix, text):
        new_p = cursor._parent.add_paragraph()
        cursor._p.addnext(new_p._p)
        new_p.paragraph_format.left_indent = Inches(0.25)
        new_p.paragraph_format.line_spacing = 1.15
        new_p.paragraph_format.space_after = Pt(4)

        r_bullet = new_p.add_run("•  ")
        r_bullet.bold = True
        r_bullet.font.size = Pt(10.5)

        r_bold = new_p.add_run(bold_prefix)
        r_bold.bold = True
        r_bold.font.size = Pt(10.5)

        r_text = new_p.add_run(text)
        r_text.font.size = Pt(10.5)
        return new_p

    def insert_code(cursor, code_text):
        new_p = cursor._parent.add_paragraph()
        cursor._p.addnext(new_p._p)
        new_p.paragraph_format.left_indent = Inches(0.3)
        new_p.paragraph_format.space_before = Pt(4)
        new_p.paragraph_format.space_after = Pt(6)
        new_p.paragraph_format.line_spacing = 1.0

        run = new_p.add_run(code_text)
        run.font.name = 'Consolas'
        run.font.size = Pt(9.5)
        run.font.color.rgb = RGBColor(30, 41, 59)
        return new_p

    def insert_image(cursor, img_rel_path, width_inches, caption):
        img_full_path = os.path.join(screenshots_dir, img_rel_path)
        new_p = cursor._parent.add_paragraph()
        cursor._p.addnext(new_p._p)
        new_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        new_p.paragraph_format.space_before = Pt(8)
        new_p.paragraph_format.space_after = Pt(2)

        run = new_p.add_run()
        if os.path.exists(img_full_path):
            run.add_picture(img_full_path, width=Inches(width_inches))
        else:
            run.add_text(f"[Image: {img_rel_path}]")

        cap_p = cursor._parent.add_paragraph()
        new_p._p.addnext(cap_p._p)
        cap_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        cap_p.paragraph_format.space_after = Pt(10)
        c_run = cap_p.add_run(caption)
        c_run.font.size = Pt(9.5)
        c_run.font.italic = True
        c_run.font.color.rgb = RGBColor(100, 116, 139)
        return cap_p

    # Map headings to paragraphs
    heading_map = {}
    for p in doc.paragraphs:
        t = p.text.strip()
        if t in [
            'TABLE OF CONTENTS', 'PROBLEM DESCRIPTION', 'PROBLEM STATEMENT',
            'OBJECTIVES', 'CONSTRAINT / LIMITATIONS', 'OOP CONCEPTS APPLIED',
            'INHERITANCE', 'POLYMORPHISM', 'EXCEPTION HANDLING', 'THREADING',
            'FILE HANDLING', 'COLLECTIONS', 'EVENT HANDLING', 'MODULES AND DESIGN',
            'MODULES DESCRIPTIONS', 'CLASS DESIGN DIAGRAMS', 'PACKAGE DIAGRAM',
            'IMPLEMENTATION', 'AI TOOLS USED', 'PROMPTS USED', 'SAMPLE JAVA CODE',
            'SAMPLE INPUT/ OUTPUT – SCREEN SHOTS', 'DEPLOYMENT',
            'DEPLOYMENT PORTAL DETAILS', 'USER MANUALS', 'GITHUB LINK', 'REFERENCES'
        ]:
            heading_map[t] = p

    # --- TABLE OF CONTENTS ---
    if 'TABLE OF CONTENTS' in heading_map:
        cur = heading_map['TABLE OF CONTENTS']
        toc_p = insert_after(cur, "")
        fld_xml = parse_xml(r'<w:fldSimple %s w:instr="TOC \o &quot;1-3&quot; \h \z \u"/>' % nsdecls('w'))
        toc_p._p.append(fld_xml)

    # --- PROBLEM DESCRIPTION ---
    if 'PROBLEM DESCRIPTION' in heading_map:
        cur = heading_map['PROBLEM DESCRIPTION']
        cur = insert_after(cur, "In conventional cinema ticketing operations, counter-based seat allocation frequently causes congested waiting lines, booking errors, and inefficient customer throughput. While modern multi-screen theaters require automated scheduling and distinct seat categorization (such as regular viewing tiers and premium luxury rows), contemporary solutions often burden small-scale venues with heavy web architectures, high relational database maintenance costs, and complex external cloud configurations.")
        cur = insert_after(cur, "The \"Book My Ticket\" system addresses these operational bottlenecks by providing an autonomous, highly responsive desktop application engineered entirely in pure Java SE with Swing. By leveraging object-oriented design and local persistent flat-file storage, the system ensures zero-dependency execution across any desktop platform with guaranteed operational stability and data integrity.")

    # --- PROBLEM STATEMENT ---
    if 'PROBLEM STATEMENT' in heading_map:
        cur = heading_map['PROBLEM STATEMENT']
        cur = insert_after(cur, "To design, develop, and evaluate an autonomous desktop movie ticket booking application in pure Java SE (JDK 17+) using Java Swing, incorporating object-oriented principles, differential seat tier pricing (Regular vs. Premium), thread-safe concurrent reservation management, and asynchronous flat-file persistence (CSV) without reliance on relational databases or external web frameworks.")

    # --- OBJECTIVES ---
    if 'OBJECTIVES' in heading_map:
        cur = heading_map['OBJECTIVES']
        cur = insert_bullet(cur, "Interactive GUI Development: ", "Engineer an intuitive graphical interface using Java Swing (JFrame, JTable, JButton, JDialog) featuring distinct Customer and Administrator environments.")
        cur = insert_bullet(cur, "Differential Seat Allocation: ", "Implement a realistic 5x8 visual seat grid with polymorphic price evaluation distinguishing standard viewing (Regular) from luxury back-row seating (Premium).")
        cur = insert_bullet(cur, "Concurrency & Race Condition Prevention: ", "Guarantee transaction atomicity and prevent double-booking using synchronized critical sections across booking operations.")
        cur = insert_bullet(cur, "Asynchronous Background Persistence: ", "Offload disk file serialization to background threads via SwingWorker to maintain complete UI responsiveness on the Event Dispatch Thread (EDT).")
        cur = insert_bullet(cur, "Comprehensive Catalog Management: ", "Equip administrators with tools to add new movies with multiple show schedules, delete discontinued movies, and perform system-wide booking audits.")
        cur = insert_bullet(cur, "Visible OOP Concept Demonstration: ", "Provide clean, standard implementations of Encapsulation, Abstraction, Inheritance, Polymorphism, Exception Handling, Collections, and Event Handling.")

    # --- CONSTRAINT / LIMITATIONS ---
    if 'CONSTRAINT / LIMITATIONS' in heading_map:
        cur = heading_map['CONSTRAINT / LIMITATIONS']
        cur = insert_bullet(cur, "Single-Machine Desktop Environment: ", "The application is engineered strictly as a standalone desktop GUI program running on a single JVM instance; multi-node client-server network distribution is outside the project scope.")
        cur = insert_bullet(cur, "Flat-File Storage Constraint: ", "Data persistence is managed via lightweight CSV text files (movies.txt and bookings.txt) instead of a relational database management system (RDBMS) or NoSQL engine.")
        cur = insert_bullet(cur, "Simulated Transaction Checkout: ", "Payment processing and ticket generation are handled internally via simulated transaction confirmation; live banking, payment gateways (UPI, credit cards), and external SMS/email APIs are excluded.")
        cur = insert_bullet(cur, "Fixed Theater Scheduling Scope: ", "Seat inventories are scoped to a standardized 5-row by 8-column layout (40 seats per screen) per show time without variable multi-screen auditorium topologies.")

    # --- OOP CONCEPTS APPLIED ---
    if 'OOP CONCEPTS APPLIED' in heading_map:
        cur = heading_map['OOP CONCEPTS APPLIED']
        cur = insert_after(cur, "The architecture of Book My Ticket is purposefully constructed around fundamental Object-Oriented Programming (OOP) paradigms. Each concept is cleanly embedded into the domain models, services, and presentation workflows to fulfill the academic rigorous standards of course 22IT321.")

    # --- INHERITANCE ---
    if 'INHERITANCE' in heading_map:
        cur = heading_map['INHERITANCE']
        cur = insert_after(cur, "Inheritance allows specialized subclasses to inherit fields and behaviors from generalized base classes, promoting code reuse and modularity. In Book My Ticket, Customer and Admin extend the abstract Person base class, while RegularSeat and PremiumSeat extend the abstract Seat base class.")
        cur = insert_code(cur, """// Hierarchy 1: User specialization
public class Customer extends Person {
    public Customer(String name, String phoneNumber) {
        super(name, phoneNumber);
    }
    @Override public String getRole() { return "Customer"; }
}

// Hierarchy 2: Seat specialization
public class PremiumSeat extends Seat {
    private static final double PREMIUM_SURCHARGE = 50.0;
    public PremiumSeat(String seatNumber, double basePrice) {
        super(seatNumber, basePrice);
    }
    @Override public double getPrice() {
        return getBasePrice() + PREMIUM_SURCHARGE;
    }
    @Override public String getSeatType() { return "Premium"; }
}""")

    # --- POLYMORPHISM ---
    if 'POLYMORPHISM' in heading_map:
        cur = heading_map['POLYMORPHISM']
        cur = insert_after(cur, "Polymorphism is demonstrated through dynamic method overriding (runtime polymorphism) and method overloading (compile-time polymorphism). Subclasses override getPrice() and getRole(), while BookingService provides overloaded calculateTotal methods tailored for standard, discounted, and bulk transactions.")
        cur = insert_code(cur, """// Overriding: Dynamic dispatch on Seat.getPrice()
for (Seat seat : selectedSeats) {
    total += seat.getPrice(); // Executes RegularSeat or PremiumSeat getPrice()
}

// Overloading: Multiple calculateTotal method signatures in BookingService
public double calculateTotal(List<Seat> seats) { ... }
public double calculateTotal(List<Seat> seats, double discountPercentage) { ... }
public double calculateTotal(int seatCount, double unitPrice) { ... }""")

    # --- EXCEPTION HANDLING ---
    if 'EXCEPTION HANDLING' in heading_map:
        cur = heading_map['EXCEPTION HANDLING']
        cur = insert_after(cur, "Custom checked exceptions (SeatAlreadyBookedException and InvalidInputException) enforce business integrity rules. Robust try-catch-finally blocks guarantee deterministic stream cleanup and prevent invalid system transitions, surfacing clear user feedback via JOptionPane dialogs.")
        cur = insert_code(cur, """public synchronized Booking bookSeats(...) throws SeatAlreadyBookedException, InvalidInputException {
    if (selectedSeats.isEmpty()) {
        throw new InvalidInputException("Please select at least one seat before confirming booking.");
    }
    for (Seat seat : selectedSeats) {
        if (seat.isBooked()) {
            throw new SeatAlreadyBookedException(seat.getSeatNumber(), show.getTime());
        }
    }
    // Proceed to reserve seats...
}""")

    # --- THREADING ---
    if 'THREADING' in heading_map:
        cur = heading_map['THREADING']
        cur = insert_after(cur, "To maintain seamless user interface responsiveness, disk file serialization is decoupled from the Java Swing Event Dispatch Thread (EDT) using SwingWorker. Furthermore, booking transactions are protected by the synchronized keyword to ensure thread-safe mutual exclusion during concurrent seat allocations.")
        cur = insert_code(cur, """// Background asynchronous file persistence
public void saveBookingsAsync() {
    final List<Booking> snapshot = new ArrayList<>(bookings.values());
    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        @Override protected Void doInBackground() {
            FileManager.saveBookings(snapshot);
            return null;
        }
    };
    worker.execute();
}

// Synchronized booking method preventing concurrent double-booking
public synchronized Booking bookSeats(Customer c, Movie m, Show s, List<Seat> seats)
        throws SeatAlreadyBookedException, InvalidInputException { ... }""")

    # --- FILE HANDLING ---
    if 'FILE HANDLING' in heading_map:
        cur = heading_map['FILE HANDLING']
        cur = insert_after(cur, "Persistent storage is realized through human-readable CSV flat files located in data/movies.txt and data/bookings.txt. The system employs BufferedReader and BufferedWriter enclosed in explicit try-catch-finally constructs to safely parse and serialize records across application sessions.")
        cur = insert_code(cur, """BufferedReader reader = null;
try {
    reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",", -1);
        // Parse CSV fields into Movie and Show objects
    }
} catch (IOException e) {
    System.err.println("Error reading movies file: " + e.getMessage());
} finally {
    if (reader != null) {
        try { reader.close(); } catch (IOException ignored) {}
    }
}""")

    # --- COLLECTIONS ---
    if 'COLLECTIONS' in heading_map:
        cur = heading_map['COLLECTIONS']
        cur = insert_after(cur, "The Java Collections Framework is extensively utilized: ArrayList manages ordered lists of movies, screening shows, and selected seats; HashMap<String, Booking> enables constant-time O(1) lookups of reservations by their generated Booking ID.")
        cur = insert_code(cur, """// Collections implementation in Services
private List<Movie> movies = new ArrayList<>();
private Map<String, Booking> bookings = new HashMap<>(); // bookingId -> Booking

public List<Booking> getBookingsByCustomer(String phoneNumber) {
    List<Booking> customerBookings = new ArrayList<>();
    for (Booking b : bookings.values()) {
        if (phoneNumber.equals(b.getCustomer().getPhoneNumber())) {
            customerBookings.add(b);
        }
    }
    return customerBookings;
}""")

    # --- EVENT HANDLING ---
    if 'EVENT HANDLING' in heading_map:
        cur = heading_map['EVENT HANDLING']
        cur = insert_after(cur, "Event-driven interaction is powered by Swing listeners: ActionListener handles button submissions and seat toggles; ListSelectionListener synchronizes available show timings upon movie row selection; WindowListener triggers state flushing during application termination.")
        cur = insert_code(cur, """// ListSelectionListener: updates show timings when movie is selected
movieTable.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        updateSelectedMovie();
    }
});

// WindowListener: guarantees persistent data flush on window close
addWindowListener(new WindowAdapter() {
    @Override public void windowClosing(WindowEvent e) {
        bookingService.saveAllSync();
        movieService.saveAll();
    }
});""")

    # --- MODULES AND DESIGN ---
    if 'MODULES AND DESIGN' in heading_map:
        cur = heading_map['MODULES AND DESIGN']
        cur = insert_after(cur, "Book My Ticket is engineered using a clean four-tier architectural model separating presentation, business logic, domain entities, and file persistence. This separation of concerns guarantees high maintainability and testability.")

    # --- MODULES DESCRIPTIONS ---
    if 'MODULES DESCRIPTIONS' in heading_map:
        cur = heading_map['MODULES DESCRIPTIONS']
        cur = insert_bullet(cur, "com.bookmyticket (Core Entry Point): ", "Houses Main.java, responsible for initializing persistent storage, setting the system Look-and-Feel, bootstrapping services, attaching JVM shutdown hooks, and launching LoginFrame on the Swing EDT.")
        cur = insert_bullet(cur, "com.bookmyticket.model (Domain Entities): ", "Defines domain concepts including Person (abstract base), Customer, Admin, Bookable (interface), Seat (abstract base), RegularSeat, PremiumSeat, Movie, Show, and Booking.")
        cur = insert_bullet(cur, "com.bookmyticket.service (Business Logic & Coordination): ", "Encapsulates application workflow rules. MovieService handles catalog querying and mutations; BookingService coordinates seat pricing, synchronized reservations, cancellations, and SwingWorker persistence.")
        cur = insert_bullet(cur, "com.bookmyticket.util (Persistence & File Management): ", "Contains FileManager, providing robust CSV serialization and deserialization for movies.txt and bookings.txt with comprehensive stream safety.")
        cur = insert_bullet(cur, "com.bookmyticket.exception (Custom Checked Exceptions): ", "Houses domain-specific checked exceptions SeatAlreadyBookedException and InvalidInputException, promoting robust fault tolerance.")
        cur = insert_bullet(cur, "com.bookmyticket.ui (Presentation Layer): ", "Implements all graphical user interfaces (LoginFrame, MovieListFrame, SeatSelectionFrame, MyBookingsFrame, AdminFrame) and centralized aesthetics (UIUtils).")

    # --- CLASS DESIGN DIAGRAMS ---
    if 'CLASS DESIGN DIAGRAMS' in heading_map:
        cur = heading_map['CLASS DESIGN DIAGRAMS']
        cur = insert_after(cur, "The class design diagram below details all domain entities, services, persistence managers, interfaces, and their relational mappings (generalization, realization, and association).")
        cur = insert_image(cur, "class_diagram.png", 6.5, "Figure: Detailed UML Class Diagram of Book My Ticket")

    # --- PACKAGE DIAGRAM ---
    if 'PACKAGE DIAGRAM' in heading_map:
        cur = heading_map['PACKAGE DIAGRAM']
        cur = insert_after(cur, "The package diagram below illustrates the hierarchical organization of packages and directional dependencies from the presentation layer down to flat-file storage.")
        cur = insert_image(cur, "package_diagram.png", 6.0, "Figure: Layered Package Architecture Diagram")

    # --- IMPLEMENTATION ---
    if 'IMPLEMENTATION' in heading_map:
        cur = heading_map['IMPLEMENTATION']
        cur = insert_after(cur, "The end-to-end booking execution follows a rigorous multi-stage pipeline:")
        cur = insert_bullet(cur, "Stage 1 (Authentication): ", "The patron inputs name and phone in LoginFrame, triggering input validation before transitioning to MovieListFrame.")
        cur = insert_bullet(cur, "Stage 2 (Catalog Query): ", "MovieListFrame renders active movies from MovieService. Selecting a film triggers a ListSelectionListener to populate available show times.")
        cur = insert_bullet(cur, "Stage 3 (Seat Matrix): ", "SeatSelectionFrame renders 40 seat buttons. Clicking available buttons updates internal List<Seat> collections, toggles button colors, and updates the dynamic total via overloaded calculateTotal(selectedSeats).")
        cur = insert_bullet(cur, "Stage 4 (Synchronized Reservation): ", "Clicking 'Confirm Booking' prompts a confirmation modal. The synchronized bookSeats(...) method reserves seats, assigns a unique ID (BMT-1001), and delegates CSV writing to SwingWorker.")
        cur = insert_bullet(cur, "Stage 5 (Receipt & Cancellation): ", "The customer receives a graphical ticket dialog. At any point, the patron can visit MyBookingsFrame to cancel bookings, immediately returning seats to the available inventory.")

    # --- AI TOOLS USED ---
    if 'AI TOOLS USED' in heading_map:
        cur = heading_map['AI TOOLS USED']
        cur = insert_after(cur, "Google Antigravity (powered by Gemini 3.8 Flash)")

    # --- PROMPTS USED ---
    if 'PROMPTS USED' in heading_map:
        cur = heading_map['PROMPTS USED']
        prompt_text = """# Task: "Book My Ticket" – a simple Java Swing movie ticket booking app + docs + course report

This is a college mini-project for the course 22IT321 Object Oriented Programming.
Keep it SIMPLE and GENERIC. Only the core functions of a movie ticket booking app.
No database, no web app, no frameworks, no external libraries. Pure Java (JDK 17+) with Swing (JFrame).
The app must run as a GUI window, not as a terminal/console program.

GitHub repo: https://github.com/Yogesh-OT/Book_my_ticket.git
Clone it and do all the work inside it.

## 1. Functional scope (minimum features only)
1. Login screen (JFrame): choose "Customer" (enter name + phone number) or "Admin" (fixed password admin123, documented in the README).
2. Movie list: shows movies with title, genre, duration, language and ticket price. Seed 4–5 sample movies, each with 2–3 show timings.
3. Seat selection: a grid of seat buttons (for example 5 rows x 8 columns). Booked seats are disabled/greyed out. There are two seat types: Regular and Premium (back rows), with different prices.
4. Booking confirmation: shows a summary (movie, show time, seats, total amount), then a Confirm button. A booking ID is generated and a ticket dialog is shown.
5. My Bookings: the customer can view their bookings and cancel one. Cancelled seats become available again.
6. Admin panel: add a movie/show, remove a movie, and view all bookings in a JTable.
Nothing beyond this: no payments gateway, no email, no seat-hold timers, no extra features.

## 2. OOP concepts that MUST be visibly used
- Encapsulation: private fields with getters/setters in the model classes.
- Abstraction: abstract class Person and abstract class Seat; one interface, e.g. Bookable (book/cancel).
- Inheritance: Customer and Admin extend Person; RegularSeat and PremiumSeat extend Seat.
- Polymorphism: overriding (getPrice() in the seat subclasses, getRole() in the Person subclasses) and overloading (e.g. two calculateTotal(...) versions).
- Exception handling: custom checked exceptions SeatAlreadyBookedException and InvalidInputException. Use try/catch/finally around file I/O and input validation, and show errors with JOptionPane.
- Threading: save bookings to file on a background thread using SwingWorker, so the UI never freezes. Make the booking method synchronized so the same seat cannot be booked twice.
- File handling: store movies in data/movies.txt and bookings in data/bookings.txt (simple CSV). Load them at startup, create the files if they are missing, and save on every change and on window close.
- Collections: ArrayList<Movie>, HashMap<String, Booking> (bookingId -> booking), and a List/Set for selected seats.
- Event handling: ActionListener for buttons, ItemListener or ActionListener for seat toggles, WindowListener to save on close, ListSelectionListener on the movie list/table."""
        cur = insert_code(cur, prompt_text)

    # --- SAMPLE JAVA CODE ---
    if 'SAMPLE JAVA CODE' in heading_map:
        cur = heading_map['SAMPLE JAVA CODE']
        cur = insert_after(cur, "Key excerpts demonstrating core architectural mechanisms:")
        cur = insert_after(cur, "1. BookingService.java – Synchronized Thread-Safe Ticket Reservation:")
        cur = insert_code(cur, """public synchronized Booking bookSeats(Customer customer, Movie movie, Show show, List<Seat> selectedSeats)
        throws SeatAlreadyBookedException, InvalidInputException {

    if (customer == null || customer.getName().trim().isEmpty() || customer.getPhoneNumber().trim().isEmpty()) {
        throw new InvalidInputException("Customer details (name and phone number) must be provided.");
    }
    if (selectedSeats == null || selectedSeats.isEmpty()) {
        throw new InvalidInputException("Please select at least one seat before confirming booking.");
    }

    // Verify availability (prevents concurrent race conditions)
    for (Seat seat : selectedSeats) {
        if (seat.isBooked()) {
            throw new SeatAlreadyBookedException(seat.getSeatNumber(), show.getTime());
        }
    }

    // Reserve seats polymorphically
    List<String> seatNumbers = new ArrayList<>();
    for (Seat seat : selectedSeats) {
        seat.book();
        seatNumbers.add(seat.getSeatNumber());
    }

    double totalAmount = calculateTotal(selectedSeats);
    String bookingId = "BMT-" + (nextBookingSequence++);
    String bookingDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

    Booking booking = new Booking(bookingId, customer, movie.getMovieId(), movie.getTitle(),
            show.getTime(), seatNumbers, totalAmount, "CONFIRMED", bookingDate);

    bookings.put(bookingId, booking);
    saveBookingsAsync(); // Dispatches file persistence to background SwingWorker
    return booking;
}""")

        cur = insert_after(cur, "2. FileManager.java – Robust Flat-File Persistence with Try-Catch-Finally:")
        cur = insert_code(cur, """public static void saveBookings(Collection<Booking> bookings) {
    File file = new File(BOOKINGS_FILE);
    BufferedWriter writer = null;
    try {
        writer = new BufferedWriter(new FileWriter(file, false));
        for (Booking booking : bookings) {
            StringBuilder sb = new StringBuilder();
            sb.append(booking.getBookingId()).append(",");
            sb.append(booking.getCustomer().getName()).append(",");
            sb.append(booking.getCustomer().getPhoneNumber()).append(",");
            sb.append(booking.getMovieId()).append(",");
            sb.append(booking.getMovieTitle()).append(",");
            sb.append(booking.getShowTime()).append(",");
            sb.append(String.join(";", booking.getSeatNumbers())).append(",");
            sb.append(booking.getTotalAmount()).append(",");
            sb.append(booking.getStatus()).append(",");
            sb.append(booking.getBookingDate());

            writer.write(sb.toString());
            writer.newLine();
        }
    } catch (IOException e) {
        System.err.println("Error saving bookings: " + e.getMessage());
    } finally {
        if (writer != null) {
            try { writer.close(); } catch (IOException ignored) {}
        }
    }
}""")

    # --- SAMPLE INPUT/ OUTPUT – SCREEN SHOTS ---
    if 'SAMPLE INPUT/ OUTPUT – SCREEN SHOTS' in heading_map:
        cur = heading_map['SAMPLE INPUT/ OUTPUT – SCREEN SHOTS']
        cur = insert_after(cur, "Actual operational screenshots captured from test execution:")
        cur = insert_image(cur, "01_login_customer.png", 4.2, "Figure 1: Customer Login Portal")
        cur = insert_image(cur, "02_login_admin.png", 4.2, "Figure 2: Administrator Login Portal")
        cur = insert_image(cur, "03_movie_list.png", 5.5, "Figure 3: Movie List and Show Timing Selection")
        cur = insert_image(cur, "04_seat_selection.png", 5.5, "Figure 4: Seat Matrix with Regular & Premium Selection")
        cur = insert_image(cur, "05_booking_ticket.png", 4.2, "Figure 5: Generated Ticket Confirmation Slip")
        cur = insert_image(cur, "06_my_bookings.png", 5.5, "Figure 6: Customer Personal Bookings and Cancellation")
        cur = insert_image(cur, "07_admin_movies.png", 5.5, "Figure 7: Admin Movie Catalog Management")
        cur = insert_image(cur, "08_admin_bookings.png", 5.5, "Figure 8: Admin System-Wide Booking Audit Table")
        cur = insert_image(cur, "09_error_dialog.png", 4.2, "Figure 9: Input Validation Error Notification")

    # --- DEPLOYMENT ---
    if 'DEPLOYMENT' in heading_map:
        cur = heading_map['DEPLOYMENT']
        cur = insert_after(cur, "The Book My Ticket application is distributed as a self-contained, standalone executable Java Archive (BookMyTicket.jar). It requires only a standard Java Runtime Environment (JRE 17+) and operates identically on Windows, Linux, and macOS platforms.")

    # --- DEPLOYMENT PORTAL DETAILS ---
    if 'DEPLOYMENT PORTAL DETAILS' in heading_map:
        cur = heading_map['DEPLOYMENT PORTAL DETAILS']
        cur = insert_after(cur, "The project is maintained on GitHub under the repository:")
        cur = insert_after(cur, "Repository URL: https://github.com/Yogesh-OT/Book_my_ticket.git", bold_prefix="Source Code: ")
        cur = insert_after(cur, "To execute the pre-built runnable JAR, run:")
        cur = insert_code(cur, "java -jar BookMyTicket.jar")
        cur = insert_after(cur, "Alternatively, platform-specific launch scripts are provided in the repository root (run.bat for Windows, run.sh for Linux/macOS).")

    # --- USER MANUALS ---
    if 'USER MANUALS' in heading_map:
        cur = heading_map['USER MANUALS']
        cur = insert_after(cur, "Customer Operational Guide:", bold_prefix="1. ")
        cur = insert_bullet(cur, "Step 1: ", "Launch the application by running run.bat, run.sh, or java -jar BookMyTicket.jar.")
        cur = insert_bullet(cur, "Step 2: ", "Select 'Customer' on the login screen, enter your Full Name and 10-digit Phone Number, and click 'Continue'.")
        cur = insert_bullet(cur, "Step 3: ", "Browse the movie table. Click on a movie row to load available show times. Select a time from the dropdown and click 'Select Seats ->'.")
        cur = insert_bullet(cur, "Step 4: ", "Click desired seats on the 5x8 grid. Rows A-C are Regular; Rows D-E are Premium. Observe the real-time total update.")
        cur = insert_bullet(cur, "Step 5: ", "Click 'Confirm Booking', review the summary, and confirm to receive your formatted ticket slip.")
        cur = insert_bullet(cur, "Step 6: ", "To cancel or review bookings, click 'My Bookings' from the main screen, highlight a booking, and click 'Cancel Selected Booking'.")

        cur = insert_after(cur, "Administrator Operational Guide:", bold_prefix="2. ")
        cur = insert_bullet(cur, "Step 1: ", "Select 'Admin' on the login screen, enter the password 'admin123', and click 'Continue'.")
        cur = insert_bullet(cur, "Step 2: ", "In the 'Manage Movies' tab, view all movies or click '+ Add New Movie' to add a new title with custom duration, price, and show times.")
        cur = insert_bullet(cur, "Step 3: ", "To remove an obsolete movie, select its row in the table and click 'Remove Selected Movie'.")
        cur = insert_bullet(cur, "Step 4: ", "Switch to the 'View All Bookings' tab to audit all system bookings across all customers in real time.")
        cur = insert_bullet(cur, "Step 5: ", "Click 'Logout' to securely exit the administrative dashboard.")

    # --- GITHUB LINK ---
    if 'GITHUB LINK' in heading_map:
        cur = heading_map['GITHUB LINK']
        cur = insert_after(cur, "Official Source Repository: https://github.com/Yogesh-OT/Book_my_ticket.git")

    # --- REFERENCES ---
    if 'REFERENCES' in heading_map:
        cur = heading_map['REFERENCES']
        cur = insert_bullet(cur, "[1] ", "Oracle Corporation, \"Java Platform, Standard Edition Documentation (JDK 17 & JDK 21),\" Oracle Java Documentation, 2024. Available: https://docs.oracle.com/en/java/javase/")
        cur = insert_bullet(cur, "[2] ", "Oracle Corporation, \"Creating a GUI With Swing,\" Java Tutorials, 2023. Available: https://docs.oracle.com/javase/tutorial/uiswing/")
        cur = insert_bullet(cur, "[3] ", "H. Schildt, \"Java: The Complete Reference,\" 12th ed., McGraw-Hill Education, New York, NY, 2021.")
        cur = insert_bullet(cur, "[4] ", "C. S. Horstmann, \"Core Java Volume I--Fundamentals,\" 12th ed., Prentice Hall, Upper Saddle River, NJ, 2022.")
        cur = insert_bullet(cur, "[5] ", "Software Engineering Institute, \"SEI CERT Oracle Coding Standard for Java,\" Carnegie Mellon University, 2023.")

    doc.save(output_path)
    print(f"Report generated successfully at {output_path}!")

if __name__ == "__main__":
    create_report()
