package com.bookmyticket.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specific movie screening time with its dedicated seat arrangement.
 * Encapsulates the collection of seats for this show.
 */
public class Show {
    private String showId;
    private String time;
    private List<Seat> seats;

    public Show(String showId, String time, double basePrice) {
        this.showId = showId;
        this.time = time;
        this.seats = new ArrayList<>();
        initializeSeats(basePrice);
    }

    /**
     * Initializes a 5x8 grid of seats (Rows A-E, Columns 1-8).
     * Rows A, B, C -> RegularSeat
     * Rows D, E    -> PremiumSeat
     */
    private void initializeSeats(double basePrice) {
        char[] rows = {'A', 'B', 'C', 'D', 'E'};
        int cols = 8;
        for (char row : rows) {
            for (int col = 1; col <= cols; col++) {
                String seatNumber = "" + row + col;
                if (row == 'D' || row == 'E') {
                    seats.add(new PremiumSeat(seatNumber, basePrice));
                } else {
                    seats.add(new RegularSeat(seatNumber, basePrice));
                }
            }
        }
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public Seat getSeat(String seatNumber) {
        for (Seat seat : seats) {
            if (seat.getSeatNumber().equalsIgnoreCase(seatNumber)) {
                return seat;
            }
        }
        return null;
    }

    public int getAvailableSeatCount() {
        int count = 0;
        for (Seat seat : seats) {
            if (!seat.isBooked()) {
                count++;
            }
        }
        return count;
    }

    public int getTotalSeatCount() {
        return seats.size();
    }

    @Override
    public String toString() {
        return time + " (" + getAvailableSeatCount() + "/" + getTotalSeatCount() + " available)";
    }
}
