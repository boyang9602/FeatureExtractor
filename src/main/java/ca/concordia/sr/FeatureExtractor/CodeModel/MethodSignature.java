package ca.concordia.sr.FeatureExtractor.CodeModel;

import java.util.List;

public class MethodSignature {
	private String name;
	private String visibility;
	private String returnType;
	private boolean _abstract;
	private List<String[]> parameters;
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
	
	public MethodSignature(String name, String visibility, String returnType, boolean _abstract, List<String> parameters) {
		this.name = name;
		this.visibility = visibility;
		this.returnType = returnType;
		this._abstract = _abstract;
		this.parseParameters(parameters);
	}
	
	private void parseParameters(List<String> parameters) {
		for(String parameter : parameters) {
			String varName = parameter.substring(0, parameter.indexOf(' '));
			String varType = parameter.substring(parameter.indexOf(' ') + 1);
			this.parameters.add(new String[] {varName, varType});
		}
	}
}
