package org.rowley.eclipse.notch;

public interface Console {

	void write(String message);

	void write(Throwable t);
}
