package event;

import org.apache.log4j.Logger;

import util.Distribution;

public class ProcessEvent extends Event {

	private final int processDuration;

	public ProcessEvent(int time, int id) {
		super(time, id);
		processDuration = determineProcessDuration();
	}

	@Override
	public void execute() {

	}

	// TODO process duration method is to be determined.
	// (function based or user supplied)
	private int determineProcessDuration() {
		// Dummy implementation
		return Distribution.exponential(startTime * nodeId);
	}

	/**
	 * @return the processDuration
	 */
	@Override
	public int getProcessDuration() {
		return processDuration;
	}

	@Override
	protected Logger getLogger() {
		return Logger.getLogger("event.process");
	}

	@Override
	public boolean isCompleted(int currentTime) {
		return (startTime + getProcessDuration()) == currentTime;
	}

}
