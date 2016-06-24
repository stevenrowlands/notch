package org.rowley.eclipse.notch.bindings;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rowley.eclipse.notch.bindings.text.TextManipulation;

import groovy.lang.Binding;

public class TextEditorBinding implements EditorBinding {

	@Override
	public boolean handles(IEditorPart editor) {
		return editor instanceof ITextEditor;
	}

	@Override
	public void addBindings(IEditorPart editor, Binding binding) {
		binding.setProperty("text", new TextManipulation((ITextEditor) editor));
	}

}
