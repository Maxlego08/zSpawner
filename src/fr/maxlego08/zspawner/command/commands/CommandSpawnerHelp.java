package fr.maxlego08.zspawner.command.commands;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerHelp extends VCommand {

	public CommandSpawnerHelp() {
		this.setPermission(Permission.ZSPAWNER_HELP);
		this.addSubCommand("help", "?", "aide");
		this.setDescription(Message.DESCRIPTION_HELP);
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {

		if (parent == null)
			return CommandType.SYNTAX_ERROR;

		parent.getSubVCommands().forEach(command -> {

			if (command.getDescription() != null && (command.getPermission() == null || hasPermission(sender, command.getPermission())))
				message(sender, Message.COMMAND_SYNTAXE_HELP, command.getSyntaxe(), command.getDescription());

		});

		return CommandType.SUCCESS;
	}

}
