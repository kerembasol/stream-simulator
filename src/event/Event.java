/**
 * 
 */
package event;

import org.apache.log4j.Logger;

import simulator.StreamNetwork;
import exception.AdditionOfAlreadyExistingNodeException;
import exception.NotEnoughAvailableTrackerStreamException;
import exception.RetrievalOfNonExistingNode;

/**
 * @author kerem
 * 
 */
public abstract class Event implements Comparable<Event> {

	protected Integer nodeId;
	protected Integer startTime;

	private final Logger logger;

	public Event(Integer time, Integer id) {
		nodeId = id;
		startTime = time;
		logger = getLogger();
	}

	public abstract void execute(StreamNetwork network)
			throws AdditionOfAlreadyExistingNodeException,
			RetrievalOfNonExistingNode,
			NotEnoughAvailableTrackerStreamException;

	public abstract boolean isCompleted(int currentTime);

	@Override
	public int compareTo(Event o) {
		if (o == null)
			return 1;
		return startTime > o.startTime ? 1 : startTime < o.startTime ? -1 : 0;
	}

	protected abstract Logger getLogger();
}
