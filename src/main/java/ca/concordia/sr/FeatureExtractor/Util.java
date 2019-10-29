package ca.concordia.sr.FeatureExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Util {
	public static String readFile(File file) throws FileNotFoundException, IOException {
		FileReader fReader = new FileReader(file);
		BufferedReader bReader = new BufferedReader(fReader);
		StringBuilder sb = new StringBuilder();
		for(String line = bReader.readLine(); line != null; line = bReader.readLine()) {
			sb.append(line);
			sb.append("\n");
		}
		bReader.close();
		return sb.toString();
	}
}
