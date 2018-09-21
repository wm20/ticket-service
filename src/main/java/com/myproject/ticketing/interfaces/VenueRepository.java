package com.myproject.ticketing.interfaces;

import com.myproject.ticketing.model.SeatHold;

public interface VenueRepository {

	/**
	 * Reserve specified hold
	 * 
	 * @param seatHoldId    The ID for seat hold request
	 * @param customerEmail The customers email id
	 * @return Reservation code
	 */
	String reserveSeats(int seatHoldId, String customerEmail);

	/**
	 * Remove seat holds that have expired
	 */
	void removeSeatHold();

	/**
	 * Get number of available seats in the venue, excluding currently held and
	 * reserved seats
	 * 
	 * @return the number of available seats
	 */
	int getNumberOfAvailableSeatsInVenue();

	/**
	 * Find and hold the best seats in the venue.
	 * @param numSeats The number of seats to find and hold
	 * @param theCustomerEmailId The customers emailid
	 * @return A SeatHold object with hold information
	 */
	SeatHold findAndHoldSeats(int numSeats, String theCustomerEmailId);

}