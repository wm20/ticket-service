package com.myproject.ticketing.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SeatReservation extends SeatRequest {

	
	 public SeatReservation(String theCustomerEmailId, Set<Seat> seats) {
		 super(seats, theCustomerEmailId);
		 this.seatReservationCode = UUID.randomUUID();
		 this.heldSeats = (seats!=null) ? seats : new HashSet<>();
		 this.customerEmailId = theCustomerEmailId;
		 // holdTime is set later by caller (Venue)
	 }
	
	/**
	 * The unique seatHoldUUId
	 */
	private UUID seatReservationCode; // UUID

	public UUID getSeatReservationCode() {
		return seatReservationCode;
	}

	public void setSeatReservationCode(UUID seatReservationCode) {
		this.seatReservationCode = seatReservationCode;
	}

}
