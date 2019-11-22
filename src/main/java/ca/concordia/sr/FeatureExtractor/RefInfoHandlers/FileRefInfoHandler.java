package ca.concordia.sr.FeatureExtractor.RefInfoHandlers;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import com.github.javaparser.ParseProblemException;

import ca.concordia.sr.FeatureExtractor.App;
import ca.concordia.sr.FeatureExtractor.utils.FileHelper;

public class FileRefInfoHandler extends RefInfoHandler {

	public FileRefInfoHandler(String filepath, String projectName, REF_TYPE refType)
			throws ParseProblemException, ClientProtocolException, IOException {
		super(filepath, projectName, refType);
	}

	@Override
	protected String refInfoLocation() {
		return "file: " + this.getUri();
	}

	@Override
	protected String getRefInfo() throws FileNotFoundException {
		return FileHelper.getFileContent(this.getUri());
	}

	@Override
	protected String getSrcCode(String path) throws FileNotFoundException {
		return FileHelper.getFileContent(App.getDataRoot() + path);
	}
}
