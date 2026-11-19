package com.bookmyticket;

import com.bookmyticket.service.BookingService;
import com.bookmyticket.service.MovieService;
import com.bookmyticket.ui.LoginFrame;
import com.bookmyticket.util.FileManager;

import javax.swing.*;

/**
 * Main application entry point for Book My Ticket.
 * Initializes the data storage, services, and launches the login window on the Swing EDT.
 */
public class Main {
    public static void main(String[] args) {
        // Set System Look and Feel for native OS aesthetics
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to default Swing Look and Feel
        }

        // Initialize persistent storage files and seed data if missing
        FileManager.initializeFiles();

        // Initialize services
        MovieService movieService = new MovieService();
        BookingService bookingService = new BookingService(movieService);

        // Add JVM shutdown hook to guarantee data is flushed upon exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bookingService.saveAllSync();
            movieService.saveAll();
        }));

        // Launch UI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(movieService, bookingService);
            loginFrame.setVisible(true);
        });
    }
}
