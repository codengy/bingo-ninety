package com.codengy.bingo90.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.codengy.bingo90.entities.Ticket;

public class TicketServiceTest {

	@Test
	public void generateTicketsCountTest() {
		List<Ticket> tickets = new TicketService().generateTickets(10, 6);
		
		assertEquals(10, tickets.size());		
		tickets.forEach(ticket -> assertEquals(6, ticket.getStripesCount()));
	}
	
}
