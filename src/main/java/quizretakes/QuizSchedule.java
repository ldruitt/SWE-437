package quizretakes;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import quizretakes.ui.StaffView;
import quizretakes.ui.StudentView;

/**
 * JavaFX front end for the Quiz-Retake application.
 *
 * @author Matt Coley
 */
public class QuizSchedule extends Application {
	/**
	 * Application window size constants.
	 */
	private final static int WIDTH = 630, HEIGHT = 605;

	@Override
	public void start(Stage primaryStage) {
		// Root node that components will be added to.
		StackPane root = new StackPane();
		root.getStyleClass().add("main");
		// Create prompt to ask user for a course
		HBox viewPrompt = new HBox();
		viewPrompt.setAlignment(Pos.CENTER);
		TextField txtCourse = new TextField();
		txtCourse.setPromptText("Specify a course ID");
		Button btnSelect = new Button("  Go  ");
		btnSelect.setOnMouseClicked(e -> {
			// Fetch course information
			String courseID = txtCourse.getText().toLowerCase();
			DataWrapper wrap = IOUtils.load(courseID);
			if(wrap == null) {
				// Show error message if the course could not be loaded
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Failed to find course information");
				alert.setHeaderText(null);
				alert.setContentText("The specified class does not exist. Please check if you " +
						"have entered the correct course ID.");
				alert.showAndWait();
				return;
			}
			// Create a view that displays the loaded course information.
			// For staff looking to view student information, holding '[SHIFT]' when clicking 'Go'
			// will display the staff-view instead of the student's scheduling view.
			// TODO: More intuitive way. Temporary until I can make something that looks nice
			Node viewQuizes = e.isShiftDown() ? new StaffView(wrap) : new StudentView(wrap);
			root.getChildren().add(viewQuizes);
			// Fancy animation to the new display into view.
			// Initial prompt is discarded on completion.
			double width = root.getWidth();
			KeyFrame start = new KeyFrame(Duration.ZERO, new KeyValue(viewQuizes
					.translateXProperty(), width, Interpolator.EASE_BOTH), new KeyValue(viewPrompt
					.translateXProperty(), 0));
			KeyFrame end = new KeyFrame(Duration.seconds(0.5), new KeyValue(viewQuizes
					.translateXProperty(), 0, Interpolator.EASE_BOTH), new KeyValue(viewPrompt
					.translateXProperty(), -width));
			Timeline animation = new Timeline(start, end);
			animation.setOnFinished(ee -> root.getChildren().remove(viewPrompt));
			animation.play();
		});
		// Hitting '[ENTER]' will also work as clicking the button.
		txtCourse.setOnAction(btnSelect.getOnAction());
		viewPrompt.getChildren().addAll(txtCourse, btnSelect);
		root.getChildren().add(viewPrompt);
		// Create the stage and set the scene.
		Scene scene = new Scene(root, WIDTH, HEIGHT);
		scene.getStylesheets().add("style.css");
		primaryStage.setScene(scene);
		primaryStage.show();
		// Unfocus the course-ID text-prompt. This allows the prompt-text to render.
		viewPrompt.requestFocus();
	}

	/**
	 * Launch JavaFX application.
	 *
	 * @param args
	 * 		Unused.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}