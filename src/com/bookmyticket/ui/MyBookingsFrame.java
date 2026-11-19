package com.bookmyticket.ui;

import com.bookmyticket.exception.InvalidInputException;
import com.bookmyticket.model.Booking;
import com.bookmyticket.model.Customer;
import com.bookmyticket.service.BookingService;
import com.bookmyticket.service.MovieService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Screen displaying the customer's personal booking history with cancellation support.
 * Demonstrates:
 * - GUI components (JTable, DefaultTableModel, JScrollPane, JButton)
 * - Event Handling (ActionListener)
 * - Exception Handling (InvalidInputException)
 * - Bookings cancellation and dynamic seat release
 */
public class MyBookingsFrame extends JFrame {
    private final Customer customer;
    private final MovieService movieService;
    private final BookingService bookingService;
    private final JFrame parentFrame;

    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JButton cancelButton;

    public MyBookingsFrame(Customer customer, MovieService movieService,
                           BookingService bookingService, JFrame parentFrame) {
        this.customer = customer;
        this.movieService = movieService;
        this.bookingService = bookingService;
        this.parentFrame = parentFrame;

        setTitle("Book My Ticket - My Bookings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(860, 520);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.COLOR_BG);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 23, 42));
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titleLbl = new JLabel("My Bookings");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("Customer: " + customer.getName() + " (" + customer.getPhoneNumber() + ")");
        subLbl.setFont(UIUtils.FONT_REGULAR);
        subLbl.setForeground(new Color(203, 213, 225));

        header.add(titleLbl, BorderLayout.WEST);
        header.add(subLbl, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center Table
        String[] columnNames = {"Booking ID", "Movie", "Show Time", "Seats", "Total (Rs)", "Status", "Booking Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel);
        UIUtils.styleTable(bookingsTable);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadCustomerBookings();

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(new EmptyBorder(16, 24, 8, 24));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Controls
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        bottomPanel.setBackground(UIUtils.COLOR_BG);

        JButton backBtn = UIUtils.createSecondaryButton("<- Back to Movie List");
        backBtn.setPreferredSize(new Dimension(180, 40));
        backBtn.addActionListener(e -> {
            parentFrame.setVisible(true);
            dispose();
        });

        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(110, 40));
        refreshBtn.addActionListener(e -> loadCustomerBookings());

        cancelButton = UIUtils.createDangerButton("Cancel Selected Booking");
        cancelButton.setPreferredSize(new Dimension(210, 40));
        cancelButton.addActionListener(e -> handleCancelBooking());

        bottomPanel.add(backBtn);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(cancelButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadCustomerBookings() {
        tableModel.setRowCount(0);
        List<Booking> list = bookingService.getBookingsByCustomer(customer.getPhoneNumber());
        for (Booking b : list) {
            tableModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getMovieTitle(),
                    b.getShowTime(),
                    b.getSeatsCommaSeparated(),
                    (int)b.getTotalAmount(),
                    b.getStatus(),
                    b.getBookingDate()
            });
        }
    }

    private void handleCancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking from the table to cancel.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 5);

        if ("CANCELLED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Booking #" + bookingId + " has already been cancelled.", "Already Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel booking #" + bookingId + "?\nThe reserved seats will become available for others to book.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean cancelled = bookingService.cancelBooking(bookingId, movieService);
                if (cancelled) {
                    JOptionPane.showMessageDialog(this, "Booking #" + bookingId + " successfully cancelled.\nSeats have been released.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCustomerBookings();
                }
            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Cancellation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
