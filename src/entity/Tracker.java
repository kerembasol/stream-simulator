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

	public List getAvailableSetOfWatchingNodesForVictim(Integer playStartTime,
			Integer playRate, Integer watchDuration, StreamNetwork network) {
		SortedMap tmpWns = new TreeMap();
		tmpWns.putAll(network.getWatchingNodes());
		List resultList = new ArrayList();
		for (Iterator iterator = tmpWns.keySet().iterator(); iterator.hasNext();) {
			Integer wnId = (Integer) iterator.next();
			WatchingNode wn = (WatchingNode) tmpWns.get(wnId);
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

	private Integer getTotalUploadRate(List wns) {
		Integer rate = new Integer(0);
		for (Iterator iterator = wns.iterator(); iterator.hasNext();) {
			WatchingNode wn = (WatchingNode) iterator.next();
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