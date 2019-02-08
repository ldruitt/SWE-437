package quizretakes.bean;

import lombok.Data;

import java.time.*;

/**
 * This bean holds information about a quiz
 *
 * @author Jeff Offutt
 */
/* *****************************************
<quizzes>
  <quiz>
    <id>1</id> --integer > 0
    <date>
      <month>1</month> --01..12
      <day>10</day> --1..31
      <hour>15</hour> --0..23
      <minute>30</minute> --> 0-59
    </date>
  </quiz>
  <quiz>
    <id>2</id>
...
</quizzes>
***************************************** */

@Data
public class QuizBean implements Comparable<QuizBean> {
	private final int ID;
	private final LocalDate date;
	private final LocalTime time;

	public QuizBean(int quizID, int month, int day, int hour, int minute) {
		ID = quizID;
		int year = Year.now().getValue();
		date = LocalDate.of(year, month, day);
		time = LocalTime.of(hour, minute);
	}

	@Override
	public int compareTo(QuizBean quizB) {
		return this.ID - quizB.ID;
	}

	@Override
	public String toString() {
		return ID + ": " + date.toString() + ": " + date.getDayOfWeek() + ": " + time.toString();
	}
}
