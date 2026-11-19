package com.bookmyticket.model;

/**
 * Represents a standard theater seat (front and middle rows: A, B, C).
 * Demonstrates Inheritance and Polymorphic method overriding.
 */
public class RegularSeat extends Seat {

    public RegularSeat(String seatNumber, double basePrice) {
        super(seatNumber, basePrice);
    }

    @Override
    public double getPrice() {
        // Regular seat costs the movie's standard base price
        return getBasePrice();
    }

    @Override
    public String getSeatType() {
        return "Regular";
    }
}
