/**
 * 
 */
package entity;

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
	private Integer availableUploadRate;
	private Integer deflectionPacketNumber;
	private Integer deflectionTime;
	private SortedMap<Integer, VictimNode> victimNodes;
	private SortedMap<Integer, List<PacketSet>> uploadBuffer;
	private Integer directVictimNodeId;

	private static final int UPLOAD_PARAM = 5;
	private static final int UPLOAD_RATE_PARAM = 4;
	private static final int LOCAL_FILE_BUFFER_PARAM = 20;
	private static final int LOCAL_FILE_WRITE_FACTOR_PARAM = 5;

	private static final int MAX_UPLOAD_BUFFER_ELEMENT_NO = 1;

	private OverridingFileBuffer localFileBuffer = null;

	public WatchingNode(Integer nodeId, Integer playStartTime,
			Integer playRate, Integer watchDuration) {
		super(nodeId, playStartTime, playRate, watchDuration);
		this.uploadRate = initUploadRate();
		this.availableUploadRate = uploadRate;
		this.uploadBuffer = new TreeMap<Integer, List<PacketSet>>();
		this.localFileBuffer = new OverridingFileBuffer(
				getLocalFileBufferSize(), getLocalFileWriteFactor());
		this.victimNodes = new TreeMap<Integer, VictimNode>();
		this.directVictimNodeId = -1;
		initDeflectionValues(getDeflectionTimeAndPacketValues(playStartTime,
				localFileBuffer.getMaxBufferSize(),
				localFileBuffer.getWritingFactor()));
	}

	private Integer initUploadRate() {
		return (Distribution.uniform(UPLOAD_RATE_PARAM) + 1) * UPLOAD_PARAM;
	}

	private void initDeflectionValues(String deflectionStr) {
		int index = deflectionStr.indexOf("#");
		this.deflectionPacketNumber = new Integer(deflectionStr.substring(0,
				index));
		this.deflectionTime = new Integer(deflectionStr.substring(index + 1));
	}

	public void associateVictimNode(VictimNode node)
			throws AdditionOfAlreadyExistingNodeException {

		if (victimNodes.containsKey(node.getNodeId()))
			throw new AdditionOfAlreadyExistingNodeException(
					"Associating already associated victim node");

		victimNodes.put(node.getNodeId(), node);
		decreaseAvailableUploadRate();
		if (this.directVictimNodeId == -1)
			this.directVictimNodeId = node.getNodeId();
	}

	public void removeVictimNode(Integer nodeId)
			throws RetrievalOfNonExistingNodeException {
		if (victimNodes.get(nodeId) == null)
			throw new RetrievalOfNonExistingNodeException(
					"Trying to remove non-existing victim node(" + nodeId
							+ ") from watching node (" + this.nodeId + ")");

		victimNodes.remove(nodeId);
		increaseAvailableUploadRate();
	}

	// TODO victimNode behavior is to be determined
	@Override
	public void detachNodeFromNetwork(StreamNetwork network) {
		network.getWatchingNodes().remove(this.nodeId);
		network.getTracker().increaseAvailableStreamRateByAmount(this.playRate);
	}

	public void addPacketSetToUploadBuffer(VictimNode vn, PacketSet set)
			throws BufferOverflowException,
			AdditionOfOutdatedPacketSetException,
			AdditionOfNewSetWithLargerSetSizeException {

		List<PacketSet> vnPacketSetList = this.uploadBuffer.get(vn.getNodeId());
		vnPacketSetList = addPacketSetToBuffer(set, vnPacketSetList,
				MAX_UPLOAD_BUFFER_ELEMENT_NO, this.maxPlaybackBufferSetSize);
		uploadBuffer.put(vn.getNodeId(), vnPacketSetList);
	}

	public void removePacketSetFromUploadBuffer(VictimNode vn)
			throws BufferUnderflowException {

		List<PacketSet> vnPacketSetList = this.uploadBuffer.get(vn.getNodeId());
		vnPacketSetList = removePacketSetFromBuffer(vnPacketSetList);

	}

	public boolean getHasDirectVictimNode() {
		return this.directVictimNodeId.intValue() != -1;
	}

	public Integer getClosestPacketNumberForGivenPlaybackTime(Integer time) {
		if (this.deflectionPacketNumber > time)
			return null;

		int iteration = (time - this.deflectionPacketNumber)
				/ this.localFileBuffer.getWritingFactor();
		return this.deflectionPacketNumber
				+ (this.localFileBuffer.getWritingFactor() * iteration);

	}

	public Integer getClosestPacketTimeForGivenPlaybackTime(Integer time) {
		if (this.deflectionPacketNumber > time)
			return null;

		int iteration = (time - this.deflectionPacketNumber)
				/ this.localFileBuffer.getWritingFactor();

		return this.deflectionTime
				+ (this.localFileBuffer.getWritingFactor() * iteration);

	}

	private String getDeflectionTimeAndPacketValues(int startTime,
			int bufferSize, int factor) {
		int bufStart = startTime;
		int bufEnd = startTime;

		int print = startTime;
		for (int i = 0, factorCount = 1; i < bufferSize; i++, bufEnd++, factorCount++) {
			if (factorCount == factor) {
				// System.out.println((bufEnd) + "-" + (print++));
				factorCount = 0;
			}
		}

		print--;
		print = getNextPrint(bufStart, factor, print);
		bufStart++;
		int deflectionPacket = -1;
		int deflectionTime = -1;
		for (int factorCount = 1;; factorCount++, bufStart++, bufEnd++) {
			if (factorCount == factor) {
				// System.out.println(bufEnd + "-" + print);
				if (isDeflectionPoint(bufStart, factor, print)) {
					deflectionPacket = bufStart + factor;
					deflectionTime = bufEnd;
					break;
				}
				++print;
				factorCount = 0;
			}
		}

		return deflectionPacket + "#" + deflectionTime;
	}

	private boolean isDeflectionPoint(int bufStart, int factor, int curPrint) {
		return bufStart + factor > curPrint;
	}

	private static int getNextPrint(int bufStart, int factor, int curPrint) {
		int returnVal;
		if (bufStart + factor > curPrint) {
			returnVal = bufStart + factor;
		} else
			returnVal = ++curPrint;
		return returnVal;
	}

	public Integer getUploadRate() {
		return uploadRate;
	}

	public Integer getAvailableUploadRate() {
		return availableUploadRate;
	}

	private void decreaseAvailableUploadRate() {
		this.availableUploadRate -= UPLOAD_PARAM;
	}

	private void increaseAvailableUploadRate() {
		this.availableUploadRate += UPLOAD_PARAM;
	}

	private Integer getLocalFileBufferSize() {
		return Distribution.uniform(LOCAL_FILE_BUFFER_PARAM) + 1;
	}

	private Integer getLocalFileWriteFactor() {
		return Distribution.uniform(LOCAL_FILE_WRITE_FACTOR_PARAM) + 1;
	}

	public OverridingFileBuffer getLocalFileBuffer() {
		return localFileBuffer;
	}

}
