package com.bookmyticket.exception;

/**
 * Custom checked exception thrown when user input validation fails.
 */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
