/**
 * 
 */
package simulator;

import java.util.List;

import entity.Node;
import entity.Tracker;

/**
 * @author kerem
 * 
 */
public class StreamNetwork {

	private Tracker tracker;
	private List<Node> watchingNodes;
	private List<Node> victimNodes;

	private static StreamNetwork instance = null;

	private StreamNetwork() {
	}

	public static StreamNetwork getInstance() {
		if (instance == null) {
			synchronized (StreamNetwork.class) {
				if (instance == null) {
					instance = new StreamNetwork();
				}
			}
		}
		return instance;
	}

}
