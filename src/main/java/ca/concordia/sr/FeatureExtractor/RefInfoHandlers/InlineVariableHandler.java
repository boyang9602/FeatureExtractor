package ca.concordia.sr.FeatureExtractor.RefInfoHandlers;

import java.io.File;
import java.io.FileNotFoundException;

public class InlineVariableHandler extends RefInfoHandler {

	public InlineVariableHandler(File refactoring, String projectName) throws FileNotFoundException {
		super(refactoring, projectName);
		this.originalClassNameWithPkg = this.getjObj().getString("class");
	}

	@Override
	public void handle() {
	}

}
