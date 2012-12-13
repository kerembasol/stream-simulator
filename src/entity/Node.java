package entity;

import java.util.ArrayList;
import java.util.List;

import simulator.StreamNetwork;
import util.Distribution;
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
	private static final int PLAYBACK_BUFFER_CONSTANT = 20; // # of seconds for
															// playback

	protected Integer maxPlaybackBufferLen;
	protected Integer maxPlaybackBufferSetSize;

	public Node(Integer nodeId, Integer playStartTime, Integer playRate,
			Integer watchDuration) {
		this.nodeId = nodeId;
		this.playStartTime = playStartTime;
		this.watchDuration = watchDuration;
		this.playRate = playRate;
		this.playAmount = Integer.valueOf(0);
		this.maxPlaybackBufferLen = getPlaybackBufferSize();
		this.maxPlaybackBufferSetSize = 1;
		this.playbackBuffer = new ArrayList<PacketSet>(maxPlaybackBufferLen);
	}

	public abstract void detachNodeFromNetwork(StreamNetwork streamnetwork)
			throws RetrievalOfNonExistingNodeException;

	public abstract void updateBuffers();

	@Override
	public int compareTo(Node n) {
		if (n == null) {
			return 1;
		} else {
			return nodeId.intValue() <= n.getNodeId().intValue() ? nodeId
					.intValue() >= n.getNodeId().intValue() ? 0 : -1 : 1;
		}
	}

	private Integer getPlaybackBufferSize() {
		return Distribution.uniform(PLAYBACK_BUFFER_CONSTANT) + 1;
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

	protected void addPacketSetToBuffer(PacketSet set, List<PacketSet> buffer,
			Integer maxSize, Integer maxSetSize)
			throws BufferOverflowException,
			AdditionOfOutdatedPacketSetException,
			AdditionOfNewSetWithLargerSetSizeException {
		if (buffer.size() == maxSize)
			throw new BufferOverflowException("Buffer overflow for node:"
					+ nodeId + " for packet set having time  " + set.getTime());
		if (buffer.get(buffer.size() - 1).getTime() >= set.getTime())
			throw new AdditionOfOutdatedPacketSetException(
					"Adding outdated packet set with time " + set.getTime()
							+ " to node with id " + nodeId);

		if (set.getSetSize() > maxSetSize)
			throw new AdditionOfNewSetWithLargerSetSizeException(
					"Addition of set with size " + set.getSetSize()
							+ " to node " + this.nodeId
							+ " with max. allowable setSize " + maxSetSize);
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
			AdditionOfOutdatedPacketSetException,
			AdditionOfNewSetWithLargerSetSizeException {

		addPacketSetToBuffer(set, this.playbackBuffer,
				this.maxPlaybackBufferLen, this.maxPlaybackBufferSetSize);
	}

	public void removePacketSetFromPlaybackBuffer()
			throws BufferUnderflowException {

		removePacketSetFromBuffer(this.playbackBuffer);
	}
}
