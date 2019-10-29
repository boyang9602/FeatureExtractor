package ca.concordia.sr.FeatureExtractor.RefInfoHandlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import ca.concordia.sr.FeatureExtractor.App;

public abstract class RefInfoHandler {
	private File originalFile;
	private JSONObject jObj;
	private String projectName;
	private Set<String> paths = new HashSet<String>();
	private String commitId;
	protected String originalClassNameWithPkg;
	protected CompilationUnit originalClassAST;
	
	public final JSONObject getjObj() {
		return jObj;
	}
	public String getProjectName() {
		return projectName;
	}
	public String getCommitId() {
		return commitId;
	}
	
	public RefInfoHandler (File refactoring, String projectName) throws FileNotFoundException {
		this.originalFile = refactoring;
		FileReader reader = new FileReader(refactoring);
		BufferedReader bReader = new BufferedReader(reader, 10240);
		try {
			String content = bReader.readLine();
			bReader.close();
			jObj = new JSONObject(content);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Read file error: " + refactoring.getAbsolutePath() + " " + refactoring.getName());
		} 
		this.projectName = projectName;
		this.computeAllPaths();
		this.readOriginalClassFile();
		this.commitId = jObj.getString("commitId");
	}
	public abstract void handle();
	
	private void computeAllPaths() {
		JSONArray originalCodeElements = jObj.getJSONArray("leftSideLocations");
		for (Object codeElement : originalCodeElements) {
			this.paths.add(((JSONObject) codeElement).getString("filePath"));
		}
	}
	
	private void readOriginalClassFile() {
		String matchedFilePath = this.matchFilePath(this.originalClassNameWithPkg);
		String originalFilePath = App.getDataRoot() + "src_code/before/" + this.getCommitId() + "/" + matchedFilePath;
		File originalFile = new File(originalFilePath);
		try {
			this.originalClassAST = StaticJavaParser.parse(originalFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("cannot find file: " + originalFilePath);
		}
	}
	
	public String matchFilePath(final String classNameWithPkg) {
		for(String path : paths) {
			if (path.substring(0, path.lastIndexOf(".java")).replace('/', '.').endsWith(classNameWithPkg)) {
				return path;
			}
		}
		throw new RuntimeException("cannot match any path for original class: " + classNameWithPkg + ". \nPlease check " + this.originalFile.getAbsolutePath());
	}
}
