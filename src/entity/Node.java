/**
 * 
 */
package entity;

import java.nio.Buffer;

import simulator.StreamNetwork;
import exception.RetrievalOfNonExistingNode;

/**
 * @author kerem
 * 
 */
public abstract class Node {

	protected Integer nodeId;
	protected Buffer buffer;
	protected Integer playRate;
	protected Integer playAmount;
	protected Integer watchDuration;
	protected Integer playStartTime;

	public Node(Integer nodeId, Integer playStartTime, Integer playRate,
			Integer watchDuration) {
		this.nodeId = nodeId;
		this.playStartTime = playStartTime;
		this.watchDuration = watchDuration;
		this.playRate = playRate;
		this.playAmount = 0;
	}

	public abstract void detachNodeFromNetwork(StreamNetwork network)
			throws RetrievalOfNonExistingNode;

	public Integer getNodeId() {
		return nodeId;
	}

	public Integer getPlayStartTime() {
		return playStartTime;
	}

	public Integer getPlayRate() {
		return playRate;
	}

	public Integer getPlayAmount() {
		return playAmount;
	}

	public void setPlayAmount(Integer amount) {
		playAmount = amount;
	}

	public Integer getWatchDuration() {
		return watchDuration;
	}

}
