package com.bookmyticket.model;

/**
 * Abstract base class demonstrating Abstraction and Encapsulation.
 * Represents a person in the Book My Ticket system.
 */
public abstract class Person {
    private String name;
    private String phoneNumber;

    public Person(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Abstract method to be overridden by subclasses (Polymorphism).
     * @return the role of this person ("Customer" or "Admin").
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + ": " + name + " (" + phoneNumber + ")";
    }
}
