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
import com.codengy.bingo90.helpers.TicketHelper;

public class TicketService {

	static final Logger logger = LoggerFactory.getLogger(TicketService.class);

	StripeService stripeService = new StripeService();
	TicketHelper helper = new TicketHelper();
	
	public List<Ticket> generateTickets(int numOfTickets, int numOfStripes) {
		List<Ticket> tickets = new LinkedList<>();
		
		IntStream.range(0, numOfTickets).forEach(ticketIndex -> {
			Ticket ticket = generateTicket(ticketIndex + 1, numOfStripes);
			tickets.add(ticket);
		});
		
		return tickets;
	}

	public Ticket generateTicket(int ticketId, int numOfStripes) {
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
	
	public List<Integer[]> generateMask(List<Integer[]> ticket) {
		List<Integer[]> masks = new LinkedList<> ();
		
		IntStream.range(0, ticket.size()).forEach(ind -> {
			masks.add(
				stripeService.generateStripeMask(ind + 1, ticket.get(ind))
			);
		});
		
		return masks;
	}
	
	public List<Integer[]> generateColumnsNumbersUsage(int ticketNumber, int numOfStripes) {
		List<Integer[]> stripes = new LinkedList<>();
		Integer[] colSums = new Integer[Ticket.STRIPE_COL_COUNT];
		Arrays.setAll(colSums, v -> 0);

		IntStream.range(0, numOfStripes).forEach(ind -> {
			stripes.add(stripeService.generateColumnsNumbersUsage(ticketNumber, ind + 1, colSums));
		});
		
		return stripes;
	}

	private Map<Integer, List<Integer>> generateTicketNumbers() {
		Map<Integer, List<Integer>> ticketNumbers;

		ticketNumbers = new HashMap<>();

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

	private List<List<Integer>> pickNumbers(Map<Integer, List<Integer>> ticketNumbers,
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

	private List<List<Integer>> populateTicket(List<Integer[]> ticketMask, List<List<Integer>> ticketNumbers) {
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
