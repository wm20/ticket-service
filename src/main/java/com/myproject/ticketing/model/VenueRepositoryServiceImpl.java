/**
 * 
 */
package com.myproject.ticketing.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.myproject.ticketing.factory.SeatFactory;
import com.myproject.ticketing.interfaces.VenueRepository;
import com.myproject.ticketing.service.util.PropertiesUtil;
import com.myproject.ticketing.service.util.TicketServiceUtil;

/**
 * A class representing the Venue Service
 * 
 * @author WM
 *
 */
public class VenueRepositoryServiceImpl implements VenueRepository {

	private static int maxSeats;
	private static long maxHoldTimeMillis;
	private static final Logger logger = LogManager.getLogger(VenueRepositoryServiceImpl.class);

	// the next two data structures are for tracking the available seats
	/**
	 * A map of available seats in the venue, with key being the seat number, and
	 * value is the Seat object
	 */
	private static Map<Integer, Seat> availableSeatsMap;

	/**
	 * A sorted set of available seats. This is used to get best available seats
	 * quickly
	 */
	private static Set<Seat> availableSeatsSet;

	// the next two data structures are for the seatHold requests
	/**
	 * A map of seat hold sets - this is for fast lookups using a map rather than
	 * the queue below
	 */
	private static Map<String, SeatHold> seatHoldsMap; // key is customerEmailId

	/**
	 * A Set that holds pending reservations that have expiration time This is used
	 * by the thread that removes expired holds, so it can block on it.
	 */
	private static BlockingQueue<SeatHold> seatHoldsQueue;

	/**
	 * A map that holds final reservations. They key is the reservationId
	 */
	private static Map<UUID, SeatReservation> reservation; // key should be reservation UUID

	/**
	 * A lock for synchronizing access to the critical data sets
	 */
	private static final Object lock = new Object();

	/**
	 * Singleton instance of this class
	 */
	private static VenueRepository venue;

	/**
	 * Singleton factory method to get an instance of Venue. A new instance will
	 * only be created for the first call of this method.
	 * 
	 * @return instance of Venue.
	 */
	public synchronized static VenueRepository getInstance() {
		if (null == venue) {
			venue = new VenueRepositoryServiceImpl();
		}
		return venue;
	}

