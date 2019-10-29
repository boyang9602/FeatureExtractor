package ca.concordia.sr.FeatureExtractor;

import java.io.File;
import java.io.FileNotFoundException;

import ca.concordia.sr.FeatureExtractor.RefInfoHandlers.ExtractAndMoveMethodHandler;
import ca.concordia.sr.FeatureExtractor.RefInfoHandlers.ExtractMethodHandler;
import ca.concordia.sr.FeatureExtractor.RefInfoHandlers.ExtractVariableHandler;
import ca.concordia.sr.FeatureExtractor.RefInfoHandlers.InlineVariableHandler;
import ca.concordia.sr.FeatureExtractor.RefInfoHandlers.MoveMethodHandler;
import ca.concordia.sr.FeatureExtractor.RefInfoHandlers.RefInfoHandler;

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
    public static void main( String[] args )
    {
    	App.dataRoot = args[0];
    	File refInfos = new File(dataRoot + "ref_infos");
    	for (File refType : refInfos.listFiles()) {
    		for (File project : refType.listFiles()) {
    			for (File refactoring : project.listFiles()) {
					try {
						RefInfoHandler handler = null;
						if (refType.getName().equals("Extract_Method")) {
							handler = new ExtractMethodHandler(refactoring, project.getName());
						} else if (refType.getName().equals("Extract_And_Move_Method")) {
							handler = new ExtractAndMoveMethodHandler(refactoring, project.getName());
						} else if (refType.getName().equals("Move_Method")) {
							handler = new MoveMethodHandler(refactoring, project.getName());
						} else if (refType.getName().equals("Extract_Variable")) {
							handler = new ExtractVariableHandler(refactoring, project.getName());
						} else if (refType.getName().equals("Inline_Variable")) {
							handler = new InlineVariableHandler(refactoring, project.getName());
						} else {
							throw new RuntimeException("UNKNOWN REFACTORING: " + refType.getName());
						}
	    				handler.handle();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						System.out.println(refactoring.getAbsolutePath() + " does not exist");
					}
    			}
    		}
		}
    }
}
