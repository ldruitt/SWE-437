package quizretakes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * @author Matt Coley
 * @author Loren Druitt
 */
public class HW4Tests {
	private final static int WEEK = 7;
	private final static String LOCATION = "Somewhere";
	// @formatter:off
	// regex for matching against quizschedule output
	private static final Pattern P_BASIC = Pattern.compile("(?<=Today is )(?<today>[A-Z, 0-9]+)\\D+(?<=, until )(?<max>[A-Z, 0-9]+)");
	private static final Pattern P_RETAKE = Pattern.compile("(?<=RETAKE: )(?<day>[A-Z, 0-9]+)(?=, at ).*(?<=at )(?<time>[0-9:]+).*(?<= in )(?<location>[a-zA-Z 0-9]+)");
	private static final Pattern P_QUIZ = Pattern.compile("(?<=\\) Quiz )(?<id>\\w+)(?= from).*(?<=from )(?<date>[A-Z, 0-9]+)");
	// @formatter:on
	// quizschedule objects / values
	private quizschedule sched;
	private courseBean course;
	private quizzes quizzes;
	private retakes retakes;
	private int daysAvailable;
	// Time values
	private LocalDate today = LocalDate.now();
	private LocalDate startSkip = today.plusDays(800);
	private LocalDate endSkip = startSkip.plusDays(7);
	// Utilities
	private Method print;
	private ByteArrayOutputStream baos;
	private PrintStream out;

	@Before
	public void setup() throws Exception {
		sched = new quizschedule();
		// Setup course structures
		String id = "swe437";
		String title = "Software Engineering";
		String retakeDuration = String.valueOf(daysAvailable = 14);
		String location = System.getProperty("user.dir");
		course = new courseBean(id, title, retakeDuration, startSkip, endSkip, location);
		quizzes = new quizzes();
		retakes = new retakes();
		// Reflection access
		print = quizschedule.class.getDeclaredMethod("printQuizScheduleForm", quizzes.class,
				retakes.class, courseBean.class);
		print.setAccessible(true);
		// Override output
		out = System.out;
		baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(new PrintStream(ps, true));
	}

	@After
	public void cleanup() {
		// Reset 'System.out' to its original value
		// - Units may be of separate instances, but use the same JVM
		// - This cleanup is required
		System.setOut(out);
	}

	/**
	 * Print the schedule
	 *
	 * @return Redirected System.out call values
	 */
	private String invoke() {
		try {
			print.invoke(sched, quizzes, retakes, course);
		} catch(Exception e) {
			e.printStackTrace();
			fail("Invoke of 'printQuizScheduleForm' failed: " + e.getMessage());
		}
		// Return 'System.out' output... Substring away the greeting message.
		String out = baos.toString();
		out = out.substring(out.indexOf("Today is"));
		this.out.println(out); // print to actual 'System.out'
		return out;
	}

	/**
	 * Convert the date format used in quizschedule to a LocalDate instance.
	 *
	 * @param str
	 * 		Date in the format: DAY, MONTH DAY-VALUE
	 *
	 * @return Date instance of given date.
	 */
	private LocalDate stringToDate(String str) {
		// DAY, MONTH DAY-VALUE
		String[] parts = str.split(" ");
		int year = today.getYear();
		int month = -1;
		int day = Integer.parseInt(parts[2]);
		//@formatter:off
		switch(parts[1]) {
			case "JANUARY": month = 1; break;
			case "FEBRUARY": month = 2; break;
			case "MARCH": month = 3; break;
			case "APRIL": month = 4; break;
			case "MAY": month = 5; break;
			case "JUNE": month = 6; break;
			case "JULY": month = 7; break;
			case "AUGUST": month = 8; break;
			case "SEPTEMBER": month = 9; break;
			case "OCTOBER": month = 10; break;
			case "NOVEMBER": month = 11; break;
			case "DECEMBER": month = 12; break;
		}
		//@formatter:on
		return LocalDate.of(year, month, day);
	}

	// ======================================================================== //

