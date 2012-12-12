/**
 * 
 */
package entity;

import java.util.ArrayList;
import java.util.List;

import simulator.Simulator;
import exception.InconsistentPacketAdditionToSetByTime;

/**
 * @author ugikbtp
 * 
 */
public class PacketSet {
	private Integer time;
	private Integer setSize;
	private List<Packet> packets;

	public PacketSet(Integer time, Integer setSize) {
		this.time = time;
		this.setSize = setSize;
		this.packets = new ArrayList<Packet>(setSize);
	}

	public PacketSet(Integer time, Integer setSize, List<Packet> packets) {
		this.time = time;
		this.setSize = setSize;
		this.packets = packets;
	}

	public void addPacket(Integer time, Packet packet)
			throws InconsistentPacketAdditionToSetByTime {
		if (packet.getPacketTime() != this.time)
			throw new InconsistentPacketAdditionToSetByTime(
					"Trying to add packet with time " + packet.getPacketTime()
							+ " to set with time " + this.time);
		if (this.packets.size() < setSize)
			this.packets.add(packet);
	}

	public void fillPacketSet(Integer time, Integer playbackRate)
			throws InconsistentPacketAdditionToSetByTime {
		for (int i = 0; i < playbackRate; i++)
			addPacket(time, new Packet(Simulator.PACKET_ID_COUNT++, time));
	}

	/**
	 * @return the time
	 */
	public Integer getTime() {
		return time;
	}

	/**
	 * @return the setSize
	 */
	public Integer getSetSize() {
		return setSize;
	}

	/**
	 * @return the packets
	 */
	public List<Packet> getPackets() {
		return packets;
	}

}
