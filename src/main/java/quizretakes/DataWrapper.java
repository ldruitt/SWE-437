package quizretakes;

import lombok.Data;
import quizretakes.bean.AppointmentBean;
import quizretakes.bean.CourseBean;
import quizretakes.bean.QuizBean;
import quizretakes.bean.RetakeBean;

import java.util.List;

/**
 * Wrapper of all beans related to a course.
 *
 * @author Matt Coley
 */
@Data
public class DataWrapper {
	private final CourseBean course;
	private final Quizzes quizzes;
	private final Retakes retakes;
	private final List<AppointmentBean> appointments;

	public DataWrapper(CourseBean course, Quizzes quizzes, Retakes retakes, List<AppointmentBean>
			appointments) {
		this.course = course;
		this.quizzes = quizzes;
		this.retakes = retakes;
		this.appointments = appointments;
	}

	public void registerAppointment(QuizBean quiz, RetakeBean retake, String name) {
		// TODO: Write appointment to file
		System.out.println(retake.getID() + "," + quiz.getID() + "," + name);
	}
}