	/**
	 * Don't print retake dates that have already passed.
	 *
	 *The observability of the results of this test are high because a tester can detect the behavior 
	 *of the program when a past retake is the only available option based on the output of this test.
	 *The controllability of this test was high as well as it is possible to set a quiz and retake in 
	 *the past and to use this as input. 
	 */
	@Test
	public void testOutdated() {
		LocalDate date = today.minusDays(WEEK);
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = 10;
		int min = 30;
		quizzes.addQuiz(new quizBean(0, month, day, hour, min));
		date = date.plusDays(1);
		month = date.getMonthValue();
		day = date.getDayOfMonth();
		retakes.addRetake(new retakeBean(0, LOCATION, month, day, hour, min));
		// Run assertions on 'System.out' calls
		String strOut = invoke();
		Matcher m = P_BASIC.matcher(strOut);
		if(m.find()) {
			String strToday = m.group("today");
			String strMax = m.group("max");
			// Assert days are the same
			assertEquals(today, stringToDate(strToday));
			assertEquals(today.plusDays(daysAvailable), stringToDate(strMax));
			// Assert nothing shows after "Currently scheduling quizzes for the next two weeks"
			// - Shows that outdated items are not shown
			String post = strOut.substring(strOut.indexOf(strMax) + strMax.length());
			assertTrue("Content after max-date, should be none", post.trim().isEmpty());
		} else {
			fail("Could not match output against regex");
		}
	}

	/**
	 * Print retake sessions that are today or later, but not quizzes that occur after the retake
	 * (You can't retake a quiz that hasn't occurred)
	 *
	 *There is high observability for this case because the expected result would be shown through the 
	 *programs behavior when a retake may be scheduled before a quiz which means it is observable to the tester.
	 *The inputs for this case are highly controllable as the quiz retake schedule may be manipulated to reflect a 
	 *schedule with the only retake being before the only quiz.
	 */
	@Test
	public void testRetakeBeforeQuiz() {
		LocalDate date = today.plusDays(2);
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = 10;
		int min = 30;
		quizzes.addQuiz(new quizBean(0, month, day, hour, min));
		date = date.minusDays(1);
		month = date.getMonthValue();
		day = date.getDayOfMonth();
		retakes.addRetake(new retakeBean(0, LOCATION, month, day, hour, min));
		// Run assertions on 'System.out' calls
		String strOut = invoke();
		Matcher m = P_RETAKE.matcher(strOut);
		if(m.find()) {
			String strDay = m.group("day");
			String strTime = m.group("time");
			String strLoc = m.group("location");
			// Assert day/time/location are the same
			assertEquals(date, stringToDate(strDay));
			assertEquals(hour + ":" + min, strTime);
			assertEquals(LOCATION, strLoc);
			// Assert nothing shows after the retake location
			// - Shows that no quizzes can be taken in this retake
			String post = strOut.substring(strOut.indexOf(strLoc) + strLoc.length());
			assertTrue("Content after location, should be none", post.trim().isEmpty());
		} else {
			fail("Could not match output against regex");
		}
	}

	/**
	 * You shouldn't be able to retake a quiz at the same time the quiz is given...
	 * (but quizschedule allows this)
	 *
	 *The observability for this test case is high as the effect of the input is simply a lack of output assuming 
	 *the program is behaving with the logic that one cannot take a retake at the same time one is participating in a current quiz
	 *While the input for this test case is highly controllable and there is a way to have this case run in 
	 *the given code, it should be more limited in its ability to schedule a retake at the same time as a quiz
	 */
	@Test
	public void testRetakeSameTime() {
		LocalDate date = today;
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = 10;
		int min = 30;
		quizzes.addQuiz(new quizBean(0, month, day, hour, min));
		retakes.addRetake(new retakeBean(0, LOCATION, month, day, hour, min));
		// Run assertions on 'System.out' calls
		String strOut = invoke();
		if(P_RETAKE.matcher(strOut).find()) {
			// Retake session is shown, but is the quiz also shown?
			// The retake session is the SAME TIME as the quiz... that shouldn't be allowed.
			if(P_QUIZ.matcher(strOut).find()) {
				fail("Should not allow quiz to be retaken at the original time of the quiz");
			}
		} else {
			fail("Missing retake session");
		}
	}

