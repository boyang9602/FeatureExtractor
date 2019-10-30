package ca.concordia.sr.FeatureExtractor.Visitor;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.TreeVisitor;

// Abstract is a verb here
public class AbstractMethodVisitor extends TreeVisitor {
	Map<String, Integer> paramIdMap = new HashMap<String, Integer>();
	Map<String, Integer> localIdMap = new HashMap<String, Integer>();
	Map<String, Integer> fieldIdMap = new HashMap<String, Integer>();
	
	Map<String, Integer> stmtIdMap = new HashMap<String, Integer>();
	
	@Override
	public void process(Node node) {
		if (node instanceof MethodDeclaration) {
			
		}
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
	
	public void onFinish() {
		// TODO: write to file
	}

}
