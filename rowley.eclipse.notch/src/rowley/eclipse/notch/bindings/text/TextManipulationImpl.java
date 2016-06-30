package rowley.eclipse.notch.bindings.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * A simple binding for manipulation of text
 */
public class TextManipulationImpl implements TextManipulation {

	private ITextEditor editor;

	private IDocument document;

	public TextManipulationImpl(ITextEditor editor) {
		this.editor = editor;
		this.document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
	}

	/* (non-Javadoc)
	 * @see rowley.eclipse.notch.bindings.text.TextManipulation#addText(java.lang.String)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see rowley.eclipse.notch.bindings.text.TextManipulation#getPrecedingLineText()
	 */
	@Override
	public String getPrecedingLineText() {
		try {
			TextSelection textSelection = (TextSelection) editor.getSelectionProvider().getSelection();
			IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			int length = 1;
			String content = document.get(textSelection.getOffset() - 0, 0);
			String nextChar = "";
			while (!nextChar.contains("\n") && !nextChar.contains("\r") && (textSelection.getOffset()-length-1) > 0) {
				content = nextChar + content;
				nextChar = document.get(textSelection.getOffset() - length, 1);
				length++;
			}
			while (content.startsWith("\t")) {
				content = content.replaceFirst("\t", "");
			}
			while(content.startsWith(" ")) {
				content = content.replaceFirst(" ", "");
			}
			return content;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see rowley.eclipse.notch.bindings.text.TextManipulation#removePrecedingLineText()
	 */
	@Override
	public String removePrecedingLineText() {
		try {
			TextSelection textSelection = (TextSelection) editor.getSelectionProvider().getSelection();
			IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			String content = getPrecedingLineText();
			document.replace(textSelection.getOffset() - content.length(), content.length(), "");
			return content;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
