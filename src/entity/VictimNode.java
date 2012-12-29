package entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import exception.AdditionOfAlreadyExistingNodeException;
import exception.RetrievalOfNonExistingNodeException;

// Referenced classes of package entity:
//            Node, WatchingNode

public class VictimNode extends Node {

	private final SortedMap<Integer, WatchingNode> watchingNodes = new TreeMap<Integer, WatchingNode>();

	public VictimNode(Integer nodeId, Integer playStartTime, Integer playRate,
			Integer watchDuration) {
		super(nodeId, playStartTime, playRate, watchDuration);
	}

	public void associateWatchingNode(WatchingNode wn)
			throws AdditionOfAlreadyExistingNodeException {
		if (watchingNodes.containsKey(wn.getNodeId())) {
			throw new AdditionOfAlreadyExistingNodeException(
					"Associating already associated watching node");
		} else {
			watchingNodes.put(wn.getNodeId(), wn);
			return;
		}
	}

	@Override
	public void detachNodeFromNetwork()
			throws RetrievalOfNonExistingNodeException,
			AdditionOfAlreadyExistingNodeException {
		// List<WatchingNode> wns = getMapAsList();
		// for (WatchingNode wn : wns) {
		// if (wn.hasDirectVictimNode()
		// && this.nodeId == wn.getDirectVictimNodeId()) {
		// StreamNetwork.getInstance().getTracker()
		// .addVictimNodelessWatchingNode(wn);
		//
		// StreamNetwork.getInstance().getTracker().addWatchingNodeWithAvailableUploadRate(wn);
		// StreamNetwork.getInstance().
		// }
		// }
		//
		// network.getVictimNodes().remove(nodeId);
	}

	private List<WatchingNode> getMapAsList() {
		List<WatchingNode> returnList = new ArrayList<WatchingNode>();
		for (Iterator<Integer> iterator = watchingNodes.keySet().iterator(); iterator
				.hasNext(); returnList.add(watchingNodes.get(iterator.next()))) {
		}
		return returnList;
	}
}