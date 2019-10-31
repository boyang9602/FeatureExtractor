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

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
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
	private Set<String> rightSidePaths = new HashSet<String>();
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
	
	public RefInfoHandler (File refactoring, String projectName, REF_TYPE refType) throws FileNotFoundException, ParseProblemException {
		this.originalFile = refactoring;
		this.projectName = projectName;
		this.type = refType;
		FileReader reader = new FileReader(refactoring);
		BufferedReader bReader = new BufferedReader(reader, 10240);
		try {
			String content = bReader.readLine();
			bReader.close();
			try {
				jObj = new JSONObject(content);
			} catch (JSONException e) {
				System.out.println("unparsable json: " + this.originalFile.getAbsolutePath());
			}
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
		
		int minStart = 999999999;
		int maxEnd = 0;
		for (Object leftSideLocation : this.getjObj().getJSONArray("leftSideLocations")) {
			int curStart = ((JSONObject)leftSideLocation).getInt("startLine");
			int curEnd = ((JSONObject)leftSideLocation).getInt("endLine");
			if (curStart < minStart) minStart = curStart;
			if (curEnd > maxEnd) maxEnd = curEnd;
		}
		this.methodSignature = new MethodSignature(methodName, methodVisibility, 
				methodReturnType, isAbstract, parameters, minStart, maxEnd);
	}
	
	private void computeAllPaths() {
		JSONArray originalCodeElements = jObj.getJSONArray("leftSideLocations");
		for (Object codeElement : originalCodeElements) {
			this.paths.add(((JSONObject) codeElement).getString("filePath"));
		}
		originalCodeElements = jObj.getJSONArray("rightSideLocations");
		for (Object codeElement : originalCodeElements) {
			this.rightSidePaths.add(((JSONObject) codeElement).getString("filePath"));
		}
	}
	
	protected void parseOriginalFile() throws ParseProblemException, FileNotFoundException {
		String matchedFilePath = this.matchFilePath(this.originalClassNameWithPkg);
		String originalFilePath = App.getDataRoot() + "src_code/before/" + this.getCommitId() + "/" + matchedFilePath;
		File originalFile = new File(originalFilePath);
		this.originalClassAST = StaticJavaParser.parse(originalFile);
	}
	
	public String matchFilePath(final String classNameWithPkg) throws RuntimeException {
		
		if (this.type == REF_TYPE.EXTRACT_VARIABLE) {
			if (rightSidePaths.size() == 1) {
				return rightSidePaths.iterator().next();
			}
			return matchFilePath(classNameWithPkg, rightSidePaths);
		} else {
			if (paths.size() == 1) {
				return paths.iterator().next();
			}
			return matchFilePath(classNameWithPkg, paths);
		}
	}
	
	private String matchFilePath(final String classNameWithPkg, Set<String> paths) {
		for(String path : paths) {
			if (path.substring(0, path.lastIndexOf(".java")).replace('/', '.').endsWith(classNameWithPkg)) {
				return path;
			}
		}
		
		// for inner class
		for(String path : paths) {
			String outerClass = classNameWithPkg;
			do {
				int lastDot = outerClass.lastIndexOf('.');
				if (lastDot == -1) {
					break;
				}
				outerClass = outerClass.substring(0, lastDot);
				if (path.substring(0, path.lastIndexOf(".java")).replace('/', '.').endsWith(outerClass)) {
					return path;
				}
			} while (true);
		}
		
		throw new RuntimeException("cannot match any path for original class: " + classNameWithPkg + ". \nPlease check " + this.originalFile.getAbsolutePath());
	}
	
	public void handle() throws IOException {
		boolean result = handleNormalMethod() || handleConstructor();
		if (!result) {
			System.out.println(this.originalFile.getAbsolutePath() + " cannot match the method");
		}
	}
	
	private boolean handleConstructor() throws IOException {
		for(ConstructorDeclaration node : this.originalClassAST.findAll(ConstructorDeclaration.class)) {
			if (node.getName().getIdentifier().equals(this.methodSignature.getName())) {
				NodeList<Parameter> parameters = node.getParameters();
				if (this.methodSignature.getParameters().size() != parameters.size()) {
					continue;
				}
				if (!isEqual(this.methodSignature.getParameters(), parameters)) {
					continue;
				}
				Range range = node.getRange().get();
				if (!this.methodSignature.isRangeIncludedOrIntersectted(range.begin.line, range.end.line)) {
					continue;
				}
				AbstractMethodVisitor amv = new AbstractMethodVisitor(node);
				amv.visitPreOrder(node);
				try {
					amv.onFinish("data/" + this.projectName + "/" + this.type.toString());
					return true;
				} catch (RuntimeException e) {
					System.out.println(this.originalFile.getAbsolutePath() + " " + e.getMessage());
				}
			}
		}
		return false;
	}
	
	private boolean handleNormalMethod() throws IOException {
		for(MethodDeclaration node : this.originalClassAST.findAll(MethodDeclaration.class)) {
			if (node.getName().getIdentifier().equals(this.methodSignature.getName())) {
				NodeList<Parameter> parameters = node.getParameters();
				if (this.methodSignature.getParameters().size() != parameters.size()) {
					continue;
				}
				if (!isEqual(this.methodSignature.getParameters(), parameters)) {
					continue;
				}
				Range range = node.getRange().get();
				if (!this.methodSignature.isRangeIncludedOrIntersectted(range.begin.line, range.end.line)) {
					continue;
				}
				AbstractMethodVisitor amv = new AbstractMethodVisitor(node);
				amv.visitPreOrder(node);
				try {
					amv.onFinish("data/" + this.projectName + "/" + this.type.toString());
					return true;
				} catch (RuntimeException e) {
					System.out.println(this.originalFile.getAbsolutePath() + " " + e.getMessage());
				}
			}
		}
		return false;
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
			String p1Type = p1[0].replaceAll("\\s", "").replaceAll("\\.\\.\\.", "");// String..., java parser does not include the ...
			String p2Type = p2.getTypeAsString().replaceAll("\\s", "");

			if (p1Type.equals(p2Type)) {
				continue;
			}
			
			int dotIndex = p2Type.lastIndexOf('.');
			if(dotIndex != -1) {
				p2Type = p2Type.substring(dotIndex + 1);
			}
			if (!p1Type.equals(p2Type)) {
				return false;
			}
		}
		return true;
	}
}
