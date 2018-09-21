/**
 * 
 */
package com.myproject.ticketing.service.util;

/**
 * @author WM
 * A class defining constants
 *
 */
public class TicketServiceConstants {

	// Define the keys in the config file
	public static final String KEY_MAX_SEATS = "ticket.service.maxSeats";
	public static final String KEY_MAX_HOLD_TIME_MILLIS = "ticket.service.maxHoldTimeMillis";
	
	// Define the default, min and max values for the MAX_SEATS
	public static final int MAX_SEATS_DEFAULT_VALUE = 500;
	public static final int MAX_SEATS_MIN_VALUE = 1;
	public static final int MAX_SEATS_MAX_VALUE = 100000;
	
	// Define the default, min, and max values for the hold time
	public static final int MAX_HOLD_TIME_MILLIS_DEFAULT_VALUE = 30000; // 30 seconds
	public static final int MAX_HOLD_TIME_MILLIS_MIN_VALUE = 10000; // 10 seconds
	public static final int MAX_HOLD_TIME_MILLIS_MAX_VALUE = 300000; // 5 minutes
	
	// valid email regex
	public static final String VALID_EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
	
	
}
