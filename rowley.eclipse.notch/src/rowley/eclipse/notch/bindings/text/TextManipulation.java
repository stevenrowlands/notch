package rowley.eclipse.notch.bindings.text;

public interface TextManipulation {

    /**
     * Adds text and then positions the cursor at the end of the text
     * 
     * @param text to insert
     */
    void addText(String text);

    /**
     * @return the text preceding the current cursor position
     */
    String getPrecedingLineText();

    /**
     * Removes and returns the preceding line of text. Cursor will be positioned at the indentation of the line
     * 
     * @return the preceding line text that was removed
     */
    String removePrecedingLineText();

}