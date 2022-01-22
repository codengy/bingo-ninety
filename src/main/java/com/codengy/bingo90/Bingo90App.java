package com.codengy.bingo90;

import java.util.List;

import com.codengy.bingo90.entities.Ticket;
import com.codengy.bingo90.exceptions.TicketException;
import com.codengy.bingo90.services.TicketService;

public class Bingo90App {
	
	final static TicketService ticketService = TicketService.getInstance();

	public static void main(String... args) {
		if (args == null || args.length < 4) {
			printHelp();
			System.exit(0);
		}
		
		Parameters params = parseArgs(args);
		
		long startTime = System.currentTimeMillis();
		List<Ticket> tickets = null;
		try {
			tickets = ticketService.generateTickets(params.getTicketNumber(), params.getStripeNumber());			
		} catch (TicketException ex) {
			System.out.println(ex.getMessage());
			System.exit(0);
		}
		long endTime = System.currentTimeMillis();
		
		if (params.isPrintTicket()) {
			tickets.forEach(ticket -> System.out.println(ticket));			
		}
		System.out.println(String.format("Elapsed Time: %sms", (endTime - startTime)));
	}
	
	static void printHelp() {
		StringBuilder builder = new StringBuilder("=== Bingo 90 Help ===\n");
		builder.append("*\t-t : number of tickets, e.g. -t 100\n");
		builder.append("*\t-s : number of stripes, e.g. -s 6\n");
		builder.append("\t-p : print tickets\n");
		builder.append("(*: required parameters)\n");
		
		System.out.println(builder.toString());
	}
	
	static Parameters parseArgs(String... args) {
		int ind = 0;
		
		int ticketNumber = 0;
		int stripeNumber = 0;
		boolean printTicket = false;
		
		while ( ind < args.length) {
			if (args[ind].equals("-t")) {
				ticketNumber = Integer.parseInt(args[ind + 1]);
				ind += 2;
				
			} else if (args[ind].equals("-s")) {
				stripeNumber = Integer.parseInt(args[ind + 1]);
				ind += 2;
				
			} else if (args[ind].equals("-p")) {
				printTicket = true;
				ind++;
			}
		}
		
		return new Parameters(ticketNumber, stripeNumber, printTicket);
	}
	
	static class Parameters {
		int ticketNumber = 0;
		int stripeNumber = 0;
		boolean printTicket = false;
		
		public Parameters(int ticketNumber, int stripeNumber, boolean printTicket) {
			this.ticketNumber = ticketNumber;
			this.stripeNumber = stripeNumber;
			this.printTicket = printTicket;
		}
		
		public int getTicketNumber() {
			return ticketNumber;
		}
		
		public int getStripeNumber() {
			return stripeNumber;
		}
		
		public boolean isPrintTicket() {
			return printTicket;
		}
	}

}







