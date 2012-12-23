/**
 * 
 */
package entity;

/**
 * @author kerem
 * 
 */
public class File {
	private Integer writeFactor;
	private Integer writeFactorCount;
	private PacketSet packetSet;

	public File(PacketSet set, Integer writeFactor) {
		packetSet = set;
		writeFactorCount = new Integer(0);
	}

	public Integer getWriteFactorCount() {
		return writeFactorCount;
	}

	public void setWriteFactorCount(Integer writeFactorCount) {
		this.writeFactorCount = writeFactorCount;
	}

	public void incrementWriteFactorCount() {
		if (writeFactorCount < writeFactor)
			writeFactorCount++;
	}

	public Integer getFileTime() {
		return packetSet.getTime();
	}
}
