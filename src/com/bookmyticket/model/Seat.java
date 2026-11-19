package com.bookmyticket.model;

/**
 * Abstract class representing a theater seat.
 * Demonstrates Abstraction, Encapsulation, and implements Bookable interface.
 */
public abstract class Seat implements Bookable {
    private String seatNumber;
    private double basePrice;
    private boolean booked;

    public Seat(String seatNumber, double basePrice) {
        this.seatNumber = seatNumber;
        this.basePrice = basePrice;
        this.booked = false;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    @Override
    public void book() {
        this.booked = true;
    }

    @Override
    public void cancel() {
        this.booked = false;
    }

    /**
     * Polymorphic method to get effective price of this seat.
     * Overridden by subclasses RegularSeat and PremiumSeat.
     */
    public abstract double getPrice();

    /**
     * Polymorphic method to get the category name ("Regular" or "Premium").
     */
    public abstract String getSeatType();

    @Override
    public String toString() {
        return seatNumber + " [" + getSeatType() + " - Rs." + (int)getPrice() + "]";
    }
}
