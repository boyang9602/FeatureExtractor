package ca.concordia.sr.FeatureExtractor.RefInfoHandlers;

import java.io.File;
import java.io.FileNotFoundException;

public class ExtractVariableHandler extends RefInfoHandler {

	public ExtractVariableHandler(File refactoring, String projectName) throws FileNotFoundException {
		super(refactoring, projectName);
		this.originalClassNameWithPkg = this.getjObj().getString("class");
	}

	@Override
	public void handle() {
	}

}
