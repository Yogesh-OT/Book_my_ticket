package com.bookmyticket.model;

/**
 * Represents a customer who books movie tickets.
 * Demonstrates Inheritance (extends Person) and Polymorphism (overrides getRole).
 */
public class Customer extends Person {

    public Customer(String name, String phoneNumber) {
        super(name, phoneNumber);
    }

    @Override
    public String getRole() {
        return "Customer";
    }
}
