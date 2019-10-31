package ca.concordia.sr.FeatureExtractor.CodeModel;

import java.util.ArrayList;
import java.util.List;

public class MethodSignature {
	private String name;
	private String visibility;
	private String returnType;
	private boolean _abstract;
	private List<String[]> parameters = new ArrayList<String[]>();
	private int startLine;
	private int endLine;
	public final String getName() {
		return name;
	}
	public final String getVisibility() {
		return visibility;
	}
	public final String getReturnType() {
		return returnType;
	}
	public boolean is_abstract() {
		return _abstract;
	}
	public final List<String[]> getParameters() {
		return parameters;
	}
	public boolean isRangeIncludedOrIntersectted(int startLine, int endLine) {
		if (startLine > this.endLine) return false;
		if (endLine < this.startLine) return false;
		return true;
	}
	
	public MethodSignature(String name, String visibility, String returnType, boolean _abstract, List<String> parameters, int startLine, int endLine) {
		this.name = name;
		this.visibility = visibility;
		this.returnType = returnType;
		this._abstract = _abstract;
		this.parseParameters(parameters);
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	private void parseParameters(List<String> parameters) {
		for(String parameter : parameters) {
			String varName = parameter.substring(0, parameter.indexOf(' '));
			String varType = parameter.substring(parameter.indexOf(' ') + 1);
			this.parameters.add(new String[]{varType, varName});
		}
	}
}
