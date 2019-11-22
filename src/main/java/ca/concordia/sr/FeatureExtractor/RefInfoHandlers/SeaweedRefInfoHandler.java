package ca.concordia.sr.FeatureExtractor.RefInfoHandlers;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import com.github.javaparser.ParseProblemException;

import ca.concordia.sr.FeatureExtractor.App;
import ca.concordia.sr.FeatureExtractor.utils.SeaweedHelper;

public class SeaweedRefInfoHandler extends RefInfoHandler {
	public SeaweedRefInfoHandler(String seaweedUri, String projectName, REF_TYPE refType)
			throws ParseProblemException, ClientProtocolException, IOException {
		super(seaweedUri, projectName, refType);
	}

	@Override
	protected String refInfoLocation() {
		return "seaweed: " + App.seaweedhost + this.getUri();
	}

	@Override
	protected String getRefInfo() throws ClientProtocolException, IOException {
		return SeaweedHelper.getFileContent(App.seaweedhost, this.getUri());
	}

	@Override
	protected String getSrcCode(String path) throws ClientProtocolException, IOException {
		return SeaweedHelper.getFileContent(App.seaweedhost, "/srdata/" + path);
	}
}
