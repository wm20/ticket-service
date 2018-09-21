package com.myproject.ticketing.model;

import java.util.HashSet;
import java.util.Set;

public abstract class SeatRequest {

	 /**
	  * Set of seats within this seat hold request
	  */
	 protected Set<Seat> heldSeats; 
	 
	 /**
	  * The emailId of the customer. // reqs say this is unique, that means one customer can only make one hold at a time. 
	  * But after final reservation, they can  make multiple.
	  */
	 protected String customerEmailId;
	 
	 /**
	  * An error message explaining why the seat hold request failed
	  */
	 protected String errorMessage;
	 
	 public SeatRequest() {
		 this.heldSeats = new HashSet<>();
	 }
	 
	 public SeatRequest(Set<Seat> seats, String theCustomerEmailId) {
		super();
		 this.heldSeats = (seats!=null) ? seats : new HashSet<>();
		 this.customerEmailId = theCustomerEmailId;
	}

	public Set<Seat> getHeldSeats() {
		return heldSeats;
	}

	public void setHeldSeats(Set<Seat> heldSeats) {
		this.heldSeats = heldSeats;
	}

	public String getCustomerEmailId() {
		return customerEmailId;
	}

	public void setCustomerEmailId(String customerEmailId) {
		this.customerEmailId = customerEmailId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


}
