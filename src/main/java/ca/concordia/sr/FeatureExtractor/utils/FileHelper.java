package ca.concordia.sr.FeatureExtractor.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileHelper {
	public static String getFileContent(String path) throws FileNotFoundException {
		FileReader reader = new FileReader(path);
		BufferedReader bReader = new BufferedReader(reader, 10240);
		StringBuffer sb = new StringBuffer();
		try {
			String line = bReader.readLine();
			while (line != null) {
				sb.append(line);
				line = bReader.readLine();
			}
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Read file error: " + path);
		}
		
		return sb.toString();
	}

}
