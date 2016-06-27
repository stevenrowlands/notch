package rowley.eclipse.notch.pref;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import rowley.eclipse.notch.Activator;
import rowley.eclipse.notch.NotchConfiguration;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_PATH, new NotchConfiguration().getScripts().getAbsolutePath());
	}

}
