package org.rowley.eclipse.notch.bindings;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.ui.IEditorPart;
import org.rowley.eclipse.notch.bindings.java.JavaManipulationImpl;

import groovy.lang.Binding;

public class JavaEditorBinding implements EditorBinding {

	@Override
	public boolean handles(IEditorPart editor) {
		return editor instanceof CompilationUnitEditor;
	}

	@Override
	public void addBindings(IEditorPart editor, Binding binding) {
		CompilationUnitEditor javaEditor = (CompilationUnitEditor) editor;
		binding.setProperty("java", new JavaManipulationImpl(javaEditor));
	}

}
