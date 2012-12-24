/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kbasol-bcc
 * 
 */
public class Test {

	public Integer id;
	public List<Integer> time = new ArrayList<Integer>();
	public List<Integer> piece = new ArrayList<Integer>();

	public Test(Integer id, Integer deflPiece, Integer deflTime,
			Integer factor, Integer lastTime) {
		this.id = id;
		for (Integer i = deflPiece, j = deflTime; j <= lastTime; i += factor, j += factor) {
			time.add(deflTime);
			piece.add(deflPiece);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// pieces(1, 9, 3, 15);
		// pieces(4, 10, 2, 15);
		// pieces(8, 6, 3, 15);
		// pieces(6, 12, 3, 15);

		Test st1 = new Test(0, 7, 15, 3, 75);
		Test st2 = new Test(1, 14, 23, 2, 75);
		Test st3 = new Test(2, 11, 16, 3, 75);
		Test st4 = new Test(3, 12, 23, 3, 75);

		List<Test> list = new ArrayList<Test>();
		list.add(st1);
		list.add(st2);
		list.add(st3);
		list.add(st4);

	}

	private void printStreamForVictim(List<Test> list, Integer joinTime) {
		List<Test> stream = new ArrayList<Test>();

		Test first = getFirstTestWithAvailablePiece(list);

		while (!isValidStream()) {

		}
	}

	private Test getFirstTestWithAvailablePiece(List<Test> list,
			Integer joinTime) {

	}

	private boolean isValidStream() {

	}

	private void pieces(int startTime, int bufferSize, int factor,
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

	private int getNextPrint(int bufStart, int factor, int curPrint) {
		int returnVal;
		if (bufStart + factor > curPrint) {
			returnVal = bufStart + factor;
		} else
			returnVal = ++curPrint;
		return returnVal;
	}

}
