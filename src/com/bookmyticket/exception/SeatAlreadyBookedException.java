package com.bookmyticket.exception;

/**
 * Custom checked exception thrown when attempting to book a seat that has
 * already been reserved.
 */
public class SeatAlreadyBookedException extends Exception {
    public SeatAlreadyBookedException(String message) {
        super(message);
    }

    public SeatAlreadyBookedException(String seatNumber, String showTime) {
        super("Seat " + seatNumber + " is already booked for show at " + showTime + ".");
    }
}
