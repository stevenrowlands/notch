package rowley.eclipse.notch.bindings.java;

import org.codehaus.groovy.control.CompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;

public class JavaFocusSupport {

	private CompilationUnitEditor editor;

	public JavaFocusSupport(CompilationUnitEditor editor) {
		this.editor = editor;	
	}
	
	public void focusAfter(IMember type) {
		try {
			ISourceRange range = type.getSourceRange();
			editor.selectAndReveal(range.getOffset() + range.getLength(), 0);
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void focusBefore(IMember type) {
		try {
			ISourceRange range = type.getSourceRange();
			editor.selectAndReveal(range.getOffset(), 0);
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}

	public void focus(IMember type) {
		try {
			ISourceRange range = type.getSourceRange();
			editor.selectAndReveal(range.getOffset(), range.getLength());
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}
}
