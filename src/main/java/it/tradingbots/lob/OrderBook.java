package it.tradingbots.lob;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OrderBook extends AggregatedOrderBook {
	private static List<PriceLevel> asks;
	private static List<PriceLevel> bids;
	
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
			return bids.subList(0, maxLevels-1);
		}
	}

	@Override
	public List<PriceLevel> getAsks(int maxLevels) {
		if (maxLevels <= 0) {
			return asks;
		} else {
			return asks.subList(0, maxLevels-1);
		}
	}

	@Override
	public void clear() {
		asks.clear();
		bids.clear();
	}

	@Override
	public void bid(long price, long volume, boolean fromTop) {
		/**
		 * Idea: two cases, beginning from the top or from the bottom of the list
		 * - case 1: from the top:
		 * 		- 1.1 if the price is in the list:
		 * 			- 1.1.1. if volume > 0, then I have to update it
		 * 			- 1.1.2 if volume <= 0, delete it
		 *		- 1.2 if the price is not in the list:
		 *			- 1.2.1 if volume > 0 then create it
		 * - case 2: from the bottom:
		 * 		- 2.1 if the price is in the list:
		 * 			- 2.1.1 if volume > 0, then I have to update it
		 * 			- 2.1.2 if volume <= 0, delete it
		 *		- 2.2 if the price is not in the list:
		 *			- 2.2.1 if volume > 0 then create it
		 **/		
		/**if (fromTop) {
			for (Iterator iterator = bids.iterator(); iterator.hasNext();) {
				PriceLevel currentElem = (PriceLevel) iterator.next();
				if (currentElem.getPrice() == price) {
					if (volume > 0 && volume != currentElem.getVolume()) {
						int index = bids.indexOf(currentElem);
						PriceLevel pl = new PriceLevel(price, volume);
						bids.remove(currentElem);
						bids.add(index, pl);
					}
					else if (volume < 0){
						bids.remove(currentElem);						
					}
				}
			}
		}**/
		
		LinkedList<Long> bidsPrices = new LinkedList<Long>();
		for (Iterator iterator = bids.iterator(); iterator.hasNext();) {
			PriceLevel currentElem = (PriceLevel) iterator.next();	
			bidsPrices.add(currentElem.getPrice());
		}
		
		if (fromTop) {
			PriceLevel pl = new PriceLevel(price, volume);
			if (bidsPrices.indexOf(price)>=0) {
				int index = bidsPrices.indexOf(price);
				if (volume > 0 && bids.get(index).getVolume() != volume) {
					bids.remove(index);
					bids.add(index, pl);
				}
				else if (volume < 0) {
					bids.remove(index);
				}
			}
			else if (volume > 0){
				for (Iterator iterator = bidsPrices.iterator(); iterator.hasNext();) {
					Long currentElem = (Long) iterator.next();
					if (price > currentElem) {
						bids.add(bidsPrices.indexOf(currentElem), pl);						
						break;
					}
				}
			}
		}
		else {
			PriceLevel pl = new PriceLevel(price, volume);
			if (bidsPrices.lastIndexOf(price)>=0) {
				int index = bidsPrices.lastIndexOf(price);
				if (volume > 0 && bids.get(index).getVolume() != volume) {
					bids.remove(index);
					bids.add(index, pl);
				}
				else if (volume < 0) {
					bids.remove(index);
				}
			}
			else if (volume > 0) {
				for (Iterator iterator = bidsPrices.descendingIterator(); iterator.hasNext();) {
					Long currentElem = (Long) iterator.next();
					if (price > currentElem) {
						bids.add(bidsPrices.lastIndexOf(currentElem), pl);
						break;
					}
				}
			}
		}
	}
	

	@Override
	public void ask(long price, long volume, boolean fromTop) {
		LinkedList<Long> askPrices = new LinkedList<Long>();
		for (Iterator iterator = asks.iterator(); iterator.hasNext();) {
			PriceLevel currentElem = (PriceLevel) iterator.next();	
			askPrices.add(currentElem.getPrice());
		}
		
		if (fromTop) {
			PriceLevel pl = new PriceLevel(price, volume);
			if (askPrices.indexOf(price)>=0) {
				int index = askPrices.indexOf(price);
				if (volume > 0 && asks.get(index).getVolume() != volume) {
					asks.remove(index);
					asks.add(index, pl);
				}
				else if (volume < 0) {
					asks.remove(index);
				}
			}
			else if (volume > 0){
				for (Iterator iterator = askPrices.iterator(); iterator.hasNext();) {
					Long currentElem = (Long) iterator.next();
					if (price < currentElem) {
						asks.add(askPrices.indexOf(currentElem), pl);						
						break;
					}
				}
			}
		}
		else {
			PriceLevel pl = new PriceLevel(price, volume);
			if (askPrices.lastIndexOf(price)>=0) {
				int index = askPrices.lastIndexOf(price);
				if (volume > 0 && asks.get(index).getVolume() != volume) {
					asks.remove(index);
					asks.add(index, pl);
				}
				else if (volume < 0) {
					asks.remove(index);
				}
			}
			else if (volume > 0) {
				for (Iterator iterator = askPrices.descendingIterator(); iterator.hasNext();) {
					Long currentElem = (Long) iterator.next();
					if (price < currentElem) {
						asks.add(askPrices.lastIndexOf(currentElem), pl);
						break;
					}
				}
			}
		}
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