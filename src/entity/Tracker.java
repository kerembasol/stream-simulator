package entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import util.Distribution;
import exception.AdditionOfAlreadyExistingNodeException;
import exception.NotEnoughAvailableTrackerStreamException;
import exception.RetrievalOfNonExistingNodeException;

// Referenced classes of package entity:
//            WatchingNode

public class Tracker {

	private Integer availableStreamRate;

	private SortedMap<Integer, WatchingNode> victimNodelessWatchingNodes;

	private static final int AVAILABLE_STREAM_RATE = 150;

	public Tracker() {
		availableStreamRate = Integer.valueOf(Distribution.uniform(150));
		victimNodelessWatchingNodes = new TreeMap<Integer, WatchingNode>();
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

	public List<WatchingNode> getCandidateWatchingNodesForBufferUpload() {
	}

	public void addVictimNodelessWatchingNode(WatchingNode wn)
			throws AdditionOfAlreadyExistingNodeException {
		if (victimNodelessWatchingNodes.containsKey(wn.getNodeId()))
			throw new AdditionOfAlreadyExistingNodeException(
					"Trying to add node " + wn.getNodeId()
							+ " to victim nodeless watching node list");
		victimNodelessWatchingNodes.put(wn.getNodeId(), wn);
	}

	public void removeVictimNodelessWatchingNode(Integer id)
			throws RetrievalOfNonExistingNodeException {
		if (!victimNodelessWatchingNodes.containsKey(id))
			throw new RetrievalOfNonExistingNodeException("Trying to add node "
					+ id + " from victim nodeless watching node list");

		victimNodelessWatchingNodes.remove(id);
	}

	/**
	 * This function returns a watching node that has no victim nodes attached
	 * and available for direct upload for the victim node.
	 * 
	 * @param playStartTime
	 * @param playRate
	 * @return
	 */

	public WatchingNode getImmeadiateAvailableWatchingNode(
			Integer playStartTime, Integer playRate) {

		Iterator<Integer> iterator = victimNodelessWatchingNodes.keySet()
				.iterator();
		if (iterator != null && iterator.hasNext())
			return victimNodelessWatchingNodes.get(iterator.next());

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