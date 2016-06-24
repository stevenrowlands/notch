package org.rowley.eclipse.notch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.handlers.IHandlerService;
import org.rowley.eclipse.notch.bindings.EditorBinding;
import org.rowley.eclipse.notch.bindings.JavaEditorBinding;
import org.rowley.eclipse.notch.bindings.TextEditorBinding;
import org.rowley.eclipse.notch.command.Commander;
import org.rowley.eclipse.notch.command.GroovyCommandHandler;
import org.rowley.eclipse.notch.pref.PreferenceConstants;

/**
 * Static configuration class for getting all the classes configured.
 * 
 * Not sure how to access eclipse services in a non static way at present.
 * 
 * This class handles all weird static accessing and configures all required
 * services for the notch plugin
 * 
 */
public class NotchConfigurer {

	public static void reconfigure() {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
		MessageConsole message = ConsoleProvider.getConsole();
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String scriptPathPreference = store.getString(PreferenceConstants.P_PATH);
		
		NotchConfiguration configuration = new NotchConfiguration(scriptPathPreference);
		Commander commandCreator = new Commander(commandService);
		commandCreator.undefine();

		EclipseConsole console = new EclipseConsole(message);

		List<EditorBinding> bindings = new ArrayList<EditorBinding>();
		bindings.add(new JavaEditorBinding());
		bindings.add(new TextEditorBinding());

		for (File script : configuration.getScripts().listFiles()) {
			String simpleName = script.getName();
			Command command = commandCreator.createCommand(simpleName);
			handlerService.activateHandler(command.getId(), new GroovyCommandHandler(configuration, console, bindings));
		}

	}

}
