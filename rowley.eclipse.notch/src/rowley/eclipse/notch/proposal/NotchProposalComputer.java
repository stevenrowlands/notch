package rowley.eclipse.notch.proposal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

import rowley.eclipse.notch.NotchConfigurer;

public class NotchProposalComputer implements IJavaCompletionProposalComputer {

	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext arg0, IProgressMonitor arg1) {
		
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		try {
			String s = arg0.computeIdentifierPrefix().toString();
			for (File file : NotchConfigurer.scripts) {
				if (file.getName().startsWith(s)) {
					ICompletionProposal propsoal = new ScriptProposal(file);
					proposals.add(propsoal);
				}
			}
		} catch (Exception e) {
			return proposals;
		}
		return proposals;
	}

	@Override
	public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext arg0,
			IProgressMonitor arg1) {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public void sessionEnded() {

	}

	@Override
	public void sessionStarted() {

	}

}
