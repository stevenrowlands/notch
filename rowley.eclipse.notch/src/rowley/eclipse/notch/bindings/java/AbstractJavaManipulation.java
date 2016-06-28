package rowley.eclipse.notch.bindings.java;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class AbstractJavaManipulation {
	
	protected CompilationUnitEditor getEditor() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = activeWindow.getActivePage();
		IEditorPart editor = activePage.getActiveEditor();
		return (CompilationUnitEditor) editor;
	}

	protected ICompilationUnit getUnit() {
		return (ICompilationUnit) getEditor().getViewPartInput();
	}

}
