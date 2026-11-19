package com.bookmyticket.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer booking.
 * Implements Bookable interface to encapsulate booking state and actions.
 */
public class Booking implements Bookable {
    private String bookingId;
    private Customer customer;
    private String movieId;
    private String movieTitle;
    private String showTime;
    private List<String> seatNumbers;
    private double totalAmount;
    private String status; // "CONFIRMED" or "CANCELLED"
    private String bookingDate;

    public Booking(String bookingId, Customer customer, String movieId, String movieTitle,
                   String showTime, List<String> seatNumbers, double totalAmount,
                   String status, String bookingDate) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.showTime = showTime;
        this.seatNumbers = (seatNumbers != null) ? new ArrayList<>(seatNumbers) : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.status = status;
        this.bookingDate = bookingDate;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Override
    public void book() {
        this.status = "CONFIRMED";
    }

    @Override
    public void cancel() {
        this.status = "CANCELLED";
    }

    @Override
    public boolean isBooked() {
        return "CONFIRMED".equalsIgnoreCase(this.status);
    }

    public String getSeatsCommaSeparated() {
        return String.join(", ", seatNumbers);
    }

    @Override
    public String toString() {
        return "Booking #" + bookingId + " | " + movieTitle + " at " + showTime +
               " | Seats: " + getSeatsCommaSeparated() + " | Rs." + (int)totalAmount +
               " | " + status;
    }
}
