/**
 * 
 */
package com.myproject.ticketing.service.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myproject.ticketing.model.SeatHold;

/**
 * A class containing utility methods
 * 
 * @author WM
 *
 */
public class TicketServiceUtil {

	private static final Pattern pattern = Pattern.compile(TicketServiceConstants.VALID_EMAIL_REGEX);
	
	/**
	 * Read the max seats property
	 * 
	 * @return the value of max seats property from the config file
	 */
	public static int readMaxSeatsProperty() {
		return PropertiesUtil.getInstance().getIntProperty(TicketServiceConstants.KEY_MAX_SEATS,
				TicketServiceConstants.MAX_SEATS_DEFAULT_VALUE, TicketServiceConstants.MAX_SEATS_MIN_VALUE,
				TicketServiceConstants.MAX_SEATS_MAX_VALUE);
	}

	/**
	 * Read the max hold time property
	 * 
	 * @return the value of max seats property from the config file
	 */
	public static long readMaxHoldTimeProperty() {
		return PropertiesUtil.getInstance().getLongProperty(TicketServiceConstants.KEY_MAX_HOLD_TIME_MILLIS,
				TicketServiceConstants.MAX_HOLD_TIME_MILLIS_DEFAULT_VALUE,
				TicketServiceConstants.MAX_HOLD_TIME_MILLIS_MIN_VALUE,
				TicketServiceConstants.MAX_HOLD_TIME_MILLIS_MAX_VALUE);
	}

	/**
	 * Create summary information for seatHold
	 * 
	 * @param seatHold: The seat hold object
	 * @return String with seat hold summary
	 */
	public static String getSeatHoldInformation(SeatHold seatHold) {

		if (null == seatHold) {
			return null;
		}
		return new StringBuffer("seatHoldId: ").append(((seatHold != null) ? seatHold.getSeatHoldId() : null))
				.append(", number of held seats: ")
				.append((seatHold.getHeldSeats() != null) ? seatHold.getHeldSeats().size() : 0)
				.append(", error message: ").append(seatHold.getErrorMessage()).toString();
	}
	
	public static boolean isEmailIdValid(String input) {
		if(null==input) {
			return false;
		}
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}
}
