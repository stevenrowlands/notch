package rowley.eclipse.notch.pref;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import rowley.eclipse.notch.Activator;
import rowley.eclipse.notch.NotchConfigurer;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Notch");
	}

	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH, "&Default Script Directory",
				getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}

	public boolean performOk() {
		boolean result = super.performOk();
		NotchConfigurer.reconfigure();
		return result;
	}

}