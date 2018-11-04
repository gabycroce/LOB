package it.tradingbots.lob;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class OrderBook extends AggregatedOrderBook {
	private LinkedList<PriceLevel> asks;
	private LinkedList<PriceLevel> bids;
	
	public OrderBook(int maxDepth) {
		super(maxDepth);
		asks = new LinkedList<PriceLevel>();
		bids = new LinkedList<PriceLevel>();
	}

	@Override
	public List<PriceLevel> getBids(int maxLevels) {
		if (maxLevels <= 0) {
			return bids;
		} else {
			return bids.subList(0, Math.min(maxLevels, bids.size()));
		}
	}

	@Override
	public List<PriceLevel> getAsks(int maxLevels) {
		if (maxLevels <= 0) {
			return asks;
		} else {
			return asks.subList(0, Math.min(maxLevels, asks.size()));
		}
	}

	@Override
	public void clear() {
		asks.clear();
		bids.clear();
	}
	
	private void insert(PriceLevel pl, LinkedListModifier<PriceLevel> modifier) {
		Optional<PriceLevel> insertionPoint = modifier.get(PriceLevel::getPrice, pl.getPrice());
		if (!insertionPoint.isPresent() && pl.getVolume() > 0) {
			/* First bid */
			modifier.add(pl);
		} else if (insertionPoint.isPresent()) {
			if (insertionPoint.get().getPrice() == pl.getPrice()) {
				/* Update */
				if (pl.getVolume() > 0)
					modifier.set(pl);
				else
					modifier.remove();
			} else /* Create */
				modifier.add(pl);
		}
	}

	@Override
	public void bid(long price, long volume, boolean fromTop) {
		LinkedListModifier<PriceLevel> modifier = new LinkedListModifier<PriceLevel>(bids, LinkedListModifier.Order.DESCENDING, fromTop);
		insert(new PriceLevel(price, volume), modifier);
	}
	

	@Override
	public void ask(long price, long volume, boolean fromTop) {
		LinkedListModifier<PriceLevel> modifier = new LinkedListModifier<PriceLevel>(asks, LinkedListModifier.Order.ASCENDING, fromTop);
		insert(new PriceLevel(price, volume), modifier);
	}

	@Override
	public boolean isEmpty() {
		return bids.isEmpty() && asks.isEmpty();
	}

	@Override
	public boolean isConsistent() {
		return bids.get(0).getPrice() <= asks.get(0).getPrice();
	}

	@Override
	public long bidVolumeAtPrice(long priceLimit) {
		long result = 0;
		
		for (Iterator iterator = bids.iterator(); iterator.hasNext();) {
			PriceLevel currentElem = (PriceLevel) iterator.next();
			if (currentElem.getPrice() >= priceLimit) {
				result += currentElem.getPrice();
			}			
		}
		
		return result;
	}

	@Override
	public long askVolumeAtPrice(long priceLimit) {
		long result = 0;
		
		for (Iterator iterator = asks.iterator(); iterator.hasNext();) {
			PriceLevel currentElem = (PriceLevel) iterator.next();
			if (currentElem.getPrice() <= priceLimit) {
				result += currentElem.getPrice();
			}
			
		}
		
		return result;
	}

	/**
	 * Serializes the order book as a String in JSON format.
	 * <p>
	 * The JSON format is as follows:
	 * {"timestamp":&lt;timestamp&gt;,"bids":"[&lt;bids array&gt;]","asks":"[
	 * &lt;asks array&gt;]}"
	 *
	 * @param maxLevels The maximum number of price levels to return. If &lt= 0
	 *                  returns all price levels.
	 * @return String
	 */	
	@Override
	public String toJSONObject(int maxLevels) {
		return null;
	}

}
