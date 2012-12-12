package entity;

import java.util.ArrayList;
import java.util.List;

import simulator.StreamNetwork;
import exception.AdditionOfOutdatedPacketSetException;
import exception.BufferOverflowException;
import exception.BufferUnderflowException;
import exception.RetrievalOfNonExistingNodeException;

public abstract class Node implements Comparable<Node> {

	protected Integer nodeId;
	protected Integer playRate;
	protected Integer playAmount;
	protected Integer watchDuration;
	protected Integer playStartTime;
	protected List<PacketSet> playbackBuffer;
	private static final int PLAYBACK_BUFFER_CONSTANT = 10; // # of seconds for
															// playback

	public Node(Integer nodeId, Integer playStartTime, Integer playRate,
			Integer watchDuration) {
		this.nodeId = nodeId;
		this.playStartTime = playStartTime;
		this.watchDuration = watchDuration;
		this.playRate = playRate;
		this.playAmount = Integer.valueOf(0);
		this.playbackBuffer = new ArrayList<PacketSet>(PLAYBACK_BUFFER_CONSTANT);
	}

	public abstract void detachNodeFromNetwork(StreamNetwork streamnetwork)
			throws RetrievalOfNonExistingNodeException;

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

	protected void addPacketSetToBuffer(PacketSet set, List<PacketSet> buffer)
			throws BufferOverflowException,
			AdditionOfOutdatedPacketSetException {
		if (buffer.size() == PLAYBACK_BUFFER_CONSTANT)
			throw new BufferOverflowException("Buffer overflow for node:"
					+ nodeId + " for packet set having time  " + set.getTime());
		if (buffer.get(buffer.size() - 1).getTime() >= set.getTime())
			throw new AdditionOfOutdatedPacketSetException(
					"Adding outdated packet set with time " + set.getTime()
							+ " to node with id " + nodeId);
		buffer.add(set);
	}

	protected void removePacketSetFromBuffer(List<PacketSet> buffer)
			throws BufferUnderflowException {
		if (buffer.size() == 0)
			throw new BufferUnderflowException("Buffer underflow for node:"
					+ nodeId);
		buffer.remove(0);
	}

	public void addPacketSetToPlaybackBuffer(PacketSet set)
			throws BufferOverflowException,
			AdditionOfOutdatedPacketSetException {
		addPacketSetToBuffer(set, this.playbackBuffer);
	}

	public void removePacketSetFromPlaybackBuffer()
			throws BufferUnderflowException {
		removePacketSetFromBuffer(this.playbackBuffer);
	}
}
