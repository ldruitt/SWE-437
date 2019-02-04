package quizretakes;

import quizretakes.bean.RetakeBean;

import java.util.*;

/**
 * This class holds a collection of retakes
 *
 * @author Jeff Offutt
 */

public class Retakes implements Iterable<RetakeBean> {
	private final List<RetakeBean> retakes = new ArrayList<>();

	public Retakes() {}

	public void sort() {
		Collections.sort(retakes);
	}

	@Override
	public Iterator<RetakeBean> iterator() {
		return retakes.iterator();
	}

	public void addRetake(RetakeBean qr) {
		retakes.add(qr);
	}

	public String toString() {
		return (Arrays.toString(retakes.toArray()));
	}
}
