package com.codengy.bingo90.entities;

import java.util.List;
import java.util.stream.IntStream;

public class Ticket {
	
	public static final int MAX_STRIPES = 6;
	
	public static final int STRIPE_ROW_COUNT = 3;
	
	public static final int STRIPE_COL_COUNT = 9;
	
	public static final int STRIPE_MAX_NUMBERS = 15;
	
	private final int ticketId;

	private List<List<Integer>> stripes;
	
	public Ticket(int ticketId, List<List<Integer>> stripes) {
		this.ticketId = ticketId;
		this.stripes = stripes;
	}
	
	public int getTicketId() {
		return ticketId;
	}
	
	public List<List<Integer>> getStripes() {
		return stripes;
	}
	
	public int getStripesCount() {
		return stripes.size();
	}
	
	@Override
	public String toString() {		
		String title = String.format("========= Ticket #%05d =========\n", ticketId);
		StringBuilder builder = new StringBuilder(title);		
		
		IntStream.range(0, stripes.size()).forEach(stripeInd -> { 
			List<Integer> stripe = stripes.get(stripeInd);
			
			IntStream.range(0, STRIPE_ROW_COUNT).forEach(ind -> {
				List<Integer> subarr = stripe.subList(ind * STRIPE_COL_COUNT,
						ind * STRIPE_COL_COUNT + STRIPE_COL_COUNT);
				builder.append(subarr + "\n");
			});
			
			if (stripeInd < stripes.size() - 1) {
				builder.append("\n");				
			}
		});		
		
		builder.append("=================================\n");		
		return builder.toString();
	}
	
}
