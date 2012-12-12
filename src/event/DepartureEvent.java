/**
 * 
 */
package event;

import simulator.StreamNetwork;
import entity.Node;
import exception.RetrievalOfNonExistingNodeException;

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
			throws RetrievalOfNonExistingNodeException {

		System.out.println("Executing departure event (start : " + startTime
				+ ", NodeId:" + nodeId + ")");

		Node node = network.getNodeById(nodeId);
		if (node == null)
			throw new RetrievalOfNonExistingNodeException(
					"Trying to depart non-existing node");

		node.detachNodeFromNetwork(network);
	}

	@Override
	public boolean isCompleted(int currentTime) {
		return startTime == currentTime;
	}

}
