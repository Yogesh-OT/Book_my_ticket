package com.bookmyticket.model;

/**
 * Interface demonstrating Abstraction.
 * Defines contract for entities that can be booked and cancelled.
 */
public interface Bookable {
    void book();
    void cancel();
    boolean isBooked();
}
