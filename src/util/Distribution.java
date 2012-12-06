/**
 * 
 */
package util;

import java.util.Random;

/**
 * @author kerem
 * 
 */
public class Distribution {

	private static Random generator = new Random();

	public static int uniform(int n) {
		return generator.nextInt(n);
	}

	public static Integer getWatchDuration() {
		return generator.nextInt(10) + 1;
	}

}
