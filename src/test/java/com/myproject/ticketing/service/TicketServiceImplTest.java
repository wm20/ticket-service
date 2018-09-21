package com.myproject.ticketing.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.myproject.ticketing.interfaces.TicketService;
import com.myproject.ticketing.model.SeatHold;
import com.myproject.ticketing.service.util.ConcurrentClientRequests;
import com.myproject.ticketing.service.util.PropertiesUtil;
import com.myproject.ticketing.service.util.TicketServiceConstants;
import com.myproject.ticketing.service.util.TicketServiceUtil;

//@Ignore("temporary")
public class TicketServiceImplTest {

	private static TicketService ticketService;
	private static TicketHoldCleanupService ticketHoldCleanupSvc;
	
	// The configuration with which to run this test. The MAX_SEATS value below is written to the app config file
	// before instantiating the application and running the unit tests.
	private static final int MYTHREADS = 500;
	private static final int SEATS_PER_THREAD = 200;
	private static final int MAX_SEATS = MYTHREADS * SEATS_PER_THREAD;
	
	// amount of time to wait for a all held seats to expire and be cleaned up
	private static long cleanupWaitTimeMillis = 30000; // default 30 seconds, but will be computed as hold time + cleanup time

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Setup the app configuration properties in the config file to correspond to the params of this test
		PropertiesUtil.getInstance().setProperty(TicketServiceConstants.KEY_MAX_SEATS, Integer.toString(MAX_SEATS));
		cleanupWaitTimeMillis = TicketServiceUtil.readMaxHoldTimeProperty() + 30000;
		System.out.println("cleanupWaitTimeMillis: " + cleanupWaitTimeMillis);
		
		ticketService = new TicketServiceImpl();
		ticketHoldCleanupSvc = new TicketHoldCleanupService();
		Thread ticketHoldCleanupThread = new Thread(ticketHoldCleanupSvc, "ticketHoldCleanupService");
		ticketHoldCleanupThread.start();
		System.out.println("setUp: numAvailableSeats:  " + ticketService.numSeatsAvailable());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		TicketHoldCleanupService.setStopFlag(true);
		Thread.currentThread().sleep(3000); // wait for finish
		ticketService = null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Make holds from a number of concurrent threads, and then wait for their holds to expire.
	 * Then confirm that number of available seats before and after the thread run match. 
	 */
	@Test
	public void testConcurrentFindAndHoldSeats() {

		int numSeatsBefore = ticketService.numSeatsAvailable();
		new ConcurrentClientRequests(MYTHREADS, SEATS_PER_THREAD, MAX_SEATS, ticketService).runConcurrentClients();
		int numSeatsAfter = ticketService.numSeatsAvailable();
		assertEquals(numSeatsBefore, numSeatsAfter);
	}

	/**
	 * Test method for confirming num seats available 
	 * {@link com.myproject.ticketing.service.TicketServiceImpl#numSeatsAvailable()}.
	 */
	@Test
	public void aTestNumSeatsAvailable() {
		int availableSeats = ticketService.numSeatsAvailable();
		assertEquals(MAX_SEATS, availableSeats); 
	}

	/**
	 * Test method to verify that number of held seats received matches requested number of seats
	 * {@link com.myproject.ticketing.service.TicketServiceImpl#findAndHoldSeats(int, java.lang.String)}.
	 */
	@Test
	public void testFindAndHoldSeats() {
		int numSeatsToHold = 5;
		SeatHold seatHold = ticketService.findAndHoldSeats(numSeatsToHold, "x.y@emailInvalidxyz.net");
		System.out.println("testFindAndHoldSeats received seatHold: " + TicketServiceUtil.getSeatHoldInformation(seatHold));
		assertNotNull(seatHold);
		assertNotNull(seatHold.getHeldSeats());
		assertEquals(numSeatsToHold, seatHold.getHeldSeats().size());
	}

