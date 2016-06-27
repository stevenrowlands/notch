package rowley.eclipse.notch.bindings;

import org.eclipse.ui.IEditorPart;

import groovy.lang.Binding;

public interface EditorBinding {

	public boolean handles(IEditorPart editor);

	public void addBindings(IEditorPart editor, Binding binding);
}
