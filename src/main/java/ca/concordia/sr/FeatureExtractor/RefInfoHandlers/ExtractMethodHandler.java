package ca.concordia.sr.FeatureExtractor.RefInfoHandlers;

import java.io.File;
import java.io.FileNotFoundException;

public class ExtractMethodHandler extends RefInfoHandler {

	public ExtractMethodHandler(File refactoring, String name) throws FileNotFoundException {
		super(refactoring, name);
		this.originalClassNameWithPkg = this.getjObj().getString("original class");
	}

	@Override
	public void handle() {
	}

}
