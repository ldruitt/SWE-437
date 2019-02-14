package quizretakes.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import quizretakes.DataWrapper;
import quizretakes.bean.AppointmentBean;
import quizretakes.bean.QuizBean;
import quizretakes.bean.RetakeBean;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * UI element that allows staff to view which retakes are scheduled for which quizzes and at
 * which retake sessions. Selecting a quiz-slot shows all of its retakes. Selecting a retake-slot
 * shows all of the quizzes being retaken during it.
 *
 * @author Matt Coley
 */
public class StaffView extends ScheduleView {
	/**
	 * Size of the content {@link #scroll}
	 */
	private final static int SCROLL_SIZE = 182;
	/**
	 * Scaled avatar size in pixels
	 */
	private final static int AVATAR_SIZE = 16;
	/**
	 * Resource path of custom avatars
	 */
	private final static String AVATAR_PATH = "/assets/avatars/%s.png";
	/**
	 * Resource path of default avatar. Used when a custom avatar does not exist.
	 */
	private final static String AVATAR_PATH_DEFAULT = "/assets/avatar.png";
	/**
	 * Pane that displays the students in a retake session or retaking a given quiz
	 */
	private ScrollPane scroll;

	public StaffView(DataWrapper wrap) {
		super(wrap);
	}

	@Override
	protected void setup() {
		// Initialize components
		// - Initial message to indicate action to take
		Label lblTemp = new Label("Select a quiz to display students retaking that quiz.\nSelect "
				+ "a retake session to show all the students in that section.");
		scroll = new ScrollPane();
		scroll.setContent(lblTemp);
		scroll.setMinHeight(SCROLL_SIZE);
		scroll.setMaxHeight(SCROLL_SIZE);
		// Center the label in the scroll-region.
		// - Need to run on JFX thread so properties are initialized
		Platform.runLater(() -> {
			scroll.setFitToWidth(true);
			scroll.setFitToHeight(true);
			lblTemp.setTranslateX(scroll.getWidth() / 2 - lblTemp.getWidth() / 2);
			lblTemp.setTranslateY(scroll.getHeight() / 2 - lblTemp.getHeight() / 2);
		});
		// Add to the UI
		getChildren().add(scroll);
		// Setup in the super-class.
		// This will create the schedule grid & date navigation.
		super.setup();
	}

	@Override
	protected void onSelectQuiz(QuizBean quiz) {
		// Select the quiz, unselect retake if selected
		super.onSelectQuiz(quiz);
		super.onSelectRetake(null);
		// Update scroll-region with information related to the given quiz
		// - What sessions the quiz is being retaken in
		List<AppointmentBean> appts = getAppointments(a -> a.getQuizID() == quiz.getID());
		setupList(appts);
	}

	@Override
	protected void onSelectRetake(RetakeBean retake) {
		// Select the retake, unselect quiz if selected
		super.onSelectQuiz(null);
		super.onSelectRetake(retake);
		// Update scroll-region with information related to the given retake session
		// - What quizzes are being retaken by which students
		List<AppointmentBean> appts = getAppointments(a -> a.getRetakeID() == retake.getID());
		setupList(appts);
	}

	/**
	 * @param predicate
	 * 		Filter to apply.
	 *
	 * @return List of appointments that match the given filter.
	 */
	private List<AppointmentBean> getAppointments(Predicate<AppointmentBean> predicate) {
		return wrap.getAppointments().stream().filter(predicate).collect(Collectors.toList());
	}

	/**
	 * Display a list of the given appointments.
	 *
	 * @param appts
	 * 		List of appointments.
	 */
	private void setupList(List<AppointmentBean> appts) {
		// Create ListView of appointments
		ObservableList<AppointmentBean> items = FXCollections.observableList(appts);
		ListView<AppointmentBean> list = new ListView<>(items);
		list.setCellFactory(l -> new AvatarCell());
		// Update content scrollpane
		scroll.setContent(list);
	}

	/**
	 * ListCell that displays an appointment.
	 */
	class AvatarCell extends ListCell<AppointmentBean> {
		private static final String fmt = "%-13s - Quiz-%d at: %s, %s:%s";

		@Override
		protected void updateItem(AppointmentBean item, boolean empty) {
			super.updateItem(item, empty);
			// No text in label of super class
			if(empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				// Get the following info:
				// - Avatar of student, default image if no custom one exists
				// - Name of student
				// - Quiz to be retaken
				// - Location of retake
				Image avatar = null;
				String name = item.getName();
				int quizID = item.getQuizID();
				int retakeID = item.getRetakeID();
				// Get retake location
				Optional<RetakeBean> retake = wrap.getRetakes().get(retakeID);
				String location = retake.isPresent() ? retake.get().getLocation() : "?";
				String date = retake.isPresent() ? getDateText(retake.get().getDate()) : "?";
				String time = retake.isPresent() ? getTimeText(retake.get().getTime()) : "?";
				try {
					// Default image
					String file = AVATAR_PATH_DEFAULT;
					avatar = new Image(StaffView.class.getResourceAsStream(file));
					// Can't check if a resource exists in the classpath...
					// - so the default loads first
					// - if this succeeds we get a new avatar
					// - otherwise we get an NPE
					file = String.format(AVATAR_PATH, name.toLowerCase());
					avatar = new Image(StaffView.class.getResourceAsStream(file));
				} catch(Exception e) {
					// No avatar for the student
				}
				// Setup cell
				setText(String.format(fmt, name, quizID, location, date, time));
				ImageView view = new ImageView(avatar);
				view.setFitHeight(AVATAR_SIZE);
				view.setFitWidth(AVATAR_SIZE);
				setGraphic(view);
			}
		}
	}
}
