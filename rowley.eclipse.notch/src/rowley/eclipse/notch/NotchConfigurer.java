package rowley.eclipse.notch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;

import rowley.eclipse.notch.command.Commander;
import rowley.eclipse.notch.command.GroovyCommandHandler;
import rowley.eclipse.notch.pref.PreferenceConstants;

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

    private static List<IHandlerActivation> handlers = new ArrayList<IHandlerActivation>();

    public static List<File> scripts = new ArrayList<File>();

    public static Console console;

    public static void reconfigure() {
        ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
        IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
        MessageConsole message = ConsoleProvider.getConsole();
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String scriptPathPreference = store.getString(PreferenceConstants.P_PATH);

        NotchConfiguration configuration = new NotchConfiguration(scriptPathPreference);
        Commander commandCreator = new Commander(commandService);
        commandCreator.undefine();

        console = new EclipseConsole(message);

        console.write("scanning " + configuration.getScripts().getAbsolutePath());
        File[] files = configuration.getScripts().listFiles();
        if (files == null) {
            files = new File[0];
        }
        if (files.length == 0) {
            console.write("no scripts found");
        }
        handlerService.deactivateHandlers(handlers);
        handlers.clear();
        for (File script : files) {
            if (!script.getAbsolutePath().toLowerCase().endsWith(".groovy"))
                continue;

            console.write("  " + script.getAbsolutePath());
            String simpleName = script.getName();
            Command command = commandCreator.createCommand(simpleName);
            GroovyCommandHandler handler = new GroovyCommandHandler(configuration, console);
            scripts.add(script);
            handlers.add(handlerService.activateHandler(command.getId(), handler));
        }
    }

}
