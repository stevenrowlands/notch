package rowley.eclipse.notch.bindings.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.template.java.CompilationUnitContext;
import org.eclipse.jdt.internal.corext.template.java.CompilationUnitContextType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateTranslator;
import org.eclipse.jface.text.templates.TemplateVariable;
import rowley.eclipse.notch.Console;

import groovy.lang.Closure;

public class JavaTemplateManipulation implements ILinkedModeListener {

	private CompilationUnitEditor editor;

	private ICompilationUnit unit;

	private IType type;

	private TemplateGroup buffers = new TemplateGroup();

	private LinkedModeModel model;

	private List<LinkedPositionGroup> groups = new ArrayList<LinkedPositionGroup>();

	private List<String> vars = new ArrayList<String>();

	private Closure closure;

	public JavaTemplateManipulation(CompilationUnitEditor editor) {
		this.editor = editor;
		this.unit = (ICompilationUnit) editor.getViewPartInput();
		this.type = getType();
	}

	private IType getType() {
		try {
			return unit.getAllTypes()[0];
		} catch (JavaModelException e) {

			throw new RuntimeException(e);
		}
	}

	public void eval() {
		try {
			begun = false;
			IRewriteTarget target = (IRewriteTarget) editor.getAdapter(IRewriteTarget.class);
			if (target != null) {
				target.endCompoundChange();
			}
			boolean hasPositions = false;
			IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			LinkedModeModel model = new LinkedModeModel();
			this.model = model;
			this.model.addLinkingListener(this);
			TemplateVariable[] variables = buffers.getVariables();
			for (int i = 0; i != variables.length; ++i) {
				TemplateVariable variable = variables[i];

				if (variable.isUnambiguous()) {
					continue;
				}
				LinkedPositionGroup group = new LinkedPositionGroup();
				groups.add(group);
				vars.add(variable.getName());
				int[] offsets = variable.getOffsets();
				int length = variable.getLength();

				String[] values = variable.getValues();
				ICompletionProposal[] proposals = new ICompletionProposal[values.length];
				for (int j = 0; j < values.length; ++j) {
					// ensurePositionCategoryInstalled(document, model);
					Position pos = new Position(offsets[0], length);
					// document.addPosition(getCategory(), pos);
					proposals[j] = new PositionBasedCompletionProposal(values[j], pos, length);
				}
				LinkedPosition first;
				if (proposals.length > 1)
					first = new ProposalPosition(document, offsets[0], length, proposals);
				else {
					first = new LinkedPosition(document, offsets[0], length);
				}

				for (int j = 0; j != offsets.length; ++j) {
					if (j == 0)
						group.addPosition(first);
					else
						group.addPosition(new LinkedPosition(document, offsets[j], length));
				}
				model.addGroup(group);
				hasPositions = true;
			}

			if (hasPositions) {
				model.forceInstall();
				LinkedModeUI ui = new LinkedModeUI(model, editor.getViewer());
				ui.setExitPosition(editor.getViewer(), 0, 0, 2147483647);
				ui.enter();

			}

		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

	public int getPositionAtEndOfMethodsOrFields() {
		try {
			if (type.getMethods().length > 0) {
				IMethod method = type.getMethods()[type.getMethods().length - 1];
				return method.getSourceRange().getOffset() + method.getSourceRange().getLength();
			}
			return 0;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int calculateOffSetForAppendMethod() {
		int endMethodPosition = getPositionAtEndOfMethodsOrFields();
		int finalOffset = endMethodPosition;
		for (TemplateWrapper wrapper : buffers.getBuffers()) {
			if (wrapper.getStart() < endMethodPosition) {
				finalOffset += wrapper.getLength();
			}
		}
		return finalOffset;
	}

	public String newLine() {
		return System.getProperty("line.seperator", "\n");
	}

	private boolean begun = false;

	private void begin() {
		if (begun)
			return;
		IRewriteTarget target = (IRewriteTarget) editor.getAdapter(IRewriteTarget.class);
		if (target != null) {
			target.beginCompoundChange();
		}
		editor.doSave(null);
		begun = true;
	}

	public void template(String template, Closure closure) {
		this.closure = closure;
		TextSelection textSelection = (TextSelection) editor.getSelectionProvider().getSelection();
		Template t = new Template("xxxx", "xxxx", "java-statements", template, true);
		CompilationUnitContextType contextType = (CompilationUnitContextType) JavaPlugin.getDefault()
				.getTemplateContextRegistry().getContextType("java-statements");
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		CompilationUnitContext context = contextType.createContext(document, textSelection.getOffset(), 0, unit);

		int start = context.getStart();
		int end = context.getEnd();
		IRegion region = new Region(start, end - start);
		// editor.getViewer(), " ".charAt(0), 0, start);

		boolean hasPositions = false;
		try {

			TemplateBuffer templateBuffer = context.evaluate(t);
			String templateString = templateBuffer.getString();
			document.replace(start, end - start, templateString);

			LinkedModeModel model = new LinkedModeModel();
			this.model = model;
			model.addLinkingListener(this);
			TemplateVariable[] variables = templateBuffer.getVariables();
			for (int i = 0; i != variables.length; ++i) {
				TemplateVariable variable = variables[i];

				if (variable.isUnambiguous()) {
					continue;
				}
				LinkedPositionGroup group = new LinkedPositionGroup();
				this.groups.add(group);
				vars.add(variable.getName());
				int[] offsets = variable.getOffsets();
				int length = variable.getLength();

				String[] values = variable.getValues();
				ICompletionProposal[] proposals = new ICompletionProposal[values.length];
				for (int j = 0; j < values.length; ++j) {
					// ensurePositionCategoryInstalled(document, model);
					Position pos = new Position(offsets[0] + start, length);
					// document.addPosition(getCategory(), pos);
					proposals[j] = new PositionBasedCompletionProposal(values[j], pos, length);
				}
				LinkedPosition first;
				if (proposals.length > 1)
					first = new ProposalPosition(document, offsets[0] + start, length, proposals);
				else {
					first = new LinkedPosition(document, offsets[0] + start, length);
				}

				for (int j = 0; j != offsets.length; ++j) {
					if (j == 0)
						group.addPosition(first);
					else
						group.addPosition(new LinkedPosition(document, offsets[j] + start, length));
				}
				model.addGroup(group);
				hasPositions = true;
			}

			if (hasPositions) {
				model.forceInstall();
				LinkedModeUI ui = new LinkedModeUI(model, editor.getViewer());
				ui.setExitPosition(editor.getViewer(), getCaretOffset(templateBuffer) + start, 0, 2147483647);
				ui.enter();

			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void template(String template) {
		begin();
		TextSelection textSelection = (TextSelection) editor.getSelectionProvider().getSelection();
		Template t = new Template("xxxx", "xxxx", "java-statements", template, true);
		CompilationUnitContextType contextType = (CompilationUnitContextType) JavaPlugin.getDefault()
				.getTemplateContextRegistry().getContextType("java-statements");
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		CompilationUnitContext context = contextType.createContext(document, textSelection.getOffset(), 0, unit);

		int start = context.getStart();
		int end = context.getEnd();
		IRegion region = new Region(start, end - start);
		try {
			TemplateBuffer templateBuffer = context.evaluate(t);
			String templateString = templateBuffer.getString();
			document.replace(start, end - start, templateString);
			int length = templateString.length();
			int off = start + templateString.length();
			buffers.addTemplate(start, templateString.length(), templateBuffer);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

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

	public void doAfter(Closure closure) {
		this.closure = closure;
	}

	@Override
	public void left(LinkedModeModel arg0, int arg1) {
		try {
			if (closure == null)
				return;
//			editor.doSave(null);
			IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			Map<String, String> vars = new HashMap<String, String>();
			for (int i = 0; i < groups.size(); i++) {
				LinkedPositionGroup group = groups.get(i);
				String name = this.vars.get(i);
				int offset = group.getPositions()[0].getOffset();
				int length = group.getPositions()[0].getLength();
				String value = document.get(offset, length);
				closure.setProperty(name, value);
			}
			closure.call(vars);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void suspend(LinkedModeModel arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume(LinkedModeModel arg0, int arg1) {
		// TODO Auto-generated method stub

	}
}
