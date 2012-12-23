package entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import simulator.StreamNetwork;
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
	public void detachNodeFromNetwork(StreamNetwork network)
			throws RetrievalOfNonExistingNodeException {
		List<WatchingNode> wns = getMapAsList();
		while (getPlayAmount().intValue() > 0) {
			Iterator<WatchingNode> iterator = wns.iterator();
			while (iterator.hasNext()) {
				WatchingNode wn = iterator.next();
				if (wn.getAvailableUploadAmount().intValue() >= wn
						.getUploadRate().intValue()) {
					continue;
				}
				wn.setAvailableUploadAmount(Integer.valueOf(wn
						.getAvailableUploadAmount().intValue() + 1));
				setPlayAmount(Integer.valueOf(getPlayAmount().intValue() - 1));
				if (getPlayAmount().intValue() == 0) {
					break;
				}
			}
		}
		WatchingNode wn;
		for (Iterator<WatchingNode> iterator1 = wns.iterator(); iterator1
				.hasNext(); wn.removeVictimNode(nodeId)) {
			wn = iterator1.next();
		}

		network.getVictimNodes().remove(nodeId);
	}

	private List<WatchingNode> getMapAsList() {
		List<WatchingNode> returnList = new ArrayList<WatchingNode>();
		for (Iterator<Integer> iterator = watchingNodes.keySet().iterator(); iterator
				.hasNext(); returnList.add(watchingNodes.get(iterator.next()))) {
		}
		return returnList;
	}
}