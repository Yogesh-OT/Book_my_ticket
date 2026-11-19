package com.bookmyticket.ui;

import com.bookmyticket.exception.InvalidInputException;
import com.bookmyticket.model.Admin;
import com.bookmyticket.model.Booking;
import com.bookmyticket.model.Movie;
import com.bookmyticket.model.Show;
import com.bookmyticket.service.BookingService;
import com.bookmyticket.service.MovieService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Administrator dashboard for managing movies and viewing system-wide bookings.
 * Demonstrates:
 * - JTabbedPane with Movie Management and All Bookings views
 * - Modal dialog for adding movies with validation
 * - Dynamic table updates and persistent file synchronization
 */
public class AdminFrame extends JFrame {
    private final Admin admin;
    private final MovieService movieService;
    private final BookingService bookingService;

    // Movie tab components
    private JTable moviesTable;
    private DefaultTableModel moviesModel;

    // Bookings tab components
    private JTable bookingsTable;
    private DefaultTableModel bookingsModel;

    public AdminFrame(Admin admin, MovieService movieService, BookingService bookingService) {
        this.admin = admin;
        this.movieService = movieService;
        this.bookingService = bookingService;

        setTitle("Book My Ticket - Administrator Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.COLOR_BG);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                movieService.saveAll();
                bookingService.saveAllSync();
            }
        });

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 23, 42));
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel titleLbl = new JLabel("Administrator Dashboard");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightHeader.setOpaque(false);

        JLabel adminBadge = new JLabel("Role: " + admin.getRole());
        adminBadge.setFont(UIUtils.FONT_REGULAR);
        adminBadge.setForeground(new Color(148, 163, 184));

        JButton logoutBtn = UIUtils.createButton("Logout", new Color(185, 28, 28), Color.WHITE);
        logoutBtn.addActionListener(e -> {
            LoginFrame login = new LoginFrame(movieService, bookingService);
            login.setVisible(true);
            dispose();
        });

        rightHeader.add(adminBadge);
        rightHeader.add(logoutBtn);

        header.add(titleLbl, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center: JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_BOLD);

        tabbedPane.addTab("Manage Movies", createMovieManagementPanel());
        tabbedPane.addTab("View All Bookings", createAllBookingsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createMovieManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG);

        // Table
        String[] columns = {"ID", "Movie Title", "Genre", "Duration (mins)", "Language", "Base Price (Rs)", "Show Timings"};
        moviesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        moviesTable = new JTable(moviesModel);
        UIUtils.styleTable(moviesTable);
        moviesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refreshMoviesTable();

        JScrollPane scrollPane = new JScrollPane(moviesTable);
        scrollPane.setBorder(new EmptyBorder(16, 20, 8, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        actionPanel.setBackground(UIUtils.COLOR_BG);

        JButton addMovieBtn = UIUtils.createPrimaryButton("+ Add New Movie");
        addMovieBtn.addActionListener(e -> showAddMovieDialog());

        JButton removeMovieBtn = UIUtils.createDangerButton("Remove Selected Movie");
        removeMovieBtn.addActionListener(e -> handleRemoveMovie());

        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> refreshMoviesTable());

        actionPanel.add(refreshBtn);
        actionPanel.add(addMovieBtn);
        actionPanel.add(removeMovieBtn);

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAllBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.COLOR_BG);

        String[] columns = {"Booking ID", "Customer Name", "Phone", "Movie", "Show Time", "Seats", "Total (Rs)", "Status", "Date"};
        bookingsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        bookingsTable = new JTable(bookingsModel);
        UIUtils.styleTable(bookingsTable);
        refreshBookingsTable();

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(new EmptyBorder(16, 20, 8, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        actionPanel.setBackground(UIUtils.COLOR_BG);

        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh Bookings");
        refreshBtn.addActionListener(e -> refreshBookingsTable());
        actionPanel.add(refreshBtn);

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshMoviesTable() {
        moviesModel.setRowCount(0);
        List<Movie> movies = movieService.getAllMovies();
        for (Movie m : movies) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < m.getShows().size(); i++) {
                sb.append(m.getShows().get(i).getTime());
                if (i < m.getShows().size() - 1) sb.append(", ");
            }
            moviesModel.addRow(new Object[]{
                    m.getMovieId(),
                    m.getTitle(),
                    m.getGenre(),
                    m.getDurationMinutes(),
                    m.getLanguage(),
                    (int)m.getBasePrice(),
                    sb.toString()
            });
        }
    }

    private void refreshBookingsTable() {
        bookingsModel.setRowCount(0);
        List<Booking> bookings = bookingService.getAllBookings();
        for (Booking b : bookings) {
            bookingsModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getCustomer().getName(),
                    b.getCustomer().getPhoneNumber(),
                    b.getMovieTitle(),
                    b.getShowTime(),
                    b.getSeatsCommaSeparated(),
                    (int)b.getTotalAmount(),
                    b.getStatus(),
                    b.getBookingDate()
            });
        }
    }

    private void showAddMovieDialog() {
        JDialog dialog = new JDialog(this, "Add New Movie", true);
        dialog.setSize(440, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
        formPanel.setBackground(Color.WHITE);

        JTextField titleField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField langField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField showsField = new JTextField(); // e.g. "10:00 AM; 03:00 PM"

        formPanel.add(createFormField("Movie Title:", titleField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormField("Genre (e.g. Action, Drama):", genreField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormField("Duration (in minutes):", durationField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormField("Language (e.g. English, Hindi):", langField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormField("Base Ticket Price (Rs):", priceField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormField("Show Timings (separated by comma):", showsField));

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnPanel.setBackground(UIUtils.COLOR_BG);

        JButton cancelBtn = UIUtils.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UIUtils.createPrimaryButton("Save Movie");
        saveBtn.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String genre = genreField.getText().trim();
                String durStr = durationField.getText().trim();
                String lang = langField.getText().trim();
                String priceStr = priceField.getText().trim();
                String showsStr = showsField.getText().trim();

                if (title.isEmpty() || genre.isEmpty() || durStr.isEmpty() ||
                        lang.isEmpty() || priceStr.isEmpty() || showsStr.isEmpty()) {
                    throw new InvalidInputException("All fields are mandatory. Please fill in all fields.");
                }

                int duration;
                try {
                    duration = Integer.parseInt(durStr);
                    if (duration <= 0) throw new NumberFormatException();
                } catch (NumberFormatException nfe) {
                    throw new InvalidInputException("Duration must be a positive integer (minutes).");
                }

                double price;
                try {
                    price = Double.parseDouble(priceStr);
                    if (price <= 0) throw new NumberFormatException();
                } catch (NumberFormatException nfe) {
                    throw new InvalidInputException("Base price must be a positive number.");
                }

                String movieId = movieService.generateNextMovieId();
                Movie movie = new Movie(movieId, title, genre, duration, lang, price);

                String[] showArr = showsStr.split("[,;]");
                int showIdx = 1;
                for (String s : showArr) {
                    if (!s.trim().isEmpty()) {
                        movie.addShow(new Show("S" + showIdx++, s.trim(), price));
                    }
                }

                movieService.addMovie(movie);
                refreshMoviesTable();
                JOptionPane.showMessageDialog(dialog, "Movie \"" + title + "\" added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Input Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel createFormField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(UIUtils.FONT_REGULAR);
        field.setFont(UIUtils.FONT_REGULAR);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void handleRemoveMovie() {
        int selectedRow = moviesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a movie from the table to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String movieId = (String) moviesModel.getValueAt(selectedRow, 0);
        String title = (String) moviesModel.getValueAt(selectedRow, 1);

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove the movie \"" + title + "\" (" + movieId + ")?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            boolean removed = movieService.removeMovie(movieId);
            if (removed) {
                refreshMoviesTable();
                JOptionPane.showMessageDialog(this, "Movie \"" + title + "\" has been removed.", "Removed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove movie.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
