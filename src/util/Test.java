/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author kbasol-bcc
 * 
 */
public class Test {

	public Integer id;
	public List<Integer> time = new ArrayList<Integer>();
	public List<Integer> piece = new ArrayList<Integer>();
	public Integer factor;

	public Test(Integer id, Integer deflPiece, Integer deflTime,
			Integer factor, Integer lastTime) {
		this.id = id;
		for (Integer i = deflPiece, j = deflTime; j <= lastTime; i += factor, j += factor) {
			time.add(j);
			piece.add(i);
		}
		this.factor = factor;
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

		st1.printStreamForVictim(list, 39);

	}

	private void printStreamForVictim(List<Test> list, Integer joinTime) {
		List<Test> stream = new ArrayList<Test>();

		// getFirstTestWithAvailablePiece()
		Integer minTime = Integer.MAX_VALUE;
		Test firstAvailableTest = null;
		for (int i = 0; i < list.size(); i++) {
			Test t = list.get(i);
			Integer tempMin = getMinAvailableTimeForPieceByGivenTime(t,
					joinTime);
			if (tempMin < minTime) {
				minTime = tempMin;
				firstAvailableTest = t;
			}
		}

		if (firstAvailableTest == null) {
			System.out.println("No available node");
			return;
		}

		HashMap<Integer, Test> streamMembers = new LinkedHashMap<Integer, Test>();
		streamMembers.put(firstAvailableTest.id, firstAvailableTest);

		List<Test> sameFactorTests = getOtherTestsByFactor(list,
				firstAvailableTest.factor, firstAvailableTest.id);

		streamMembers = addOtherAvailableTests(sameFactorTests, streamMembers,
				joinTime, minTime, firstAvailableTest.factor);

		if (streamMembers == null)
			System.out.println("No available stream");
		else
			System.out.println(streamMembers.keySet());

	}

	private List<Test> getOtherTestsByFactor(List<Test> list, Integer factor,
			Integer id) {
		List<Test> returnList = new ArrayList<Test>();
		for (Test t : list)
			if (t.factor == factor && t.id != id)
				returnList.add(t);
		return returnList;
	}

	private HashMap<Integer, Test> addOtherAvailableTests(List<Test> list,
			HashMap<Integer, Test> streamMembers, Integer searchedPiece,
			Integer timeLimit, Integer factor) {

		for (Test t : list) {
			if (!streamMembers.containsKey(t.id)) {
				int i = 0;
				while (t.time.get(i) <= timeLimit) {
					if (t.piece.get(i) == searchedPiece + streamMembers.size()) {
						streamMembers.put(t.id, t);
						break;
					}
					i++;
				}
			}
		}

		if (streamMembers.size() == factor)
			return streamMembers;
		return null;
	}

	private Integer getMinAvailableTimeForPieceByGivenTime(Test t,
			Integer joinTime) {

		for (int i = 0; i < t.piece.size(); i++)
			if (t.piece.get(i) == joinTime)
				return t.time.get(i);

		return Integer.MAX_VALUE;

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
