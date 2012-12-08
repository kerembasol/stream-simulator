/**
 * 
 */
package simulator;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import util.Distribution;
import entity.Node;
import entity.Tracker;
import entity.VictimNode;
import entity.WatchingNode;
import exception.AdditionOfAlreadyExistingNodeException;
import exception.NotEnoughAvailableTrackerStreamException;
import exception.RetrievalOfNonExistingNode;

/**
 * @author kerem
 * 
 */
public class StreamNetwork {

	private Tracker tracker;
	private SortedMap<Integer, WatchingNode> watchingNodes;
	private SortedMap<Integer, VictimNode> victimNodes;

	private static StreamNetwork instance = null;

	private static final int WATCH_DURATION_PARAM = 50;
	private static final int PLAY_RATE_PARAM = 20;

	private StreamNetwork() {
		tracker = new Tracker();
		watchingNodes = new TreeMap<Integer, WatchingNode>();
		victimNodes = new TreeMap<Integer, VictimNode>();
	}

	public static StreamNetwork getInstance() {
		if (instance == null) {
			synchronized (StreamNetwork.class) {
				if (instance == null) {
					instance = new StreamNetwork();
				}
			}
		}
		return instance;
	}

	public Node handleNewNode(Integer nodeId, Integer startTime)
			throws AdditionOfAlreadyExistingNodeException,
			RetrievalOfNonExistingNode,
			NotEnoughAvailableTrackerStreamException {

		if (getNodeById(nodeId) != null)
			throw new AdditionOfAlreadyExistingNodeException(
					"Trying to add already existing node (ID:" + nodeId
							+ ") to the network");

		Integer watchDuration = getWatchDuration();
		Integer playRate = getPlayRate();

		Node node = null;
		System.out.println((new StringBuilder("\tNode ")).append(nodeId)
				.append(" requesting a playback rate of ").append(playRate)
				.toString());
		if (tracker.hasAvailableBandwith(playRate)) {
			node = new WatchingNode(nodeId, startTime, playRate, watchDuration);
			insertWatchingNode(nodeId, (WatchingNode) node);
		} else {
			List<WatchingNode> wns = tracker
					.getAvailableSetOfWatchingNodesForVictim(startTime,
							playRate, watchDuration, this);
			if (wns != null) {
				node = new VictimNode(nodeId, startTime, playRate,
						watchDuration);
				insertVictimNode(nodeId, (VictimNode) node, wns);
			}
		}

		return node;
	}

	public Node getNodeById(Integer nodeId) {
		if (watchingNodes.containsKey(nodeId))
			return watchingNodes.get(nodeId);

		if (victimNodes.containsKey(nodeId))
			return victimNodes.get(nodeId);

		return null;
	}

	private Integer getWatchDuration() {
		return Distribution.uniform(WATCH_DURATION_PARAM);
	}

	private Integer getPlayRate() {
		return Distribution.uniform(PLAY_RATE_PARAM);
	}

	public void insertWatchingNode(Integer nodeId, WatchingNode node)
			throws AdditionOfAlreadyExistingNodeException,
			NotEnoughAvailableTrackerStreamException {
		if (watchingNodes.containsKey(nodeId))
			throw new AdditionOfAlreadyExistingNodeException(
					"Adding already existing watching node");
		watchingNodes.put(nodeId, node);
		tracker.decreaseAvailableStreamRateByAmount(node.getPlayRate());
		System.out.println((new StringBuilder("\tNode ")).append(nodeId)
				.append(" joined network as a Watching node. Playback rate:")
				.append(node.getPlayRate()).append(". Available upload rate: ")
				.append(node.getUploadRate())
				.append(" .Available network rate remaining: ")
				.append(tracker.getAvailableStreamRate()).toString());
	}

	public void insertVictimNode(Integer nodeId, VictimNode vn,
			List<WatchingNode> wns)
			throws AdditionOfAlreadyExistingNodeException,
			RetrievalOfNonExistingNode {

		if (victimNodes.containsKey(nodeId))
			throw new AdditionOfAlreadyExistingNodeException(
					"Adding already existing victim node");
		for (WatchingNode wn : wns)
			if (!watchingNodes.containsKey(wn.getNodeId()))
				throw new RetrievalOfNonExistingNode(
						"Trying to retrieve non-existing watching node");

		victimNodes.put(nodeId, vn);
		Set<WatchingNode> distributingWns = distributedPlayRateRoundRobin(vn,
				wns);

		System.out.print((new StringBuilder("\tNode ")).append(nodeId)
				.append(" joined network as a Victim Node. Playback rate : ")
				.append(vn.getPlayRate())
				.append(". Stream received from following Watching Nodes : ")
				.toString());
		System.out.print("< ");
		for (WatchingNode wn : distributingWns) {
			wn.associateVictimNode(nodeId, vn);
			vn.associateWatchingNode(wn.getNodeId(), wn);
			System.out.print((new StringBuilder("-")).append(wn.getNodeId())
					.toString());
		}
		System.out.println("- >");

	}

	private Set<WatchingNode> distributedPlayRateRoundRobin(VictimNode vn,
			List<WatchingNode> candidateWns) {
		Integer playAmount = 0;

		Set<WatchingNode> distributingWns = new TreeSet<WatchingNode>();
		while (playAmount < vn.getPlayRate()) {
			for (WatchingNode wn : candidateWns) {
				if (wn.getAvailableUploadAmount() > 0) {
					wn.setAvailableUploadAmount(wn.getAvailableUploadAmount() - 1);
					vn.setPlayAmount(vn.getPlayAmount() + 1);
					distributingWns.add(wn);
					if (vn.getPlayAmount() == vn.getPlayRate())
						break;
				}
			}
		}
		return distributingWns;
	}

	/**
	 * @return the watchingNodes
	 */
	public SortedMap<Integer, WatchingNode> getWatchingNodes() {
		return watchingNodes;
	}

	/**
	 * @return the victimNodes
	 */
	public SortedMap<Integer, VictimNode> getVictimNodes() {
		return victimNodes;
	}

	public Tracker getTracker() {
		return tracker;
	}

}
