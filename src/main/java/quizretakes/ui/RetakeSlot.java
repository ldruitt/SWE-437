package quizretakes.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import lombok.Data;
import quizretakes.bean.RetakeBean;

import java.time.LocalDateTime;

@Data
public class RetakeSlot {
	private final LocalDateTime time;
	private final StackPane view;
	private RetakeBean retake;

	public RetakeSlot(LocalDateTime time) {
		this.time = time;
		view = new StackPane();
		view.setMinSize(80, 20);
	}

	public void setRetake(RetakeBean retake) {
		this.retake = retake;
		// Duplicates shouldn't occur (not that it would break anything).
		// This method is only called once.
		view.getStyleClass().add("quiz-slot");
		Label lbl = new Label("Retake: Quiz-" + retake.getID() + "\nAt: " + retake.getLocation());
		view.getChildren().add(lbl);
	}
}