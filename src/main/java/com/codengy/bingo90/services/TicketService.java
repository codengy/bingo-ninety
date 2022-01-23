package com.codengy.bingo90.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codengy.bingo90.entities.Ticket;
import com.codengy.bingo90.exceptions.TicketException;
import com.codengy.bingo90.helpers.TicketHelper;

public final class TicketService {
	
	private final static TicketService INSTANCE = new TicketService();
	
	private TicketService() { }
	
	public static TicketService getInstance() {
		return INSTANCE;
	}

	static final Logger logger = LoggerFactory.getLogger(TicketService.class);

	private StripeService stripeService = StripeService.getInstance();
	private TicketHelper helper = TicketHelper.getInstance();
	
	public List<Ticket> generateTickets(int numOfTickets, int numOfStripes) throws TicketException {	
		if (numOfTickets < 1) {
			throw new TicketException("Number of tickets can be 1 and greater");
		}	
		
		List<Ticket> tickets = new LinkedList<>();
		
		for (int ticketIndex = 0; ticketIndex < numOfTickets; ticketIndex++) {
			Ticket ticket = generateTicket(ticketIndex + 1, numOfStripes);
			tickets.add(ticket);
		}
		
		return tickets;
	}

	public Ticket generateTicket(int ticketId, int numOfStripes) throws TicketException {
		if (numOfStripes < 1 || numOfStripes > 6) {
			throw new TicketException("Number of stripes can be between 1 and 6");
		}		
		
		Map<Integer, List<Integer>> ticketNumbers = generateTicketNumbers();
		List<Integer[]> stripes = generateColumnsNumbersUsage(ticketId, numOfStripes);
		List<Integer[]> masks = generateMask(stripes);

		List<List<Integer>> pickedNumbers = pickNumbers(ticketNumbers, stripes);
		List<List<Integer>> populatedTicket = populateTicket(masks, pickedNumbers);

//		IntStream.range(0, stripes.size()).forEach(ind -> System.out.println(Arrays.toString(stripes.get(ind))));
//		IntStream.range(0, masks.size()).forEach(ind -> helper.printStripeMask(ind + 1, masks.get(ind)));
//		IntStream.range(0, pickedNumbers.size()).forEach(ind -> System.out.println(pickedNumbers.get(ind)));
//		IntStream.range(0, populatedTicket.size()).forEach(ind -> helper.printTicket(ind + 1, populatedTicket.get(ind)));

		return new Ticket(ticketId, populatedTicket);
	}
	
	List<Integer[]> generateMask(List<Integer[]> ticket) {
		List<Integer[]> masks = new LinkedList<> ();
		
		IntStream.range(0, ticket.size()).forEach(ind -> {
			masks.add(
				stripeService.generateStripeMask(ind + 1, ticket.get(ind))
			);
		});
		
		return masks;
	}
	
	List<Integer[]> generateColumnsNumbersUsage(int ticketNumber, int numOfStripes) {
		List<Integer[]> stripes = new LinkedList<>();
		Integer[] colSums = new Integer[Ticket.STRIPE_COL_COUNT];
		Arrays.setAll(colSums, v -> 0);

		IntStream.range(0, numOfStripes).forEach(ind -> {
			stripes.add(stripeService.generateColumnsNumbersUsage(ticketNumber, ind + 1, colSums));
		});
		
		return stripes;
	}

	Map<Integer, List<Integer>> generateTicketNumbers() {
		Map<Integer, List<Integer>> ticketNumbers = new HashMap<>();

		int[] firstStripeColumnNumbers = IntStream.rangeClosed(1, 9).toArray();
		ticketNumbers.put(1, helper.arrayToListAndShuffle(firstStripeColumnNumbers));

		// init number for stripe 1 to 7
		IntStream.range(1, Ticket.STRIPE_COL_COUNT - 1).forEach(ind -> {
			int start = ind * 10;
			int end = start + 9;
			int[] midStripeColumnNumbers = IntStream.rangeClosed(start, end).toArray();
			ticketNumbers.put(ind + 1, helper.arrayToListAndShuffle(midStripeColumnNumbers));
		});

		int[] lastStripeColumnNumbers = IntStream.rangeClosed(80, 90).toArray();
		ticketNumbers.put(9, helper.arrayToListAndShuffle(lastStripeColumnNumbers));

		return ticketNumbers;
	}

	List<List<Integer>> pickNumbers(Map<Integer, List<Integer>> ticketNumbers,
			List<Integer[]> columnsNumbersUsage) {
		List<List<Integer>> pickedNumbers = new LinkedList<>();

		Integer[] numSum = new Integer[Ticket.STRIPE_COL_COUNT];
		Arrays.setAll(numSum, i -> 0);

		IntStream.range(0, columnsNumbersUsage.size()).forEach(ind -> {
			List<Integer> stripe = new LinkedList<>();
			Integer[] columnNumbers = columnsNumbersUsage.get(ind);

			IntStream.range(0, columnNumbers.length).forEach(index -> {
				Integer colNum = columnNumbers[index];
				List<Integer> stripeNumbers = ticketNumbers.get(index + 1);
				stripe.addAll(stripeNumbers.subList(numSum[index], numSum[index] + colNum));
				numSum[index] += colNum;
			});

			Collections.sort(stripe);
			pickedNumbers.add(stripe);
		});

		return pickedNumbers;
	}

	List<List<Integer>> populateTicket(List<Integer[]> ticketMask, List<List<Integer>> ticketNumbers) {
		List<List<Integer>> populatedTicket = new ArrayList<>(ticketNumbers.size());

		IntStream.range(0, ticketNumbers.size()).forEach(stripeIndex -> {
			List<Integer> stripeNumbers = ticketNumbers.get(stripeIndex);
			Integer[] stripeMask = ticketMask.get(stripeIndex);

			List<Integer> populatedStripe = stripeService.populateStripe(stripeMask, stripeNumbers);
			populatedTicket.add(populatedStripe);
		});

		return populatedTicket;
	}

}
