/**
 * 
 */
package simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import util.Distribution;
import event.ArrivalEvent;
import event.Event;
import exception.AdditionOfAlreadyExistingNodeException;
import exception.NotEnoughAvailableTrackerStreamException;
import exception.RetrievalOfNonExistingNode;

/**
 * @author kerem
 * 
 */
public class Simulator {

	public static Integer CURRENT_TIME = 0;
	public static final Integer SIMULATION_LENGTH = 10000;
	public static final double POISSON_PARAM = 1;
	public static final double EXPO_PARAM = 1;
	public static Integer NODE_COUNT = 0;

	private static SortedMap<Integer, List<Event>> simulationEvents;
	private final StreamNetwork network;

	private static final int EVENT_CREATE_NUMBER = 3;
	private static final int EVENT_CREATE_PERC = 30;

	public Simulator() {
		simulationEvents = new TreeMap<Integer, List<Event>>();
		network = StreamNetwork.getInstance();

	}

	public void runSimulation() throws AdditionOfAlreadyExistingNodeException,
			RetrievalOfNonExistingNode,
			NotEnoughAvailableTrackerStreamException {

		generateEvents();
		while (CURRENT_TIME <= SIMULATION_LENGTH) {
			List<Event> currentEvents = simulationEvents.get(CURRENT_TIME);
			if (currentEvents != null) {
				for (Event event : currentEvents) {
					System.out.println("EXECUTE EVENT (Time:" + CURRENT_TIME
							+ ")");
					event.execute(network);
				}
			}
			generateEvents();
		}
	}

	private void generateEvents() {
		if (shouldCreateEvent()) {
			for (int i = 1; i < getNumOfEventsToCreate(); i++) {
				Event arrivalEvent = new ArrivalEvent(CURRENT_TIME,
						NODE_COUNT++);
				addEventToSimulation(CURRENT_TIME, arrivalEvent);
			}
		}

	}

	public static void addEventToSimulation(Integer eventTime, Event event) {
		List<Event> events = simulationEvents.get(eventTime);
		if (events == null)
			events = new ArrayList<Event>();

		events.add(event);
		simulationEvents.put(eventTime, events);
	}

	private int getNumOfEventsToCreate() {
		return Distribution.uniform(EVENT_CREATE_NUMBER) + 1;
	}

	private boolean shouldCreateEvent() {
		return Distribution.uniform(100) > 100 - EVENT_CREATE_PERC;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Simulator simulator = new Simulator();
		try {
			simulator.runSimulation();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

	}

}
