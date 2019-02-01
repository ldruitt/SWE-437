package quizretakes.bean;

import lombok.Data;

import java.time.*;

/**
 * This bean holds information about a quiz retake session
 *
 * @author Jeff Offutt
 */
/* *****************************************
<retakes>
  <retake>
    <id>1</id> <!-- Should be unique and in order -->
    <location>Inn 204</location> --String, building & room
    <date>
      <month>2</month> --01..12
      <day>7</day> --1..31
      <hour>10</hour> --0..23
      <minute>00</minute> --> 0-59
    </date>
  </retake>
  <retake>
    <id>2</id>
...
</retakes>
***************************************** */

@Data public class RetakeBean implements Comparable<RetakeBean> {
	private final int ID;
	private final String location;
	private final LocalDate date;
	private final LocalTime time;

	public RetakeBean(int ID, String location, int month, int day, int hour, int minute) {
		this.ID = ID;
		this.location = location;
		int year = Year.now().getValue();
		date = LocalDate.of(year, month, day);
		time = LocalTime.of(hour, minute);
	}

	@Override
	public int compareTo(RetakeBean quizR) {
		return this.ID - quizR.ID;
	}

	@Override
	public String toString() {
		return ID + ": " + location + ": " + date.toString() + ": " + date
                .getDayOfWeek() + ": " + time.toString();
	}

	// Date methods
	public Month getMonth() {
		return date.getMonth();
	}
	public int getMonthNum() {
		return date.getMonthValue();
	}
	public DayOfWeek getDayOfWeek() {
		return date.getDayOfWeek();
	}
	public String dateAsString() {
		return date.toString();
	}

	// Time methods
	public String timeAsString() {
		return time.toString();
	}
}
