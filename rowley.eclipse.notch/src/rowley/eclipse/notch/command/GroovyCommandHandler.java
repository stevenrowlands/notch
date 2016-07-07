package rowley.eclipse.notch.command;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

import rowley.eclipse.notch.Console;


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
		try {
			String name = CommandIdToNameMapper.commandIdToName(event.getCommand().getId());
			new GroovyExecutor().execute(source.getFile(name), console);
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
