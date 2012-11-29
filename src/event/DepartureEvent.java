/**
 * 
 */
package event;

import org.apache.log4j.Logger;

/**
 * @author kerem
 * 
 */
public class DepartureEvent extends Event {

	/**
	 * @param time
	 * @param id
	 */
	public DepartureEvent(int time, int id) {
		super(time, id);
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Logger getLogger() {
		return Logger.getLogger("event.departure");
	}

	@Override
	public boolean isCompleted(int currentTime) {
		return startTime == currentTime;
	}

}
