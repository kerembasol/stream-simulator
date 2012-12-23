/**
 * 
 */
package entity;

import java.util.LinkedList;

/**
 * @author kerem
 * 
 */
public class OverridingFileBuffer {

	private LinkedList<File> fileBuffer;
	private Integer maxBufferSize;
	private Integer writingFactor;

	public OverridingFileBuffer(Integer size, Integer factor) {
		maxBufferSize = size;
		writingFactor = factor;
		fileBuffer = new LinkedList<>();
	}

	public void addPacketSet(PacketSet set) {
		if (fileBuffer.size() == maxBufferSize)
			fileBuffer.remove();
		fileBuffer.add(new File(set, writingFactor));
	}

	/**
	 * This function simulates the writing of the packet by a factor. For
	 * instance, if the factor is 3, the packet set will be written to file only
	 * after 3 iterations of the simulation. Therefore this function has to be
	 * called at each step of the simulation.
	 */
	public void updateBuffer() {
		for (File f : fileBuffer)
			f.incrementWriteFactorCount();
	}
}
