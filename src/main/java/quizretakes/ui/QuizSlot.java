package quizretakes.ui;

import java.time.LocalDateTime;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import quizretakes.bean.QuizBean;
import quizretakes.bean.RetakeBean;
import lombok.Data;

/**
 * Representation of a quiz or retake that can be displayed as a slot in the WeekView.
 *
 * @author Matt Coley
 */
@Data
public class QuizSlot {
	/**
	 * Time of the quiz/retake.
	 */
	private final LocalDateTime time;
	/**
	 * Holds the node representation of the quiz/retake.
	 */
	private final StackPane view;
	/**
	 * The quiz/retake this slot represents. <br>
	 * <b>Note:</b> Retake extends Quiz.
	 */
	private QuizBean quiz;

	public QuizSlot(LocalDateTime time) {
		this.time = time;
		view = new StackPane();
		view.setMinSize(80, 20);
	}

	public void setQuiz(QuizBean quiz) {
		this.quiz = quiz;
		// Add a label and custom style to the slot depending on if the quiz is an instance of a
		// Retake or not.
		Label lbl;
		if(quiz instanceof RetakeBean) {
			RetakeBean r = ((RetakeBean) quiz);
			lbl = new Label("Retake: " + quiz.getID() + "\nAt: " + r.getLocation());
			view.getStyleClass().add("retake-slot");
		} else {
			lbl = new Label("Quiz: " + quiz.getID());
			view.getStyleClass().add("quiz-slot");
		}
		view.getChildren().add(lbl);
	}
}