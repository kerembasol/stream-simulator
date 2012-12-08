package entity;

import java.nio.Buffer;

import simulator.StreamNetwork;
import exception.RetrievalOfNonExistingNode;

public abstract class Node implements Comparable<Node> {

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
		playAmount = Integer.valueOf(0);
	}

	public abstract void detachNodeFromNetwork(StreamNetwork streamnetwork)
			throws RetrievalOfNonExistingNode;

	@Override
	public int compareTo(Node n) {
		if (n == null) {
			return 1;
		} else {
			return nodeId.intValue() <= n.getNodeId().intValue() ? nodeId
					.intValue() >= n.getNodeId().intValue() ? 0 : -1 : 1;
		}
	}

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
