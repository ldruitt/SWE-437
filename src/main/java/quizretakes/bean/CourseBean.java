//JO, 10-Jan-2019
// Stores course information in a bean
package quizretakes.bean;

import lombok.Data;

import java.time.*;

/**
 * This bean holds information about a course
 *
 * @author Jeff Offutt
 */
/* *****************************************
<course>
  <courseID>swe437</courseID> <!-- Used in file names and displayed -->
  <courseTitle>Software testing</courseTitle> <!-- String to be displayed -->
  <retakeDuration>14</retakeDuration> <!-- int, number of days retake is possible -->
  <startSkipMonth>1</startSkipMonth>
  <startSkipDay>21</startSkipDay>
  <endSkipMonth>1</endSkipMonth>
  <endSkipDay>25</endSkipDay>
  <dataLocation>/var/www/CS/webapps/offutt/WEB-INF/data/</dataLocation>
</course>
***************************************** */

@Data
public class CourseBean {
	private String courseID;
	private String courseTitle;
	private String retakeDuration;
	private LocalDate startSkip;
	private LocalDate endSkip;

	public CourseBean(String courseID, String courseTitle, String retakeDuration, LocalDate
			startSkip, LocalDate endSkip) {
		this.courseID = courseID;
		this.courseTitle = courseTitle;
		this.retakeDuration = retakeDuration;
		this.startSkip = startSkip;
		this.endSkip = endSkip;
	}
}
