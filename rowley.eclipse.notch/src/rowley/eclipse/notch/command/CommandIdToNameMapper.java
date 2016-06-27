package rowley.eclipse.notch.command;

/**
 * This class handles the mapping between a commands id and the corresponding
 * real script name
 */
public class CommandIdToNameMapper {

	private static final String CATEGORY = "rowley.eclipse.notch";
	public static final String COMMAND_PREFIX = CATEGORY + ".";

	public static String category() {
		return CATEGORY;
	}

	public static String commandIdToName(String commandId) {
		return commandId.replace(COMMAND_PREFIX, "");
	}

	public static String nameToCommandId(String name) {
		return COMMAND_PREFIX + name;
	}
}
