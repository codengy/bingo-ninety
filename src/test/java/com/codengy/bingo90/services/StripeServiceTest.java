package com.codengy.bingo90.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class StripeServiceTest {

	StripeService stripeService = StripeService.getInstance();

	@Test
	public void generateColumnsNumbersUsageSixStripesSizeTest() {
		Integer[] columnSums = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		IntStream.range(0, 6).forEach(ind -> {
			Integer[] columnUsage = stripeService.generateColumnsNumbersUsage(1, ind + 1, columnSums);
			
			assertEquals(9, columnUsage.length, "Every stripe has to have 9 columns");
		});
	}

	@Test
	public void generateColumnsNumbersUsageSixStripesSumTest() {
		Integer[] columnSums = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		IntStream.range(0, 6).forEach(ind -> {
			Integer[] columnUsage = stripeService.generateColumnsNumbersUsage(1, ind + 1, columnSums);
			int sum = Arrays.stream(columnUsage).reduce(0, (subtotal, element) -> subtotal + element);
			
			assertEquals(15, sum, "Sum of all numbers usage has to be 15");
		});
	}

	@Test
	public void generateColumnsNumbersUsageValuesTest() {
		Integer[] columnSums = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		Integer[] columnUsage = stripeService.generateColumnsNumbersUsage(1, 1, columnSums);

		Stream.of(columnUsage).forEach(
				val -> assertTrue(val > 0 && val <= 3, String.format("Value %s has to be between 1 and 3", val)));
	}
	
	@Test
	public void generateStripeMaskSizeTest() {
		Integer[] stripeNumbersCount = new Integer[] { 1, 2, 1, 3, 2, 2, 1, 2, 1 };
		Integer[] stripeMask = stripeService.generateStripeMask(1, stripeNumbersCount);
		
		assertEquals(27, stripeMask.length);
	}
	
	@Test
	public void generateStripeMaskValuesTest() {
		Integer[] stripeNumbersCount = new Integer[] { 1, 2, 1, 3, 2, 2, 1, 2, 1 };
		Integer[] stripeMask = stripeService.generateStripeMask(1, stripeNumbersCount);
		
		Stream.of(stripeMask).forEach(val -> 
			assertTrue(val >= 0 && val <= 1, String.format("Value %s has to be between 0 and 1",val)));
	}
	
	@Test
	public void generateStripeMaskSumTest() {
		Integer[] stripeNumbersCount = new Integer[] { 1, 2, 1, 3, 2, 2, 1, 2, 1 };
		Integer[] stripeMask = stripeService.generateStripeMask(1, stripeNumbersCount);
		
		int sum = Stream.of(stripeMask).reduce(0, (subtotal, element) -> subtotal + element);
		assertEquals(15, sum, "Sum of stripe mask has to be 15");
	}
	
	@Test
	public void generateStripeRowMaskSumTest() {
		Integer[] stripeNumbersCount = new Integer[] { 1, 2, 1, 3, 2, 2, 1, 2, 1 };
		Integer[] stripeMask = stripeService.generateStripeMask(1, stripeNumbersCount);
		
		List<Integer> maskRow1 = Arrays.asList(stripeMask).subList(0, 9);
		List<Integer> maskRow2 = Arrays.asList(stripeMask).subList(9, 18);
		List<Integer> maskRow3 = Arrays.asList(stripeMask).subList(18, 27);

		int rowSum1 = maskRow1.stream().reduce(0, (subtotal, element) -> subtotal + element);
		int rowSum2 = maskRow2.stream().reduce(0, (subtotal, element) -> subtotal + element);
		int rowSum3 = maskRow3.stream().reduce(0, (subtotal, element) -> subtotal + element);

		assertEquals(5, rowSum1, "Sum of 1st row mask elements has to be 5");
		assertEquals(5, rowSum2, "Sum of 2nd row mask elements has to be 5");
		assertEquals(5, rowSum3, "Sum of 3rd row mask elements has to be 5");
	}
	
	@Test
	public void populateStripePositionTest() {
		Integer[] mask = new Integer[] { 
				0, 0, 1, 1, 1, 1, 0, 1, 0, 
				1, 1, 1, 0, 1, 0, 1, 0, 0, 
				0, 0, 0, 1, 1, 0, 1, 1, 1 
		};
		List<Integer> numbers = Arrays.asList(new Integer[] { 8, 10, 23, 26, 30, 37, 40, 46, 47, 53, 62, 69, 71, 77, 86 });
		List<Integer> populatedStripe = stripeService.populateStripe(mask, numbers);
		
		Integer[] expectedStripe = new Integer[] { 
				0,  0, 23, 30, 40, 53,  0, 71,  0, 
				8, 10, 26,  0, 46,  0, 62,  0,  0, 
				0,  0,  0, 37, 47,  0, 69, 77, 86 
		};
		
		assertArrayEquals(expectedStripe, populatedStripe.toArray());
	}

}
