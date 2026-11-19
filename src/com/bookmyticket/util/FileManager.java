package com.bookmyticket.util;

import com.bookmyticket.model.*;

import java.io.*;
import java.util.*;

/**
 * Utility class for persistent file storage.
 * Demonstrates File Handling with File, BufferedReader, BufferedWriter,
 * and robust try-catch-finally error management.
 */
public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String MOVIES_FILE = "data/movies.txt";
    private static final String BOOKINGS_FILE = "data/bookings.txt";

    /**
     * Ensures the data directory and required files exist on startup.
     */
    public static void initializeFiles() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File moviesFile = new File(MOVIES_FILE);
        if (!moviesFile.exists()) {
            seedDefaultMovies();
        }

        File bookingsFile = new File(BOOKINGS_FILE);
        if (!bookingsFile.exists()) {
            try {
                bookingsFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating bookings file: " + e.getMessage());
            }
        }
    }

    /**
     * Seeds initial 5 popular movies if movies.txt does not exist or is empty.
     */
    public static void seedDefaultMovies() {
        List<Movie> defaultMovies = new ArrayList<>();

        Movie m1 = new Movie("M1", "Inception", "Sci-Fi", 148, "English", 200.0);
        m1.addShow(new Show("S1", "10:00 AM", 200.0));
        m1.addShow(new Show("S2", "02:30 PM", 200.0));
        m1.addShow(new Show("S3", "06:45 PM", 200.0));
        defaultMovies.add(m1);

        Movie m2 = new Movie("M2", "Interstellar", "Sci-Fi", 169, "English", 250.0);
        m2.addShow(new Show("S1", "11:15 AM", 250.0));
        m2.addShow(new Show("S2", "03:45 PM", 250.0));
        m2.addShow(new Show("S3", "08:00 PM", 250.0));
        defaultMovies.add(m2);

        Movie m3 = new Movie("M3", "The Dark Knight", "Action", 152, "English", 220.0);
        m3.addShow(new Show("S1", "09:30 AM", 220.0));
        m3.addShow(new Show("S2", "01:15 PM", 220.0));
        m3.addShow(new Show("S3", "07:00 PM", 220.0));
        defaultMovies.add(m3);

        Movie m4 = new Movie("M4", "Avatar: Way of Water", "Sci-Fi", 192, "English", 240.0);
        m4.addShow(new Show("S1", "10:30 AM", 240.0));
        m4.addShow(new Show("S2", "04:00 PM", 240.0));
        m4.addShow(new Show("S3", "08:30 PM", 240.0));
        defaultMovies.add(m4);

        Movie m5 = new Movie("M5", "Dangal", "Drama", 161, "Hindi", 180.0);
        m5.addShow(new Show("S1", "12:00 PM", 180.0));
        m5.addShow(new Show("S2", "05:00 PM", 180.0));
        m5.addShow(new Show("S3", "09:00 PM", 180.0));
        defaultMovies.add(m5);

        saveMovies(defaultMovies);
    }

    /**
     * Reads movies from data/movies.txt using BufferedReader.
     * Demonstrates try-catch-finally block.
     */
    public static List<Movie> loadMovies() {
        List<Movie> movies = new ArrayList<>();
        File file = new File(MOVIES_FILE);
        if (!file.exists() || file.length() == 0) {
            seedDefaultMovies();
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                // Format: movieId,title,genre,duration,language,basePrice,timing1;timing2
                String[] parts = line.split(",", -1);
                if (parts.length >= 7) {
                    String movieId = parts[0].trim();
                    String title = parts[1].trim();
                    String genre = parts[2].trim();
                    int duration = Integer.parseInt(parts[3].trim());
                    String language = parts[4].trim();
                    double basePrice = Double.parseDouble(parts[5].trim());

                    Movie movie = new Movie(movieId, title, genre, duration, language, basePrice);
                    String[] times = parts[6].split(";");
                    int showIdx = 1;
                    for (String time : times) {
                        if (!time.trim().isEmpty()) {
                            movie.addShow(new Show("S" + showIdx++, time.trim(), basePrice));
                        }
                    }
                    movies.add(movie);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Movies file not found: " + e.getMessage());
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading movies file: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Failed to close reader: " + e.getMessage());
                }
            }
        }
        return movies;
    }

    /**
     * Saves movies list to data/movies.txt using BufferedWriter.
     * Demonstrates try-catch-finally block.
     */
    public static void saveMovies(List<Movie> movies) {
        File file = new File(MOVIES_FILE);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, false));
            for (Movie movie : movies) {
                StringBuilder sb = new StringBuilder();
                sb.append(movie.getMovieId()).append(",");
                sb.append(movie.getTitle()).append(",");
                sb.append(movie.getGenre()).append(",");
                sb.append(movie.getDurationMinutes()).append(",");
                sb.append(movie.getLanguage()).append(",");
                sb.append(movie.getBasePrice()).append(",");

                List<Show> shows = movie.getShows();
                for (int i = 0; i < shows.size(); i++) {
                    sb.append(shows.get(i).getTime());
                    if (i < shows.size() - 1) {
                        sb.append(";");
                    }
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving movies: " + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Failed to close writer: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Reads bookings from data/bookings.txt into a Map.
     * Demonstrates Collections (HashMap) and try-catch-finally.
     */
    public static Map<String, Booking> loadBookings() {
        Map<String, Booking> bookings = new HashMap<>();
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) {
            return bookings;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                // Format: bookingId,customerName,customerPhone,movieId,movieTitle,showTime,seats,totalAmount,status,bookingDate
                String[] parts = line.split(",", -1);
                if (parts.length >= 10) {
                    String bookingId = parts[0].trim();
                    String customerName = parts[1].trim();
                    String customerPhone = parts[2].trim();
                    String movieId = parts[3].trim();
                    String movieTitle = parts[4].trim();
                    String showTime = parts[5].trim();
                    String[] seatsArr = parts[6].split(";");
                    List<String> seats = new ArrayList<>();
                    for (String s : seatsArr) {
                        if (!s.trim().isEmpty()) {
                            seats.add(s.trim());
                        }
                    }
                    double totalAmount = Double.parseDouble(parts[7].trim());
                    String status = parts[8].trim();
                    String bookingDate = parts[9].trim();

                    Customer customer = new Customer(customerName, customerPhone);
                    Booking booking = new Booking(bookingId, customer, movieId, movieTitle,
                            showTime, seats, totalAmount, status, bookingDate);
                    bookings.put(bookingId, booking);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Bookings file not found: " + e.getMessage());
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading bookings file: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Failed to close reader: " + e.getMessage());
                }
            }
        }
        return bookings;
    }

    /**
     * Saves all bookings to data/bookings.txt using BufferedWriter.
     * Demonstrates try-catch-finally block.
     */
    public static void saveBookings(Collection<Booking> bookings) {
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
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Failed to close writer: " + e.getMessage());
                }
            }
        }
    }
}
