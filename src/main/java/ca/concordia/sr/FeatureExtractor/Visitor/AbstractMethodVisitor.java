package ca.concordia.sr.FeatureExtractor.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.TreeVisitor;

// Abstract is a verb here
public class AbstractMethodVisitor extends TreeVisitor {
	private final MethodDeclaration originalNode;
	List<String> paramList = new ArrayList<String>();
	List<String> localList = new ArrayList<String>();
	List<String> fieldList = new ArrayList<String>();
	List<String> constList = new ArrayList<String>();
	List<String> lambdaList = new ArrayList<String>();

	Map<String, List<String>> paramMethodMap = new HashMap<String, List<String>>();
	Map<String, List<String>> localMethodMap = new HashMap<String, List<String>>();
	Map<String, List<String>> fieldMethodMap = new HashMap<String, List<String>>(); // do not differentiate field method and static method
	List<String> thisMethodList = new ArrayList<String>();
	
	Map<String, Integer> stmtIdMap = new HashMap<String, Integer>();
	
	List<String> methodTokens = new LinkedList<String>();
	private int anonyClassOverrideCounts = 0;
	
	public AbstractMethodVisitor(final MethodDeclaration node) {
		this.originalNode = node;
	}
	
	/*
	 * AST
	 * 1. body: **Declaration
	 * 2. comments, ignored
	 * 3. statement
	 * 4. expression
	 * 5. module: ignored
	 */
	@Override
	public void process(Node node) {
		if (node instanceof MethodDeclaration) {
			// check if the root node or override in anonymous class
			if (node == this.originalNode) {
				int paramCount = 0;
				for(Parameter parameter : ((MethodDeclaration) node).getParameters()) {
					methodTokens.add("PARAM" + paramCount);
					paramList.add(parameter.getName().getIdentifier());
					paramMethodMap.put("PARAM" + paramCount++, new ArrayList<String>());
				}
			} else {
				if (node.getParentNode().get() instanceof ObjectCreationExpr) {
					methodTokens.add("ANONYCLASS.OVERRIDE" + anonyClassOverrideCounts++);
				}
			}
		} else if (node instanceof Statement) {
			stmtHandler((Statement) node);
		} else if (node instanceof Expression) {
			exprHandler((Expression) node);
		}
	}
	
	/*
	 * handle the code block in a method, return the abstracted statement,
	 * e.g. WHILE.IF.STMT, or much more details, WHILE.IF.STMT.PARAM.LOCAL, etc.
	 */
	private String computeNestedBlock(Node node) {
		return "";
	}
	
	private void exprHandler(Expression expr) {
		if (expr instanceof ArrayAccessExpr) {
			addVarToTokenList(expr.getChildNodes().get(0).toString(), "USE");			
		} else if (expr instanceof ArrayInitializerExpr) {
			for (Node node : expr.getChildNodes()) {
				if (node instanceof NameExpr) {
					addVarToTokenList(node.toString(), "USE");
				}
			}
		} else if (expr instanceof AssignExpr) {
			addVarToTokenList(expr.getChildNodes().get(0).toString(), "ASSIGN");
		} else if (expr instanceof CastExpr) {
			Node varToBeCast = expr.getChildNodes().get(1);
			if (varToBeCast instanceof NameExpr) {
				addVarToTokenList(varToBeCast.toString(), "CAST");
			}
		} else if (expr instanceof InstanceOfExpr) {
			Node varToCheck = expr.getChildNodes().get(0);
			if (varToCheck instanceof NameExpr) {
				addVarToTokenList(varToCheck.toString(), "INSTOF");
			}
		} else if (expr instanceof LiteralExpr) {
			int nameId = insertToListIfNotExist(constList, expr.getClass().toString() + expr.toString());
			methodTokens.add("CONST" + nameId + ".USE");
		} else if (expr instanceof LambdaExpr) {
			int nameId = insertToListIfNotExist(lambdaList, expr.toString());
			methodTokens.add("LAMBDA" + nameId);
		} else if (expr instanceof MethodCallExpr) {
			MethodCallExpr mce = (MethodCallExpr) expr;
			Node head = mce.getChildNodes().get(0);
			String methodStr = mce.getName().getIdentifier() + mce.getArguments().toString();
			if (head instanceof NameExpr) {
				String callerName = ((NameExpr) head).getName().getIdentifier();
				if (callerName == "this") {
					int methodId = insertToListIfNotExist(this.thisMethodList, methodStr);
					methodTokens.add("THIS.METHOD" + methodId);
				} else {
					int nameId = paramList.indexOf(callerName);
					if (nameId != -1) {
						List<String> methodList = paramMethodMap.get("PARAM" + nameId);
						int methodId = insertToListIfNotExist(methodList, methodStr);
						methodTokens.add("PARAM" + nameId + "." + "METHOD" + methodId);
					} else {
						nameId = localList.indexOf(callerName);
						if (nameId != -1) {
							List<String> methodList = localMethodMap.get("LOCALVAR" + nameId);
							int methodId = insertToListIfNotExist(methodList, methodStr);
							methodTokens.add("LOCALVAR" + nameId + "." + "METHOD" + methodId);
						} else {
							nameId = insertToListIfNotExist(fieldList, callerName);
							List<String> methodList = fieldMethodMap.get("FIELD" + nameId);
							if (methodList == null) {
								methodList = new ArrayList<String>();
								fieldMethodMap.put("FIELD" + nameId, methodList);
							}
							int methodId = insertToListIfNotExist(methodList, methodStr);
							methodTokens.add("FIELD" + nameId + "." + "METHOD" + methodId);
						}
					}	
				}
			} else {
				int methodId = insertToListIfNotExist(this.thisMethodList, methodStr);
				methodTokens.add("THIS.METHOD" + methodId);
			}
		} else if (expr instanceof SuperExpr) {
			methodTokens.add("SUPER");
		} else if (expr instanceof VariableDeclarationExpr) {
			// it will lose information if it is in a block and has same name with other variables outside; ignore this problem currently
			VariableDeclarationExpr vde = (VariableDeclarationExpr) expr;
			String varName = vde.findAll(VariableDeclarator.class).get(0).toString();
			int nameId = insertToListIfNotExist(localList, varName);
			localMethodMap.put("LOCALVAR" + nameId, new ArrayList<String>());
			methodTokens.add("LOCALVAR" + nameId);
		}
		// Unary & Binary
	}
	
	private void stmtHandler(Statement stmt) {
		
	}
	
	private void addVarToTokenList(String varName, String expl) {
		int nameId = paramList.indexOf(varName);
		if (nameId != -1) {
			methodTokens.add("PARAM" + nameId + "." + expl);
		} else {
			nameId = localList.indexOf(varName);
			if (nameId != -1) {
				methodTokens.add("LOCALVAR" + nameId + "." + expl);
			} else {
				nameId = insertToListIfNotExist(fieldList, varName);
				methodTokens.add("FIELD" + nameId + "." + expl);
			}
		}
	}
	
	private int insertToListIfNotExist(List<String> list, String str) {
		int index = list.indexOf(str);
		if (index != -1) {
			return index;
		}
		
		list.add(str);
		return list.size() - 1;
	}
	
	public void onFinish() {
		// TODO: write to file
		System.out.println(this.methodTokens);
	}

}
