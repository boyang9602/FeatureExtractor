package ca.concordia.sr.FeatureExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.javaparser.ParseProblemException;

import ca.concordia.sr.FeatureExtractor.RefInfoHandlers.FileRefInfoHandler;
import ca.concordia.sr.FeatureExtractor.RefInfoHandlers.RefInfoHandler.REF_TYPE;
import ca.concordia.sr.FeatureExtractor.RefInfoHandlers.SeaweedRefInfoHandler;
import ca.concordia.sr.FeatureExtractor.utils.SeaweedHelper;

/**
 * Hello world!
 *
 */
public class App 
{
	private static String dataRoot;
	public final static String seaweedhost = "http://localhost:8888";
	public final static String getDataRoot() {
		return App.dataRoot;
	}
    public static void main( String[] args ) throws IOException
    {
    	App.dataRoot = args[0];
    	Map<String, REF_TYPE> refTypes = new HashMap<String, REF_TYPE>();
    	refTypes.put("Extract_Method", REF_TYPE.EXTRACT_METHOD);
    	refTypes.put("Extract_And_Move_Method", REF_TYPE.EXTRACT_AND_MOVE_METHOD);
    	refTypes.put("Move_Method", REF_TYPE.MOVE_METHOD);
    	refTypes.put("Extract_Variable", REF_TYPE.EXTRACT_VARIABLE);
    	refTypes.put("Inline_Variable", REF_TYPE.INLINE_VARIABLE);
    	String[] projects = {"accumulo", "elasticsearch", "fastjson", "hadoop", "hive", "jenkins", 
    			"junit", "kafka", "logstash", "spring-framework"};
    	handleEachInSeaweed(projects, refTypes);
    	// new RefInfoHandler(new File("/home/bo/eclipse-workspace/RefactoringDetector/data/ref_infos/Move_Method/hive/325.json"), "hadoop", REF_TYPE.MOVE_METHOD).handle();
    	// new SeaweedRefInfoHandler("/srdata/ref_infos/Extract_Method/elasticsearch/0.json", "accumulo", REF_TYPE.EXTRACT_METHOD);
    	
    	// handleEach(new File(dataRoot + "ref_infos/Extract_Method/"), REF_TYPE.EXTRACT_METHOD);
    	// handleEach(new File(dataRoot + "ref_infos/Extract_And_Move_Method/"), REF_TYPE.EXTRACT_AND_MOVE_METHOD);
    	// handleEach(new File(dataRoot + "ref_infos/Move_Method/"), REF_TYPE.MOVE_METHOD);
    	// handleEach(new File(dataRoot + "ref_infos/Extract_Variable/"), REF_TYPE.EXTRACT_VARIABLE);
    	// handleEach(new File(dataRoot + "ref_infos/Inline_Variable/"), REF_TYPE.INLINE_VARIABLE);
    }
    
    public static void handleEachInSeaweed(final String[] projects, final Map<String, REF_TYPE> refTypes) {
    	for (Entry<String, REF_TYPE> refType : refTypes.entrySet()) {
    		for (String project : projects) {
    			String lastFileName = "";
    			boolean hasMore = true;
    			while(hasMore) {
    				JSONObject resp;
					try {
						resp = SeaweedHelper.getFileList(seaweedhost, "/srdata/ref_infos/" + refType.getKey() + "/" + project + "/", 
								lastFileName, 20);
	    				hasMore = resp.getBoolean("ShouldDisplayLoadMore");
	    				lastFileName = resp.getString("LastFileName");
	        			for (Object fileInfo : resp.getJSONArray("Entries")) {
	        				try {
								new SeaweedRefInfoHandler(((JSONObject) fileInfo).getString("FullPath"), project, refType.getValue()).handle();
							} catch (ParseProblemException e) {
								e.printStackTrace();
								System.out.println("could not parse" + ((JSONObject) fileInfo).getString("FullPath"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
	        			}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
    			}
    		}
    	}
    }
    
    public static void handleEach(File refTypeFolder, REF_TYPE refType) throws IOException {
		for (File project : refTypeFolder.listFiles()) {
			for (File refactoring : project.listFiles()) {
				try {
					new FileRefInfoHandler(refactoring.getAbsolutePath(), project.getName(), refType).handle();
				} catch (FileNotFoundException e) {
					System.out.println(refactoring.getAbsolutePath() + " does not exist");
				} catch (ParseProblemException e) {
					System.out.println(refactoring.getAbsolutePath() + " parse error");
				}
			}
		}
    }
}
