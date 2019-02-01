package quizretakes;

// import java.io.Serializable; ?? Needed?

import quizretakes.bean.QuizBean;

import java.util.*;

/**
 * This class holds a collection of quizzes
 *
 * @author Jeff Offutt
 */

public class quizzes implements Iterable<QuizBean> {
	private final List<QuizBean> quizzes = new ArrayList<>();

	public quizzes(){}

	public quizzes(int quizID, int month, int day, int hour, int minute) {
		QuizBean qb = new QuizBean(quizID, month, day, hour, minute);
		quizzes.add(qb);
	}

	public quizzes(QuizBean qb) {
		quizzes.add(qb);
	}

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
