package quizretakes;

import quizretakes.bean.RetakeBean;

import java.util.*;
import java.util.stream.Stream;

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

	public Stream<RetakeBean> stream() {
		return retakes.stream();
	}

	public Optional<RetakeBean> get(int id) {
		return stream().filter(r -> r.getID() == id).findAny();
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
