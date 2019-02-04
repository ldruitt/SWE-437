package quizretakes;

// import java.io.Serializable; ?? Needed?

import quizretakes.bean.QuizBean;

import java.util.*;

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
