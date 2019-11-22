package ca.concordia.sr.FeatureExtractor.utils;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class SeaweedHelper {
	private static CloseableHttpClient httpClient = HttpClients.createDefault();
	public static JSONObject getFileList(String host, String path, String lastFileName, int perPage) throws ClientProtocolException, IOException {
		lastFileName = lastFileName == null ? "" : lastFileName;
		HttpGet req = new HttpGet(String.format("%s%s/?lastFileName=%s&limit=%d", host, path, lastFileName, perPage));
		req.setHeader(HttpHeaders.ACCEPT, "application/json");
		CloseableHttpResponse resp = httpClient.execute(req);
		
		return new JSONObject(EntityUtils.toString(resp.getEntity()));
	}
	public static String getFileContent(String host, String fullPath) throws ClientProtocolException, IOException {
		HttpGet req = new HttpGet(host + fullPath);
		CloseableHttpResponse resp = httpClient.execute(req);
		return EntityUtils.toString(resp.getEntity());
	}
}
