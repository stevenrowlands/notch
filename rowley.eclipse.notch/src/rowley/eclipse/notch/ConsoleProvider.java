package rowley.eclipse.notch;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

public class ConsoleProvider {

	private static final String CONSOLE_NAME = "NotchConsole";

	public static MessageConsole getConsole() {
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		IConsole[] existing = consoleManager.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (CONSOLE_NAME.equals(existing[i].getName()))
				return (MessageConsole) existing[i];

		MessageConsole console = new MessageConsole(CONSOLE_NAME, null);
		consoleManager.addConsoles(new IConsole[] { console });
		return console;

	}
}
