package com.codengy.bingo90;

import java.util.List;

import com.codengy.bingo90.entities.Ticket;
import com.codengy.bingo90.services.TicketService;

public class Bingo90App {

	public static void main(String... args) {
		TicketService ticketService = new TicketService();
		
		long startTime = System.currentTimeMillis();
		List<Ticket> tickets = ticketService.generateTickets(2, Ticket.MAX_STRIPES);
		long endTime = System.currentTimeMillis();
		
		tickets.forEach(ticket -> System.out.println(ticket));
		System.out.println(String.format("Elapsed Time: %sms", (endTime - startTime)));
	}

}







