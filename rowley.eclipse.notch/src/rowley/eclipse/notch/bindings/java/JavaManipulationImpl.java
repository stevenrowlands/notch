package rowley.eclipse.notch.bindings.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.corext.codemanipulation.AddGetterSetterOperation;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.template.java.CompilationUnitContext;
import org.eclipse.jdt.internal.corext.template.java.CompilationUnitContextType;
import org.eclipse.jdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jdt.internal.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.text.edits.TextEdit;

import groovy.lang.Closure;

/**
 * Manipulates java code
 */
public class JavaManipulationImpl implements JavaManipulation {

	private CompilationUnitEditor editor;

	private ICompilationUnit unit;

	private IDocument document;

	private IType primaryType;

	private JavaFocusSupport focus;

	private ASTSupport astSupport;

	public JavaManipulationImpl(CompilationUnitEditor editor) {
		this.editor = editor;
		this.unit = (ICompilationUnit) editor.getViewPartInput();
		this.document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		this.primaryType = getType();
		this.focus = new JavaFocusSupport(editor);
		this.astSupport = new ASTSupport();
	}

	private IType getType() {
		try {
			return unit.getAllTypes()[0];
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IImportDeclaration addImport(String imp) {
		try {
			return unit.createImport(imp, null, null);
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IMethod addConstructor(String contents) {
		try {
			editor.doSave(null);

			CompilationUnit astRoot = astSupport.parse(unit);
			AbstractTypeDeclaration declaration = (AbstractTypeDeclaration) ASTNodes
					.getParent(NodeFinder.perform(astRoot, primaryType.getNameRange()), AbstractTypeDeclaration.class);

			ASTRewrite astRewrite = ASTRewrite.create(astRoot.getAST());
			ListRewrite listRewriter = astRewrite.getListRewrite(declaration,
					declaration.getBodyDeclarationsProperty());

			String delimiter = StubUtility.getLineDelimiterUsed(primaryType);
			Initializer dd = (Initializer) listRewriter.getASTRewrite().createStringPlaceholder(CodeFormatterUtil
					.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, contents, 0, delimiter, unit.getJavaProject()),
					ASTNode.INITIALIZER);

			MethodDeclaration parsedNamed = astSupport.parse(contents);
			String name = parsedNamed.getName().getIdentifier();

			FieldDeclaration lastField = astSupport.getLastField(declaration);
			MethodDeclaration fm = astSupport.getFirstMethod(declaration);
			if (lastField != null) {
				listRewriter.insertAfter(dd, lastField, null);
			} else if (fm != null) {
				listRewriter.insertBefore(dd, fm, null);
			} else {
				listRewriter.insertFirst(dd, null);
			}
			TextEdit edit = astRewrite.rewriteAST();
			JavaModelUtil.applyEdit(unit, edit, false, null);

			editor.doSave(null);
			int params = parsedNamed.parameters().size();

			for (IMethod i : primaryType.getMethods()) {
				if (i.getElementName().equals(name) && i.getParameterNames().length == params) {
					for (int j = 0; j < i.getParameterNames().length; j++) {
						SingleVariableDeclaration dec = (SingleVariableDeclaration)parsedNamed.parameters().get(j);
						SimpleType type = (SimpleType)dec.getType();
						String fqn = type.getName().getFullyQualifiedName();
						return i;
					}
				}
			}
			
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IMethod addMethod(String contents) {
		try {
//			editor.doSave(null);

			CompilationUnit astRoot = astSupport.parse(unit);
			AbstractTypeDeclaration declaration = (AbstractTypeDeclaration) ASTNodes
					.getParent(NodeFinder.perform(astRoot, primaryType.getNameRange()), AbstractTypeDeclaration.class);

			ASTRewrite astRewrite = ASTRewrite.create(astRoot.getAST());
			ListRewrite listRewriter = astRewrite.getListRewrite(declaration,
					declaration.getBodyDeclarationsProperty());

			String delimiter = StubUtility.getLineDelimiterUsed(primaryType);
			MethodDeclaration dd = (MethodDeclaration) listRewriter.getASTRewrite()
					.createStringPlaceholder(CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, contents,
							0, delimiter, unit.getJavaProject()), ASTNode.METHOD_DECLARATION);
			MethodDeclaration parsedNamed = astSupport.parse(contents);
			String name = parsedNamed.getName().getIdentifier();

			FieldDeclaration lastField = astSupport.getLastField(declaration);
			MethodDeclaration fm = astSupport.getFirstMethod(declaration);

			listRewriter.insertLast(dd, null);
			TextEdit edit = astRewrite.rewriteAST();
			JavaModelUtil.applyEdit(unit, edit, false, null);
			return getMethod(name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private IMethod getMethod(String name) {
		try {
			for (IMethod method : primaryType.getMethods()) {
				if (method.getElementName().equals(name)) {
					return method;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	private IField getField(String name) {
		try {
			for (IField field : primaryType.getFields()) {
				if (field.getElementName().equals(name)) {
					return field;
				}
			}
			return null;
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * addField at the start of the class
	 * 
	 * @param declaration
	 */
	@Override
	public IField addFieldFirst(String content) {
		try {
			VariableDeclarationStatement var = processDeclaration(content);
			VariableDeclarationFragment frag = getFragment(var);
			frag.getParent();
			String name = frag.getName().getIdentifier();
			if (!primaryType.getField(name).exists()) {
				IField firstField = null;
				if (primaryType.getFields().length > 1) {
					firstField = primaryType.getFields()[0];
				}
				primaryType.createField(content, firstField, false, null);
			}
			
			return getField(name);
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}


	/**
	 * addField at the end of the classes field list
	 * 
	 * @param declaration
	 */
	@Override
	public IField addFieldLast(String content) {
		try {
			VariableDeclarationStatement var = processDeclaration(content);
			VariableDeclarationFragment frag = getFragment(var);
			frag.getParent();
			String name = frag.getName().getIdentifier();
			if (!primaryType.getField(name).exists()) {
				primaryType.createField(content, null, false, null);
			}
			return getField(name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Default addField - will add new static fields at the top. new general
	 * fields at the bottom
	 * 
	 * @param declaration
	 */
	public void addField(String declaration) {
		try {
			VariableDeclarationStatement var = processDeclaration(declaration);
			VariableDeclarationFragment frag = getFragment(var);
			frag.getParent();
			String name = frag.getName().getIdentifier();
			if (!primaryType.getField(name).exists()) {
				IField firstField = null;
				if (primaryType.getFields().length > 1) {
					firstField = primaryType.getFields()[0];
				}
				if (Flags.isStatic(var.getModifiers())) {
					primaryType.createField(declaration, firstField, false, null);
				} else {
					primaryType.createField(declaration, null, false, null);
				}
			}
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

	private IField[] getSetterFields() {
		try {
			List<IField> fields = new ArrayList<IField>();
			for (IField field : primaryType.getFields()) {
				if (Flags.isFinal(field.getFlags()) || Flags.isAbstract(field.getFlags())
						|| Flags.isStatic(field.getFlags()))
					continue;

				fields.add(field);
			}
			return fields.toArray(new IField[fields.size()]);
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

	private IField[] getGetterFields() {
		try {
			List<IField> fields = new ArrayList<IField>();
			for (IField field : primaryType.getFields()) {
				if (Flags.isAbstract(field.getFlags()) || Flags.isStatic(field.getFlags()))
					continue;

				fields.add(field);
			}
			return fields.toArray(new IField[fields.size()]);
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

	private IJavaElement getInsertPoint() {
		try {
			IJavaElement sibling = null;
			if (primaryType.getMethods().length > 0) {
				sibling = primaryType.getMethods()[primaryType.getMethods().length - 1];
			} else if (primaryType.getFields().length > 0) {
				sibling = primaryType.getFields()[primaryType.getFields().length - 1];
			}
			return sibling;
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}
	
	public JavaManipulationImpl generateGettersAndSetters() {
		try {
			ASTParser pp = ASTParser.newParser(AST.JLS8);
			pp.setKind(ASTParser.K_COMPILATION_UNIT);
			pp.setSource(unit);
			pp.setResolveBindings(true);
			org.eclipse.jdt.core.dom.CompilationUnit astRoot = (org.eclipse.jdt.core.dom.CompilationUnit) pp
					.createAST(null);

			CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings(unit.getJavaProject());
			AddGetterSetterOperation op = new AddGetterSetterOperation(primaryType, getSetterFields(),
					getSetterFields(), new IField[0], astRoot, null, null, settings, true, false);
			op.setSkipAllExisting(true);
			IRunnableContext context = JavaPlugin.getActiveWorkbenchWindow();
			if (context == null) {
				context = new BusyIndicatorRunnableContext();
			}
			op.run(null);
			return this;
		} catch (Exception e) {

			throw new RuntimeException(e);
		}

	}

	private String getCategory() {
		return "TemplateProposalCategory_" + super.toString();
	}

	public String getClassName() {
		return getName() + ".class";
	}

	public String getName() {
		return unit.getElementName().replace(".java", "");
	}


	private int getCaretOffset(TemplateBuffer buffer) {
		TemplateVariable[] variables = buffer.getVariables();
		for (int i = 0; i != variables.length; ++i) {
			TemplateVariable variable = variables[i];
			if (variable.getType().equals("cursor")) {
				return variable.getOffsets()[0];
			}
		}
		return buffer.getString().length();
	}

	public void template(String template) {
		JavaTemplateManipulation jt = new JavaTemplateManipulation(this.editor);
		jt.template(template, null);
	}

	public void template(String template, Closure closure) {
		JavaTemplateManipulation jt = new JavaTemplateManipulation(this.editor);
		jt.template(template, closure);
	}


	private VariableDeclarationStatement processDeclaration(String declaration) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(declaration.toCharArray());
		parser.setKind(ASTParser.K_STATEMENTS);
		ASTNode node = parser.createAST(null);
		Block block = (Block) node;
		VariableDeclarationStatement var = (VariableDeclarationStatement) block.statements().get(0);
		return var;

	}

	private VariableDeclarationFragment getFragment(VariableDeclarationStatement var) {
		try {
			for (Object fragment : var.fragments()) {
				if (fragment instanceof VariableDeclarationFragment) {
					VariableDeclarationFragment cdf = (VariableDeclarationFragment) fragment;
					return cdf;
				}
			}
			return null;
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

	@Override
	public void focusAfter(IMember type) {
		focus.focusAfter(type);
	}

	@Override
	public void focusBefore(IMember type) {
		focus.focusBefore(type);
	}

	@Override
	public void focus(IMember type) {
		focus.focus(type);
	}

}
