package com.myproject.ticketing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.myproject.ticketing.interfaces.TicketService;
import com.myproject.ticketing.interfaces.TicketServiceCLIInterface;
import com.myproject.ticketing.service.TicketHoldCleanupService;
import com.myproject.ticketing.service.TicketServiceImpl;
import com.myproject.ticketing.service.util.PropertiesUtil;

/**
 * The application main class
 * 
 * @author WM
 *
 */
public class Application {

	private static final Logger logger = LogManager.getLogger(Application.class);
	private static TicketService ticketService;
	private static TicketServiceCLIInterface cli;
	private static TicketHoldCleanupService ticketHoldCleanupSvc;

	public static void main(String[] args) {
		System.out.println("Application starting");
		logger.info("Started application");
		addShutdownHook();
		ticketService = new TicketServiceImpl();
		// start the cleanup thread
		ticketHoldCleanupSvc = new TicketHoldCleanupService();
		Thread ticketHoldCleanupThread = new Thread(ticketHoldCleanupSvc, "ticketHoldCleanupService");
		ticketHoldCleanupThread.start();
		

		System.out.println(
				"Note: You are running the main program. If you want to run unit tests, exit with Ctrl + C and then run 'mvn test' to execute tests. \n\n");


		cli = new TicketServiceCLI(ticketService);
		printAppConfig();
		cli.executeCLI();
	}
	
	/**
	 * Set the cleanup thread to stop
	 */
	public static void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				TicketHoldCleanupService.setStopFlag(true);
			}
		});
	}
	
	/**
	 * Print the application configuration
	 */
	private static void printAppConfig() {
		System.out.println("\n\n==================Application Configuration===============");
		System.out.println(PropertiesUtil.getAllProperties());
		System.out.println("==========================================================\n\n");
	}

}
