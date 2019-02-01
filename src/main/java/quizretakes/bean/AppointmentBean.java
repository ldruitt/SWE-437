package quizretakes.bean;

import lombok.Data;

/**
 * This bean holds a single quiz retake appointment
 *
 * @author Jeff Offutt
 */

@Data
public class AppointmentBean {
	private final int quizID;
	private final int retakeID;
	private final String name;

	public AppointmentBean(int retakeID, int quizID, String name) {
		this.retakeID = retakeID;
		this.quizID = quizID;
		this.name = name;
	}

	@Override
	public String toString() {
		return retakeID + ":" + quizID + ":" + name;
	}
}
