// JO, 6-Jan-2019
// Readsappointments
// Stores in a ArrayList and returns

package quizretakes;

import quizretakes.bean.AppointmentBean;

import java.lang.*;


import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class apptsReader {
	private static final String separator = ",";

	public static List<AppointmentBean> read(String filename) throws IOException {
		// read appointments file
		List<AppointmentBean> appts = new ArrayList<>();
		AppointmentBean a;
		File file = new File(filename);
		if(!file.exists()) {
			throw new IOException("No appointments to read.");
		} else {
			FileReader fw = new FileReader(file.getAbsoluteFile());
			BufferedReader bw = new BufferedReader(fw);

			String line;
			while((line = bw.readLine()) != null) {
				String[] s = line.split(separator);
				a = new AppointmentBean(Integer.parseInt(s[0]), Integer.parseInt(s[1]), s[2]);
				appts.add(a);
			}
			bw.close();
		}
		return (appts);
	}
}
