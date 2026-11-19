package com.bookmyticket.ui;

import com.bookmyticket.exception.InvalidInputException;
import com.bookmyticket.model.Admin;
import com.bookmyticket.model.Customer;
import com.bookmyticket.service.BookingService;
import com.bookmyticket.service.MovieService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login screen allowing users to log in as Customer or Admin.
 * Demonstrates:
 * - GUI components (JFrame, JPanel, JTextField, JPasswordField, JRadioButton)
 * - Event Handling (ActionListener)
 * - Exception Handling (custom InvalidInputException caught and presented via JOptionPane)
 */
public class LoginFrame extends JFrame {
    private final MovieService movieService;
    private final BookingService bookingService;

    private JRadioButton customerRadio;
    private JRadioButton adminRadio;
    private JPanel customerPanel;
    private JPanel adminPanel;
    private JTextField nameField;
    private JTextField phoneField;
    private JPasswordField passwordField;

    public LoginFrame(MovieService movieService, BookingService bookingService) {
        this.movieService = movieService;
        this.bookingService = bookingService;

        setTitle("Book My Ticket - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIUtils.COLOR_BG);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42));
        headerPanel.setBorder(new EmptyBorder(24, 24, 20, 24));

        JLabel titleLabel = new JLabel("Book My Ticket", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Movie Ticket Booking System", SwingConstants.CENTER);
        subtitleLabel.setFont(UIUtils.FONT_REGULAR);
        subtitleLabel.setForeground(new Color(148, 163, 184));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(Box.createVerticalStrut(6), BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Center card panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UIUtils.COLOR_BG);
        centerPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Role selection
        JLabel roleLabel = new JLabel("Select Account Type:");
        roleLabel.setFont(UIUtils.FONT_BOLD);
        roleLabel.setForeground(UIUtils.COLOR_TEXT_MAIN);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        rolePanel.setBackground(UIUtils.COLOR_BG);

        customerRadio = new JRadioButton("Customer", true);
        customerRadio.setFont(UIUtils.FONT_BOLD);
        customerRadio.setBackground(UIUtils.COLOR_BG);
        customerRadio.setFocusPainted(false);

        adminRadio = new JRadioButton("Admin", false);
        adminRadio.setFont(UIUtils.FONT_BOLD);
        adminRadio.setBackground(UIUtils.COLOR_BG);
        adminRadio.setFocusPainted(false);

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(customerRadio);
        roleGroup.add(adminRadio);

        rolePanel.add(customerRadio);
        rolePanel.add(adminRadio);

        // Customer Inputs Panel
        customerPanel = new JPanel();
        customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));
        customerPanel.setBackground(Color.WHITE);
        customerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel nameLbl = new JLabel("Full Name:");
        nameLbl.setFont(UIUtils.FONT_REGULAR);
        nameField = new JTextField(15);
        nameField.setFont(UIUtils.FONT_REGULAR);

        JLabel phoneLbl = new JLabel("Phone Number (10 digits):");
        phoneLbl.setFont(UIUtils.FONT_REGULAR);
        phoneField = new JTextField(15);
        phoneField.setFont(UIUtils.FONT_REGULAR);

        customerPanel.add(nameLbl);
        customerPanel.add(Box.createVerticalStrut(4));
        customerPanel.add(nameField);
        customerPanel.add(Box.createVerticalStrut(12));
        customerPanel.add(phoneLbl);
        customerPanel.add(Box.createVerticalStrut(4));
        customerPanel.add(phoneField);

        // Admin Inputs Panel
        adminPanel = new JPanel();
        adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.Y_AXIS));
        adminPanel.setBackground(Color.WHITE);
        adminPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        adminPanel.setVisible(false);

        JLabel pwdLbl = new JLabel("Admin Password (default: admin123):");
        pwdLbl.setFont(UIUtils.FONT_REGULAR);
        passwordField = new JPasswordField(15);
        passwordField.setFont(UIUtils.FONT_REGULAR);

        adminPanel.add(pwdLbl);
        adminPanel.add(Box.createVerticalStrut(4));
        adminPanel.add(passwordField);

        // Radio Action Listeners to toggle panels
        ActionListener toggleListener = e -> {
            boolean isCustomer = customerRadio.isSelected();
            customerPanel.setVisible(isCustomer);
            adminPanel.setVisible(!isCustomer);
            centerPanel.revalidate();
            centerPanel.repaint();
        };
        customerRadio.addActionListener(toggleListener);
        adminRadio.addActionListener(toggleListener);

        centerPanel.add(roleLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(rolePanel);
        centerPanel.add(Box.createVerticalStrut(16));
        centerPanel.add(customerPanel);
        centerPanel.add(adminPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Actions Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
        bottomPanel.setBackground(UIUtils.COLOR_BG);

        JButton loginButton = UIUtils.createPrimaryButton("Continue to Book My Ticket");
        loginButton.setPreferredSize(new Dimension(380, 42));
        loginButton.addActionListener(e -> handleLogin());

        bottomPanel.add(loginButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Set default enter key action
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        try {
            if (customerRadio.isSelected()) {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();

                // Input validation with custom exception
                if (name.isEmpty()) {
                    throw new InvalidInputException("Customer name cannot be empty. Please enter your name.");
                }
                if (phone.isEmpty()) {
                    throw new InvalidInputException("Phone number cannot be empty. Please enter your 10-digit number.");
                }
                if (!phone.matches("\\d{10}")) {
                    throw new InvalidInputException("Invalid phone number. Must be exactly 10 digits (e.g., 9876543210).");
                }

                Customer customer = new Customer(name, phone);
                MovieListFrame movieFrame = new MovieListFrame(customer, movieService, bookingService);
                movieFrame.setVisible(true);
                dispose();

            } else {
                String password = new String(passwordField.getPassword()).trim();
                if (password.isEmpty()) {
                    throw new InvalidInputException("Please enter the administrator password.");
                }

                if (!Admin.authenticate(password)) {
                    throw new InvalidInputException("Incorrect admin password! Check README for default credentials (admin123).");
                }

                Admin admin = new Admin("Administrator");
                AdminFrame adminFrame = new AdminFrame(admin, movieService, bookingService);
                adminFrame.setVisible(true);
                dispose();
            }
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
