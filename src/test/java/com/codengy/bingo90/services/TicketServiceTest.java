package com.codengy.bingo90.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.codengy.bingo90.entities.Ticket;
import com.codengy.bingo90.exceptions.TicketException;

public class TicketServiceTest {

	TicketService ticketService = TicketService.getInstance();

	@Test
	public void generateTicketsSizeTest() throws TicketException {
		List<Ticket> tickets = ticketService.generateTickets(100, 6);

		assertEquals(100, tickets.size(), "100 tickets have to be generated");
		
		tickets.forEach(ticket ->
		
			assertEquals(6, ticket.getStripesCount(), "Ticket has to have 6 stripes"));
	}
	
	@Test
	public void generateTicketsNumbersTest() throws TicketException {
		List<Ticket> tickets = ticketService.generateTickets(1, 6);
		
		List<Integer> ticketNumbers = new LinkedList<>();
		tickets.get(0).getStripes().forEach(stripeNumbers -> ticketNumbers.addAll(stripeNumbers));
		
		List<Integer> uniqueNumbers = ticketNumbers.stream().distinct().collect(Collectors.toList());
		
		// 0 represents an empty cell and with other 90 numbers there is 91 unique number from 0 to 90
		assertEquals(91, uniqueNumbers.size(), "Ticket with 6 stripes has to have all unique numbers between 0 and 90");		
	}

	@Test
	public void generateMaskSumTest() {
		List<Integer[]> numbersUsage = new ArrayList<>();
		numbersUsage.add(new Integer[] { 3, 1, 2, 2, 1, 2, 1, 1, 2 });

		List<Integer[]> mask = ticketService.generateMask(numbersUsage);

		assertEquals(1, mask.size(), "Size of masks has to be 1 for 1 stripe");

		int sum = Arrays.stream(mask.get(0)).reduce(0, (subtotal, element) -> subtotal + element);

		assertEquals(15, sum, "Sum of all mask elements has to be 15");
	}

	@Test
	public void generateMaskRowSumTest() {
		List<Integer[]> numbersUsage = new ArrayList<>();
		numbersUsage.add(new Integer[] { 3, 1, 2, 2, 1, 2, 1, 1, 2 });

		List<Integer[]> stripeMask = ticketService.generateMask(numbersUsage);

		List<Integer> maskRow1 = Arrays.asList(stripeMask.get(0)).subList(0, 9);
		List<Integer> maskRow2 = Arrays.asList(stripeMask.get(0)).subList(9, 18);
		List<Integer> maskRow3 = Arrays.asList(stripeMask.get(0)).subList(18, 27);

		int rowSum1 = maskRow1.stream().reduce(0, (subtotal, element) -> subtotal + element);
		int rowSum2 = maskRow2.stream().reduce(0, (subtotal, element) -> subtotal + element);
		int rowSum3 = maskRow3.stream().reduce(0, (subtotal, element) -> subtotal + element);

		assertEquals(5, rowSum1, "Sum of 1st row mask elements has to be 5");
		assertEquals(5, rowSum2, "Sum of 2nd row mask elements has to be 5");
		assertEquals(5, rowSum3, "Sum of 3rd row mask elements has to be 5");
	}

	@Test
	public void generateMaskValuesTest() {
		List<Integer[]> numbersUsage = new ArrayList<>();
		numbersUsage.add(new Integer[] { 3, 1, 2, 2, 1, 2, 1, 1, 2 });

		List<Integer[]> mask = ticketService.generateMask(numbersUsage);

		Arrays.stream(mask.get(0)).forEach(val -> 
			assertTrue(val >= 0 && val <= 1, String.format("Value %s element has to be 0 or 1", val)));
	}

	@Test
	public void generateColumnsNumbersUsageOneStripeTest() {
		List<Integer[]> numbersUsage = ticketService.generateColumnsNumbersUsage(1, 1);

		assertEquals(9, numbersUsage.get(0).length, "Every stripe has to have 9 numbers which is equal to number of columns");
	}

	@Test
	public void generateColumnsNumbersUsageOneStripeValuesTest() {
		List<Integer[]> numbersUsage = ticketService.generateColumnsNumbersUsage(1, 1);

		Stream.of(numbersUsage.get(0)).forEach(val -> 
			assertTrue(val > 0 && val <= 3, String.format("Value %s has to be between 1 and 3", val)));
	}

	@Test
	public void generateColumnsNumbersUsageOneStripeSumTest() {
		List<Integer[]> numbersUsage = ticketService.generateColumnsNumbersUsage(1, 1);

		int sum = Stream.of(numbersUsage.get(0)).reduce(0, (subtotal, element) -> subtotal + element);
		
		assertEquals(15, sum, "Sum of all stripe used numbers has to be 15");
	}

	@Test
	public void generateTicketNumbersSizeTest() {
		Map<Integer, List<Integer>> ticketNumbers = ticketService.generateTicketNumbers();

		assertEquals(9, ticketNumbers.size(), "There should be 9 numbers lists for 9 columns");
		assertEquals(9, ticketNumbers.get(1).size(), "1st column numbers size has to be 9");
		assertEquals(11, ticketNumbers.get(9).size(), "9th column numbers size has to be 11");
		IntStream.range(2, 9).forEach(ind -> 
			assertEquals(10, ticketNumbers.get(ind).size(), "2nd - 8th column numbers size has to be 10"));
	}

	@Test
	public void generateTicketNumbersValuesTest() {
		Map<Integer, List<Integer>> ticketNumbers = ticketService.generateTicketNumbers();

		ticketNumbers.get(1)
				.forEach(val -> assertTrue(val > 0 && val <= 9, String.format("Value %s has to between 1 and 9", val)));
		ticketNumbers.get(9).forEach(
				val -> assertTrue(val >= 80 && val <= 90, String.format("Value %s has to between 80 and 90", val)));

		IntStream.range(2, 9).forEach(stripeIndex -> {
			ticketNumbers.get(stripeIndex).forEach(val -> 
				assertTrue(
					val >= (stripeIndex - 1) * 10 && val <= (stripeIndex - 1) * 10 + 9,
					String.format("Value %s has to between %s and %s", val, (stripeIndex - 1) * 10, (stripeIndex * 10 - 1) + 9)));
		});
	}

}
