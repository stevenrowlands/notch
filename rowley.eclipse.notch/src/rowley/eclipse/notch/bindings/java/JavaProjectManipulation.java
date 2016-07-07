package rowley.eclipse.notch.bindings.java;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Manipulates java code
 */
public class JavaProjectManipulation extends AbstractJavaManipulation {
	
	public void addFile(String relativeFilePath, String content) {
		try {
			IProject project = getUnit().getJavaProject().getProject();
			String[] parts = relativeFilePath.split("/");
			IFolder currentFolder = project.getFolder(parts[0]);
			if (!currentFolder.exists()) {
				currentFolder.create(true, true, null);
			}
			for (int i = 1; i < parts.length - 1; i++) {
				currentFolder = currentFolder.getFolder(parts[i]);
				if (!currentFolder.exists()) {
					currentFolder.create(true, true, null);
				}
			}

			IFile file = currentFolder.getFile(parts[parts.length - 1]);
			if (!file.exists()) {
				file.create(new ByteArrayInputStream(content.getBytes()), true, null);
			}
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.getLocationURI());
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			IDE.openEditorOnFileStore(page, fileStore);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
}
