package rowley.eclipse.notch.bindings.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A simple binding for manipulation of text
 */
public class TextManipulation {

	private ITextEditor editor;

	private IDocument document;

	public TextManipulation(ITextEditor editor) {
		this.editor = editor;
		this.document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
	}

	/**
	 * Adds text and then positions the cursor at the end of the text
	 * 
	 * @param text to insert
	 */
	public void addText(String text) {
		try {
			TextSelection textSelection = (TextSelection) editor.getSelectionProvider().getSelection();
			InsertEdit edit = new InsertEdit(textSelection.getOffset(), text);
			edit.apply(document);
			editor.selectAndReveal(textSelection.getOffset() + text.length(), 0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
