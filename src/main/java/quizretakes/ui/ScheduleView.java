package quizretakes.ui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import quizretakes.DataWrapper;
import quizretakes.bean.QuizBean;
import quizretakes.bean.RetakeBean;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * UI element that displays quizzes and retakes in a scheule-like display. One quiz and one
 * retake session can be selected at a time. One week at a time is shown <i>(Users can move
 * through weeks)</i>. Implementations will add additional functionality on top of this scheduler
 * via {@link #onSelectQuiz(QuizBean)} amd {@link #onSelectRetake(RetakeBean)}.
 *
 * @author Matt Coley
 */
public abstract class ScheduleView extends VBox {
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
	 * Currently displayed week in the {@link #grid}.
	 */
	private LocalDate day;
	/**
	 * Grid to display quizes and retakes on.
	 */
	private final GridPane grid = new GridPane();
	/**
	 * Easy access to all quiz-slots in the {@link #grid}.
	 */
	protected final Set<QuizSlot> slots = new HashSet<>();
	/**
	 * Wrapper holding all course information.
	 */
	protected final DataWrapper wrap;
	/**
	 * The selected quiz to retake.
	 */
	protected QuizBean quiz;
	/**
	 * The selected retake session to attend.
	 */
	protected RetakeBean retake;

	public ScheduleView(DataWrapper wrap) {
		this.wrap = wrap;
		setup();
		repopulate();
	}

	/**
	 * Configure all UI elements.
	 */
	protected void setup() {
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
		getChildren().addAll(nav, new ScrollPane(grid));
	}

	/**
	 * Populate the grid with the week indicated by the selected {@link #day}.
	 */
	private void repopulate() {
		// Reset grid items
		grid.getChildren().clear();
		slots.clear();
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
					slots.add(slot);
					slot.setQuiz(quizFound.get());
					registerEvents(slot);
				}
			}
		}
		updateSelectionCSS();
	}

	/**
	 * Register click events so that the user can sign up for a quiz retake.
	 *
	 * @param slot
	 * 		The timeslot to register.
	 */
	private void registerEvents(QuizSlot slot) {
		slot.getView().setOnMouseClicked(e -> {
			// Update selected quiz and retake elements
			if(slot.getQuiz() instanceof RetakeBean) {
				onSelectRetake((RetakeBean) slot.getQuiz());
			} else {
				onSelectQuiz(slot.getQuiz());
			}
			// Update selection css pseudo classes of slots
			updateSelectionCSS();
		});
	}

	/**
	 * Called when a slot holding a QuizBean is selected.
	 *
	 * @param quiz
	 * 		Quiz in the slot.
	 */
	protected abstract void onSelectQuiz(QuizBean quiz);

	/**
	 * Called when a slot holding a RetakeBean is selected.
	 *
	 * @param retake
	 * 		Retake in the slot.
	 */
	protected abstract void onSelectRetake(RetakeBean retake);

	/**
	 * Update the 'selected' pseudo-class of the slots. This more clearly shows the user which
	 * item they have selected since they are more vibrant.
	 */
	protected void updateSelectionCSS() {
		slots.forEach(s -> s.setSelected(s.getQuiz().equals(quiz) || s.getQuiz().equals(retake)));
	}

	/**
	 * @return Formatted string of the current week date.
	 */
	protected String getCurrentDateText() {
		return getDateText(day);
	}

	/**
	 * @param date
	 * 		Date to format.
	 *
	 * @return Formatted string of the given day.
	 */
	protected String getDateText(LocalDate date) {
		return date.format(FMT_DATE);
	}

	/**
	 * @param time
	 * 		Time to format.
	 *
	 * @return Formatted string of the given time.
	 */
	protected String getTimeText(LocalTime time) {
		return time.format(FMT_TIME);
	}

	/**
	 * @param time
	 * 		Time to format.
	 *
	 * @return Formatted string of the given time.
	 */
	protected String getTimeText(LocalDateTime time) {
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
