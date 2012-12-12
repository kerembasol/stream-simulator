/**
 * 
 */
package entity;

/**
 * @author ugikbtp
 * 
 */
public class Packet {

	private Integer packetId;
	private Integer packetTime;
	private Integer packetSize;

	public Packet(Integer id, Integer time) {
		this.packetId = id;
		this.packetTime = time;
		this.packetSize = 1; // All packets are of size 1 currently
	}

	public Packet(Integer id, Integer time, Integer size) {
		this.packetId = id;
		this.packetTime = time;
		this.packetSize = size;
	}

	/**
	 * @return the packetId
	 */
	public Integer getPacketId() {
		return packetId;
	}

	/**
	 * @return the packetTime
	 */
	public Integer getPacketTime() {
		return packetTime;
	}

	/**
	 * @return the packetSize
	 */
	public Integer getPacketSize() {
		return packetSize;
	}

}
