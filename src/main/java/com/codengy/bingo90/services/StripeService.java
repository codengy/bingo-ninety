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

	public Integer[] generateColumnsNumbersUsage(int ticketNumber, int stripeNumber, Integer[] columnSums) {
		int usedNumbers = 9;
		IntStream.range(0, columnSums.length).forEach(ind -> {
			columnSums[ind]++;
		});

		// Column numbers usage is 1 for every column of each stripe
		// because every column has to have at least 1 element
		// and remaining 6 elements are chosen randomly
		Integer[] stripeColumnNumbersUsage = generateRow(usedNumbers);

		if (stripeNumber == 6) {
			// For the last stripe just copy remaining numbers
			IntStream.range(0, Ticket.STRIPE_COL_COUNT).forEach(ind -> {
				int remainingNumbers = helper.getColumnTotalNumbers(ind) - columnSums[ind];
				stripeColumnNumbersUsage[ind] += remainingNumbers;
				columnSums[ind] += remainingNumbers;
			});

		} else {
			populateFirstFiveStripes(stripeNumber, columnSums, stripeColumnNumbersUsage);
		}

		return stripeColumnNumbersUsage;
	}
	
	void populateFirstFiveStripes(int stripeNumber, Integer[] columnSums, Integer[] stripeColumnNumbersUsage) {
		Random random = new Random();
		int remainingStripes = Ticket.MAX_STRIPES - stripeNumber;
		int usedNumbers = 9;
		
		while (usedNumbers < Ticket.STRIPE_MAX_NUMBERS) {
			double randomNumber = random.nextDouble();
			int selectedColumn = random.nextInt(Ticket.STRIPE_COL_COUNT);

			// check critical column
			boolean correctProbability = false;

			if (stripeNumber > 3) {
				// For last 3 stripes there is need for probability correction because more numbers can remain 
				// and stripes won't be populated regularly
				for (int i = columnSums.length - 1; i >= 0 ; i--) {
					if (helper.getColumnTotalNumbers(i) - columnSums[i] - 1 >= (Ticket.MAX_STRIPES - stripeNumber) * 3) {
						selectedColumn = i;
						correctProbability = true;
						break;
					}
				}
			}

			int remainingNumbers = helper.getColumnTotalNumbers(selectedColumn) - columnSums[selectedColumn];
			double probability = !correctProbability
					? (double) remainingNumbers / (double) helper.getColumnTotalNumbers(selectedColumn)
					: 1;

			if (remainingNumbers <= remainingStripes) {
				probability = 0;
			}

			if (randomNumber < probability && stripeColumnNumbersUsage[selectedColumn] < 3 && columnSums[selectedColumn] < 10) {
				stripeColumnNumbersUsage[selectedColumn]++;
				usedNumbers++;
				columnSums[selectedColumn]++;
			}
		}
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

		Map<Integer, List<Integer>> columnIndexes = new HashMap<>();
		IntStream.range(0, Ticket.STRIPE_ROW_COUNT).forEach(ind -> columnIndexes.put(ind + 1, new LinkedList<>()));

		for (int i = 0; i < Ticket.STRIPE_COL_COUNT; i++) {
			int colNumCount = stripeNumberCount[i];
			columnIndexes.get(colNumCount).add(i);
		}

		// populate all three positions of the stripe column
		populateColumnMaskWithThreeNumbers(columnIndexes, rowNumCount, stripeMask);

		// populate two positions of the stripe column mask
		populateColumnMaskWithTwoNumbers(columnIndexes, rowNumCount, stripeMask);
		
		// populate one positions of the stripe column mask
		populateColumnMaskWithOneNumber(columnIndexes, rowNumCount, stripeMask);
		
		return stripeMask;
	}
	
	void populateColumnMaskWithThreeNumbers(Map<Integer, List<Integer>> columnIndexes, int[] rowNumCount, Integer[] stripeMask) {
		for (int i = 0; i < columnIndexes.get(3).size(); i++) {
			int columnIndex = columnIndexes.get(3).get(i);
			
			IntStream.range(0, Ticket.STRIPE_ROW_COUNT).forEach(index -> {
				stripeMask[columnIndex + index * 9] = 1;
				rowNumCount[index]++;
			});
		}
	}
	
	void populateColumnMaskWithTwoNumbers(Map<Integer, List<Integer>> columnIndexes, int[] rowNumCount, Integer[] stripeMask) {
		Random random = new Random();
		
		int colsWithTwoNumbersCount = columnIndexes.get(2).size();
		for (int i = 0; i < colsWithTwoNumbersCount; i++) {
			int ind = columnIndexes.get(2).get(i);
			double probability = random.nextDouble();
			int unsetCount = colsWithTwoNumbersCount - i;

			// For stripes with 6 columns of 2 numbers probability has to be manually corrected
			// because in some cases there won't be place left over for columns with 1 number
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
	}
	
	void populateColumnMaskWithOneNumber(Map<Integer, List<Integer>> columnIndexes, int[] rowNumCount, Integer[] stripeMask) {
		Random random = new Random();
		
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
