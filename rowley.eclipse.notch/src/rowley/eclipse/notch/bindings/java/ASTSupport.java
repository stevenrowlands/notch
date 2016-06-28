package rowley.eclipse.notch.bindings.java;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;

public class ASTSupport {

	public FieldDeclaration getLastField(AbstractTypeDeclaration typeDeclaration) {
		FieldDeclaration field = null;
		for (ASTNode childNode : ASTNodes.getChildren(typeDeclaration)) {
			if (childNode instanceof FieldDeclaration) {
				field = (FieldDeclaration) childNode;
			}
		}
		return field;
	}

	public MethodDeclaration parse(String contents) {
		ASTParser ppp = ASTParser.newParser(AST.JLS8);
		ppp.setSource(contents.toCharArray());
		ppp.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
		ASTNode node = ppp.createAST(null);
		TypeDeclaration block = (TypeDeclaration) node;
		MethodDeclaration m = block.getMethods()[0];
		
		return m;
	}
	
	public ASTNode parseNode(String contents) {
		ASTParser ppp = ASTParser.newParser(AST.JLS8);
		ppp.setSource(contents.toCharArray());
		ppp.setKind(ASTParser.K_EXPRESSION);
		ASTNode node = ppp.createAST(null);
		return node;
	}



	public CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		org.eclipse.jdt.core.dom.CompilationUnit astRoot = (org.eclipse.jdt.core.dom.CompilationUnit) parser.createAST(null);
		return astRoot;
	}
	
	public MethodDeclaration getMethod(AbstractTypeDeclaration typeDeclaration, String name) {
		for (ASTNode childNode : ASTNodes.getChildren(typeDeclaration)) {
			if (childNode instanceof MethodDeclaration) {
				if (((MethodDeclaration) childNode).getName().getIdentifier().equals(name)) {
					return (MethodDeclaration) childNode;
				}
			}
		}
		return null;
	}

	public MethodDeclaration getFirstMethod(AbstractTypeDeclaration typeDeclaration) {
		for (ASTNode childNode : ASTNodes.getChildren(typeDeclaration)) {
			if (childNode instanceof MethodDeclaration) {
				return (MethodDeclaration) childNode;
			}
		}
		return null;
	}
}
