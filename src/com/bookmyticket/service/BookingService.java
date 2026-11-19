package com.bookmyticket.service;

import com.bookmyticket.exception.InvalidInputException;
import com.bookmyticket.exception.SeatAlreadyBookedException;
import com.bookmyticket.model.*;
import com.bookmyticket.util.FileManager;

import javax.swing.SwingWorker;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service managing ticket reservations and cancellations.
 * Demonstrates:
 * - Threading: SwingWorker background file saving.
 * - Concurrency: Synchronized booking method to prevent race conditions.
 * - Polymorphism (Method Overloading): calculateTotal methods.
 * - Collections: HashMap<String, Booking> for O(1) booking lookups.
 * - Exception Handling: Throws SeatAlreadyBookedException and InvalidInputException.
 */
public class BookingService {
    // HashMap collection: bookingId -> Booking
    private Map<String, Booking> bookings;
    private int nextBookingSequence;

    public BookingService(MovieService movieService) {
        this.bookings = new HashMap<>(FileManager.loadBookings());
        initSequence();
        syncBookedSeatsWithMovies(movieService.getAllMovies());
    }

    private void initSequence() {
        int max = 1000;
        for (String id : bookings.keySet()) {
            if (id.startsWith("BMT-")) {
                try {
                    int num = Integer.parseInt(id.substring(4));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        this.nextBookingSequence = max + 1;
    }

    /**
     * Synchronizes persistent confirmed bookings with the in-memory seat objects in shows.
     */
    public void syncBookedSeatsWithMovies(List<Movie> movies) {
        for (Booking booking : bookings.values()) {
            if (booking.isBooked()) {
                for (Movie movie : movies) {
                    if (movie.getMovieId().equalsIgnoreCase(booking.getMovieId())) {
                        Show show = movie.getShowByTime(booking.getShowTime());
                        if (show != null) {
                            for (String seatNo : booking.getSeatNumbers()) {
                                Seat seat = show.getSeat(seatNo);
                                if (seat != null) {
                                    seat.book();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // POLYMORPHISM: METHOD OVERLOADING EXAMPLES
    // ==========================================

    /**
     * Overloaded calculateTotal version 1:
     * Calculates total price by summing individual seat prices (polymorphic getPrice).
     */
    public double calculateTotal(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Seat seat : seats) {
            total += seat.getPrice();
        }
        return total;
    }

    /**
     * Overloaded calculateTotal version 2:
     * Calculates total price with an applied discount percentage.
     */
    public double calculateTotal(List<Seat> seats, double discountPercentage) {
        double subtotal = calculateTotal(seats);
        if (discountPercentage <= 0.0) {
            return subtotal;
        }
        double discount = subtotal * (discountPercentage / 100.0);
        return Math.max(0.0, subtotal - discount);
    }

    /**
     * Overloaded calculateTotal version 3:
     * Calculates total price for simple fixed price per seat calculation.
     */
    public double calculateTotal(int seatCount, double unitPrice) {
        if (seatCount <= 0 || unitPrice <= 0) {
            return 0.0;
        }
        return seatCount * unitPrice;
    }

    // ==========================================
    // SYNCHRONIZED THREAD-SAFE BOOKING METHOD
    // ==========================================

    /**
     * Thread-safe synchronized method to book tickets.
     * Prevents race conditions where two threads/users try to book the exact same seat.
     *
     * @param customer customer booking the tickets
     * @param movie selected movie
     * @param show selected show
     * @param selectedSeats list of seats chosen
     * @return confirmed Booking object
     * @throws SeatAlreadyBookedException if any selected seat is already occupied
     * @throws InvalidInputException if inputs are invalid or no seats are selected
     */
    public synchronized Booking bookSeats(Customer customer, Movie movie, Show show, List<Seat> selectedSeats)
            throws SeatAlreadyBookedException, InvalidInputException {

        if (customer == null || customer.getName().trim().isEmpty() || customer.getPhoneNumber().trim().isEmpty()) {
            throw new InvalidInputException("Customer details (name and phone number) must be provided.");
        }
        if (movie == null) {
            throw new InvalidInputException("Please select a movie.");
        }
        if (show == null) {
            throw new InvalidInputException("Please select a valid show timing.");
        }
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            throw new InvalidInputException("Please select at least one seat before confirming booking.");
        }

        // Verify none of the seats are already booked
        for (Seat seat : selectedSeats) {
            if (seat.isBooked()) {
                throw new SeatAlreadyBookedException(seat.getSeatNumber(), show.getTime());
            }
        }

        // Reserve seats
        List<String> seatNumbers = new ArrayList<>();
        for (Seat seat : selectedSeats) {
            seat.book();
            seatNumbers.add(seat.getSeatNumber());
        }

        double totalAmount = calculateTotal(selectedSeats);
        String bookingId = "BMT-" + (nextBookingSequence++);
        String bookingDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

        Booking booking = new Booking(
                bookingId,
                customer,
                movie.getMovieId(),
                movie.getTitle(),
                show.getTime(),
                seatNumbers,
                totalAmount,
                "CONFIRMED",
                bookingDate
        );

        bookings.put(bookingId, booking);

        // Save bookings asynchronously on a background thread so UI remains responsive
        saveBookingsAsync();

        return booking;
    }

    /**
     * Cancels an existing booking and frees up the occupied seats.
     */
    public synchronized boolean cancelBooking(String bookingId, MovieService movieService)
            throws InvalidInputException {
        if (bookingId == null || !bookings.containsKey(bookingId)) {
            throw new InvalidInputException("Booking ID not found: " + bookingId);
        }

        Booking booking = bookings.get(bookingId);
        if (!booking.isBooked()) {
            throw new InvalidInputException("Booking #" + bookingId + " is already cancelled.");
        }

        // Mark booking as cancelled
        booking.cancel();

        // Release seats back to the show
        Movie movie = movieService.getMovieById(booking.getMovieId());
        if (movie != null) {
            Show show = movie.getShowByTime(booking.getShowTime());
            if (show != null) {
                for (String seatNo : booking.getSeatNumbers()) {
                    Seat seat = show.getSeat(seatNo);
                    if (seat != null) {
                        seat.cancel();
                    }
                }
            }
        }

        // Persist update in background
        saveBookingsAsync();
        return true;
    }

    /**
     * Asynchronously persists bookings to disk using SwingWorker.
     * Keeps the Java Swing event dispatch thread (EDT) completely responsive.
     */
    public void saveBookingsAsync() {
        // Create an unmodifiable snapshot of bookings to write safely
        final List<Booking> snapshot = new ArrayList<>(bookings.values());

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                FileManager.saveBookings(snapshot);
                return null;
            }

            @Override
            protected void done() {
                // Background file save completed
            }
        };
        worker.execute();
    }

    /**
     * Synchronously persists bookings (e.g., during window close shutdown).
     */
    public void saveAllSync() {
        FileManager.saveBookings(bookings.values());
    }

    public Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>(bookings.values());
        list.sort((b1, b2) -> b2.getBookingId().compareTo(b1.getBookingId()));
        return list;
    }

    public List<Booking> getBookingsByCustomer(String phoneNumber) {
        List<Booking> customerBookings = new ArrayList<>();
        if (phoneNumber == null) return customerBookings;

        for (Booking booking : bookings.values()) {
            if (phoneNumber.trim().equals(booking.getCustomer().getPhoneNumber().trim())) {
                customerBookings.add(booking);
            }
        }
        customerBookings.sort((b1, b2) -> b2.getBookingId().compareTo(b1.getBookingId()));
        return customerBookings;
    }
}
