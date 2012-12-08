package entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import simulator.StreamNetwork;
import exception.AdditionOfAlreadyExistingNodeException;
import exception.RetrievalOfNonExistingNode;

// Referenced classes of package entity:
//            Node, WatchingNode

public class VictimNode extends Node {

	private final SortedMap watchingNodes = new TreeMap();

	public VictimNode(Integer nodeId, Integer playStartTime, Integer playRate,
			Integer watchDuration) {
		super(nodeId, playStartTime, playRate, watchDuration);
	}

	public void associateWatchingNode(Integer nodeId, WatchingNode wn)
			throws AdditionOfAlreadyExistingNodeException {
		if (watchingNodes.containsKey(nodeId)) {
			throw new AdditionOfAlreadyExistingNodeException(
					"Associating already associated watching node");
		} else {
			watchingNodes.put(nodeId, wn);
			return;
		}
	}

	@Override
	public void detachNodeFromNetwork(StreamNetwork network)
			throws RetrievalOfNonExistingNode {
		List wns = getMapAsList();
		while (getPlayAmount().intValue() > 0) {
			Iterator iterator = wns.iterator();
			while (iterator.hasNext()) {
				WatchingNode wn = (WatchingNode) iterator.next();
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
		for (Iterator iterator1 = wns.iterator(); iterator1.hasNext(); wn
				.removeVictimNode(nodeId)) {
			wn = (WatchingNode) iterator1.next();
		}

		network.getVictimNodes().remove(nodeId);
	}

	private List getMapAsList() {
		List returnList = new ArrayList();
		for (Iterator iterator = watchingNodes.keySet().iterator(); iterator
				.hasNext(); returnList.add(watchingNodes.get(iterator.next()))) {
		}
		return returnList;
	}
}