package ca.concordia.sr.FeatureExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ca.concordia.sr.FeatureExtractor.RefInfoHandlers.RefInfoHandler;
import ca.concordia.sr.FeatureExtractor.RefInfoHandlers.RefInfoHandler.REF_TYPE;

/**
 * Hello world!
 *
 */
public class App 
{
	private static String dataRoot;
	public final static String getDataRoot() {
		return App.dataRoot;
	}
    public static void main( String[] args ) throws IOException
    {
    	App.dataRoot = args[0];
    	// new RefInfoHandler(new File("/home/bo/eclipse-workspace/RefactoringDetector/data/ref_infos/Extract_And_Move_Method/accumulo/206.json"), "hadoop", REF_TYPE.EXTRACT_METHOD).handle();
    	handleEach(new File(dataRoot + "ref_infos/Extract_Method/"), REF_TYPE.EXTRACT_METHOD);
    	handleEach(new File(dataRoot + "ref_infos/Extract_And_Move_Method/"), REF_TYPE.EXTRACT_AND_MOVE_METHOD);
    	handleEach(new File(dataRoot + "ref_infos/Move_Method/"), REF_TYPE.MOVE_METHOD);
    	handleEach(new File(dataRoot + "ref_infos/Extract_Variable/"), REF_TYPE.EXTRACT_VARIABLE);
    	handleEach(new File(dataRoot + "ref_infos/Inline_Variable/"), REF_TYPE.INLINE_VARIABLE);
    }
    
    public static void handleEach(File refTypeFolder, REF_TYPE refType) throws IOException {
		for (File project : refTypeFolder.listFiles()) {
			for (File refactoring : project.listFiles()) {
				try {
					new RefInfoHandler(refactoring, project.getName(), refType).handle();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.out.println(refactoring.getAbsolutePath() + " does not exist");
				}
			}
		}
    }
}
