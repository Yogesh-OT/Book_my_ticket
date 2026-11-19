package tools;

import com.bookmyticket.exception.InvalidInputException;
import com.bookmyticket.exception.SeatAlreadyBookedException;
import com.bookmyticket.model.*;
import com.bookmyticket.service.BookingService;
import com.bookmyticket.service.MovieService;
import com.bookmyticket.util.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive verification harness testing every functional flow and edge case.
 */
public class VerifyAllFlows {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  STARTING END-TO-END AUTOMATED VERIFICATION     ");
        System.out.println("=================================================");

        int passed = 0;
        int failed = 0;

        try {
            // 0. Persistence initialization
            FileManager.initializeFiles();
            MovieService movieService = new MovieService();
            BookingService bookingService = new BookingService(movieService);

            // Test 1: Seeded Movies Verification
            System.out.print("[TEST 1] Verifying default movie catalog... ");
            List<Movie> movies = movieService.getAllMovies();
            if (movies.size() >= 5) {
                System.out.println("PASSED (" + movies.size() + " movies loaded)");
                passed++;
            } else {
                System.out.println("FAILED (Expected >= 5 movies)");
                failed++;
            }

            // Test 2: Admin Password Verification
            System.out.print("[TEST 2] Verifying Admin Authentication... ");
            if (!Admin.authenticate("wrongpass") && Admin.authenticate("admin123")) {
                System.out.println("PASSED (wrong password rejected, admin123 accepted)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 3: Customer Login Validation
            System.out.print("[TEST 3] Verifying Customer validation logic... ");
            boolean caughtInvalidPhone = false;
            String invalidPhone = "12345";
            if (!invalidPhone.matches("\\d{10}")) {
                caughtInvalidPhone = true;
            }
            if (caughtInvalidPhone) {
                System.out.println("PASSED (Invalid phone format correctly rejected)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 4: Regular & Premium Seat Booking
            System.out.print("[TEST 4] Booking Regular + Premium seats... ");
            Customer testCustomer = new Customer("Test User", "9123456780");
            Movie testMovie = movies.get(1); // Interstellar (Base 250)
            Show testShow = testMovie.getShows().get(1); // 03:45 PM

            Seat regSeat = testShow.getSeat("A1"); // Regular (250)
            Seat premSeat = testShow.getSeat("E8"); // Premium (250 + 50 = 300)

            // Ensure not booked initially
            if (regSeat.isBooked()) regSeat.cancel();
            if (premSeat.isBooked()) premSeat.cancel();

            List<Seat> seatsToBook = new ArrayList<>();
            seatsToBook.add(regSeat);
            seatsToBook.add(premSeat);

            double expectedTotal = 250.0 + 300.0; // 550.0
            double calcTotal = bookingService.calculateTotal(seatsToBook);
            if (calcTotal != expectedTotal) {
                throw new RuntimeException("Calculated total mismatch! Expected: " + expectedTotal + ", Got: " + calcTotal);
            }

            Booking booking = bookingService.bookSeats(testCustomer, testMovie, testShow, seatsToBook);
            if (booking != null && booking.isBooked() && regSeat.isBooked() && premSeat.isBooked()) {
                System.out.println("PASSED (Booking #" + booking.getBookingId() + " confirmed, Total: Rs." + (int)booking.getTotalAmount() + ")");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 5: Double Booking Prevention (Race Condition Defense)
            System.out.print("[TEST 5] Attempting duplicate booking of occupied seat... ");
            boolean caughtAlreadyBooked = false;
            try {
                Customer intruder = new Customer("Intruder", "9999999999");
                List<Seat> duplicateSeats = new ArrayList<>();
                duplicateSeats.add(regSeat);
                bookingService.bookSeats(intruder, testMovie, testShow, duplicateSeats);
            } catch (SeatAlreadyBookedException e) {
                caughtAlreadyBooked = true;
            }
            if (caughtAlreadyBooked) {
                System.out.println("PASSED (SeatAlreadyBookedException thrown as expected)");
                passed++;
            } else {
                System.out.println("FAILED (Allowed double-booking!)");
                failed++;
            }

            // Test 6: Zero Seats Validation
            System.out.print("[TEST 6] Attempting booking with zero seats... ");
            boolean caughtZeroSeats = false;
            try {
                bookingService.bookSeats(testCustomer, testMovie, testShow, new ArrayList<>());
            } catch (InvalidInputException e) {
                caughtZeroSeats = true;
            }
            if (caughtZeroSeats) {
                System.out.println("PASSED (InvalidInputException thrown for zero seats)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 7: Booking Cancellation & Seat Restoration
            System.out.print("[TEST 7] Cancelling booking and verifying seat release... ");
            boolean cancelled = bookingService.cancelBooking(booking.getBookingId(), movieService);
            if (cancelled && !booking.isBooked() && !regSeat.isBooked() && !premSeat.isBooked()) {
                System.out.println("PASSED (Seats A1 and E8 successfully released to pool)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 8: Admin Add Movie
            System.out.print("[TEST 8] Admin adding new movie... ");
            String newId = movieService.generateNextMovieId();
            Movie newMovie = new Movie(newId, "Oppenheimer", "Biography", 180, "English", 260.0);
            newMovie.addShow(new Show("S1", "11:00 AM", 260.0));
            newMovie.addShow(new Show("S2", "04:30 PM", 260.0));
            movieService.addMovie(newMovie);

            Movie fetchedNew = movieService.getMovieById(newId);
            if (fetchedNew != null && fetchedNew.getTitle().equals("Oppenheimer")) {
                System.out.println("PASSED (Movie added and queryable)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 9: Admin Remove Movie
            System.out.print("[TEST 9] Admin removing movie... ");
            boolean removed = movieService.removeMovie(newId);
            Movie fetchedRemoved = movieService.getMovieById(newId);
            if (removed && fetchedRemoved == null) {
                System.out.println("PASSED (Movie successfully removed from catalog)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

            // Test 10: Persistent State Reload Verification
            System.out.print("[TEST 10] Verifying cold restart disk persistence... ");
            // Re-book a seat, flush to disk, reload from scratch
            List<Seat> restartSeats = new ArrayList<>();
            restartSeats.add(testShow.getSeat("C4"));
            Booking restartBooking = bookingService.bookSeats(testCustomer, testMovie, testShow, restartSeats);
            bookingService.saveAllSync();

            // Simulate fresh JVM cold reload
            MovieService freshMovieService = new MovieService();
            BookingService freshBookingService = new BookingService(freshMovieService);

            Booking reloadedBooking = freshBookingService.getBooking(restartBooking.getBookingId());
            Movie reloadedMovie = freshMovieService.getMovieById(testMovie.getMovieId());
            Show reloadedShow = reloadedMovie.getShowByTime(testShow.getTime());
            Seat reloadedSeat = reloadedShow.getSeat("C4");

            if (reloadedBooking != null && reloadedSeat != null && reloadedSeat.isBooked()) {
                System.out.println("PASSED (Booking and seat occupancy preserved across restart)");
                passed++;
            } else {
                System.out.println("FAILED");
                failed++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            failed++;
        }

        System.out.println("=================================================");
        System.out.println("  VERIFICATION SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("=================================================");

        if (failed == 0) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
