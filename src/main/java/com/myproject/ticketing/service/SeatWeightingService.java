package com.myproject.ticketing.service;

import com.myproject.ticketing.model.Seat;

/**
 * A service that ranks seats in order of priority
 * For the purposes of this implementation, 
 * Seats are ordered by seat number. A lower
 * numbered seat is closer to the Stage, and thus 
 * is considered to be higher preference than one of a greater number.
 * This can be easily extended to rank seats using additional criteria.
 * @author WM
 *
 */
public class SeatWeightingService {

	public static int getSeatRank(Seat seat) {
		return seat.getSeatNumber();
	}
}
