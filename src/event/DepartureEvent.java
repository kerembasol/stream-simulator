/**
 * 
 */
package event;

import simulator.StreamNetwork;
import entity.Node;
import exception.RetrievalOfNonExistingNode;

/**
 * @author kerem
 * 
 */
public class DepartureEvent extends Event {

	/**
	 * @param time
	 * @param id
	 */
	public DepartureEvent(Integer time, Integer id) {
		super(time, id);
	}

	@Override
	public void execute(StreamNetwork network)
			throws RetrievalOfNonExistingNode {

		System.out.println("Executing departure event (start : " + startTime
				+ ", NodeId:" + nodeId + ")");

		Node node = network.getNodeById(nodeId);
		if (node == null)
			throw new RetrievalOfNonExistingNode(
					"Trying to depart non-existing node");

		node.detachNodeFromNetwork(network);
	}

	@Override
	public boolean isCompleted(int currentTime) {
		return startTime == currentTime;
	}

}
