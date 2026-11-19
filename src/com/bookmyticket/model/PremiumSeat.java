package com.bookmyticket.model;

/**
 * Represents a premium theater seat (back rows: D, E) with extra legroom.
 * Demonstrates Inheritance and Polymorphic method overriding.
 */
public class PremiumSeat extends Seat {
    private static final double PREMIUM_SURCHARGE = 50.0;

    public PremiumSeat(String seatNumber, double basePrice) {
        super(seatNumber, basePrice);
    }

    @Override
    public double getPrice() {
        // Premium seat includes a fixed luxury surcharge
        return getBasePrice() + PREMIUM_SURCHARGE;
    }

    @Override
    public String getSeatType() {
        return "Premium";
    }

    public static double getSurcharge() {
        return PREMIUM_SURCHARGE;
    }
}
