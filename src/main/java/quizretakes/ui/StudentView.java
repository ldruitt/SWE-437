package quizretakes.ui;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import quizretakes.DataWrapper;
import quizretakes.bean.*;

/**
 * UI element that allows students to schedule a quiz retake.  Students will select a quiz and
 * retake slot then enter their name and click the "submit" button to register for their quiz.
 *
 * @author Matt Coley
 */
public class StudentView extends ScheduleView {
	/**
	 * The number of days after a quiz a retake is allowed.
	 */
	private final int daysAvailable;
	/**
	 * Label displaying the selected {@link #quiz}.
	 */
	private Label lblQuiz;
	/**
	 * Label displaying the selected {@link #retake}.
	 */
	private Label lblRetake;
	/**
	 * Text input for the student's name for scheduling.
	 */
	private TextField txtName;
	/**
	 * Button to register the {@link #txtName student} for their retake.
	 */
	private Button btnRegister;

	public StudentView(DataWrapper wrap) {
		super(wrap);
		daysAvailable = wrap.getCourse().getRetakeDuration();
	}

	@Override
	protected void setup() {
		// Initialize components
		lblQuiz = new Label();
		lblRetake = new Label();
		txtName = new TextField();
		btnRegister = new Button("Register");
		// Setup current selection display (registration form, has all needed fields)
		TilePane registerForm = new TilePane(Orientation.HORIZONTAL);
		registerForm.setAlignment(Pos.CENTER);
		registerForm.setPadding(new Insets(4, 0, 9, 0));
		txtName.setPromptText("Enter your name");
		txtName.textProperty().addListener(e -> updateForm());
		btnRegister.setDisable(true);
		btnRegister.setOnAction(e -> {
			// registering returns a boolean for it's success.
			if(wrap.registerAppointment(quiz, retake, txtName.getText())) {
				// register success
				resetOnSuccess();
			} else {
				// register failed due to silenced IOException
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Failed to register appointment");
				alert.setHeaderText(null);
				alert.setContentText("The quiz retake could not be made at this time. Contact the"
						+ " professor if this issue persists.");
				alert.showAndWait();
			}
		});
		registerForm.getChildren().addAll(lblQuiz, lblRetake, txtName, btnRegister);
		// Set initial form text & add to the UI
		updateForm();
		getChildren().add(registerForm);
		// Setup in the super-class.
		// This will create the schedule grid & date navigation.
		super.setup();
	}

	@Override
	protected void onSelectQuiz(QuizBean quiz) {
		this.quiz = quiz;
		// Update the form
		updateForm();
	}

	@Override
	protected void onSelectRetake(RetakeBean retake) {
		this.retake = retake;
		// Update the form
		updateForm();
	}

	/**
	 * Update the registration form's components when the user's input changes.
	 */
	private void updateForm() {
		// Update labels
		if(quiz != null) {
			String sQuiz = String.valueOf(quiz.getID()) + " @" + getDateText(quiz.getDate());
			lblQuiz.setText("Quiz: " + sQuiz);
		} else {
			lblQuiz.setText("Quiz: Select a quiz");
		}
		if(retake != null) {
			String sRetake = String.valueOf(retake.getID()) + " @" + getDateText(retake.getDate());
			lblRetake.setText("Retake: " + sRetake);
		} else {
			lblRetake.setText("Retake: Select a retake");
		}
		// Set disabled state of the button based on validity of inputs.
		// - Raw input checks
		boolean disabled = quiz == null || retake == null || txtName.getText().isEmpty();
		if(!disabled) {
			// Button is set to not be disabled, so the raw-input is OK.
			// Need to do a date-check on the quiz/retake relation.
			int diff = retake.getDate().getDayOfYear() - quiz.getDate().getDayOfYear();
			CourseBean c = wrap.getCourse();
			LocalDate end = quiz.getDate().plusDays(daysAvailable);
			int allowed = daysAvailable;
			// Skip range check, add one week if end-date is in skip-time.
			if(end.isAfter(c.getStartSkip()) && end.isBefore(c.getEndSkip())) {
				allowed += 7;
			}
			// Ensure selected items are within the allowed number of days.
			disabled = diff < 0 || diff > allowed;
		}
		btnRegister.setDisable(disabled);
	}

	/**
	 * Reset inputs and tell the user that their appointment was successfully registered.
	 */
	private void resetOnSuccess() {
		// Success popup
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Successfully registered");
		alert.setHeaderText("Your appointment has been scheduled");
		alert.setContentText("Please arrive in time to finish the quiz before the end of the " +
				"retake period.\nIf you cannot make it, please cancel by sending email to " +
				"your professor.\n\n" + "Your retake session is at " + getDateText(retake.getDate
				()) + "-" + getTimeText(retake.getTime()) + " in " + retake.getLocation());
		alert.showAndWait();
		// Reset inputs
		txtName.setText("");
		quiz = null;
		retake = null;
		updateForm();
		updateSelectionCSS();
	}

	/**
	 * Update the 'selected' pseudo-class of the slots. This more clearly shows the user which
	 * item they have selected since they are more vibrant.
	 */
	@Override
	protected void updateSelectionCSS() {
		slots.forEach(s -> s.setSelected(s.getQuiz().equals(quiz) || s.getQuiz().equals(retake)));
	}
}
