package quizretakes.ui;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import quizretakes.DataWrapper;
import quizretakes.bean.*;

/**
 * UI element that allows users to visually select which quiz to retake at which retake session.
 * One week at a time is shown <i>(Users can move through weeks)</i>.
 *
 * @author Matt Coley
 */
public class WeekView extends VBox {
	// Time formats
	/**
	 * Sample: 2/7/19
	 */
	private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("M/d/YY");
	/**
	 * Sample: 8:30 AM
	 */
	private static final DateTimeFormatter FMT_TIME = DateTimeFormatter.ofPattern("h:mm a");
	/**
	 * All slots will be 30 minutes long.
	 */
	private static final Duration SLOT_LEN = Duration.ofMinutes(30);
	// Range of times to display, because showing times that aren't reasonable is pointless
	/**
	 * Time of day that the view starts at.
	 */
	private static final LocalTime timeStart = LocalTime.of(7, 0);
	/**
	 * Time of day that the view ends at.
	 */
	private static final LocalTime timeEnd = LocalTime.of(19, 0);
	/**
	 * Wrapper holding all course information.
	 */
	private final DataWrapper wrap;
	/**
	 * Currently displayed week in the {@link #grid}.
	 */
	private LocalDate day;
	/**
	 * The selected quiz to retake.
	 */
	private QuizBean quiz;
	/**
	 * The selected retake session to attend.
	 */
	private RetakeBean retake;
	/**
	 * The number of days after a quiz a retake is allowed.
	 */
	private final int daysAvailable;
	/**
	 * Grid to display quizes and retakes on.
	 */
	private final GridPane grid = new GridPane();
	/**
	 * Label displaying the selected {@link #quiz}.
	 */
	private final Label lblQuiz = new Label();
	/**
	 * Label displaying the selected {@link #retake}.
	 */
	private final Label lblRetake = new Label();
	/**
	 * Text input for the student's name for scheduling.
	 */
	private final TextField txtName = new TextField();
	/**
	 * Button to register the {@link #txtName student} for their retake.
	 */
	private final Button btnRegister = new Button("Register");

	public WeekView(DataWrapper wrap) {
		this.wrap = wrap;
		setup();
		repopulate();
		daysAvailable = wrap.getCourse().getRetakeDuration();
	}

	/**
	 * Configure all UI elements.
	 */
	private void setup() {
		// Setup navigation
		// - day to mark beginning of week
		// - can be moved forward/backwards in weeks
		day = LocalDate.now();
		day = day.minusDays(day.getDayOfWeek().getValue() - 1);
		TilePane nav = new TilePane(Orientation.HORIZONTAL);
		nav.setPadding(new Insets(5, 10, 5, 0));
		nav.setHgap(10.0);
		nav.setAlignment(Pos.CENTER);
		Button btnPrev = new Button("Previous");
		Button btnNext = new Button("  Next  ");
		Label lblCurrent = new Label(getCurrentDateText());
		btnPrev.setOnAction(e -> {
			// Go back a week
			day = day.minusDays(7);
			lblCurrent.setText(getCurrentDateText());
			repopulate();
		});
		btnNext.setOnAction(e -> {
			// Go forward a week
			day = day.plusDays(7);
			lblCurrent.setText(getCurrentDateText());
			repopulate();
		});
		nav.getChildren().addAll(btnPrev, lblCurrent, btnNext);
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
		// Set initial form text
		updateForm();
		// Add all components (grid is set up in 'repopulate')
		getChildren().addAll(nav, registerForm, new ScrollPane(grid));
	}

	/**
	 * Populate the grid with the week indicated by the selected {@link #day}.
	 */
	private void repopulate() {
		// Reset grid items
		grid.getChildren().clear();
		// Get week range to display
		LocalDate weekStart = day.minusDays(day.getDayOfWeek().getValue() - 1);
		LocalDate weekEnd = weekStart.plusDays(6);
		int row = 1;
		// Create grid column labels (date)
		for(LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
			Label lblDate = new Label(date.format(FMT_DATE));
			lblDate.setPadding(new Insets(1));
			lblDate.setTextAlignment(TextAlignment.CENTER);
			GridPane.setHalignment(lblDate, HPos.CENTER);
			grid.add(lblDate, date.getDayOfWeek().getValue(), 0);
		}
		// Create grid row labels (time)
		for(LocalDateTime time = weekStart.atTime(timeStart); !time.isAfter(weekStart.atTime
				(timeEnd)); time = time.plus(SLOT_LEN)) {
			Label lblSlotTime = new Label(getTimeText(time));
			GridPane.setHalignment(lblSlotTime, HPos.RIGHT);
			grid.add(lblSlotTime, 0, row);
			row++;
		}
		// Create grid entries (time-slots per day)
		for(LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
			row = 1;
			for(LocalDateTime time = date.atTime(timeStart); !time.isAfter(date.atTime(timeEnd));
				time = time.plus(SLOT_LEN)) {
				// Create the slot
				QuizSlot slot = new QuizSlot(time);
				Node view = slot.getView();
				grid.add(view, slot.getTime().getDayOfWeek().getValue(), row);
				row++;
				// Find matching quizzes at the given time. Apply to the slot if found.
				// Final to allow stream access to non-final variable "time".
				final LocalDateTime timeAlias = time;
				Optional<QuizBean> quizFound = Stream.concat(wrap.getQuizzes().stream(), wrap
						.getRetakes().stream()).filter(q -> match(q, timeAlias)).findAny();
				if(quizFound.isPresent()) {
					slot.setQuiz(quizFound.get());
					registerEvents(slot);
				}
			}
		}
	}

	/**
	 * Register click events so that the user can sign up for a quiz retake.
	 *
	 * @param slot
	 * 		The timeslot to register.
	 */
	private void registerEvents(QuizSlot slot) {
		slot.getView().setOnMouseClicked(e -> {
			if(slot.getQuiz() instanceof RetakeBean) {
				retake = (RetakeBean) slot.getQuiz();
			} else {
				quiz = slot.getQuiz();
			}
			updateForm();
		});
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
	}

	/**
	 * @return Formatted string of the current week date.
	 */
	private String getCurrentDateText() {
		return getDateText(day);
	}

	/**
	 * @param date
	 * 		Date to format.
	 *
	 * @return Formatted string of the given day.
	 */
	private String getDateText(LocalDate date) {
		return date.format(FMT_DATE);
	}

	/**
	 * @param time
	 * 		Time to format.
	 *
	 * @return Formatted string of the given time.
	 */
	private String getTimeText(LocalTime time) {
		return time.format(FMT_TIME);
	}

	/**
	 * @param time
	 * 		Time to format.
	 *
	 * @return Formatted string of the given time.
	 */
	private String getTimeText(LocalDateTime time) {
		return time.format(FMT_TIME);
	}

	/**
	 * Check if the given time matches the time the quiz is at.
	 *
	 * @param quiz
	 * 		The quiz instance.
	 * @param time
	 * 		An arbitrary time.
	 *
	 * @return {@code true} if the quiz time matches the given time. {@code false} otherwise.
	 */
	private static boolean match(QuizBean quiz, LocalDateTime time) {
		// Thankfully the time API implements equals in such a way that this works.
		// The time specified in XML must be in a duration as specified by the constant above.
		LocalDateTime quizTime = quiz.getDate().atTime(quiz.getTime());
		return quizTime.equals(time);
	}
}
