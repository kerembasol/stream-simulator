package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import simulator.StreamNetwork;
import util.Distribution;
import exception.AdditionOfAlreadyExistingNodeException;
import exception.InconsistentPacketAdditionToSetByTime;
import exception.NotEnoughAvailableTrackerStreamException;
import exception.RetrievalOfNonExistingNodeException;

public class Tracker {

	private Integer availableStreamRate;

	private SortedMap<Integer, WatchingNode> victimNodelessWatchingNodes;
	private SortedMap<Integer, WatchingNode> watchingNodesWithAvailableUploadRate;

	private static final int AVAILABLE_STREAM_RATE = 150;

	public Tracker() {
		availableStreamRate = Integer.valueOf(Distribution
				.uniform(AVAILABLE_STREAM_RATE));
		victimNodelessWatchingNodes = new TreeMap<Integer, WatchingNode>();
		watchingNodesWithAvailableUploadRate = new TreeMap<Integer, WatchingNode>();
	}

	public boolean hasAvailableBandwith(Integer playRate) {
		return playRate.intValue() <= availableStreamRate.intValue();
	}

	// public List<WatchingNode> getAvailableSetOfWatchingNodesForVictim(
	// Integer playStartTime, Integer playRate, Integer watchDuration) {
	//
	// SortedMap<Integer, WatchingNode> tmpWns = new TreeMap<Integer,
	// WatchingNode>();
	// tmpWns.putAll(network.getWatchingNodes());
	// List<WatchingNode> resultList = new ArrayList<WatchingNode>();
	// for (Iterator<Integer> iterator = tmpWns.keySet().iterator(); iterator
	// .hasNext();) {
	// Integer wnId = iterator.next();
	// WatchingNode wn = tmpWns.get(wnId);
	// if (wn.getPlayStartTime().intValue() <= playStartTime.intValue()) {
	// resultList.add(tmpWns.get(wnId));
	// }
	// }
	//
	// if (getTotalUploadRate(resultList).intValue() < playRate.intValue()) {
	// return null;
	// } else {
	// return resultList;
	// }
	//
	// }

	private HashMap<Integer, WatchingNode> addOtherWNsMatchingStreamCriteria(
			List<WatchingNode> list,
			HashMap<Integer, WatchingNode> streamMembers,
			Integer searchedPiece, Integer timeLimit, Integer factor) {

		for (WatchingNode wn : list) {
			if (!streamMembers.containsKey(wn.getNodeId())) {

				Integer minTime = wn
						.getClosestPacketTimeForGivenPlaybackTime(searchedPiece);

				if (minTime <= timeLimit) {
					Integer minPacket = wn
							.getClosestPacketNumberForGivenPlaybackTime(searchedPiece);
					minPacket += wn.getLocalFileBuffer().getWritingFactor();

					if (minPacket == searchedPiece + streamMembers.size()) {
						streamMembers.put(wn.getNodeId(), wn);
						if (streamMembers.size() == factor)
							break;
					}
				}
			}
		}

		if (streamMembers.size() <= factor)
			return null;
		return streamMembers;
	}

	private List<WatchingNode> getOtherCandidateWNsByFactor(
			List<WatchingNode> list, Integer factor, Integer id) {
		List<WatchingNode> returnList = new ArrayList<WatchingNode>();
		for (WatchingNode wn : list)
			if (wn.getLocalFileBuffer().getWritingFactor() == factor
					&& wn.getNodeId() != id)
				returnList.add(wn);
		return returnList;
	}

	private Integer getMinAvailableTimeForPieceByGivenTime(WatchingNode wn,
			Integer joinTime) {

		Integer minPacket = wn
				.getClosestPacketNumberForGivenPlaybackTime(joinTime);
		if (minPacket != joinTime)
			return Integer.MAX_VALUE;

		return wn.getClosestPacketTimeForGivenPlaybackTime(joinTime);
	}