	/**
	 * Test method for reserving a number of seats is successful
	 * {@link com.myproject.ticketing.service.TicketServiceImpl#reserveSeats(int, java.lang.String)}.
	 */
	@Test
	public void testReserveSeats() {
		int numSeatsToReserve = 2;
		if(ticketService.numSeatsAvailable() < numSeatsToReserve) {
			System.out.println("Can't reserve greater than available seats, this test will fail. Check configuration of MAX_SEATS");
		}
		SeatHold seatHold = ticketService.findAndHoldSeats(numSeatsToReserve, "bob.smith@emailInvalidxyz.net");
		System.out.println("testFindAndHoldSeats received : " + seatHold);
		assertNotNull(seatHold);
		assertNotNull(seatHold.getHeldSeats());
		String reservationCode = ticketService.reserveSeats(seatHold.getSeatHoldId(), "bob.smith@emailInvalidxyz.net");
		assertNotNull(reservationCode);

		int numRemainingSeats = ticketService.numSeatsAvailable();
		System.out.println("after testReserveSeats, numRemainingSeats: " + numRemainingSeats);
		// assertEquals(MAX_SEATS - numSeatsToReserve, numRemainingSeats); // this can't be asserted as it depends on other seats are being held previously
	}
	
	/**
	 * Verify that hold request of greater than max seats fails
	 */
	@Test 
	public void testRequestHoldGreaterThanAvailableVerifyDenied() {
		int numSeatsToReserve = MAX_SEATS + 1;
		SeatHold seatHold = ticketService.findAndHoldSeats(numSeatsToReserve, "a.b@emailInvalidxyz.net");
		System.out.println("testReserveGreaterThanAvailable returned error message: " + seatHold.getErrorMessage());
		assertTrue(seatHold.getSeatHoldId()==0);
		assertTrue(seatHold.getHeldSeats().size()<1);
		
	}
	
	/**
	 * Verify that multiple requests from same customer (identified by same email id) are denied
	 */
	@Test 
	public void testMultipleHoldRequestsVerifyDenied() {
		int numSeatsToReserve = 1;
		SeatHold seatHold = ticketService.findAndHoldSeats(numSeatsToReserve, "c.d@emailInvalidxyz.net");
		assertTrue(seatHold.getHeldSeats().size()==1); // first request should be successful
		System.out.println("testMultipleHoldRequestsVerifyDenied first request returned seatHold: " + seatHold);
		
		// second request should be denied with an error message received. 
		// Of course, this requires a long enough hold time on the first request, so ensure configuration app.properties has this
		seatHold = ticketService.findAndHoldSeats(numSeatsToReserve, "c.d@emailInvalidxyz.net");
		System.out.println("testMultipleHoldRequestsVerifyDenied second request returned seatHold: " + seatHold);
		assertTrue(seatHold.getSeatHoldId()==0); 
		assertTrue(seatHold.getHeldSeats().size()<1); // no seats should be assigned
		
	}

	/**
	 * Test that expired holds are cleaned up correctly by making a hold, waiting for it to expire
	 * and then confirming that seats have been released.
	 */
	@Test
	public void testExpiredHoldCleanup() {

		int numSeatsAtStart = ticketService.numSeatsAvailable();
		System.out.println("**** testExpiredHoldCleanup: BEGIN: numSeatsAvailable: " + numSeatsAtStart);

		// make holds and reservations until all seats taken
		SeatHold seatHold = ticketService.findAndHoldSeats(MAX_SEATS, "x.y@invalidemailxyz.net");
		System.out.println("number of held seats: " + seatHold.getHeldSeats().size());
		System.out.println("Seat hold expiring at: " + seatHold.getHoldTimeHumanReadable());
		
		
		int numSeatsAvailableAfterHoldAll = ticketService.numSeatsAvailable();
		assertEquals(0, numSeatsAvailableAfterHoldAll); // NO seats should be available after hold all

		System.out.println("\"**** testExpiredHoldCleanup: DURING hold ALL: numSeatsAvailable: \"" + ticketService.numSeatsAvailable());

		try {
			System.out.println("sleeping " + cleanupWaitTimeMillis + " milliseconds to wait for hold time to expire");
			Thread.sleep(cleanupWaitTimeMillis);
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted error" + e.toString());
		}

		// wait until holds expire, then confirm that seats are available again
		int numSeatsAfterHoldExpire = ticketService.numSeatsAvailable();
		System.out.println("**** testExpiredHoldCleanup: AFTER hold expire: numSeatsAvailable: " + numSeatsAfterHoldExpire);
		assertEquals(MAX_SEATS, ticketService.numSeatsAvailable()); // ALL seats should be available after hold all

	}
}
