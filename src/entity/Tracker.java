package entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import simulator.StreamNetwork;
import util.Distribution;
import exception.NotEnoughAvailableTrackerStreamException;

// Referenced classes of package entity:
//            WatchingNode

public class Tracker {

	private Integer availableStreamRate;
	private static final int AVAILABLE_STREAM_RATE = 150;

	public Tracker() {
		availableStreamRate = Integer.valueOf(Distribution.uniform(150));
	}

	public boolean hasAvailableBandwith(Integer playRate) {
		return playRate.intValue() <= availableStreamRate.intValue();
	}

	public List<WatchingNode> getAvailableSetOfWatchingNodesForVictim(
			Integer playStartTime, Integer playRate, Integer watchDuration) {

		SortedMap<Integer, WatchingNode> tmpWns = new TreeMap<Integer, WatchingNode>();
		tmpWns.putAll(network.getWatchingNodes());
		List<WatchingNode> resultList = new ArrayList<WatchingNode>();
		for (Iterator<Integer> iterator = tmpWns.keySet().iterator(); iterator
				.hasNext();) {
			Integer wnId = iterator.next();
			WatchingNode wn = tmpWns.get(wnId);
			if (wn.getPlayStartTime().intValue() <= playStartTime.intValue()) {
				resultList.add(tmpWns.get(wnId));
			}
		}

		if (getTotalUploadRate(resultList).intValue() < playRate.intValue()) {
			return null;
		} else {
			return resultList;
		}

	}

	/**
	 * This function returns a watching node that has no victim nodes attached
	 * and available for direct upload for the victim node.
	 * 
	 * @param playStartTime
	 * @param playRate
	 * @return
	 */
	// TODO Tracker should keep track of this instead of iterating through
	// StreamNetwork instance
	public WatchingNode getImmeadiateAvailableWatchingNode(
			Integer playStartTime, Integer playRate) {
		for (Integer i : StreamNetwork.getInstance().getWatchingNodes()
				.keySet()) {
			WatchingNode wn = StreamNetwork.getInstance().getWatchingNodes()
					.get(i);
			if (wn != null && !wn.getHasDirectVictimNode())
				return wn;
		}

		return null;
	}

	private Integer getTotalUploadRate(List<WatchingNode> wns) {
		Integer rate = new Integer(0);
		for (Iterator<WatchingNode> iterator = wns.iterator(); iterator
				.hasNext();) {
			WatchingNode wn = iterator.next();
			rate = Integer.valueOf(rate.intValue()
					+ wn.getUploadRate().intValue());
		}

		return rate;
	}

	public void decreaseAvailableStreamRateByAmount(Integer rate)
			throws NotEnoughAvailableTrackerStreamException {
		if (rate.intValue() > availableStreamRate.intValue()) {
			throw new NotEnoughAvailableTrackerStreamException(
					"Available stream rate of tracker is being decreased by an amount exceeding the r"
							+ "ate itself ");
		} else {
			availableStreamRate = Integer.valueOf(availableStreamRate
					.intValue() - rate.intValue());
			return;
		}
	}

	public void increaseAvailableStreamRateByAmount(Integer rate) {
		availableStreamRate = Integer.valueOf(availableStreamRate.intValue()
				+ rate.intValue());
	}

	public Integer getAvailableStreamRate() {
		return availableStreamRate;
	}
}