/**
 * 
 */
package entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import simulator.StreamNetwork;
import exception.AdditionOfAlreadyExistingNodeException;
import exception.RetrievalOfNonExistingNode;

/**
 * @author kerem
 * 
 */
public class VictimNode extends Node {

	private final SortedMap<Integer, WatchingNode> watchingNodes;

	public VictimNode(Integer nodeId, Integer playStartTime, Integer playRate,
			Integer watchDuration) {
		super(nodeId, playStartTime, playRate, watchDuration);
		this.watchingNodes = new TreeMap<Integer, WatchingNode>();
	}

	public void associateWatchingNode(Integer nodeId, WatchingNode wn)
			throws AdditionOfAlreadyExistingNodeException {

		if (watchingNodes.containsKey(nodeId))
			throw new AdditionOfAlreadyExistingNodeException(
					"Associating already associated watching node");

		watchingNodes.put(nodeId, wn);
	}

	@Override
	public void detachNodeFromNetwork(StreamNetwork network)
			throws RetrievalOfNonExistingNode {

		List<WatchingNode> wns = getMapAsList();
		while (this.getPlayAmount() > 0) {

			for (WatchingNode wn : wns) {
				if (wn.getAvailableUploadAmount() < wn.getUploadRate()) {
					wn.setAvailableUploadAmount(wn.getAvailableUploadAmount() + 1);
					this.setPlayAmount(this.getPlayAmount() - 1);
					if (this.getPlayAmount() == 0)
						break;
				}
			}
		}

		for (WatchingNode wn : wns)
			wn.removeVictimNode(this.nodeId);
		network.getVictimNodes().remove(this.nodeId);
	}

	private List<WatchingNode> getMapAsList() {
		List<WatchingNode> returnList = new ArrayList<WatchingNode>();

		Iterator<Integer> iterator = watchingNodes.keySet().iterator();
		while (iterator.hasNext()) {
			returnList.add(watchingNodes.get(iterator.next()));
		}

		return returnList;
	}
}
