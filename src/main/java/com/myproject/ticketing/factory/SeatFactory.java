/**
 * 
 */
package com.myproject.ticketing.factory;

import java.util.Set;
import java.util.TreeSet;

import com.myproject.ticketing.model.Seat;
import com.myproject.ticketing.model.SeatStatus;

/**
 * @author WM
 *
 */
public class SeatFactory {

	/**
	 * Creates specified number of seats
	 * @param numSeatsToCreate The number of seats to create
	 * @return a sorted set of seats
	 */
	public static Set<Seat> createSeats(int numSeatsToCreate) {
		Set<Seat> seats = new TreeSet<>();
		for(int i=1; i<=numSeatsToCreate; i++) {
			Seat seat = createSeat(i);
			seats.add(seat);
		}
		return seats;
	}

	private static Seat createSeat(int seatNumber) {
		Seat seat = new Seat(seatNumber, SeatStatus.available);
		return seat;
	}

}
