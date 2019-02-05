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

public class ReaderUtils {
	private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
	private static final String SEPARATOR = ",";

	public static List<AppointmentBean> appointments(String filename) throws IOException {
		List<AppointmentBean> appts = new ArrayList<>();
		File file = new File(filename);
		if(!file.exists()) {
			throw new IOException("No appointments to read.");
		} else {
			FileReader fw = new FileReader(file.getAbsoluteFile());
			BufferedReader bw = new BufferedReader(fw);
			String line;
			while((line = bw.readLine()) != null) {
				String[] s = line.split(SEPARATOR);
				appts.add(new AppointmentBean(Integer.parseInt(s[0]), Integer.parseInt(s[1]), s[2]));
			}
			bw.close();
		}
		return (appts);
	}

	public static Retakes retakes(String filename) throws IOException,
			ParserConfigurationException, SAXException {
		Retakes retakeList = new Retakes();
		RetakeBean retake;

		DocumentBuilder builder = FACTORY.newDocumentBuilder();
		Document document = builder.parse(ReaderUtils.class.getResourceAsStream("/" +filename));

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

	public static CourseBean course(String filename) throws IOException,
			ParserConfigurationException, SAXException {
		CourseBean course = null;

		DocumentBuilder builder = FACTORY.newDocumentBuilder();
		Document document = builder.parse(ReaderUtils.class.getResourceAsStream("/" +filename));

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
				String retakeDuration = getValue(elem, "retakeDuration");

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

	public static Quizzes quizzes(String filename) throws IOException,
			ParserConfigurationException, SAXException {
		Quizzes quizList = new Quizzes();

		DocumentBuilder builder = FACTORY.newDocumentBuilder();
		Document document = builder.parse(ReaderUtils.class.getResourceAsStream("/" +filename));

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
