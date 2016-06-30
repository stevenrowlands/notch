package rowley.eclipse.notch.command;

import java.util.List;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import rowley.eclipse.notch.Console;
import rowley.eclipse.notch.bindings.EditorBinding;
import rowley.eclipse.notch.bindings.java.JavaManipulationImpl;
import rowley.eclipse.notch.bindings.java.JavaProjectManipulation;
import rowley.eclipse.notch.bindings.text.TextManipulationImpl;

/**
 * This class handles all commands that have groovy scripts associated with them
 */
public class GroovyCommandHandler implements IHandler {

	private ScriptSourceProvider source;
	private Console console;

	public GroovyCommandHandler(ScriptSourceProvider source, Console console) {
		this.source = source;
		this.console = console;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
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
		binding.setProperty("console", console);
		if (editor instanceof ITextEditor) {
			binding.setProperty("text", new TextManipulationImpl((ITextEditor) editor));
		}
		try {
			String name = CommandIdToNameMapper.commandIdToName(event.getCommand().getId());
			shell.evaluate(source.getFile(name));
		} catch (Exception e) {
			console.write(e);
		}

		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener arg0) {
		// Do Nothing
	}

	@Override
	public void addHandlerListener(IHandlerListener arg0) {
		// Do Nothing
	}

	@Override
	public void dispose() {
		// Do Nothing
	}

}
