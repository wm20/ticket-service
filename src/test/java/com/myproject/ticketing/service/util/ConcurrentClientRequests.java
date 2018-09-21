/**
 * 
 */
package com.myproject.ticketing.service.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.myproject.ticketing.interfaces.TicketService;

/**
 * A utility class to create multiple threads to 
 * @author WM
 *
 */
public class ConcurrentClientRequests {
	private int numThreads;
	private int seatsPerThread;
	private TicketService ticketService;
	
	public ConcurrentClientRequests(int numThreads, int seatsPerThread, int maxSeats, TicketService ticketService) {
		super();
		this.numThreads = numThreads;
		this.seatsPerThread = seatsPerThread;
		this.ticketService = ticketService;
	}

	/**
	 * run concurrent client hold requests
	 */
	public  void runConcurrentClients()  {
		try {
			ExecutorService executor = Executors.newFixedThreadPool(numThreads);
			
			for (int i = 0; i < numThreads; i++) {
				Runnable myRunnable = new SeatHoldRequestThread(ticketService, seatsPerThread);
				executor.execute(myRunnable);
			}
			executor.shutdown();
			// Wait until all threads complete
			while (!executor.isTerminated()) {
				// do nothing here
			}
			System.out.println("Finished all threads");
			
			// get maxHoldTime and wait that long + additional 30 seconds for all holds to expire
			try {
				long waitTime = TicketServiceUtil.readMaxHoldTimeProperty() + 30000;
				System.out.println("thread sleeping " + waitTime + " millis to wait for hold time to expire before checking available seats..");
				Thread.currentThread().sleep(waitTime);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted error" + e.toString());
			}

			// after holds expire, confirm that seats are available again
			System.out.println("**** Available seats: AFTER HOLD Expiration: " + ticketService.numSeatsAvailable());
		} catch (Exception e) {
			System.out.println("exception: " + e.toString());
			e.printStackTrace();
		}
	}
 
	
}