	/**
	 * Print out the retake/quiz combo for a retake later in the day.
	 *This has a high observability because the programs response can be tested against expected behavior
	 *when a retake is scheduled later in the same day.
	 *The controllability for this test case is high as the retake schedule could easily contain this 
	 *combination of data if desired.
	 */
	@Test
	public void testRetakeLaterInDay() {
		LocalDate date = today;
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = 10;
		int min = 30;
		quizzes.addQuiz(new quizBean(0, month, day, hour, min));
		retakes.addRetake(new retakeBean(0, LOCATION, month, day, hour + 3, min));
		// Run assertions on 'System.out' calls
		String strOut = invoke();
		if(P_RETAKE.matcher(strOut).find()) {
			// Retake session is shown, but is the quiz also shown?
			// The retake session is later in the day, so it should be shown
			if(!P_QUIZ.matcher(strOut).find()) {
				fail("Missing option for quiz under retake session");
			}
		} else {
			fail("Missing retake session");
		}
	}

	/**
	 * Print out the retake/quiz combo for a retake tomorrow.
	 *
	 *This test case is highly observable and will return a result that will show the programs behavior when a 
	 *retake is scheduled the next day to the tester.
	 *The controllability is high for this case because we can manipulate the retake schedule to contain this
	 *information.
	 */
	@Test
	public void testRetakeTomorrow() {
		LocalDate date = today;
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = 10;
		int min = 30;
		quizzes.addQuiz(new quizBean(0, month, day, hour, min));
		date = date.plusDays(1);
		month = date.getMonthValue();
		day = date.getDayOfMonth();
		retakes.addRetake(new retakeBean(0, LOCATION, month, day, hour, min));
		// Run assertions on 'System.out' calls
		String strOut = invoke();
		if(P_RETAKE.matcher(strOut).find()) {
			// Retake session is shown, but is the quiz also shown?
			// The retake session is the next day, so it should be shown
			if(!P_QUIZ.matcher(strOut).find()) {
				fail("Missing option for quiz under retake session");
			}
		} else {
			fail("Missing retake session");
		}
	}

	/**
	 * Print out the retake/quiz combo for the last day possible.
	 *
	 *This test case is highly observable and will return a result that will show the programs behavior when 
	 *a retake is at the limit of available days to the tester.
	 *The controllability is high for this case because we can manipulate the retake schedule to contain this
	 *information.
	 */
	@Test
	public void testRetakeDaysAvailable() {
		LocalDate date = today;
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = 10;
		int min = 30;
		quizzes.addQuiz(new quizBean(0, month, day, hour, min));
		// +daysAvailable later
		date = date.plusDays(daysAvailable);
		month = date.getMonthValue();
		day = date.getDayOfMonth();
		retakes.addRetake(new retakeBean(0, LOCATION, month, day, hour, min));
		// Run assertions on 'System.out' calls
		String strOut = invoke();
		if(P_RETAKE.matcher(strOut).find()) {
			// Retake session is shown, but is the quiz also shown?
			// The retake session is on the last possible day to retake the quiz.
			// It should be shown.
			if(!P_QUIZ.matcher(strOut).find()) {
				fail("Missing option for quiz under retake session");
			}
		} else {
			fail("Missing retake session");
		}
	}

	/**
	 * Don't print retake dates that exceed the days allowed.
	 * Since you can't schedule a retake ahead-of-time, testing for today+daysAvailable is valid.
	 *The results of this test are highly observable because the expected behavior of the program when a retake 
	 *is only scheduled after the boundary of the days available will be revealed to the tester.
	 *The controllability is high for this test because we can easily add this data to the retake schedule.
	 */
	@Test
	public void testRetakeDaysAvailablePlusOne() {
		LocalDate date = today;
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = 10;
		int min = 30;
		quizzes.addQuiz(new quizBean(0, month, day, hour, min));
		// +daysAvailable later + 1 more day
		date = date.plusDays(daysAvailable + 1);
		month = date.getMonthValue();
		day = date.getDayOfMonth();
		retakes.addRetake(new retakeBean(0, LOCATION, month, day, hour, min));
		// Run assertions on 'System.out' calls
		String strOut = invoke();
		// The retake session is just past the last possible day, so it should not be shown.
		// In the future it may be shown, but since there is no possible way for anything to be
		// validly scheduled for it right now, it should just be hidden.
		if(P_RETAKE.matcher(strOut).find()) {
			fail("Showing retake-session past allowed retake date.");
		}
	}
	/*
	*This test case is highly observable because the expected behavior can be compared to the actual behavior
	*when a skip week is scheduled between quizzes and retakes.
	*The inputs for this test were highly controllable, the skip week, test dates, and retake dates can all be 
	*set by the tester.
	*/
	@Test
	public void testSkippableSkip() {
		LocalDate date = today;
		// - Skip region is between quiz and retake dates
		course.setStartSkip(date.plusDays(2));
		course.setEndSkip(date.plusDays(3));
		// Quiz date
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = 10;
		int min = 30;
		quizzes.addQuiz(new quizBean(0, month, day, hour, min));
		// Retake a week later
		date = date.plusDays(WEEK);
		month = date.getMonthValue();
		day = date.getDayOfMonth();
		retakes.addRetake(new retakeBean(0, LOCATION, month, day, hour, min));
		// Run assertions on 'System.out' calls
		String strOut = invoke();
		if(P_RETAKE.matcher(strOut).find()) {
			// Retake session is shown, but is the quiz also shown?
			// The retake session is beyond the skip-region so it should be shown.
			Matcher m = P_QUIZ.matcher(strOut);
			if(m.find()) {
				// Check that the quiz is marked as today
				String strDay = m.group("date");
				assertEquals(today, stringToDate(strDay));
			} else {
				fail("Missing option for quiz under retake session");
			}
		} else {
			fail("Missing retake session");
		}
	}

