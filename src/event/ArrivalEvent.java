/**
 * 
 */
package event;

import simulator.Simulator;
import simulator.StreamNetwork;
import entity.Node;
import exception.AdditionOfAlreadyExistingNodeException;
import exception.AdditionOfOutdatedPacketSetException;
import exception.BufferOverflowException;
import exception.InconsistentPacketAdditionToSetByTime;
import exception.NotEnoughAvailableTrackerStreamException;
import exception.RetrievalOfNonExistingNodeException;

/**
 * @author kerem
 * 
 */
public class ArrivalEvent extends Event {

	public ArrivalEvent(Integer time, Integer id) {
		super(time, id);
	}

	@Override
	public void execute(StreamNetwork network)
			throws AdditionOfAlreadyExistingNodeException,
			NotEnoughAvailableTrackerStreamException,
			RetrievalOfNonExistingNodeException, BufferOverflowException,
			AdditionOfOutdatedPacketSetException,
			InconsistentPacketAdditionToSetByTime {

		System.out.println("Executing arrival event (start : " + startTime
				+ ", NodeId:" + nodeId + ")");

		Node node = network.handleNewNode(nodeId, startTime);
		if (node != null) {
			Integer newStartTime = node.getPlayStartTime()
					+ node.getWatchDuration();
			Simulator.addEventToSimulation(newStartTime, new DepartureEvent(
					newStartTime, node.getNodeId()));
		}

	}

	@Override
	public boolean isCompleted(int currentTime) {
		return startTime == currentTime;
	}

}
