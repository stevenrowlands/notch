package rowley.eclipse.notch.bindings;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import rowley.eclipse.notch.bindings.text.TextManipulationImpl;

import groovy.lang.Binding;

public class TextEditorBinding implements EditorBinding {

	@Override
	public boolean handles(IEditorPart editor) {
		return editor instanceof ITextEditor;
	}

	@Override
	public void addBindings(IEditorPart editor, Binding binding) {
		binding.setProperty("text", new TextManipulationImpl((ITextEditor) editor));
	}

}
