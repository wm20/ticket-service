package com.myproject.ticketing.service.util;

import com.myproject.ticketing.interfaces.TicketService;
import com.myproject.ticketing.model.SeatHold;

public class SeatHoldRequestThread implements Runnable {
	private final TicketService ticketService;
	private final int numSeatsToReserve;

	public SeatHoldRequestThread(TicketService ticketService, int numSeatsToReserve) {
		this.ticketService = ticketService;
		this.numSeatsToReserve = numSeatsToReserve;
	}

	@Override
	public void run() {

		// Reserve specified number of seats
		try {
			String myThreadName = Thread.currentThread().getName();
			String debugStringPrefix = new StringBuffer("myThreadName: ").append(myThreadName ).toString();
			System.out.println( "myThreadName: " + myThreadName + "BEGIN:available seats" + ticketService.numSeatsAvailable());

			// make holds until all seats taken
			String emailId = new StringBuffer("emailid-").append(myThreadName).append("@invalidemailxyz.net").toString();
			SeatHold seatHold = ticketService.findAndHoldSeats(numSeatsToReserve, emailId);
			System.out.println(debugStringPrefix + " After seatHold: number of held seats: " + seatHold.getHeldSeats().size());
			System.out.println( "myThreadName: " + myThreadName + "DURING SEAT HOLD:available seats" + ticketService.numSeatsAvailable());
			System.out.println(debugStringPrefix + " Seat hold expiring at: " + seatHold.getHoldTimeHumanReadable());

			System.out.println(debugStringPrefix + "AFTER HOLD: available seats: " + ticketService.numSeatsAvailable());


		} catch (Exception e) {
			System.out.println(e.toString());

		}
	}
	
}