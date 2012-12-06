/**
 * 
 */
package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import simulator.StreamNetwork;
import util.Distribution;
import exception.NotEnoughAvailableTrackerStreamException;

/**
 * @author kerem
 * 
 */
public class Tracker {

	private Integer availableStreamRate;

	private static final int AVAILABLE_STREAM_RATE = 150;

	public Tracker() {
		this.availableStreamRate = Distribution.uniform(AVAILABLE_STREAM_RATE);
	}

	public boolean hasAvailableBandwith(Integer playRate) {
		return playRate <= availableStreamRate;
	}

	public List<WatchingNode> getAvailableSetOfWatchingNodesForVictim(
			Integer playStartTime, Integer playRate, Integer watchDuration,
			StreamNetwork network) {

		SortedMap<Integer, WatchingNode> tmpWns = new TreeMap<Integer, WatchingNode>();
		tmpWns.putAll(network.getWatchingNodes());

		List<WatchingNode> resultList = new ArrayList<WatchingNode>();
		for (Integer wnId : tmpWns.keySet()) {
			WatchingNode wn = tmpWns.get(wnId);
			if (wn.getPlayStartTime() <= playStartTime)
				resultList.add(tmpWns.get(wnId));
		}

		if (getTotalUploadRate(resultList) < playRate)
			return null;

		return resultList;

	}

	private Integer getTotalUploadRate(List<WatchingNode> wns) {
		Integer rate = new Integer(0);
		for (WatchingNode wn : wns)
			rate += wn.getUploadRate();

		return rate;
	}

	public void decreaseAvailableStreamRateByAmount(Integer rate)
			throws NotEnoughAvailableTrackerStreamException {
		if (rate > availableStreamRate)
			throw new NotEnoughAvailableTrackerStreamException(
					"Available stream rate of tracker is being decreased by an amount exceeding the rate itself ");
		availableStreamRate -= rate;
	}

	public void increaseAvailableStreamRateByAmount(Integer rate) {
		availableStreamRate += rate;
	}

}
