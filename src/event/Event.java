/**
 * 
 */
package event;

import org.apache.log4j.Logger;

/**
 * @author kerem
 * 
 */
public abstract class Event implements Comparable<Event> {

	protected int nodeId;
	protected int startTime;

	private final Logger logger;

	public Event(int time, int id) {
		nodeId = id;
		startTime = time;
		logger = getLogger();
	}

	public abstract void execute();

	public abstract boolean isCompleted(int currentTime);

	@Override
	public int compareTo(Event o) {
		if (o == null)
			return 1;
		return startTime > o.startTime ? 1 : startTime < o.startTime ? -1 : 0;
	}

	/**
	 * This function will be overwritten in {@link ProcessEvent} only
	 * 
	 * @return
	 */
	public int getProcessDuration() {
		return 0;
	}

	protected abstract Logger getLogger();
}
