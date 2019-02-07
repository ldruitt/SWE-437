package quizretakes.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RetakeBean extends QuizBean {
	private final String location;

	public RetakeBean(int ID, String location, int month, int day, int hour, int minute) {
		super(ID, month, day, hour, minute);
		this.location = location;
	}
}
