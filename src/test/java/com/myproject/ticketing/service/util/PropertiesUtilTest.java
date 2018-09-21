package com.myproject.ticketing.service.util;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for the PropertiesUtil class that reads/writes properties in the app config file.
 * @author WM
 *
 */
public class PropertiesUtilTest {
	
	private static PropertiesUtil propsUtil;
	private static final int MAX_SEATS_VAL = getARandomInt();
	private static int originalMaxSeatsValue;
	
	@BeforeClass
	public static void setupBeforeClass() {
		propsUtil = PropertiesUtil.getInstance();
		originalMaxSeatsValue = TicketServiceUtil.readMaxSeatsProperty(); // save the original value
		propsUtil.setProperty(TicketServiceConstants.KEY_MAX_SEATS, Integer.toString(MAX_SEATS_VAL));
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// set the original value back
		propsUtil.setProperty(TicketServiceConstants.KEY_MAX_SEATS, Integer.toString(originalMaxSeatsValue));
	}

	/**
	 * Test that property value can be read from config file
	 */
	@Test
	public void atestGetProperty() {
		int maxSeats  = TicketServiceUtil.readMaxSeatsProperty();
		System.out.println("propsUtilTest received configured maxSeats: " + maxSeats);
		assertEquals(MAX_SEATS_VAL, maxSeats);
	}
	
	/**
	 * Test that property value can be set and read back 
	 */
	@Test
	public void testSetProperty() {
		int maxSeatsExpected = getARandomInt();
		// set the new value
		propsUtil.setProperty(TicketServiceConstants.KEY_MAX_SEATS, Integer.toString(maxSeatsExpected));
		int maxSeatsReceived  = TicketServiceUtil.readMaxSeatsProperty();
		assertEquals(maxSeatsExpected, maxSeatsReceived);
	}
	
	/**
	 * Return a random int within the bounds 1 and 1000
	 * @return random int value
	 */
	private static int getARandomInt() {
		return new Random().ints(1, 1000).findFirst().getAsInt();
	}
	
	

}