	/**
	 * Retake is more than +daysAvailable away, but there is a skip-region from next +7 days to
	 * +14 days. So the retake is scheduled 3 weeks instead.
	 * <br>
	 * The user should be allowed to sign up... but isn't allowed. Fail.
	 *
	 *The observability is high for this test as it should be clear from the results how the process executes 
	 *and handles a retake possibly being scheduled after a skip week interferes with the retake schedule.
	 *The controllability is high because of the ability to set the quiz dates, retake schedule, and skip week.
	 */
	@Test
	public void testRetakeBeyondSkip() {
		LocalDate date = today;
		// Skip region is next-week until daysAvailable from today
		course.setStartSkip(date.plusDays(WEEK));
		course.setEndSkip(date.plusDays(WEEK * 2));
		// Quiz date today
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = 10;
		int min = 30;
		quizzes.addQuiz(new quizBean(0, month, day, hour, min));
		// Retake a is beyond daysAvailable
		date = date.plusDays(daysAvailable + 2);
		month = date.getMonthValue();
		day = date.getDayOfMonth();
		retakes.addRetake(new retakeBean(0, LOCATION, month, day, hour, min));
		// Run assertions on 'System.out' calls
		String strOut = invoke();
		if(P_RETAKE.matcher(strOut).find()) {
			// Retake session is shown, but is the quiz also shown?
			// The quiz should be shown because of the skip-region blocking off the 2nd half of
			// the possible retake time. Thus, another week should be allocated.
			if(!P_QUIZ.matcher(strOut).find()) {
				fail("Missing option for quiz under retake session. Should be shown due to " +
						"skip-region time");
			}
		} else {
			fail("Missing retake session");
		}
	}

	/**
	 * The retake session is during the skip-region... You shouldn't be able to sign up for this,
	 * but quizschedule allows you to do so... Fail.
	 *
	 *This test has a high observability as we can perceive the result of the output when a quiz might be 
	 *scheduled during a skip week
	 *The controllability for this test was high as the program allowed us to do schedule a retake during a
	 *skip week, however, logically it should not allow this behavior.
	 */
	@Test
	public void testRetakeDuringSkip() {
		// - Retake later so skip region gives an extra week
		LocalDate date = today;
		// Skip region is next-week until daysAvailable from today
		course.setStartSkip(date.plusDays(WEEK));
		course.setEndSkip(date.plusDays(WEEK * 2));
		// Quiz date today
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		int hour = 10;
		int min = 30;
		quizzes.addQuiz(new quizBean(0, month, day, hour, min));
		// Retake a is beyond daysAvailable
		date = date.plusDays(WEEK + 1);
		month = date.getMonthValue();
		day = date.getDayOfMonth();
		retakes.addRetake(new retakeBean(0, LOCATION, month, day, hour, min));
		// Run assertions on 'System.out' calls
		String strOut = invoke();
		if(P_RETAKE.matcher(strOut).find()) {
			fail("Retake sessions should not be allowed during the skip-regiond");
		}
	}
}