	private List<WatchingNode> getWatchingNodesWithAvailableUploadRatesByRate(
			Integer playRate) {

		SortedMap<Integer, WatchingNode> all = StreamNetwork.getInstance()
				.getWatchingNodes();
		Iterator<Integer> iterator = all.keySet().iterator();

		List<WatchingNode> returnList = new ArrayList<WatchingNode>();
		while (iterator.hasNext()) {
			WatchingNode wn = all.get(iterator.next());
			if (wn.hasDirectVictimNode()
					&& wn.getAvailableUploadRate() >= playRate)
				returnList.add(wn);
		}

		return returnList;
	}

	public List<WatchingNode> getAvailableSetOfWatchingNodesForVictim(
			Integer playStartTime, Integer playRate) {

		// get first Watching Node with an available piece
		Integer minTime = Integer.MAX_VALUE;
		List<WatchingNode> candidateWNs = getWatchingNodesWithAvailableUploadRatesByRate(playRate);
		WatchingNode firstAvailableWN = null;
		for (int i = 0; i < candidateWNs.size(); i++) {
			WatchingNode wn = candidateWNs.get(i);
			Integer tempMin = getMinAvailableTimeForPieceByGivenTime(wn,
					playStartTime);
			if (tempMin < minTime) {
				minTime = tempMin;
				firstAvailableWN = wn;
			}
		}

		if (firstAvailableWN == null) {
			System.out
					.println("No available set of watching nodes with a valid stream for the victim joining at "
							+ playStartTime);
			return null;
		}

		HashMap<Integer, WatchingNode> streamMembers = new LinkedHashMap<Integer, WatchingNode>();
		streamMembers.put(firstAvailableWN.getNodeId(), firstAvailableWN);

		List<WatchingNode> sameFactorWNs = getOtherCandidateWNsByFactor(
				candidateWNs, firstAvailableWN.getLocalFileBuffer()
						.getWritingFactor(), firstAvailableWN.getNodeId());

		streamMembers = addOtherWNsMatchingStreamCriteria(sameFactorWNs,
				streamMembers, playStartTime, minTime, firstAvailableWN
						.getLocalFileBuffer().getWritingFactor());

		if (streamMembers == null)
			System.out.println("No available stream");
		else
			System.out.println(streamMembers.keySet());

		return null;
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
			throw new RetrievalOfNonExistingNodeException(
					"Trying to remove node " + id
							+ " from victim nodeless watching node list");

		victimNodelessWatchingNodes.remove(id);
	}

	public boolean hasGivenWatchingNodeInNodeWithAvailableUploadRateList(
			Integer wnId) {
		return watchingNodesWithAvailableUploadRate.containsKey(wnId);
	}

	public void addWatchingNodeWithAvailableUploadRate(WatchingNode wn)
			throws AdditionOfAlreadyExistingNodeException {
		if (watchingNodesWithAvailableUploadRate.containsKey(wn.getNodeId()))
			throw new AdditionOfAlreadyExistingNodeException(
					"Trying to add node "
							+ wn.getNodeId()
							+ " to watching nodes with available upload rate list");

		watchingNodesWithAvailableUploadRate.put(wn.getNodeId(), wn);
	}

	public void removeWatchingNodeWithAvailableUploadRate(WatchingNode wn)
			throws RetrievalOfNonExistingNodeException {
		if (!watchingNodesWithAvailableUploadRate.containsKey(wn.getNodeId()))
			throw new RetrievalOfNonExistingNodeException(
					"Trying to remove node " + wn.getNodeId()
							+ " from nodes with available upload rate list");
		watchingNodesWithAvailableUploadRate.remove(wn.getNodeId());
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

	public PacketSet createPacketSet(Integer time, Integer playRate)
			throws InconsistentPacketAdditionToSetByTime {
		PacketSet set = new PacketSet(time, playRate);
		set.fillPacketSet(time, playRate);
		return set;
	}

}