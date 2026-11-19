package com.bookmyticket.ui;

import com.bookmyticket.exception.InvalidInputException;
import com.bookmyticket.exception.SeatAlreadyBookedException;
import com.bookmyticket.model.*;
import com.bookmyticket.service.BookingService;
import com.bookmyticket.service.MovieService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen for selecting theater seats and confirming reservations.
 * Demonstrates:
 * - Seat layout grid (5 rows x 8 cols)
 * - Polymorphic pricing: RegularSeat vs PremiumSeat
 * - Collections: List<Seat> for selected seats
 * - Event Handling: ActionListener on seat toggle buttons
 * - Custom Exceptions: SeatAlreadyBookedException, InvalidInputException
 * - Threaded save with SwingWorker in background
 */
public class SeatSelectionFrame extends JFrame {
    private final Customer customer;
    private final Movie movie;
    private final Show show;
    private final MovieService movieService;
    private final BookingService bookingService;
    private final JFrame parentFrame;

    // Collections: List of selected seats
    private final List<Seat> selectedSeats = new ArrayList<>();

    private JLabel selectionSummaryLabel;
    private JLabel totalAmountLabel;
    private JButton confirmButton;
    private JPanel seatGridPanel;

    public SeatSelectionFrame(Customer customer, Movie movie, Show show,
                              MovieService movieService, BookingService bookingService,
                              JFrame parentFrame) {
        this.customer = customer;
        this.movie = movie;
        this.show = show;
        this.movieService = movieService;
        this.bookingService = bookingService;
        this.parentFrame = parentFrame;

        setTitle("Book My Ticket - Seat Selection (" + movie.getTitle() + ")");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(880, 680);
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

        JLabel titleLbl = new JLabel(movie.getTitle() + " - " + show.getTime());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(Color.WHITE);

        JLabel infoLbl = new JLabel(movie.getLanguage() + " | " + movie.getGenre() + " | Regular: Rs." +
                (int)movie.getBasePrice() + " | Premium: Rs." + (int)(movie.getBasePrice() + PremiumSeat.getSurcharge()));
        infoLbl.setFont(UIUtils.FONT_REGULAR);
        infoLbl.setForeground(new Color(203, 213, 225));

        header.add(titleLbl, BorderLayout.WEST);
        header.add(infoLbl, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center Container
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setBackground(UIUtils.COLOR_BG);
        centerContainer.setBorder(new EmptyBorder(16, 32, 16, 32));

        // Screen Curved Representation
        JPanel screenPanel = new JPanel();
        screenPanel.setPreferredSize(new Dimension(650, 40));
        screenPanel.setMaximumSize(new Dimension(750, 40));
        screenPanel.setBackground(new Color(203, 213, 225));
        screenPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184), 2, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JLabel screenLabel = new JLabel("------------- ALL EYES THIS WAY: CINEMA SCREEN -------------", SwingConstants.CENTER);
        screenLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        screenLabel.setForeground(new Color(51, 65, 85));
        screenPanel.add(screenLabel);

        centerContainer.add(screenPanel);
        centerContainer.add(Box.createVerticalStrut(20));

        // Seat Grid (5 rows x 8 cols)
        seatGridPanel = new JPanel(new GridLayout(5, 8, 10, 10));
        seatGridPanel.setBackground(UIUtils.COLOR_BG);
        seatGridPanel.setMaximumSize(new Dimension(750, 260));

        buildSeatButtons();
        centerContainer.add(seatGridPanel);
        centerContainer.add(Box.createVerticalStrut(20));

        // Legend Panel
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 6));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(6, 16, 6, 16)
        ));
        legendPanel.setMaximumSize(new Dimension(750, 44));

        legendPanel.add(createLegendBadge("Regular (A-C)", UIUtils.COLOR_SEAT_REGULAR));
        legendPanel.add(createLegendBadge("Premium (D-E)", UIUtils.COLOR_SEAT_PREMIUM));
        legendPanel.add(createLegendBadge("Selected", UIUtils.COLOR_SEAT_SELECTED));
        legendPanel.add(createLegendBadge("Booked (Unavailable)", UIUtils.COLOR_SEAT_BOOKED));

        centerContainer.add(legendPanel);
        add(centerContainer, BorderLayout.CENTER);

        // Bottom Summary & Actions Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                new EmptyBorder(16, 24, 16, 24)
        ));

        JPanel summaryLeft = new JPanel();
        summaryLeft.setLayout(new BoxLayout(summaryLeft, BoxLayout.Y_AXIS));
        summaryLeft.setOpaque(false);

        selectionSummaryLabel = new JLabel("Selected Seats: None");
        selectionSummaryLabel.setFont(UIUtils.FONT_REGULAR);
        selectionSummaryLabel.setForeground(UIUtils.COLOR_TEXT_MAIN);

        totalAmountLabel = new JLabel("Total Amount: Rs. 0");
        totalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalAmountLabel.setForeground(UIUtils.COLOR_PRIMARY);

        summaryLeft.add(selectionSummaryLabel);
        summaryLeft.add(Box.createVerticalStrut(4));
        summaryLeft.add(totalAmountLabel);

        JPanel buttonRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonRight.setOpaque(false);

        JButton backButton = UIUtils.createSecondaryButton("<- Back to Movies");
        backButton.setPreferredSize(new Dimension(160, 42));
        backButton.addActionListener(e -> {
            parentFrame.setVisible(true);
            dispose();
        });

        confirmButton = UIUtils.createSuccessButton("Confirm Booking");
        confirmButton.setPreferredSize(new Dimension(180, 42));
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> handleConfirmBooking());

        buttonRight.add(backButton);
        buttonRight.add(confirmButton);

        bottomPanel.add(summaryLeft, BorderLayout.WEST);
        bottomPanel.add(buttonRight, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createLegendBadge(String labelText, Color color) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badge.setOpaque(false);

        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(18, 18));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(color.darker(), 1, true));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIUtils.FONT_SMALL);
        lbl.setForeground(UIUtils.COLOR_TEXT_MAIN);

        badge.add(colorBox);
        badge.add(lbl);
        return badge;
    }

    private void buildSeatButtons() {
        seatGridPanel.removeAll();
        for (Seat seat : show.getSeats()) {
            JButton seatBtn = new JButton(seat.getSeatNumber());
            seatBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            seatBtn.setFocusPainted(false);
            seatBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (seat.isBooked()) {
                // Booked seat: disabled and grayed out
                seatBtn.setBackground(UIUtils.COLOR_SEAT_BOOKED);
                seatBtn.setForeground(Color.WHITE);
                seatBtn.setEnabled(false);
                seatBtn.setToolTipText(seat.getSeatNumber() + " is already booked.");
            } else {
                // Available seat: Regular or Premium color
                boolean isPremium = seat instanceof PremiumSeat;
                Color defaultBg = isPremium ? UIUtils.COLOR_SEAT_PREMIUM : UIUtils.COLOR_SEAT_REGULAR;
                Color currentBg = selectedSeats.contains(seat) ? UIUtils.COLOR_SEAT_SELECTED : defaultBg;
                seatBtn.setBackground(currentBg);
                seatBtn.setForeground(Color.WHITE);
                seatBtn.setToolTipText(seat.getSeatNumber() + " (" + seat.getSeatType() + " - Rs." + (int)seat.getPrice() + ")");

                // Event handling: ActionListener for seat selection toggle
                seatBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toggleSeatSelection(seat, seatBtn, defaultBg);
                    }
                });
            }
            seatGridPanel.add(seatBtn);
        }
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private void toggleSeatSelection(Seat seat, JButton seatBtn, Color defaultBg) {
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
            seatBtn.setBackground(defaultBg);
        } else {
            selectedSeats.add(seat);
            seatBtn.setBackground(UIUtils.COLOR_SEAT_SELECTED);
        }
        updateSummary();
    }

    private void updateSummary() {
        if (selectedSeats.isEmpty()) {
            selectionSummaryLabel.setText("Selected Seats: None");
            totalAmountLabel.setText("Total Amount: Rs. 0");
            confirmButton.setEnabled(false);
        } else {
            StringBuilder sb = new StringBuilder("Selected Seats: ");
            for (int i = 0; i < selectedSeats.size(); i++) {
                Seat s = selectedSeats.get(i);
                sb.append(s.getSeatNumber()).append(" (").append(s.getSeatType()).append(")");
                if (i < selectedSeats.size() - 1) {
                    sb.append(", ");
                }
            }
            selectionSummaryLabel.setText(sb.toString());

            // Polymorphism: Calling overloaded calculateTotal(List<Seat>)
            double total = bookingService.calculateTotal(selectedSeats);
            totalAmountLabel.setText("Total Amount: Rs. " + (int)total);
            confirmButton.setEnabled(true);
        }
    }

    private void handleConfirmBooking() {
        try {
            // Validation: at least one seat selected
            if (selectedSeats.isEmpty()) {
                throw new InvalidInputException("No seats selected! Please select at least one seat before booking.");
            }

            // Polymorphism: calculate total
            double total = bookingService.calculateTotal(selectedSeats);

            // Show confirmation dialog before final submission
            StringBuilder summaryMsg = new StringBuilder();
            summaryMsg.append("Booking Summary:\n\n");
            summaryMsg.append("Customer: ").append(customer.getName()).append(" (").append(customer.getPhoneNumber()).append(")\n");
            summaryMsg.append("Movie: ").append(movie.getTitle()).append(" (").append(movie.getLanguage()).append(")\n");
            summaryMsg.append("Show Time: ").append(show.getTime()).append("\n");
            summaryMsg.append("Seats: ");
            for (Seat s : selectedSeats) {
                summaryMsg.append(s.getSeatNumber()).append(" [").append(s.getSeatType()).append(" - Rs.").append((int)s.getPrice()).append("] ");
            }
            summaryMsg.append("\nTotal Payable: Rs. ").append((int)total).append("\n\n");
            summaryMsg.append("Do you want to confirm this booking?");

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    summaryMsg.toString(),
                    "Confirm Ticket Booking",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                // Thread-safe booking invocation
                Booking confirmedBooking = bookingService.bookSeats(customer, movie, show, selectedSeats);

                // Show generated Ticket Dialog
                showTicketDialog(confirmedBooking);

                // Return to movie list
                parentFrame.setVisible(true);
                dispose();
            }

        } catch (SeatAlreadyBookedException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Seat Unavailable", JOptionPane.ERROR_MESSAGE);
            // Refresh grid to reflect newly booked seats
            buildSeatButtons();
            selectedSeats.clear();
            updateSummary();
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTicketDialog(Booking booking) {
        JDialog ticketDialog = new JDialog(this, "Ticket Confirmed!", true);
        ticketDialog.setSize(440, 480);
        ticketDialog.setLocationRelativeTo(this);
        ticketDialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.COLOR_ACCENT);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("BOOKING CONFIRMED", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        ticketDialog.add(header, BorderLayout.NORTH);

        JPanel ticketBody = new JPanel();
        ticketBody.setLayout(new BoxLayout(ticketBody, BoxLayout.Y_AXIS));
        ticketBody.setBackground(Color.WHITE);
        ticketBody.setBorder(new EmptyBorder(20, 24, 20, 24));

        ticketBody.add(createTicketRow("Booking Reference:", booking.getBookingId(), true));
        ticketBody.add(createTicketRow("Movie:", booking.getMovieTitle(), true));
        ticketBody.add(createTicketRow("Show Timing:", booking.getShowTime(), false));
        ticketBody.add(createTicketRow("Seats Reserved:", booking.getSeatsCommaSeparated(), true));
        ticketBody.add(createTicketRow("Customer:", booking.getCustomer().getName() + " (" + booking.getCustomer().getPhoneNumber() + ")", false));
        ticketBody.add(createTicketRow("Total Paid:", "Rs. " + (int)booking.getTotalAmount(), true));
        ticketBody.add(createTicketRow("Status:", booking.getStatus(), true));
        ticketBody.add(createTicketRow("Date & Time:", booking.getBookingDate(), false));

        ticketDialog.add(ticketBody, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        footer.setBackground(Color.WHITE);
        JButton closeBtn = UIUtils.createPrimaryButton("Done (Close Ticket)");
        closeBtn.setPreferredSize(new Dimension(220, 38));
        closeBtn.addActionListener(e -> ticketDialog.dispose());
        footer.add(closeBtn);

        ticketDialog.add(footer, BorderLayout.SOUTH);
        ticketDialog.setVisible(true);
    }

    private JPanel createTicketRow(String label, String value, boolean isBold) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(4, 0, 4, 0));

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
}
