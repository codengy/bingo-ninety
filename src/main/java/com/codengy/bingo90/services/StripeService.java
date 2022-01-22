package com.codengy.bingo90.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codengy.bingo90.entities.Ticket;
import com.codengy.bingo90.helpers.TicketHelper;

public final class StripeService {
	
	private final static StripeService INSTANCE = new StripeService();
	
	private StripeService() { }
	
	public static StripeService getInstance() {
		return INSTANCE;
	}
	
	static final Logger logger = LoggerFactory.getLogger(StripeService.class);
	
	private TicketHelper helper = TicketHelper.getInstance();
	
	public static final double PROBABILITY = 0.5d;

	public Integer[] generateColumnsNumbersUsage(int ticketNumber, int stripeNumber, Integer[] columnSums) {
		int leftStripes = Ticket.MAX_STRIPES - stripeNumber;
		int usedNumbers = 9;
		IntStream.range(0, columnSums.length).forEach(ind -> {
			columnSums[ind]++;
		});

		Integer[] stripeMask = generateRow(usedNumbers);
		Random random = new Random();

		if (stripeNumber == 6) {
			IntStream.range(0, Ticket.STRIPE_COL_COUNT).forEach(ind -> {
				int leftNumbers = helper.getColumnTotalNumbers(ind) - columnSums[ind];
				stripeMask[ind] += leftNumbers;
				columnSums[ind] += leftNumbers;
			});

		} else {
			while (usedNumbers < Ticket.STRIPE_MAX_NUMBERS) {
				double randomNumber = random.nextDouble();
				int selectedColumn = random.nextInt(Ticket.STRIPE_COL_COUNT);

				// check critical column
				boolean correctProbability = false;

				if (stripeNumber > 3) {
					for (int i = columnSums.length - 1; i >= 0 ; i--) {
						if (helper.getColumnTotalNumbers(i) - columnSums[i] - 1 >= (Ticket.MAX_STRIPES - stripeNumber) * 3) {
							selectedColumn = i;
							correctProbability = true;
							break;
						}
					}
				}

				int leftNumbers = helper.getColumnTotalNumbers(selectedColumn) - columnSums[selectedColumn];
				double probability = !correctProbability
						? (double) leftNumbers / (double) helper.getColumnTotalNumbers(selectedColumn)
						: 1;

				if (leftNumbers <= leftStripes) {
					probability = 0;
				}

				if (randomNumber < probability && stripeMask[selectedColumn] < 3 && columnSums[selectedColumn] < 10) {
					stripeMask[selectedColumn]++;
					usedNumbers++;
					columnSums[selectedColumn]++;
				}
			}
		}

		return stripeMask;
	}

	Integer[] generateRow(int n) {
		Integer[] row = new Integer[n];
		Arrays.setAll(row, v -> 1);
		return row;
	}

	Integer[] generateStripeMask(int stripeNumber, Integer[] stripeNumberCount) {
		int[] rowNumCount = new int[Ticket.STRIPE_ROW_COUNT];
		Arrays.setAll(rowNumCount, ind -> 0);
		Integer[] stripeMask = new Integer[ Ticket.STRIPE_COL_COUNT * Ticket.STRIPE_ROW_COUNT ];
		Arrays.setAll(stripeMask, v -> 0);

		Random random = new Random();

		Map<Integer, List<Integer>> columnIndexes = new HashMap<>();
		IntStream.range(0, Ticket.STRIPE_ROW_COUNT).forEach(ind -> columnIndexes.put(ind + 1, new LinkedList<>()));

		for (int i = 0; i < Ticket.STRIPE_COL_COUNT; i++) {
			int colNumCount = stripeNumberCount[i];
			columnIndexes.get(colNumCount).add(i);
		}

		// populate all three positions
		for (int i = 0; i < columnIndexes.get(3).size(); i++) {
			int columnIndex = columnIndexes.get(3).get(i);
			
			IntStream.range(0, Ticket.STRIPE_ROW_COUNT).forEach(index -> {
				stripeMask[columnIndex + index * 9] = 1;
				rowNumCount[index]++;
			});
		}

		int colsWithTwoNumbersCount = columnIndexes.get(2).size();
		for (int i = 0; i < colsWithTwoNumbersCount; i++) {
			int ind = columnIndexes.get(2).get(i);
			double probability = random.nextDouble();
			int unsetCount = colsWithTwoNumbersCount - i;

			if (colsWithTwoNumbersCount == 6 && unsetCount % 2 == 0) {
				probability = i * .25d;
			}

			if (probability < .33d && rowNumCount[0] < 5 && rowNumCount[1] < 5) {
				// populate first two row positions
				stripeMask[ind] = 1;
				rowNumCount[0]++;

				stripeMask[ind + 9] = 1;
				rowNumCount[1]++;

			} else if (probability < (.33d + .33d) && rowNumCount[1] < 5 && rowNumCount[2] < 5) {
				// populate last two row positions
				stripeMask[ind + 9] = 1;
				rowNumCount[1]++;

				stripeMask[ind + 18] = 1;
				rowNumCount[2]++;

			} else if (probability <= 1 && rowNumCount[0] < 5 && rowNumCount[2] < 5) {
				// populate first and last row positions
				stripeMask[ind] = 1;
				rowNumCount[0]++;

				stripeMask[ind + 18] = 1;
				rowNumCount[2]++;
			}
		}

		// populate stripe with 1 number
		for (int i = 0; i < columnIndexes.get(1).size(); i++) {
			int columnIndex = columnIndexes.get(1).get(i);
			List<Integer> freeIndexes = new LinkedList<>();
			
			IntStream.range(0, Ticket.STRIPE_ROW_COUNT).forEach(index -> {
				if (rowNumCount[index] < 5) {
					freeIndexes.add(index);
				}
			});
			
			int randomIndex = random.nextInt(freeIndexes.size());
			int index = freeIndexes.get(randomIndex);
			
			stripeMask[columnIndex + index * 9] = 1;
			rowNumCount[index]++;
		}
		
		return stripeMask;
	}
	
	public List<Integer> populateStripe(Integer[] stripeMask, List<Integer> stripeNumbers) {
		Integer[] stripe = new Integer[Ticket.STRIPE_COL_COUNT * Ticket.STRIPE_ROW_COUNT];
		List<Integer> numbers = new LinkedList<>(stripeNumbers);

		IntStream.range(0, Ticket.STRIPE_COL_COUNT).forEach(ind -> {
			IntStream.range(0, Ticket.STRIPE_ROW_COUNT).forEach(colInd -> {
				int cellIndex = ind + colInd * 9; 
				if (stripeMask[cellIndex] == 1) {
					stripe[cellIndex] = numbers.remove(0);
				} else {
					stripe[cellIndex] = 0;
				}
			});
		});

		return Arrays.asList(stripe);
	}
	
}
