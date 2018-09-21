package com.myproject.ticketing.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.myproject.ticketing.model.VenueRepositoryServiceImpl;

/**
 * A thread to remove expired seat hold requests
 * @author WM
 *
 */
public class TicketHoldCleanupService implements Runnable {
 
	private static AtomicBoolean stop = new AtomicBoolean(false);
	private static final Logger logger = LogManager.getLogger(TicketHoldCleanupService.class); 
	
    @Override
    public void run() {
    	logger.info("Thread cleanup service started.");
        while(!stop.get()) {
            try {
                VenueRepositoryServiceImpl.getInstance().removeSeatHold();
//                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Thread exiting..");
    }

	public static boolean getStopFlag() {
		return stop.get();
	}

	public static void setStopFlag( boolean flag) {
		TicketHoldCleanupService.stop.set(flag);
	}
}
