package org.rowley.eclipse.notch;

import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class EclipseConsole implements Console {

	private MessageConsole console;
	private MessageConsoleStream out;

	public EclipseConsole(MessageConsole console) {
		this.console = console;
		this.out = console.newMessageStream();

	}

	@Override
	public void write(String message) {
		out.println(message);
	}

	@Override
	public void write(Throwable t) {
		out.print(t.toString());
		t.printStackTrace();
	}
}
