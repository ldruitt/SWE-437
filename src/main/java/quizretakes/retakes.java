package quizretakes;

import quizretakes.bean.RetakeBean;

import java.util.*;

/**
 * This class holds a collection of retakes
 *
 * @author Jeff Offutt
 */

public class retakes implements Iterable<RetakeBean> {
	private final List<RetakeBean> retakes;

	public retakes() {
		retakes = new ArrayList<>();
	}

	public retakes(int ID, String location, int month, int day, int hour, int minute) {
		retakes = new ArrayList<>();
		RetakeBean qr = new RetakeBean(ID, location, month, day, hour, minute);
		retakes.add(qr);
	}

	public retakes(RetakeBean qr) {
		retakes = new ArrayList<>();
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
