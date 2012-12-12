/**
 * 
 */
package event;

import java.nio.BufferOverflowException;

import simulator.StreamNetwork;
import exception.AdditionOfAlreadyExistingNodeException;
import exception.AdditionOfOutdatedPacketSetException;
import exception.InconsistentPacketAdditionToSetByTime;
import exception.NotEnoughAvailableTrackerStreamException;
import exception.RetrievalOfNonExistingNodeException;

/**
 * @author kerem
 * 
 */
public abstract class Event implements Comparable<Event> {

	protected Integer nodeId;
	protected Integer startTime;

	public Event(Integer time, Integer id) {
		nodeId = id;
		startTime = time;

	}

	public abstract void execute(StreamNetwork network)
			throws AdditionOfAlreadyExistingNodeException,
			RetrievalOfNonExistingNodeException,
			NotEnoughAvailableTrackerStreamException, BufferOverflowException,
			AdditionOfOutdatedPacketSetException,
			InconsistentPacketAdditionToSetByTime,
			RetrievalOfNonExistingNodeException,
			exception.BufferOverflowException,
			AdditionOfOutdatedPacketSetException,
			InconsistentPacketAdditionToSetByTime;

	public abstract boolean isCompleted(int currentTime);

	@Override
	public int compareTo(Event o) {
		if (o == null)
			return 1;
		return startTime > o.startTime ? 1 : startTime < o.startTime ? -1 : 0;
	}

}
