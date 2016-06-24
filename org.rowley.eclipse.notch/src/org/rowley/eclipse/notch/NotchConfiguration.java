package org.rowley.eclipse.notch;

import java.io.File;

import org.rowley.eclipse.notch.command.ScriptSourceProvider;

/**
 * Defines the configuration of the environment. This includes the location of
 * scripts for example
 */
public class NotchConfiguration implements ScriptSourceProvider {

	private File home;

	public NotchConfiguration() {
		this.home = new File(System.getProperty("user.home"), "scripts");
	}

	public NotchConfiguration(String path) {
		this.home = new File(path);
	}

	public File getScripts() {
		return home;
	}

	public File getFile(String name) {
		return new File(getScripts(), name);
	}

}
