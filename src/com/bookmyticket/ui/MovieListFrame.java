package com.bookmyticket.ui;

import com.bookmyticket.model.Customer;
import com.bookmyticket.model.Movie;
import com.bookmyticket.model.Show;
import com.bookmyticket.service.BookingService;
import com.bookmyticket.service.MovieService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Screen displaying available movies and screening shows.
 * Demonstrates:
 * - GUI components (JFrame, JTable, JScrollPane, JComboBox, JButton)
 * - Event Handling (ListSelectionListener for movie table, ActionListener for buttons, WindowListener for auto-save)
 */
public class MovieListFrame extends JFrame {
    private final Customer customer;
    private final MovieService movieService;
    private final BookingService bookingService;

    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> showCombo;
    private JLabel detailsLabel;
    private JButton selectSeatsButton;

    public MovieListFrame(Customer customer, MovieService movieService, BookingService bookingService) {
        this.customer = customer;
        this.movieService = movieService;
        this.bookingService = bookingService;

        setTitle("Book My Ticket - Browse Movies");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 560);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.COLOR_BG);

        // WindowListener to save state on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                bookingService.saveAllSync();
                movieService.saveAll();
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

        JLabel titleLbl = new JLabel("Now Showing");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userPanel.setOpaque(false);

        JLabel userLbl = new JLabel("Logged in as: " + customer.getName() + " (" + customer.getPhoneNumber() + ")");
        userLbl.setFont(UIUtils.FONT_REGULAR);
        userLbl.setForeground(new Color(203, 213, 225));

        JButton myBookingsBtn = UIUtils.createButton("My Bookings", new Color(51, 65, 85), Color.WHITE);
        myBookingsBtn.addActionListener(e -> {
            MyBookingsFrame bookingsFrame = new MyBookingsFrame(customer, movieService, bookingService, this);
            bookingsFrame.setVisible(true);
            setVisible(false);
        });

        JButton logoutBtn = UIUtils.createButton("Logout", new Color(185, 28, 28), Color.WHITE);
        logoutBtn.addActionListener(e -> {
            LoginFrame login = new LoginFrame(movieService, bookingService);
            login.setVisible(true);
            dispose();
        });

        userPanel.add(userLbl);
        userPanel.add(myBookingsBtn);
        userPanel.add(logoutBtn);

        header.add(titleLbl, BorderLayout.WEST);
        header.add(userPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center: Movie Table
        String[] columnNames = {"Movie ID", "Title", "Genre", "Duration", "Language", "Base Price", "Shows Available"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        movieTable = new JTable(tableModel);
        UIUtils.styleTable(movieTable);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        populateMovieTable();

        JScrollPane scrollPane = new JScrollPane(movieTable);
        scrollPane.setBorder(new EmptyBorder(16, 24, 8, 24));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom: Selection & Action Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UIUtils.COLOR_BG);
        bottomPanel.setBorder(new EmptyBorder(12, 24, 20, 24));

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));

        JLabel selectShowLbl = new JLabel("Select Show Time:");
        selectShowLbl.setFont(UIUtils.FONT_BOLD);
        showCombo = new JComboBox<>();
        showCombo.setFont(UIUtils.FONT_REGULAR);
        showCombo.setPreferredSize(new Dimension(160, 32));

        detailsLabel = new JLabel("Please select a movie from the table above.");
        detailsLabel.setFont(UIUtils.FONT_REGULAR);
        detailsLabel.setForeground(UIUtils.COLOR_TEXT_MUTED);

        selectionPanel.add(selectShowLbl);
        selectionPanel.add(showCombo);
        selectionPanel.add(Box.createHorizontalStrut(20));
        selectionPanel.add(detailsLabel);

        selectSeatsButton = UIUtils.createPrimaryButton("Select Seats ->");
        selectSeatsButton.setPreferredSize(new Dimension(180, 42));
        selectSeatsButton.setEnabled(false);

        selectSeatsButton.addActionListener(e -> handleSelectSeats());

        bottomPanel.add(selectionPanel, BorderLayout.CENTER);
        bottomPanel.add(selectSeatsButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // ListSelectionListener: updates show timings when a movie is highlighted
        movieTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateSelectedMovie();
                }
            }
        });

        if (movieTable.getRowCount() > 0) {
            movieTable.setRowSelectionInterval(0, 0);
        }
    }

    public void populateMovieTable() {
        tableModel.setRowCount(0);
        List<Movie> movies = movieService.getAllMovies();
        for (Movie movie : movies) {
            StringBuilder showTimes = new StringBuilder();
            for (int i = 0; i < movie.getShows().size(); i++) {
                showTimes.append(movie.getShows().get(i).getTime());
                if (i < movie.getShows().size() - 1) {
                    showTimes.append(", ");
                }
            }

            tableModel.addRow(new Object[]{
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getGenre(),
                    movie.getDurationMinutes() + " mins",
                    movie.getLanguage(),
                    "Rs. " + (int)movie.getBasePrice(),
                    showTimes.toString()
            });
        }
    }

    private void updateSelectedMovie() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow >= 0) {
            String movieId = (String) tableModel.getValueAt(selectedRow, 0);
            Movie movie = movieService.getMovieById(movieId);
            if (movie != null) {
                showCombo.removeAllItems();
                for (Show show : movie.getShows()) {
                    showCombo.addItem(show.getTime());
                }
                detailsLabel.setText("Selected: " + movie.getTitle() + " | Base Ticket: Rs. " + (int)movie.getBasePrice());
                selectSeatsButton.setEnabled(true);
                return;
            }
        }
        showCombo.removeAllItems();
        detailsLabel.setText("Please select a movie from the table above.");
        selectSeatsButton.setEnabled(false);
    }

    private void handleSelectSeats() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a movie first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String movieId = (String) tableModel.getValueAt(selectedRow, 0);
        Movie movie = movieService.getMovieById(movieId);
        String selectedTime = (String) showCombo.getSelectedItem();

        if (movie == null || selectedTime == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid movie and show time.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Show show = movie.getShowByTime(selectedTime);
        if (show == null) {
            JOptionPane.showMessageDialog(this, "Show time details not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SeatSelectionFrame seatFrame = new SeatSelectionFrame(customer, movie, show, movieService, bookingService, this);
        seatFrame.setVisible(true);
        setVisible(false);
    }
}
