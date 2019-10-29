package ca.concordia.sr.FeatureExtractor.Visitor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;

import ca.concordia.sr.FeatureExtractor.CodeModel.MethodSignature;

public class AbstractMethodVisitor extends TreeVisitor {

	public AbstractMethodVisitor(MethodSignature method) {
		
	}
	@Override
	public void process(Node node) {
	}

}
