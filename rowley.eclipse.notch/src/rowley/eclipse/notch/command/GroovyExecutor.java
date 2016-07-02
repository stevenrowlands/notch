package rowley.eclipse.notch.command;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import rowley.eclipse.notch.Console;
import rowley.eclipse.notch.bindings.java.JavaManipulationImpl;
import rowley.eclipse.notch.bindings.java.JavaProjectManipulation;
import rowley.eclipse.notch.bindings.text.TextBinding;

public class GroovyExecutor {

	public void execute(File file, Console console) throws CompilationFailedException, IOException {
		Binding binding = new Binding();
		CompilerConfiguration config = new CompilerConfiguration();
		GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding, config);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = activeWindow.getActivePage();
		IEditorPart editor = activePage.getActiveEditor();
		editor.setFocus();

		binding.setProperty("editor", editor);

		binding.setProperty("java", new JavaManipulationImpl());
		binding.setProperty("javaproject", new JavaProjectManipulation());
		if (console != null)
			binding.setProperty("console", console);

		if (editor instanceof ITextEditor)
			binding.setProperty("text", new TextBinding((ITextEditor) editor));

		shell.evaluate(file);
	}
}
