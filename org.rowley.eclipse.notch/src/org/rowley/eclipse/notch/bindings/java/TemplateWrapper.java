package org.rowley.eclipse.notch.bindings.java;

import org.eclipse.jface.text.templates.TemplateBuffer;

public class TemplateWrapper {

	private int start;

	private int length;
	private TemplateBuffer buffer;
	
	public int shift;

	public TemplateWrapper(int start, int length, TemplateBuffer buffer) {
		this.start = start;
		this.length = length;
		this.buffer = buffer;

	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public TemplateBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(TemplateBuffer buffer) {
		this.buffer = buffer;
	}

}
