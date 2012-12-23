/**
 * 
 */
package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import simulator.StreamNetwork;
import util.Distribution;
import exception.AdditionOfAlreadyExistingNodeException;
import exception.AdditionOfNewSetWithLargerSetSizeException;
import exception.AdditionOfOutdatedPacketSetException;
import exception.BufferOverflowException;
import exception.BufferUnderflowException;
import exception.RetrievalOfNonExistingNodeException;

/**
 * @author kerem
 * 
 */
public class WatchingNode extends Node {

	private Integer uploadRate;
	private Integer availableUploadAmount;
	private Integer deflectionPacketNumber;
	private Integer deflectionTime;
	private SortedMap<Integer, VictimNode> victimNodes;
	private List<PacketSet> uploadBuffer;
	private boolean hasDirectVictimNode;

	private static final int UPLOAD_RATE_PARAM = 5;
	private static final int UPLOAD_RATE_LEN = 4;
	private static final int UPLOAD_BUFFER_PARAM = 20; // # of seconds for
														// playback

	private Integer maxUploadBufferSize;
	private Integer maxUploadBufferSetSize;

	public WatchingNode(Integer nodeId, Integer playStartTime,
			Integer playRate, Integer watchDuration) {
		super(nodeId, playStartTime, playRate, watchDuration);
		this.uploadRate = initUploadRate();
		this.victimNodes = new TreeMap<Integer, VictimNode>();
		this.availableUploadAmount = uploadRate;
		this.maxUploadBufferSize = getUploadBufferSize();
		this.maxUploadBufferSetSize = uploadRate - UPLOAD_BUFFER_PARAM;
		this.uploadBuffer = new ArrayList<PacketSet>(maxUploadBufferSize);
		this.hasDirectVictimNode = false;
		this.deflectionPacketNumber = -1;
		this.deflectionTime = -1;
	}

	private Integer initUploadRate() {
		return (Distribution.uniform(UPLOAD_RATE_LEN) + 1) * UPLOAD_RATE_PARAM;
	}

	private Integer getUploadBufferSize() {
		return Distribution.uniform(UPLOAD_BUFFER_PARAM) + 1;
	}

	public void associateVictimNode(VictimNode node)
			throws AdditionOfAlreadyExistingNodeException {

		if (victimNodes.containsKey(node.getNodeId()))
			throw new AdditionOfAlreadyExistingNodeException(
					"Associating already associated victim node");

		victimNodes.put(node.getNodeId(), node);
	}

	public Integer getUploadRate() {
		return uploadRate;
	}

	public Integer getAvailableUploadAmount() {
		return availableUploadAmount;
	}

	public void setAvailableUploadAmount(Integer uploadAmount) {
		this.availableUploadAmount = uploadAmount;
	}

	public void removeVictimNode(Integer nodeId)
			throws RetrievalOfNonExistingNodeException {
		if (victimNodes.get(nodeId) == null)
			throw new RetrievalOfNonExistingNodeException(
					"Trying to remove non-existing victim node(" + nodeId
							+ ") from watching node (" + this.nodeId + ")");

		victimNodes.remove(nodeId);
	}

	// TODO victimNode behavior is to be determined
	@Override
	public void detachNodeFromNetwork(StreamNetwork network) {
		network.getWatchingNodes().remove(this.nodeId);
		network.getTracker().increaseAvailableStreamRateByAmount(this.playRate);
	}

	public void addPacketSetToUploadBuffer(PacketSet set)
			throws BufferOverflowException,
			AdditionOfOutdatedPacketSetException,
			AdditionOfNewSetWithLargerSetSizeException {

		addPacketSetToBuffer(set, this.uploadBuffer, this.maxUploadBufferSize,
				this.maxUploadBufferSetSize);
	}

	public void removePacketSetFromUploadBuffer()
			throws BufferUnderflowException {

		removePacketSetFromBuffer(this.uploadBuffer);
	}

	public boolean getHasDirectVictimNode() {
		return hasDirectVictimNode;
	}

}
