package com.codengy.bingo90.helpers;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.codengy.bingo90.entities.Ticket;

public class TicketHelper {
	
	private final static TicketHelper INSTANCE = new TicketHelper();
	
	private TicketHelper() { }
	
	public static TicketHelper getInstance() {
		return INSTANCE;
	}
	
	public List<Integer> arrayToListAndShuffle(int[] source) {
		List<Integer> numbers = Arrays.stream(source).boxed().collect(Collectors.toCollection(LinkedList::new));
		Collections.shuffle(numbers);
		return numbers;
	}
	
	public int getColumnTotalNumbers(int column) {
		if (column < 0 || column > 8) {
			throw new RuntimeException("[Should NEVER happened] Column index has to be between 0 and 8");
		}
		switch (column) {
			case 0: return 9;
			case 8: return 11;
			default: return 10;
		}
	}

	public void printStripeMask(int stripeNumber, Integer[] stripeMask) {
		StringBuilder builder = new StringBuilder("\n");

		IntStream.range(0, Ticket.STRIPE_ROW_COUNT).forEach(ind -> {
			List<Integer> subarr = Arrays.asList(stripeMask).subList(ind * Ticket.STRIPE_COL_COUNT,
					ind * Ticket.STRIPE_COL_COUNT + Ticket.STRIPE_COL_COUNT);
			builder.append(subarr + "\n");
		});

		System.out.println(builder.toString());
	}
	
	public void printTicket(int ticketNumber, List<Integer> ticket) {
		StringBuilder builder = new StringBuilder("\n");

		IntStream.range(0, Ticket.STRIPE_ROW_COUNT).forEach(ind -> {
			List<Integer> subarr = ticket.subList(ind * Ticket.STRIPE_COL_COUNT,
					ind * Ticket.STRIPE_COL_COUNT + Ticket.STRIPE_COL_COUNT);
			builder.append(subarr + "\n");
		});

		System.out.println(builder.toString());
	}

}
