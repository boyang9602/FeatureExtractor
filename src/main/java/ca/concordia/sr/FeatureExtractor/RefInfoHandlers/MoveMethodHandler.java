package ca.concordia.sr.FeatureExtractor.RefInfoHandlers;

import java.io.File;
import java.io.FileNotFoundException;

public class MoveMethodHandler extends RefInfoHandler {

	public MoveMethodHandler(File refactoring, String projectName) throws FileNotFoundException {
		super(refactoring, projectName);
		this.originalClassNameWithPkg = this.getjObj().getString("original class");
	}

	@Override
	public void handle() {
	}

}
