package rowley.eclipse.notch;

import org.eclipse.ui.IStartup;

public class NotchStartup implements IStartup {

	@Override
	public void earlyStartup() {
		NotchConfigurer.reconfigure();
	}
}
