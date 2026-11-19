package com.bookmyticket.model;

/**
 * Represents an administrator who manages movies and views all bookings.
 * Demonstrates Inheritance (extends Person) and Polymorphism (overrides getRole).
 */
public class Admin extends Person {
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    public Admin(String name) {
        super(name, "N/A");
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    public static boolean authenticate(String password) {
        return DEFAULT_ADMIN_PASSWORD.equals(password);
    }
}
