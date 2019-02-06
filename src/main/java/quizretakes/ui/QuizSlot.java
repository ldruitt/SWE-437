package quizretakes.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import lombok.Data;
import quizretakes.bean.QuizBean;
import quizretakes.bean.RetakeBean;

import java.time.LocalDateTime;

@Data
public class QuizSlot {
	private final LocalDateTime time;
	private final StackPane view;
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