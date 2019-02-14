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

import java.net.URL;
import java.util.List;
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
	private final static int SCROLL_SIZE = 190;
	private final static int AVATAR_SIZE = 16;
	private final static String AVATAR_PATH = "assets/avatars/%s.png";
	private final static String AVATAR_PATH_DEFAULT = "assets/avatar.png";
	private ScrollPane scroll;

	public StaffView(DataWrapper wrap) {
		super(wrap);
	}

	@Override
	protected void setup() {
		// Initialize components
		// - Initial message to indicate action to take
		Label lblTemp = new Label("Select a quiz to display students retaking that quiz.\nSelect a retake session to show all the students in that section.");
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
		ObservableList<AppointmentBean> items = FXCollections.observableList(appts);
		ListView<AppointmentBean> list = new ListView<>(items);
		list.setCellFactory(l -> new AvatarCell());
		scroll.setContent(list);
	}

	/**
	 * ListCell that displays an appointment.
	 */
	static class AvatarCell extends ListCell<AppointmentBean> {
		@Override
		protected void updateItem(AppointmentBean item, boolean empty) {
			super.updateItem(item, empty);
			// No text in label of super class
			if(empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				// Graphic of student avatar, if one is present
				Image graphic = null;
				String name = item.getName();
				try {
					// Default image
					String file = AVATAR_PATH_DEFAULT;
					URL url = Thread.currentThread().getContextClassLoader().getResource(file);
					graphic = new Image(url.openStream());
					// Can't check if a resource exists in the classpath...
					// - so the default loads first
					// - if this succeeds we get a new avatar
					// - otherwise we get an NPE
					file = String.format(AVATAR_PATH, name.toLowerCase());
					url = Thread.currentThread().getContextClassLoader().getResource(file);
					graphic = new Image(url.openStream());
				} catch(Exception e) {}
				// Setup cell
				setText(name);
				ImageView view = new ImageView(graphic);
				view.setFitHeight(AVATAR_SIZE);
				view.setFitWidth(AVATAR_SIZE);
				setGraphic(view);
			}
		}
	}
}
