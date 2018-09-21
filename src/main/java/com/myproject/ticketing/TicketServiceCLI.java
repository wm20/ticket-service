package com.myproject.ticketing;

import java.util.Scanner;

import com.myproject.ticketing.interfaces.TicketService;
import com.myproject.ticketing.interfaces.TicketServiceCLIInterface;
import com.myproject.ticketing.model.SeatHold;
import com.myproject.ticketing.service.util.TicketServiceUtil;

/**
 * A class to run a basic CLI for interactive execution of commands on the TicketService
 */
public class TicketServiceCLI implements TicketServiceCLIInterface {
	
	private TicketService ticketService;

	public TicketServiceCLI(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	/* (non-Javadoc)
	 * @see com.myproject.ticketing.TicketServiceCLIInterface#executeCLI()
	 */
	@Override
	public  void executeCLI() {
		System.out.println("======================================================================");
		System.out.println("Welcome to the Ticket Service CLI");
		System.out.println("======================================================================");
		int input = -1;

		try (Scanner scanner = new Scanner(System.in);) {
			do {

				printSelectionChoices();

				input = getUserIntSelection(scanner);

				switch (input) {
				case 0:
					System.out.println("User input was 0, system exiting");
					System.exit(0);
				case 1:
					System.out.println("\n\t ***** Response: Number of seats available: "
							+ ticketService.numSeatsAvailable() + "\n\n");
					break;
				case 2:
					printSubMenu1ForFindAndHoldSeatsOption();
					int numSeats = getUserIntSelection(scanner);
					printEnterEmailIdOption();
					String customerEmail = getCustomerEmailIdInput(scanner);
					SeatHold seatHold = ticketService.findAndHoldSeats(numSeats, customerEmail);
					System.out.println(
							"\n\t ***** Response: Seat hold request: " + TicketServiceUtil.getSeatHoldInformation(seatHold) + "\n\n");
					break;
				case 3:
					printSubMenu1ForReserverSeatsOption();
					int seatHoldId = getUserIntSelection(scanner);
					printEnterEmailIdOption();
					String customerEmailId = getCustomerEmailIdInput(scanner);
					String reservationCode = ticketService.reserveSeats(seatHoldId, customerEmailId);
					System.out.println("\n\t ***** Response: Reserve seat request: " + reservationCode + "\n\n");
					break;
				default:
					System.out.println("Invalid input, select a valid integer choice");
					break;
				}
			} while (input != 0);
		} catch (Exception e) {
			System.out.println("Exiting due to error: " + e.toString());
			System.exit(1);
		}

	}
	
	private  int getUserIntSelection(Scanner scanner) {
		while (!scanner.hasNextInt()) {
			System.out.println("Invalid input, please enter a valid integer:");
			scanner.next();
		}
		return scanner.nextInt();
	}

	private  String getCustomerEmailIdInput(Scanner scanner) {
		while (!scanner.hasNext()) {
			System.out.println("Invalid input, please enter a valid emailid");
			scanner.next();
		}
		return scanner.next();
	}

	private static void printSubMenu1ForReserverSeatsOption() {
		System.out.println("\t\t\tEnter Seat hold id for reservation: ");
	}

	private static void printSubMenu1ForFindAndHoldSeatsOption() {
		System.out.println("\t\t\tEnter number of seats to find and hold: ");
	}

	private static void printEnterEmailIdOption() {
		System.out.println("\t\t\tEnter customer emailId: ");
	}

	private static void printSelectionChoices() {
		System.out.println("Enter your selection number: ");
		System.out.println("\t\t 1. Print number of available seats");
		System.out.println("\t\t 2. Find and hold seats");
		System.out.println("\t\t 3. Reserve seats");
		System.out.println("\t\t 0: Exit the application");
	}

}
