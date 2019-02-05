package quizretakes.bean;

import lombok.Data;
import quizretakes.Quizzes;
import quizretakes.Retakes;

import java.util.List;

@Data
public class BeanWrapper {
	private final CourseBean course;
	private final Quizzes quizzes;
	private final Retakes retakes;
	private final List<AppointmentBean> appointments;

	public BeanWrapper(CourseBean course, Quizzes quizzes, Retakes retakes,
					   List<AppointmentBean> appointments) {
		this.course = course;
		this.quizzes = quizzes;
		this.retakes = retakes;
		this.appointments = appointments;
	}
}