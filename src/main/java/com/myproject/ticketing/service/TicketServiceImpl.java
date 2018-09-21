/**
 * 
 */
package com.myproject.ticketing.service;

import com.myproject.ticketing.interfaces.TicketService;
import com.myproject.ticketing.interfaces.VenueRepository;
import com.myproject.ticketing.model.SeatHold;
import com.myproject.ticketing.model.VenueRepositoryServiceImpl;

/**
 * @author WM
 *
 */
public class TicketServiceImpl implements TicketService {
	
	private VenueRepository venue = null;
	
	/**
	 * Constructor
	 */
	public TicketServiceImpl() {
	    venue = VenueRepositoryServiceImpl.getInstance();
	}

	/* (non-Javadoc)
	 * @see com.myproject.ticketing.interfaces.TicketService#numSeatsAvailable()
	 */
	@Override
	public int numSeatsAvailable() {
		return venue.getNumberOfAvailableSeatsInVenue();
	}

	/* (non-Javadoc)
	 * @see com.myproject.ticketing.interfaces.TicketService#findAndHoldSeats(int, java.lang.String)
	 */
	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		return venue.findAndHoldSeats(numSeats, customerEmail);
	}

	/* (non-Javadoc)
	 * @see com.myproject.ticketing.interfaces.TicketService#reserveSeats(int, java.lang.String)
	 */
	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		return venue.reserveSeats(seatHoldId, customerEmail);
	}

}
