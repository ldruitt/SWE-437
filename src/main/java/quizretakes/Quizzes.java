package quizretakes;

import quizretakes.bean.QuizBean;

import java.util.*;
import java.util.stream.Stream;

/**
 * This class holds a collection of quizzes
 *
 * @author Jeff Offutt
 */

public class Quizzes implements Iterable<QuizBean> {
	private final List<QuizBean> quizzes = new ArrayList<>();

	public Quizzes(){}

	public void sort() {
		Collections.sort(quizzes);
	}

	public Stream<QuizBean> stream() {
		return quizzes.stream();
	}

	@Override
	public Iterator<QuizBean> iterator() {
		return quizzes.iterator();
	}

	public void addQuiz(QuizBean qb) {
		quizzes.add(qb);
	}

	public String toString() {
		return (Arrays.toString(quizzes.toArray()));
	}

}
