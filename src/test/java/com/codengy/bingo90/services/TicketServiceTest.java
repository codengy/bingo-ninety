package com.codengy.bingo90.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
	public void generateTicketStripeSizeExceptionTest() {
		assertThrows(TicketException.class, () -> ticketService.generateTickets(1, 8));
	}
	
	@Test
	public void generateTicketsSizeExceptionTest() {
		assertThrows(TicketException.class, () -> ticketService.generateTickets(-1, 0));
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
	
	@Test
	public void populateTicketTest() {		
		List<List<Integer>> ticket = ticketService.populateTicket(ticketMask(), ticketNumbers());
		
		assertArrayEquals(new Integer[] {
				0, 18,  0, 31, 40,  0, 60, 70,  0, 
				3,  0,  0,  0,  0, 50, 61, 73, 84, 
				4,  0, 22, 32,  0,  0,  0, 79, 87 
		}, ticket.get(0).toArray());
		
		assertArrayEquals(new Integer[] {
				0, 10, 20, 35,  0, 52,  0,  0, 83,
				1, 13,  0,  0, 47, 58,  0, 71,  0,
				5, 19, 29,  0,  0,  0, 68, 77,  0 
		}, ticket.get(1).toArray());
		
		assertArrayEquals(new Integer[] {
				6, 14,  0,  0,  0,  0, 63, 76, 85,
				0, 17, 24, 30, 45,  0, 64,  0,  0,
				0,  0, 27, 36,  0, 51,  0, 78, 89
		}, ticket.get(2).toArray());
		
		assertArrayEquals(new Integer[] {
				0,  0,  0,  0, 43, 54, 62, 75, 82,
				2, 11, 21,  0,  0, 57, 67,  0,  0,
				8,  0,  0, 38,  0, 59, 69,  0, 86
		}, ticket.get(3).toArray());
		
		assertArrayEquals(new Integer[] {
				0,  0, 23,  0, 41, 55, 66,  0, 80,
				7, 12, 26,  0, 48, 56,  0,  0,  0,
				0, 15,  0, 37, 49,  0,  0, 74, 81
		}, ticket.get(4).toArray());
		
		assertArrayEquals(new Integer[] {
				9,  0, 25, 33, 42,  0,  0,  0, 88,
				0, 16,  0, 34, 44,  0, 65, 72,  0,
				0,  0, 28, 39, 46, 53,  0,  0, 90
		}, ticket.get(5).toArray());
	}
	
	List<Integer[]> ticketMask() {
		return Arrays.asList(
			new Integer[] { 
					0, 1, 0, 1, 1, 0, 1, 1, 0, 
					1, 0, 0, 0, 0, 1, 1, 1, 1, 
					1, 0, 1, 1, 0, 0, 0, 1, 1 
			},
			new Integer[] { 
					0, 1, 1, 1, 0, 1, 0, 0, 1,
					1, 1, 0, 0, 1, 1, 0, 1, 0,
					1, 1, 1, 0, 0, 0, 1, 1, 0
			},
			new Integer[] { 
					1, 1, 0, 0, 0, 0, 1, 1, 1,
					0, 1, 1, 1, 1, 0, 1, 0, 0,
					0, 0, 1, 1, 0, 1, 0, 1, 1
			},
			new Integer[] { 
					0, 0, 0, 0, 1, 1, 1, 1, 1,
					1, 1, 1, 0, 0, 1, 1, 0, 0,
					1, 0, 0, 1, 0, 1, 1, 0, 1
			},
			new Integer[] { 
					0, 0, 1, 0, 1, 1, 1, 0, 1,
					1, 1, 1, 0, 1, 1, 0, 0, 0,
					0, 1, 0, 1, 1, 0, 0, 1, 1
			},
			new Integer[] { 
					1, 0, 1, 1, 1, 0, 0, 0, 1,
					0, 1, 0, 1, 1, 0, 1, 1, 0,
					0, 0, 1, 1, 1, 1, 0, 0, 1
			}
		);
	}
	
	List<List<Integer>> ticketNumbers() {		
		return Arrays.asList(
			Arrays.asList(new Integer[] {
					3, 4, 18, 22, 31, 32, 40, 50, 60, 61, 70, 73, 79, 84, 87
			}),
			Arrays.asList(new Integer[] {
					1, 5, 10, 13, 19, 20, 29, 35, 47, 52, 58, 68, 71, 77, 83
			}),
			Arrays.asList(new Integer[] {
					6, 14, 17, 24, 27, 30, 36, 45, 51, 63, 64, 76, 78, 85, 89	
			}),
			Arrays.asList(new Integer[] {
					2, 8, 11, 21, 38, 43, 54, 57, 59, 62, 67, 69, 75, 82, 86
			}),
			Arrays.asList(new Integer[] {
					7, 12, 15, 23, 26, 37, 41, 48, 49, 55, 56, 66, 74, 80, 81
			}),
			Arrays.asList(new Integer[] {
					9, 16, 25, 28, 33, 34, 39, 42, 44, 46, 53, 65, 72, 88, 90
			})
		);
	}

}
