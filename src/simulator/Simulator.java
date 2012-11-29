/**
 * 
 */
package simulator;

import java.util.PriorityQueue;

import event.ArrivalEvent;
import event.Event;
import event.ProcessEvent;

/**
 * @author kerem
 * 
 */
public class Simulator {

	public static int CURRENT_TIME = 0;
	public static final int SIMULATION_LENGTH = 10000;
	public static final double POISSON_PARAM = 1;
	public static final double EXPO_PARAM = 1;
	public static int NODE_COUNT = 0;

	private PriorityQueue<Event> simulation;
	private StreamNetwork network;

	public Simulator() {
		simulation = new PriorityQueue<Event>();
		network = new StreamNetwork();
	}

	public void runSimulation() {

		generateEvents();

		while (CURRENT_TIME <= SIMULATION_LENGTH) {
			Event event = simulation.peek();
			if (event != null)
				event.execute(network);

			if (event.isCompleted(CURRENT_TIME))
				simulation.remove(event);

		}
	}

	private void generateEvents() {
		Event arrivalEvent = new ArrivalEvent(CURRENT_TIME, NODE_COUNT);
		Event processEvent = new ProcessEvent(CURRENT_TIME + 1, NODE_COUNT);
		Event departureEvent = new ProcessEvent(CURRENT_TIME + 2
				+ processEvent.getProcessDuration(), NODE_COUNT);

		simulation.add(arrivalEvent);
		simulation.add(processEvent);
		simulation.add(departureEvent);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Simulator simulator = new Simulator();
		simulator.runSimulation();

	}

}
