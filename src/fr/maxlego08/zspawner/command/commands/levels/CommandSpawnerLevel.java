package fr.maxlego08.zspawner.command.commands.levels;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerLevel extends VCommand {

	public CommandSpawnerLevel() {
		this.setPermission(Permission.ZSPAWNER_LEVEL_USE);
		this.setDescription(Message.DESCRIPTION_LEVEL);
		this.addSubCommand("level");
		this.addSubCommand(new CommandLevelCreate());
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {

		this.subVCommands.forEach(command -> {
			if (command.getDescription() != null
					&& (command.getPermission() == null || hasPermission(sender, command.getPermission())))
				messageWO(sender, Message.COMMAND_SYNTAXE_HELP, command.getSyntaxe(), command.getDescription());
		});

		return CommandType.SUCCESS;
	}

}
