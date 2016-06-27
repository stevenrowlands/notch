package rowley.eclipse.notch;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

public class ReloadCommandHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener arg0) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		NotchConfigurer.reconfigure();
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

	}

}
