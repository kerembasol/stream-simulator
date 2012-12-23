/**
 * 
 */
package util;

/**
 * @author kbasol-bcc
 * 
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// pieces(1, 9, 3, 15);
		// pieces(4, 10, 2, 15);
		// pieces(8, 6, 3, 15);
		pieces(6, 12, 3, 15);

	}

	private static void pieces(int startTime, int bufferSize, int factor,
			int iterationNumber) {

		int bufStart = startTime;
		int bufEnd = startTime;

		int print = startTime;
		for (int i = 0, factorCount = 1; i < bufferSize; i++, bufEnd++, factorCount++) {
			if (factorCount == factor) {
				System.out.println((bufEnd) + "-" + (print++));
				factorCount = 0;
			}
		}

		print--;
		print = getNextPrint(bufStart, factor, print);
		bufStart++;
		for (int i = 0, factorCount = 1; i < iterationNumber; i++, factorCount++, bufStart++, bufEnd++) {
			if (factorCount == factor) {
				System.out.println(bufEnd + "-" + print);
				print = getNextPrint(bufStart, factor, print);
				factorCount = 0;
			}
		}

	}

	private static int getNextPrint(int bufStart, int factor, int curPrint) {
		int returnVal;
		if (bufStart + factor > curPrint) {
			returnVal = bufStart + factor;
			System.out.println("Deflection point:" + returnVal);
		} else
			returnVal = ++curPrint;
		return returnVal;
	}
}
