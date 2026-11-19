package com.bookmyticket.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a movie featured in the theater.
 * Encapsulates movie details and its associated screening shows.
 */
public class Movie {
    private String movieId;
    private String title;
    private String genre;
    private int durationMinutes;
    private String language;
    private double basePrice;
    private List<Show> shows;

    public Movie(String movieId, String title, String genre, int durationMinutes, String language, double basePrice) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.language = language;
        this.basePrice = basePrice;
        this.shows = new ArrayList<>();
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }

    public void addShow(Show show) {
        this.shows.add(show);
    }

    public Show getShowByTime(String time) {
        for (Show show : shows) {
            if (show.getTime().equalsIgnoreCase(time.trim())) {
                return show;
            }
        }
        return null;
    }

    public Show getShowById(String showId) {
        for (Show show : shows) {
            if (show.getShowId().equalsIgnoreCase(showId.trim())) {
                return show;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return title + " (" + language + ", " + genre + ", " + durationMinutes + "m, Base Rs." + (int)basePrice + ")";
    }
}