	/**
	 * Private constructor to create instance
	 * 
	 */
	private VenueRepositoryServiceImpl() {
		PropertiesUtil.getInstance();
		maxSeats = TicketServiceUtil.readMaxSeatsProperty();
		maxHoldTimeMillis = TicketServiceUtil.readMaxHoldTimeProperty();
		availableSeatsMap = new HashMap<>();
		availableSeatsSet = new TreeSet<>();
		reservation = new HashMap<>();
		seatHoldsQueue = new DelayQueue<>();
		seatHoldsMap = new HashMap<>();

		// create seats in venue and put in lookup structures
		availableSeatsSet = SeatFactory.createSeats(maxSeats);
		for (Seat seat : availableSeatsSet) {
			availableSeatsMap.put(new Integer(seat.getSeatNumber()), seat);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.myproject.ticketing.model.VenueRepositoryInterface#reserveSeats(int,
	 * java.lang.String)
	 */
	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {

		SeatReservation seatReservation = null;
		// validate input
		if (!TicketServiceUtil.isEmailIdValid(customerEmail)) {
			return "Error: Invalid customer emailId";
		}
		
		synchronized (lock) {
			// Look up the seatHold
			SeatHold seatHold = seatHoldsMap.get(customerEmail);
			if(null==seatHold) {
				return new StringBuffer("Error: No seatHold found for seatHoldId: ").append(seatHoldId).toString();
			}

			// Remove the seatHold instance from the seatHolds data structures
			Iterator<SeatHold> iter = seatHoldsQueue.iterator();
			SeatHold seatHoldFromQ;
			while (iter.hasNext()) {
				seatHoldFromQ = iter.next();
				if (seatHoldFromQ.equals(seatHold)) {
					iter.remove(); // remove current object from seatHoldQueue as it's being reserved
				}
				break;
			}
			seatHoldsMap.remove(seatHold.getCustomerEmailId()); // remove from seathold map

			// now move the seatHold in the final reservation
			seatReservation = new SeatReservation(seatHold.getCustomerEmailId(), seatHold.getHeldSeats());

			reservation.put(seatReservation.getSeatReservationCode(), seatReservation);
		}

		return (seatReservation != null && seatReservation.getSeatReservationCode() != null)
				? seatReservation.getSeatReservationCode().toString()
				: null;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.myproject.ticketing.model.VenueRepositoryInterface#removeSeatHold()
	 */
	@Override
	public void removeSeatHold() {
		try {
			// check if any expired seat holds are available
			SeatHold seatHold = seatHoldsQueue.take(); // cleanup takes time depending on number of objects
														// due to api offering one object at a time.
			if (logger.isInfoEnabled()) {
				logger.info("removeSeatHold take: " + seatHold);
			}

			synchronized (lock) {
				// remove from seatHoldsMap
				seatHoldsMap.remove(seatHold.getCustomerEmailId());

				// Add seats back into the available seats map
				for (Seat seat : seatHold.getHeldSeats()) {
					seat.setSeatStatus(SeatStatus.available);
					availableSeatsMap.put(new Integer(seat.getSeatNumber()), seat);
				}
				availableSeatsSet.addAll(seatHold.getHeldSeats()); // add all back to available set
				if (logger.isInfoEnabled()) {
					logger.info("removeSeatHold: Added back to availableSeats total seats:"
							+ seatHold.getHeldSeats().size());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.myproject.ticketing.model.VenueRepositoryInterface#
	 * getNumberOfAvailableSeatsInVenue()
	 */
	@Override
	public int getNumberOfAvailableSeatsInVenue() {
		synchronized (lock) {
			return availableSeatsSet.size();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.myproject.ticketing.model.VenueRepositoryInterface#findAndHoldSeats(int,
	 * java.lang.String)
	 */
	@Override
	public SeatHold findAndHoldSeats(int numSeats, String theCustomerEmailId) {

		SeatHold mySeatHold;
		Set<Seat> seatsFoundSet = new TreeSet<>();

		// validate input
		if (numSeats == 0 || !TicketServiceUtil.isEmailIdValid(theCustomerEmailId)) {
			mySeatHold = new SeatHold();
			mySeatHold.setErrorMessage("ERROR: Invalid input number of seats or emailId input received.");
			return mySeatHold;
		}

		synchronized (lock) {

			if (seatHoldsMap.get(theCustomerEmailId) != null) {
				// Indicate error that another hold req already exists with this
				// customerId and deny request by returning
				mySeatHold = new SeatHold();
				mySeatHold.setErrorMessage(
						"Customer already has a current unexpired hold. Only one hold request permitted at a time for a customer.");
				return mySeatHold;
			}

			// check if requested number of seats are available
			if (availableSeatsSet.size() < numSeats) { // requested number of seats are not available
				mySeatHold = new SeatHold();
				mySeatHold.setErrorMessage("Number of requested seats exceeds available seats");
				return mySeatHold;
			}

			// get and assign seats
			int count = 1;
			Seat aSeat = null;
			while (count <= numSeats) {
				for (Iterator<Seat> iter = availableSeatsSet.iterator(); iter.hasNext();) {
					aSeat = iter.next();
					iter.remove(); // remove the seat from availableSeatsSet
					availableSeatsMap.remove(aSeat.getSeatNumber()); // remove seat from availableSeatsMap
					aSeat.setSeatStatus(SeatStatus.hold);
					seatsFoundSet.add(aSeat); // add it to set of seats for reservation
					++count;
					if (count > numSeats) { // number of seats have been found
						break;
					}
				}
			}

			// seats have been found, create seat hold
			mySeatHold = new SeatHold(theCustomerEmailId, seatsFoundSet);

			// add it to the map and queue of seatHolds
						try {
							maxHoldTimeMillis = TicketServiceUtil.readMaxHoldTimeProperty();
							// set the seat hold time to 60 seconds from now
							mySeatHold.setHoldTime(System.currentTimeMillis() + maxHoldTimeMillis);
							seatHoldsQueue.put(mySeatHold);
							seatHoldsMap.put(mySeatHold.getCustomerEmailId(), mySeatHold);
							if (logger.isInfoEnabled()) {
								logger.info("addSeatHold: Added seatHold for customer: " + mySeatHold.getCustomerEmailId()
										+ ", for seats: " + mySeatHold.getHeldSeats());
							}
						} catch (InterruptedException ie) {
							logger.error(ie);
						}
						// remove all seats in seatHold from availableSeats
						for (Seat seat : mySeatHold.getHeldSeats()) {
							availableSeatsMap.remove(seat.getSeatNumber());
							availableSeatsSet.remove(seat);
						}

			if (logger.isInfoEnabled()) {
				logger.info("venue.findAndHoldSeats found seatHold: " + mySeatHold);
				logger.info("venue.findAndHoldSeats total seats assigned: " + mySeatHold.getHeldSeats().size());
			}

			return mySeatHold;
		}

	}

}
