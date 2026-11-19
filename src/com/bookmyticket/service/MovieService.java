package com.bookmyticket.service;

import com.bookmyticket.exception.InvalidInputException;
import com.bookmyticket.model.Movie;
import com.bookmyticket.model.Show;
import com.bookmyticket.util.FileManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service managing movies and show listings.
 * Demonstrates Collections (ArrayList), Encapsulation, and Business Logic separation.
 */
public class MovieService {
    private List<Movie> movies;

    public MovieService() {
        this.movies = new ArrayList<>(FileManager.loadMovies());
    }

    public List<Movie> getAllMovies() {
        return Collections.unmodifiableList(movies);
    }

    public Movie getMovieById(String movieId) {
        if (movieId == null) return null;
        for (Movie movie : movies) {
            if (movie.getMovieId().equalsIgnoreCase(movieId.trim())) {
                return movie;
            }
        }
        return null;
    }

    public Movie getMovieByTitle(String title) {
        if (title == null) return null;
        for (Movie movie : movies) {
            if (movie.getTitle().equalsIgnoreCase(title.trim())) {
                return movie;
            }
        }
        return null;
    }

    /**
     * Adds a new movie and saves to persistent storage.
     */
    public void addMovie(Movie movie) throws InvalidInputException {
        if (movie == null || movie.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("Movie title cannot be empty.");
        }
        if (movie.getBasePrice() <= 0) {
            throw new InvalidInputException("Base price must be greater than zero.");
        }
        if (movie.getShows().isEmpty()) {
            throw new InvalidInputException("A movie must have at least one show timing.");
        }
        movies.add(movie);
        FileManager.saveMovies(movies);
    }

    /**
     * Removes a movie by ID and updates persistent storage.
     */
    public boolean removeMovie(String movieId) {
        Movie toRemove = getMovieById(movieId);
        if (toRemove != null) {
            movies.remove(toRemove);
            FileManager.saveMovies(movies);
            return true;
        }
        return false;
    }

    /**
     * Generates the next sequential movie ID (e.g., M1, M2, ...).
     */
    public String generateNextMovieId() {
        int maxId = 0;
        for (Movie movie : movies) {
            String id = movie.getMovieId();
            if (id.startsWith("M")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxId) {
                        maxId = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return "M" + (maxId + 1);
    }

    public void reloadMovies() {
        this.movies = new ArrayList<>(FileManager.loadMovies());
    }

    public void saveAll() {
        FileManager.saveMovies(movies);
    }
}
