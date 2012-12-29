/**
 * 
 */
package simulator;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import util.Distribution;
import entity.Node;
import entity.Tracker;
import entity.VictimNode;
import entity.WatchingNode;
import exception.AdditionOfAlreadyExistingNodeException;
import exception.AdditionOfNewSetWithLargerSetSizeException;
import exception.AdditionOfOutdatedPacketSetException;
import exception.BufferOverflowException;
import exception.IllegalValueException;
import exception.InconsistentPacketAdditionToSetByTime;
import exception.NotEnoughAvailableTrackerStreamException;
import exception.RetrievalOfNonExistingNodeException;

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
	private static final int PLAY_RATE_PARAM = 5;

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
			RetrievalOfNonExistingNodeException,
			NotEnoughAvailableTrackerStreamException, BufferOverflowException,
			AdditionOfOutdatedPacketSetException,
			InconsistentPacketAdditionToSetByTime,
			AdditionOfNewSetWithLargerSetSizeException, IllegalValueException {

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

		// Decide whether the incoming node is going to be a WatchingNode or a
		// VictimNode
		if (tracker.hasAvailableBandwith(playRate)) {
			// For WatchingNode, directly start streaming
			node = new WatchingNode(nodeId, startTime, playRate, watchDuration);
			insertWatchingNode(nodeId, (WatchingNode) node);

		} else {

			// If there is an immediately available WatchingNode, attach to it
			WatchingNode immWn = tracker.getImmeadiateAvailableWatchingNode(
					startTime, playRate);
			if (immWn != null) {
				node = new VictimNode(nodeId, startTime, playRate,
						watchDuration);
				insertVictimNodeBySingleWatchingNode((VictimNode) node, immWn);
				tracker.removeVictimNodelessWatchingNode(node.getNodeId());
			} else {
				// For VictimNode, determine a set of available WatchingNodes
				List<WatchingNode> wns = tracker
						.getAvailableSetOfWatchingNodesForVictim(startTime,
								playRate);
				if (wns != null) {
					node = new VictimNode(nodeId, startTime, playRate,
							watchDuration);
					insertVictimNodeByWatchingNodeSet((VictimNode) node, wns);
				} else
					System.out
							.println("No available set of watching nodes with a valid stream for the victim "
									+ nodeId + " joining at " + startTime);
			}
		}

		return node;
	}

	public void insertWatchingNode(Integer nodeId, WatchingNode node)
			throws AdditionOfAlreadyExistingNodeException,
			NotEnoughAvailableTrackerStreamException, BufferOverflowException,
			AdditionOfOutdatedPacketSetException,
			InconsistentPacketAdditionToSetByTime,
			AdditionOfNewSetWithLargerSetSizeException {

		if (watchingNodes.containsKey(nodeId))
			throw new AdditionOfAlreadyExistingNodeException(
					"Adding already existing watching node");

		node.addPacketSetToPlaybackBuffer(tracker.createPacketSet(
				Simulator.CURRENT_TIME, node.getPlayRate()));

		addWatchingNode(node);
		tracker.decreaseAvailableStreamRateByAmount(node.getPlayRate());
		tracker.addVictimNodelessWatchingNode(node);
		tracker.addWatchingNodeWithAvailableUploadRate(node);

		System.out.println((new StringBuilder("\tNode ")).append(nodeId)
				.append(" joined network as a Watching node. Playback rate:")
				.append(node.getPlayRate()).append(". Available upload rate: ")
				.append(node.getUploadRate())
				.append(" .Available network rate remaining: ")
				.append(tracker.getAvailableStreamRate()).toString());
	}

	public void insertVictimNodeByWatchingNodeSet(VictimNode vn,
			List<WatchingNode> wns)
			throws AdditionOfAlreadyExistingNodeException,
			RetrievalOfNonExistingNodeException, IllegalValueException {

		if (victimNodes.containsKey(vn.getNodeId()))
			throw new AdditionOfAlreadyExistingNodeException(
					"Adding already existing victim node");

		for (WatchingNode wn : wns)
			if (!watchingNodes.containsKey(wn.getNodeId()))
				throw new RetrievalOfNonExistingNodeException(
						"Trying to retrieve non-existing watching node");

		victimNodes.put(vn.getNodeId(), vn);
		// Set<WatchingNode> distributingWns = distributedPlayRateRoundRobin(vn,
		// wns);

		System.out.print((new StringBuilder("\tNode ")).append(vn.getNodeId())
				.append(" joined network as a Victim Node. Playback rate : ")
				.append(vn.getPlayRate())
				.append(". Stream received from following Watching Nodes : ")
				.toString());
		System.out.print("< ");
		for (WatchingNode wn : wns) {
			wn.associateVictimNode(vn);
			vn.associateWatchingNode(wn);
			System.out.print((new StringBuilder("-")).append(wn.getNodeId())
					.toString());
			if (wn.getAvailableUploadRate() < 0)
				throw new IllegalValueException("Upload value :"
						+ wn.getAvailableUploadRate()
						+ " cannot be negative for watching node :"
						+ wn.getNodeId());
			if (wn.getAvailableUploadRate() == 0)
				tracker.removeWatchingNodeWithAvailableUploadRate(wn);
		}
		System.out.println("- >");

	}

	public void insertVictimNodeBySingleWatchingNode(VictimNode vn,
			WatchingNode wn) throws AdditionOfAlreadyExistingNodeException,
			RetrievalOfNonExistingNodeException, IllegalValueException {
		if (victimNodes.containsKey(vn.getNodeId()))
			throw new AdditionOfAlreadyExistingNodeException(
					"Adding already existing victim node");

		if (!watchingNodes.containsKey(wn.getNodeId()))
			throw new RetrievalOfNonExistingNodeException(
					"Trying to retrieve non-existing watching node");

		victimNodes.put(vn.getNodeId(), vn);
		wn.associateVictimNode(vn);
		vn.associateWatchingNode(wn);
		tracker.removeVictimNodelessWatchingNode(wn.getNodeId());
		if (wn.getAvailableUploadRate() < 0)
			throw new IllegalValueException(
					"Available upload rate of watching node " + wn.getNodeId()
							+ " is " + wn.getAvailableUploadRate());

		if (wn.getAvailableUploadRate() == 0)
			tracker.removeWatchingNodeWithAvailableUploadRate(wn);

		System.out.print((new StringBuilder("\tNode "))
				.append(vn.getNodeId())
				.append(" joined network as a Victim Node. Playback rate : ")
				.append(vn.getPlayRate())
				.append(". Stream received from Watching Node : "
						+ wn.getNodeId()).toString());

	}

	// private Set<WatchingNode> distributedPlayRateRoundRobin(VictimNode vn,
	// List<WatchingNode> candidateWns) {
	// Integer playAmount = 0;
	//
	// Set<WatchingNode> distributingWns = new TreeSet<WatchingNode>();
	// while (playAmount < vn.getPlayRate()) {
	// for (WatchingNode wn : candidateWns) {
	// if (wn.getAvailableUploadAmount() > 0) {
	// wn.setAvailableUploadAmount(wn.getAvailableUploadAmount() - 1);
	// vn.setPlayAmount(vn.getPlayAmount() + 1);
	// distributingWns.add(wn);
	// if (vn.getPlayAmount() == vn.getPlayRate())
	// break;
	// }
	// }
	// }
	// return distributingWns;
	// }

	public void updateBuffers() {
		for (Integer intWn : watchingNodes.keySet()) {
			WatchingNode wn = watchingNodes.get(intWn);
			wn.updateBuffers();
		}

		for (Integer intVn : victimNodes.keySet()) {
			VictimNode vn = victimNodes.get(intVn);
			vn.updateBuffers();
		}
	}

	public Node getNodeById(Integer nodeId) {
		if (watchingNodes.containsKey(nodeId))
			return watchingNodes.get(nodeId);

		if (victimNodes.containsKey(nodeId))
			return victimNodes.get(nodeId);

		return null;
	}

	private Integer getWatchDuration() {
		return Distribution.uniform(WATCH_DURATION_PARAM) + 10;
	}

	public void addWatchingNode(WatchingNode wn)
			throws AdditionOfAlreadyExistingNodeException {
		if (watchingNodes.containsKey(wn.getNodeId()))
			throw new AdditionOfAlreadyExistingNodeException(
					"Trying to add WatchingNode : " + wn.getNodeId()
							+ " to the network");
		watchingNodes.put(wn.getNodeId(), wn);
	}

	public void removeWatchingNode(WatchingNode wn)
			throws RetrievalOfNonExistingNodeException {
		if (!watchingNodes.containsKey(wn.getNodeId()))
			throw new RetrievalOfNonExistingNodeException(
					"Trying to remove WatchingNode : " + wn.getNodeId()
							+ " from the network");

		watchingNodes.remove(wn.getNodeId());

	}

	/**
	 * As of now, only fixed playbackrate is considered. (No different channel
	 * playback such as HD vs normal)
	 * 
	 * @return
	 */
	private Integer getPlayRate() {
		return PLAY_RATE_PARAM;
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
