package ca.concordia.sr.FeatureExtractor.RefInfoHandlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import ca.concordia.sr.FeatureExtractor.App;
import ca.concordia.sr.FeatureExtractor.CodeModel.MethodSignature;
import ca.concordia.sr.FeatureExtractor.Visitor.AbstractMethodVisitor;

public class RefInfoHandler {
	public enum REF_TYPE {
		EXTRACT_METHOD,
		EXTRACT_AND_MOVE_METHOD,
		MOVE_METHOD,
		EXTRACT_VARIABLE,
		INLINE_VARIABLE
	}
	
	private File originalFile;
	private JSONObject jObj;
	private String projectName;
	private Set<String> paths = new HashSet<String>();
	private String commitId;
	protected String originalClassNameWithPkg;
	protected CompilationUnit originalClassAST;
	private REF_TYPE type;
	private MethodSignature methodSignature;
	
	public final JSONObject getjObj() {
		return jObj;
	}
	public String getProjectName() {
		return projectName;
	}
	public String getCommitId() {
		return commitId;
	}
	
	private String getMethodBeforeKey() {
		String key = null;
		switch (this.type) {
		case EXTRACT_METHOD:
		case EXTRACT_AND_MOVE_METHOD:
			key = "from method";
			break;
		case MOVE_METHOD:
			key = "original method";
			break;
		case EXTRACT_VARIABLE:
		case INLINE_VARIABLE:
			key = "method";
			break;
		}
		return key;
	}
	
	private String getClassBeforeKey() {
		String key = null;
		switch (this.type) {
		case EXTRACT_METHOD:
		case EXTRACT_AND_MOVE_METHOD:
		case MOVE_METHOD:
			key = "original class";
			break;
		case EXTRACT_VARIABLE:
		case INLINE_VARIABLE:
			key = "class";
			break;
		}
		return key;
	}
	
	public RefInfoHandler (File refactoring, String projectName, REF_TYPE refType) throws FileNotFoundException {
		this.originalFile = refactoring;
		this.projectName = projectName;
		this.type = refType;
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
		this.commitId = jObj.getString("commitId");
		this.computeAllPaths();
		this.originalClassNameWithPkg = this.getjObj().getString(this.getClassBeforeKey());
		this.parseOriginalFile();
		this.getOperatedMethod();
	}
	
	public void getOperatedMethod() {
		JSONObject methodSigJObj = this.getjObj().getJSONObject(this.getMethodBeforeKey());
		List<String> parameters = new ArrayList<String>();
		for(Object p : methodSigJObj.getJSONArray("parameters")) {
			parameters.add((String)p);
		}
		String methodName = methodSigJObj.getString("name");
		String methodVisibility = methodSigJObj.getString("visibility");
		boolean isAbstract = methodSigJObj.getBoolean("abstract");
		String methodReturnType = "Unknow";
		try {
			methodReturnType = methodSigJObj.getString("return type");
		} catch (JSONException e) {
		}
		this.methodSignature = new MethodSignature(methodName, methodVisibility, 
				methodReturnType, isAbstract, parameters);
	}
	
	private void computeAllPaths() {
		JSONArray originalCodeElements = jObj.getJSONArray("leftSideLocations");
		for (Object codeElement : originalCodeElements) {
			this.paths.add(((JSONObject) codeElement).getString("filePath"));
		}
	}
	
	protected void parseOriginalFile() {
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
		if (paths.size() == 1) {
			return paths.iterator().next();
		}
		for(String path : paths) {
			if (path.substring(0, path.lastIndexOf(".java")).replace('/', '.').endsWith(classNameWithPkg)) {
				return path;
			}
		}
		
		// for inner class
		for(String path : paths) {
			if (path.substring(0, path.lastIndexOf(".java")).replace('/', '.').endsWith(classNameWithPkg.substring(0, classNameWithPkg.lastIndexOf('.')))) {
				return path;
			}
		}
		throw new RuntimeException("cannot match any path for original class: " + classNameWithPkg + ". \nPlease check " + this.originalFile.getAbsolutePath());
	}
	
	public void handle() throws IOException {
		for(MethodDeclaration node : this.originalClassAST.findAll(MethodDeclaration.class)) {
			if (node.getName().getIdentifier().equals(this.methodSignature.getName())) {
				NodeList<Parameter> parameters = node.getParameters();
				if (this.methodSignature.getParameters().size() != parameters.size()) {
					continue;
				}
				if (!isEqual(this.methodSignature.getParameters(), parameters)) {
					continue;
				}
				if (parameters.equals(this.methodSignature.getParameters())) {
					AbstractMethodVisitor amv = new AbstractMethodVisitor(node);
					amv.visitPreOrder(node);
					amv.onFinish();
				}
			}
		}
	}
	
	private boolean isEqual(List<String[]> pl1, NodeList<Parameter> pl2) {
		if(pl1.size() == 0) {
			return true;
		}
		Iterator<String[]> i1 = pl1.iterator();
		Iterator<Parameter> i2 = pl2.iterator();
		while(i1.hasNext()) {
			String[] p1 = i1.next();
			Parameter p2 = i2.next();
			if (!p1[0].replaceAll("\\s", "").equals(p2.getType().toString().replaceAll("\\s", ""))) {
				return false;
			}
		}
		return true;
	}
}
