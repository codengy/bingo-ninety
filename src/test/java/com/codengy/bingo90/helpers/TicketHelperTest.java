package com.codengy.bingo90.helpers;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TicketHelperTest {
	
	TicketHelper helper = TicketHelper.getInstance();

	@Test
	public void arrayToListAndShuffleTest() {
		int[] arr = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
 		
		List<Integer> shuffled = helper.arrayToListAndShuffle(arr);
		
		assertFalse(Arrays.equals(arr, shuffled.stream().mapToInt(i -> i).toArray()));
	}
	
	@Test
	public void getColumnTotalNumbersTest() {
		assertEquals(9, helper.getColumnTotalNumbers(0));
		assertEquals(10, helper.getColumnTotalNumbers(5));
		assertEquals(11, helper.getColumnTotalNumbers(8));
	}
	
}
