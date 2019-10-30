package ca.concordia.sr.FeatureExtractor.Visitor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;

public class AbstractMethodVisitor extends TreeVisitor {
	
	@Override
	public void process(Node node) {
		System.out.println(node);
		// if method: parameters, record
		// if annotation, ignore
		// if annotation's name, ignore
		// if modifier of method, record
		// if simplename of method, ignore
		// if classorinterfacetye of method, ignore
		// if simplename of classorinterfacetype of method, ignore
		// if voidtype of method, ignore
		// if blockstmt of method, ignore
		// if expressionstmt of blockstmt of method
		// if variabledeclaration, record in map
		// if variabledeclaration's children, ignore
		// if method call, record, $caller.$method
		// nested method call a.method(b.methodb), it may extract variable
		// map record the constant, too
		// methodA.paraX, paraX as parameter of methodA
		// varA.methodA, call methodA from varA
		// WhileStmt -> Blockstmt
	}

}
