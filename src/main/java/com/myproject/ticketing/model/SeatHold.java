package com.myproject.ticketing.model;

import static java.lang.Math.toIntExact;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A class representing a customer request to hold one or more seats
 * @author WM
 *
 */
public class SeatHold extends SeatRequest implements Delayed{

	
	 
	 /**
	  * The unique id for seat hold required by the interface
	  */
	 private int seatHoldId;
	 
	 /**
	  * a counter to get Id
	  */
	 private static final AtomicInteger counter = new AtomicInteger();
	 
	 
	 /**
	  * The time until which this seat hold request is valid
	  */
	 private long holdTime; // timestamp in millis
	 
	 
	 /**
	  * Human readable time format
	  */
	 private String holdTimeHumanReadable;
	 
	 /**
	  * Time at which the hold request originated
	  */
	 private long requestTime;
	 
	 public SeatHold(String theCustomerEmailId, Set<Seat> seats) {
		 super(seats, theCustomerEmailId);
		 counter.compareAndSet(Integer.MAX_VALUE, 0); // cycle if 
		 this.seatHoldId = counter.incrementAndGet();
		 this.heldSeats = (seats!=null) ? seats : new HashSet<>();
		 this.customerEmailId = theCustomerEmailId;
		 // holdTime is set later by caller (Venue)
	 }
	 

	 /**
	  * Default Constructor
	  * 
	  */
	 public SeatHold() {
		 super();
	 }
	 
	 /**
	  * Set the human readable remaining time
	  */
     private void setHumanReadableTime() {
    	 long durationMillis = holdTime - requestTime;
    	 this.holdTimeHumanReadable = String.format("%02d min, %02d sec", 
    			    TimeUnit.MILLISECONDS.toMinutes(durationMillis),
    			    TimeUnit.MILLISECONDS.toSeconds(durationMillis) - 
    			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillis))
    			);
     }
     
	 
	public long getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(long holdTime) {
		this.holdTime = holdTime;
		this.requestTime = System.currentTimeMillis();
		setHumanReadableTime();
	}

	public String getHoldTimeHumanReadable() {
		return holdTimeHumanReadable;
	}

	@Override
	 public long getDelay(TimeUnit  unit) {
	     long diff = holdTime - System.currentTimeMillis();
	     return unit.convert(diff, TimeUnit.MILLISECONDS);
	 }
	 
	 @Override
	 public int compareTo(Delayed o) {
	     return toIntExact(
	       this.holdTime - ((SeatHold) o).holdTime);
	 }
	 
	 @Override
	 public int hashCode() {
	     final int prime = 31;

	     int result = 1;
	     result = prime * result + new Integer(seatHoldId).hashCode();
	     result = prime * result + customerEmailId.hashCode();

	     return result;
	 }

	 @Override
	 public boolean equals( Object obj ) {
	     if( this == obj ) {
	         return true;
	     }

	     if( obj == null ) {
	         return false;
	     }

	     if(!(obj instanceof SeatHold)) {
	    	 return false;
	     }
	     
	     // equals
	     SeatHold other = (SeatHold)obj;
	     return seatHoldId == other.getSeatHoldId(); 
	 }

	public int getSeatHoldId() {
		return seatHoldId;
	}


	public void setSeatHoldId(int seatHoldId) {
		this.seatHoldId = seatHoldId;
	}


	@Override
	public String toString() {
		return "SeatHold [seatHoldId=" + seatHoldId + ", customerEmailId="
				+ customerEmailId + ", holdTime=" + holdTime + ", errorMessage=" + errorMessage
				+ ", holdTimeHumanReadable=" + holdTimeHumanReadable + ", heldSeats=" + heldSeats + "]";
	}
}
