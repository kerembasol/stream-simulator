/**
 * 
 */
package event;

import org.apache.log4j.Logger;

/**
 * @author kerem
 * 
 */
public class ArrivalEvent extends Event {

	/**
	 * @param time
	 * @param id
	 */
	public ArrivalEvent(int time, int id) {
		super(time, id);
	}

	@Override
	public void execute() {

	}

	@Override
	protected Logger getLogger() {
		return Logger.getLogger("event.arrival");
	}

	@Override
	public boolean isCompleted(int currentTime) {
		return startTime == currentTime;
	}

}
