package quizretakes.ui;

import quizretakes.DataWrapper;
import quizretakes.bean.QuizBean;
import quizretakes.bean.RetakeBean;

/**
 * UI element that allows staff to view which retakes are scheduled for which quizzes and at
 * which retake sessions. Selecting a quiz-slot shows all of its retakes. Selecting a retake-slot
 * shows all of the quizzes being retaken during it.
 *
 * @author Matt Coley
 */
public class StaffView extends ScheduleView {
	public StaffView(DataWrapper wrap) {
		super(wrap);
	}

	@Override
	protected void setup() {
		// Initialize components

		//TODO: How to elegantly display additional data to staff?

		// Setup in the super-class.
		// This will create the schedule grid & date navigation.
		super.setup();
	}

	@Override
	protected void onSelectQuiz(QuizBean quiz) {

	}

	@Override
	protected void onSelectRetake(RetakeBean retake) {

	}
}
