package com.codengy.bingo90.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
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

}
