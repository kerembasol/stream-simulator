/**
 * 
 */
package event;

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
public abstract class Event implements Comparable<Event> {

	protected Integer nodeId;
	protected Integer startTime;

	public Event(Integer time, Integer id) {
		nodeId = id;
		startTime = time;
	}

	public abstract void execute()
			throws AdditionOfAlreadyExistingNodeException,
			RetrievalOfNonExistingNodeException,
			NotEnoughAvailableTrackerStreamException, BufferOverflowException,
			AdditionOfOutdatedPacketSetException,
			InconsistentPacketAdditionToSetByTime,
			RetrievalOfNonExistingNodeException,
			exception.BufferOverflowException,
			InconsistentPacketAdditionToSetByTime,
			AdditionOfNewSetWithLargerSetSizeException, IllegalValueException;

	public abstract boolean isCompleted(int currentTime);

	@Override
	public int compareTo(Event o) {
		if (o == null)
			return 1;
		return startTime > o.startTime ? 1 : startTime < o.startTime ? -1 : 0;
	}

}
