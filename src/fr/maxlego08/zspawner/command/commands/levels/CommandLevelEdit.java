package fr.maxlego08.zspawner.command.commands.levels;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.command.commands.levels.edits.CommandLevelEditMinDelay;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandLevelEdit extends VCommand {

	public CommandLevelEdit() {
		this.setPermission(Permission.ZSPAWNER_LEVEL_EDIT);
		this.setDescription(Message.DESCRIPTION_LEVEL_EDIT);
		this.addSubCommand("edit");
		this.addSubCommand("update");
		this.addSubCommand(new CommandLevelEditMinDelay());
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
