package quizretakes;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import quizretakes.bean.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.*;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.io.*;

/**
 * Course XML data utilities.
 */
public class IOUtils {
	/**
	 * Document parsing factory.
	 */
	private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
	/**
	 * Separator used for appointment information.
	 */
	private static final String SEPARATOR = ",";

	/**
	 * Read all information of a given course.
	 * <ul>
	 * <li>Couse summary</li>
	 * <li>Quizzes</li>
	 * <li>Quiz retakes</li>
	 * <li>Retake appointments</li>
	 * </ul>
	 *
	 * @param courseID
	 * 		Course to read data of.
	 *
	 * @return Wrapper of course data.  {@code null} if the course files could not be located.
	 */
	public static DataWrapper load(String courseID) {
		// File name constants
		String baseCourse = "course";
		String baseQuizzes = "quiz-orig";
		String baseRetakes = "quiz-retakes";
		String baseAppts = "quiz-appts";

		// Load the course info
		CourseBean course;
		String courseFileName = baseCourse + "-" + courseID + ".xml";
		try {
			course = IOUtils.course(courseFileName);
		} catch(Exception ex) {
			return null;
		}

		// Filenames to be built from above and the courseID
		String quizzesFileName = baseQuizzes + "-" + courseID + ".xml";
		String retakesFileName = baseRetakes + "-" + courseID + ".xml";
		String apptsFileName = baseAppts + "-" + courseID + ".txt";

		// Load the quizzes, retakes, and current appointments
		Quizzes quizList;
		Retakes retakesList;
		List<AppointmentBean> appointments;

		try {
			// Read the files and put in wrapper bean
			quizList = IOUtils.quizzes(quizzesFileName);
			retakesList = IOUtils.retakes(retakesFileName);
			appointments = IOUtils.appointments(apptsFileName);
			return new DataWrapper(course, quizList, retakesList, appointments);
		} catch(Exception ex) {
			return null;
		}
	}

	private static List<AppointmentBean> appointments(String filename) throws IOException {
		List<AppointmentBean> appts = new ArrayList<>();
		File file = new File(filename);
		if(!file.exists()) {
			System.out.println("No appointments file to read from!");
			file.createNewFile();
		} else {
			FileReader fw = new FileReader(file.getAbsoluteFile());
			BufferedReader bw = new BufferedReader(fw);
			String line;
			while((line = bw.readLine()) != null) {
				String[] s = line.split(SEPARATOR);
				appts.add(new AppointmentBean(Integer.parseInt(s[0]), Integer.parseInt(s[1]),
						s[2]));
			}
			bw.close();
		}
		return appts;
	}

	private static Retakes retakes(String filename) throws IOException,
			ParserConfigurationException, SAXException {
		Retakes retakeList = new Retakes();
		RetakeBean retake;

		DocumentBuilder builder = FACTORY.newDocumentBuilder();
		Document document = builder.parse(IOUtils.class.getResourceAsStream("/" + filename));

		// Get all the nodes
		NodeList nodeList = document.getDocumentElement().getChildNodes();
		for(int i = 0; i < nodeList.getLength(); i++) {
			// XML structure is simple--a bunch of quizzes
			// Not validating the data values
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;

				// retake IDs should be unique
				int ID = Integer.parseInt(getValue(elem, "id"));
				// Location is a string (building and room probably)
				String location = getValue(elem, "location");
				// month is an integer 1..12
				int month = Integer.parseInt(getValue(elem, "month"));
				// day is integer 1..31
				int day = Integer.parseInt(getValue(elem, "day"));
				// hour is integer 0..23
				int hour = Integer.parseInt(getValue(elem, "hour"));
				// minute is integer 0..59
				int minute = Integer.parseInt(getValue(elem, "minute"));
				// Put one XML record into a bean and add it to the list
				retake = new RetakeBean(ID, location, month, day, hour, minute);
				retakeList.addRetake(retake);
			}
		}
		// XML file may not be sorted
		retakeList.sort();
		return (retakeList);
	}

	private static CourseBean course(String filename) throws IOException,
			ParserConfigurationException, SAXException {
		CourseBean course = null;

		DocumentBuilder builder = FACTORY.newDocumentBuilder();
		Document document = builder.parse(IOUtils.class.getResourceAsStream("/" + filename));

		// Get all the nodes
		NodeList nodeList = document.getDocumentElement().getChildNodes();
		for(int i = 0; i < nodeList.getLength(); i++) {
			// XML structure is simple--6 elements
			// Not validating the data values
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;

				// quiz IDs should be unique
				String courseID = getValue(elem, "courseID");
				String courseTitle = getValue(elem, "courseTitle");
				int retakeDuration = Integer.parseInt(getValue(elem, "retakeDuration"));
				// startSkipMonth is an integer 1..12
				int startSkipMonth = Integer.parseInt(getValue(elem, "startSkipMonth"));
				// startSkipDay is integer 1..31
				int startSkipDay = Integer.parseInt(getValue(elem, "startSkipDay"));
				// endSkipMonth is an integer 1..12
				int endSkipMonth = Integer.parseInt(getValue(elem, "endSkipMonth"));
				// endSkipDay is integer 1..31
				int endSkipDay = Integer.parseInt(getValue(elem, "endSkipDay"));

				int year = Year.now().getValue();
				LocalDate startSkip = LocalDate.of(year, startSkipMonth, startSkipDay);
				LocalDate endSkip = LocalDate.of(year, endSkipMonth, endSkipDay);

				course = new CourseBean(courseID, courseTitle, retakeDuration, startSkip, endSkip);
			}
		}
		return (course);
	}

	private static Quizzes quizzes(String filename) throws IOException,
			ParserConfigurationException, SAXException {
		Quizzes quizList = new Quizzes();

		DocumentBuilder builder = FACTORY.newDocumentBuilder();
		Document document = builder.parse(IOUtils.class.getResourceAsStream("/" + filename));

		// Get all the nodes
		NodeList nodeList = document.getDocumentElement().getChildNodes();
		for(int i = 0; i < nodeList.getLength(); i++) {
			// XML structure is simple--a bunch of quizzes
			// Not validating the data values
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;

				// quiz IDs should be unique
				int ID = Integer.parseInt(getValue(elem, "id"));
				// month is an integer 1..12
				int month = Integer.parseInt(getValue(elem, "month"));
				// day is integer 1..31
				int day = Integer.parseInt(getValue(elem, "day"));
				// hour is integer 0..23
				int hour = Integer.parseInt(getValue(elem, "hour"));
				// minute is integer 0..59
				int minute = Integer.parseInt(getValue(elem, "minute"));
				// Put one XML record into a bean and add it to the list
				quizList.addQuiz(new QuizBean(ID, month, day, hour, minute));
			}
		}
		// XML file may not be sorted
		quizList.sort();
		return (quizList);
	}

	private static String getValue(Element host, String name) {
		return host.getElementsByTagName(name).item(0).getChildNodes().item(0).getNodeValue();
	}
}
