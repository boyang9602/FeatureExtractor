package ca.concordia.sr.FeatureExtractor.RefInfoHandlers;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import com.github.javaparser.ParseProblemException;

import ca.concordia.sr.FeatureExtractor.App;
import ca.concordia.sr.FeatureExtractor.utils.SeaweedHelper;

public class SeaweedRefInfoHandler extends RefInfoHandler {
	private String seaweedUri;
	
	public SeaweedRefInfoHandler (String seaweedUri, String projectName, REF_TYPE refType) throws ParseProblemException, JSONException, ClientProtocolException, IOException {
		super(projectName, refType);
		this.seaweedUri = seaweedUri;
	}

	@Override
	public String refInfoLocation() {
		return "seaweed: " + App.seaweedhost + this.seaweedUri;
	}

	@Override
	public String getRefInfo() throws ClientProtocolException, IOException {
		return SeaweedHelper.getFileContent(App.seaweedhost, seaweedUri);
	}
}
