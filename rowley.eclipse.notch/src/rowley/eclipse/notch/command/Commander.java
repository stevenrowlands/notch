package rowley.eclipse.notch.command;

import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.commands.ICommandService;

/**
 * This class is responsible for registering all scripts as eclipse commands
 */
public class Commander {

	private ICommandService commandService;

	public Commander(ICommandService commandService) {
		this.commandService = commandService;
	}

	public void undefine() {
		for (Command command : commandService.getDefinedCommands()) {
			if (command.isDefined() && command.getId().startsWith(CommandIdToNameMapper.COMMAND_PREFIX)) {
				command.undefine();
			}
		}
	}

	private Category getCategory() {
		Category category = commandService.getCategory(CommandIdToNameMapper.category());
		if (!category.isDefined()) {
			category.define("Notch", "Dynamic Commands Created by Notch");
		}
		return category;
	}

	public Command createCommand(String name) {
		Command command = commandService.getCommand(CommandIdToNameMapper.nameToCommandId(name));
		if (!command.isDefined()) {
			command.define(name, "Dynamic Command", getCategory());
		}
		return command;
	}
}
