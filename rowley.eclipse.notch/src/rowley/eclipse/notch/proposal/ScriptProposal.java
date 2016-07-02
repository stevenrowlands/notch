package rowley.eclipse.notch.proposal;

import java.io.File;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import rowley.eclipse.notch.NotchConfigurer;
import rowley.eclipse.notch.command.GroovyExecutor;

public class ScriptProposal implements ICompletionProposal {

    private File file;

    public ScriptProposal(File file) {
        this.file = file;
    }

    @Override
    public void apply(IDocument document) {
        try {
            new GroovyExecutor().execute(file, NotchConfigurer.console);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public IContextInformation getContextInformation() {
        IContextInformation context = new ContextInformation(file.getName(), file.getName());
        return context;
    }

    @Override
    public String getDisplayString() {
        return file.getName();
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public Point getSelection(IDocument arg0) {
        return null;
    }

}
