package com.myproject.ticketing.model;

import com.myproject.ticketing.service.SeatWeightingService;

/**
 * A class representing a seat in a venue
 * 
 * @author WM
 *
 */
public class Seat implements Comparable<Seat>{

	/***
	 * The number for the seat
	 */
	private int seatNumber; // numeric
	
	/***
	 * Current Status of the seat
	 */
	private SeatStatus seatStatus; 
	 
	/**
	 * Price at which the seat is sold
	 */
	private float price;

	public int getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(int number) {
		this.seatNumber = number;
	}

	public SeatStatus getSeatStatus() {
		return seatStatus;
	}

	public void setSeatStatus(SeatStatus seatStatus) {
		this.seatStatus = seatStatus;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	
	public Seat(int seatNumber, SeatStatus status) {
		this.seatNumber = seatNumber;
		this.seatStatus = status;
	}

	/**
	 * Compare this seat to another seat
	 */
	@Override
	public int compareTo(Seat otherSeat) {
	    
	     int rank = SeatWeightingService.getSeatRank(this) - SeatWeightingService.getSeatRank(otherSeat);
	     
	     // reverse the return values since lower numbered seats are ranked greater than higher numbered seats.
	     if(rank<0) {
	    	 return -1;
	     } else if(rank>0) {
	    	 return 1;
	     }
	     else {
	    	 return 0;
	     }
	}

	@Override
	public String toString() {
		return "Seat [seatNumber=" + seatNumber + ", seatStatus=" + seatStatus + "]";
		// return "Seat [seatNumber=" + seatNumber + ", seatStatus=" + seatStatus + ", price=" + price + "]";
	}
	
	
	
}
