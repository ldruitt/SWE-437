package quizretakes.ui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import quizretakes.bean.BeanWrapper;
import quizretakes.bean.QuizBean;
import quizretakes.bean.RetakeBean;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

public class WeekView extends GridPane {
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

	public WeekView(BeanWrapper wrap) {
		// TODO: Allow user to tab to the next week
		LocalDate today = LocalDate.now();
		LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
		LocalDate weekEnd = weekStart.plusDays(6);
		int row = 1;
		// Create grid column labels (date)
		for(LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
			Label lblDate = new Label(date.format(FMT_DATE));
			lblDate.setPadding(new Insets(1));
			lblDate.setTextAlignment(TextAlignment.CENTER);
			GridPane.setHalignment(lblDate, HPos.CENTER);
			add(lblDate, date.getDayOfWeek().getValue(), 0);
		}
		// Create grid row labels (time)
		for(LocalDateTime time = weekStart.atTime(timeStart); !time.isAfter(weekStart.atTime
				(timeEnd)); time = time.plus(SLOT_LEN)) {
			Label lblSlotTime = new Label(time.format(FMT_TIME));
			GridPane.setHalignment(lblSlotTime, HPos.RIGHT);
			add(lblSlotTime, 0, row);
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
				add(view, slot.getTime().getDayOfWeek().getValue(), row);
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

	/**
	 * Register click events so that the user can sign up for a quiz retake.
	 *
	 * @param slot
	 * 		The timeslot to register.
	 */
	private static void registerEvents(QuizSlot slot) {
		// TODO: On-click:
		// - confirm they meant this class
		// - prompt name
		// - write appointment to disk
	}
}
