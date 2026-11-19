package tools;

import com.bookmyticket.model.*;
import com.bookmyticket.service.BookingService;
import com.bookmyticket.service.MovieService;
import com.bookmyticket.ui.*;
import com.bookmyticket.util.FileManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen capture automation tool.
 * Opens and renders UI frames and dialogs into high-fidelity PNG images in docs/screenshots/.
 */
public class CaptureScreens {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        File dir = new File("docs/screenshots");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        FileManager.initializeFiles();
        MovieService movieService = new MovieService();
        BookingService bookingService = new BookingService(movieService);

        Customer sampleCustomer = new Customer("Alex Johnson", "9876543210");

        try {
            // 1. Login Frame - Customer View
            System.out.println("Capturing 01_login_customer.png...");
            LoginFrame loginCustomer = new LoginFrame(movieService, bookingService);
            captureWindow(loginCustomer, 460, 520, "docs/screenshots/01_login_customer.png");

            // 2. Login Frame - Admin View
            System.out.println("Capturing 02_login_admin.png...");
            LoginFrame loginAdmin = new LoginFrame(movieService, bookingService);
            setAdminRadioSelected(loginAdmin);
            captureWindow(loginAdmin, 460, 520, "docs/screenshots/02_login_admin.png");

            // 3. Movie List Frame
            System.out.println("Capturing 03_movie_list.png...");
            MovieListFrame movieList = new MovieListFrame(sampleCustomer, movieService, bookingService);
            captureWindow(movieList, 850, 560, "docs/screenshots/03_movie_list.png");

            // 4. Seat Selection Frame
            System.out.println("Capturing 04_seat_selection.png...");
            Movie movie = movieService.getAllMovies().get(0); // Inception
            Show show = movie.getShows().get(0); // 10:00 AM
            SeatSelectionFrame seatSelection = new SeatSelectionFrame(sampleCustomer, movie, show, movieService, bookingService, movieList);
            simulateSeatSelections(seatSelection, show);
            captureWindow(seatSelection, 880, 680, "docs/screenshots/04_seat_selection.png");

            // 5. Booking Confirmation Ticket Dialog
            System.out.println("Capturing 05_booking_ticket.png...");
            List<Seat> bookedSeats = new ArrayList<>();
            for (Seat s : show.getSeats()) {
                if (!s.isBooked()) {
                    bookedSeats.add(s);
                    if (bookedSeats.size() == 3) break;
                }
            }
            Booking confirmedBooking;
            if (!bookedSeats.isEmpty()) {
                confirmedBooking = bookingService.bookSeats(sampleCustomer, movie, show, bookedSeats);
            } else {
                // Use existing booking
                confirmedBooking = bookingService.getAllBookings().get(0);
            }

            JDialog ticketDialog = createTicketDialog(confirmedBooking);
            captureWindow(ticketDialog, 440, 480, "docs/screenshots/05_booking_ticket.png");

            // 6. My Bookings Frame
            System.out.println("Capturing 06_my_bookings.png...");
            MyBookingsFrame myBookings = new MyBookingsFrame(sampleCustomer, movieService, bookingService, movieList);
            captureWindow(myBookings, 860, 520, "docs/screenshots/06_my_bookings.png");

            // 7. Admin Frame - Manage Movies Tab
            System.out.println("Capturing 07_admin_movies.png...");
            Admin admin = new Admin("Administrator");
            AdminFrame adminFrame = new AdminFrame(admin, movieService, bookingService);
            captureWindow(adminFrame, 920, 600, "docs/screenshots/07_admin_movies.png");

            // 8. Admin Frame - All Bookings Tab
            System.out.println("Capturing 08_admin_bookings.png...");
            selectAdminTab(adminFrame, 1);
            captureWindow(adminFrame, 920, 600, "docs/screenshots/08_admin_bookings.png");

            // 9. Error Dialog (No Seats Selected)
            System.out.println("Capturing 09_error_dialog.png...");
            JDialog errorDialog = createErrorDialog("No seats selected! Please select at least one seat before confirming booking.", "Input Validation Error");
            captureWindow(errorDialog, 460, 200, "docs/screenshots/09_error_dialog.png");

            System.out.println("All screenshots successfully captured in docs/screenshots/!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    private static void captureWindow(Window window, int width, int height, String outputPath) throws Exception {
        window.setSize(width, height);
        window.addNotify();
        window.validate();

        Container content;
        if (window instanceof RootPaneContainer) {
            content = ((RootPaneContainer) window).getContentPane();
        } else {
            content = window;
        }

        content.setSize(width, height);
        content.doLayout();
        content.validate();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        content.printAll(g2);
        g2.dispose();

        ImageIO.write(image, "png", new File(outputPath));
        window.dispose();
    }

    private static void setAdminRadioSelected(LoginFrame frame) {
        try {
            Field fCustomer = LoginFrame.class.getDeclaredField("customerRadio");
            fCustomer.setAccessible(true);
            JRadioButton rCustomer = (JRadioButton) fCustomer.get(frame);

            Field fAdmin = LoginFrame.class.getDeclaredField("adminRadio");
            fAdmin.setAccessible(true);
            JRadioButton rAdmin = (JRadioButton) fAdmin.get(frame);

            Field fCustomerPanel = LoginFrame.class.getDeclaredField("customerPanel");
            fCustomerPanel.setAccessible(true);
            JPanel pCustomer = (JPanel) fCustomerPanel.get(frame);

            Field fAdminPanel = LoginFrame.class.getDeclaredField("adminPanel");
            fAdminPanel.setAccessible(true);
            JPanel pAdmin = (JPanel) fAdminPanel.get(frame);

            rCustomer.setSelected(false);
            rAdmin.setSelected(true);
            pCustomer.setVisible(false);
            pAdmin.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void simulateSeatSelections(SeatSelectionFrame frame, Show show) {
        try {
            Field fSelected = SeatSelectionFrame.class.getDeclaredField("selectedSeats");
            fSelected.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Seat> list = (List<Seat>) fSelected.get(frame);

            // Select A3 (Regular), A4 (Regular), and D4 (Premium)
            Seat s1 = show.getSeat("A3");
            Seat s2 = show.getSeat("A4");
            Seat s3 = show.getSeat("D4");

            if (s1 != null) list.add(s1);
            if (s2 != null) list.add(s2);
            if (s3 != null) list.add(s3);

            // Update summary
            java.lang.reflect.Method updateMethod = SeatSelectionFrame.class.getDeclaredMethod("updateSummary");
            updateMethod.setAccessible(true);
            updateMethod.invoke(frame);

            // Re-render buttons
            java.lang.reflect.Method buildGrid = SeatSelectionFrame.class.getDeclaredMethod("buildSeatButtons");
            buildGrid.setAccessible(true);
            buildGrid.invoke(frame);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void selectAdminTab(AdminFrame frame, int tabIndex) {
        try {
            Container content = frame.getContentPane();
            for (Component c : content.getComponents()) {
                if (c instanceof JTabbedPane) {
                    ((JTabbedPane) c).setSelectedIndex(tabIndex);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JDialog createTicketDialog(Booking booking) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Ticket Confirmed!");
        dialog.setSize(440, 480);
        dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.COLOR_ACCENT);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("BOOKING CONFIRMED", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        dialog.add(header, BorderLayout.NORTH);

        JPanel ticketBody = new JPanel();
        ticketBody.setLayout(new BoxLayout(ticketBody, BoxLayout.Y_AXIS));
        ticketBody.setBackground(Color.WHITE);
        ticketBody.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        ticketBody.add(createTicketRow("Booking Reference:", booking.getBookingId(), true));
        ticketBody.add(createTicketRow("Movie:", booking.getMovieTitle(), true));
        ticketBody.add(createTicketRow("Show Timing:", booking.getShowTime(), false));
        ticketBody.add(createTicketRow("Seats Reserved:", booking.getSeatsCommaSeparated(), true));
        ticketBody.add(createTicketRow("Customer:", booking.getCustomer().getName() + " (" + booking.getCustomer().getPhoneNumber() + ")", false));
        ticketBody.add(createTicketRow("Total Paid:", "Rs. " + (int)booking.getTotalAmount(), true));
        ticketBody.add(createTicketRow("Status:", booking.getStatus(), true));
        ticketBody.add(createTicketRow("Date & Time:", booking.getBookingDate(), false));

        dialog.add(ticketBody, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        footer.setBackground(Color.WHITE);
        JButton closeBtn = UIUtils.createPrimaryButton("Done (Close Ticket)");
        closeBtn.setPreferredSize(new Dimension(220, 38));
        footer.add(closeBtn);

        dialog.add(footer, BorderLayout.SOUTH);
        return dialog;
    }

    private static JPanel createTicketRow(String label, String value, boolean isBold) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_REGULAR);
        lbl.setForeground(UIUtils.COLOR_TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(isBold ? UIUtils.FONT_BOLD : UIUtils.FONT_REGULAR);
        val.setForeground(UIUtils.COLOR_TEXT_MAIN);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private static JDialog createErrorDialog(String message, String title) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        return pane.createDialog(null, title);
    }
}
