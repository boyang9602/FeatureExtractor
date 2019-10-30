package ca.concordia.sr.FeatureExtractor.Visitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.TreeVisitor;

// Abstract is a verb here
public class AbstractMethodVisitor extends TreeVisitor {
	Map<String, Integer> paramIdMap = new HashMap<String, Integer>();
	Map<String, Integer> localIdMap = new HashMap<String, Integer>();
	Map<String, Integer> fieldIdMap = new HashMap<String, Integer>();
	
	Map<String, Integer> stmtIdMap = new HashMap<String, Integer>();
	
	List<String> methodTokens = new LinkedList<String>();
	
	@Override
	public void process(Node node) {
		if (node instanceof MethodDeclaration) {
			int paramCount = 0;
			for(Parameter parameter : ((MethodDeclaration) node).getParameters()) {
				paramIdMap.put(parameter.getName().asString(), paramCount++);
			}
		}
		if (node instanceof ExpressionStmt) {
			Optional<Node> parent = node.getParentNode();
			if (!parent.isPresent()) {
				throw new RuntimeException("ExpressionStmt does not have a parent node! ");
			}
			Optional<Node> gradparent = parent.get().getParentNode();
			if (parent.get() instanceof BlockStmt) {
				if (gradparent.isPresent()) {
					throw new RuntimeException("BlockStmt does not have a parent node! ");
				}
				if (gradparent.get() instanceof MethodDeclaration) {
					// a normal expression statement, it could be:
					// 1. local variable declaration
					// 2. method call
					// 3. some calculation
					// 4. ...
					// record the stmt type and related vars
					// nested method call a.method(b.methodb), it may extract variable
				} else {
					computeNestedBlock(node);
				}
			}
		}
		// other stmts like whilestmt, ifstmt etc, record their condition
		// try, todos
		System.out.println(node);
	}
	
	/*
	 * handle the code block in a method, return the abstracted statement,
	 * e.g. WHILE.IF.STMT, or much more details, WHILE.IF.STMT.PARAM.LOCAL, etc.
	 */
	private String computeNestedBlock(Node node) {
		return "";
	}
	
	public void onFinish() {
		// TODO: write to file
	}

}
