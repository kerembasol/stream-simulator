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
	private SortedMap<Integer, VictimNode> victimNodes;
	private List<PacketSet> uploadBuffer;

	private static final int UPLOAD_RATE = 15;
	private static final int UPLOAD_BUFFER_CONSTANT = 20; // # of seconds for
															// playback

	public WatchingNode(Integer nodeId, Integer playStartTime,
			Integer playRate, Integer watchDuration) {
		super(nodeId, playStartTime, playRate, watchDuration);
		this.uploadRate = initUploadRate();
		this.victimNodes = new TreeMap<Integer, VictimNode>();
		this.availableUploadAmount = uploadRate;
		this.playbackBuffer = new ArrayList<PacketSet>(UPLOAD_BUFFER_CONSTANT);
	}

	private Integer initUploadRate() {
		return Distribution.uniform(UPLOAD_RATE);
	}

	public void associateVictimNode(Integer nodeId, VictimNode node)
			throws AdditionOfAlreadyExistingNodeException {

		if (victimNodes.containsKey(nodeId))
			throw new AdditionOfAlreadyExistingNodeException(
					"Associating already associated victim node");

		victimNodes.put(nodeId, node);
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
			AdditionOfOutdatedPacketSetException {
		addPacketSetToBuffer(set, this.uploadBuffer);
	}

	public void removePacketSetFromUploadBuffer()
			throws BufferUnderflowException {
		removePacketSetFromBuffer(this.uploadBuffer);
	}

}
