package ca.concordia.sr.FeatureExtractor.RefInfoHandlers;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.github.javaparser.ParseProblemException;

import ca.concordia.sr.FeatureExtractor.App;
import ca.concordia.sr.FeatureExtractor.utils.FileHelper;

public class FileRefInfoHandler extends RefInfoHandler {
	private String filepath;
	public FileRefInfoHandler (String filepath, String projectName, REF_TYPE refType) throws ParseProblemException, JSONException, ClientProtocolException, IOException {
		super(projectName, refType);
		this.filepath = filepath;
	}

	@Override
	protected String refInfoLocation() {
		return "file: " + filepath;
	}

	@Override
	protected String getRefInfo() throws FileNotFoundException {
		return FileHelper.getFileContent(filepath);
	}

	@Override
	protected String getSrcCode(String path) throws FileNotFoundException {
		return FileHelper.getFileContent(App.getDataRoot() + path);
	}
}
