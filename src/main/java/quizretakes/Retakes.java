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

	public Retakes(int ID, String location, int month, int day, int hour, int minute) {
		RetakeBean qr = new RetakeBean(ID, location, month, day, hour, minute);
		retakes.add(qr);
	}

	public Retakes(RetakeBean qr) {
		retakes.add(qr);
	}

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
