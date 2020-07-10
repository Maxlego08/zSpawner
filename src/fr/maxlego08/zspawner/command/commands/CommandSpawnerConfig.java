package fr.maxlego08.zspawner.command.commands;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerConfig extends VCommand {

	public CommandSpawnerConfig() {
		this.setPermission(Permission.ZSPAWNER_CONFIF);
		this.addSubCommand("config");
		this.setDescription(Message.DESCRIPTION_CONFIG);
		this.setConsoleCanUse(false);
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {
		manager.openConfig(player);
		return CommandType.SUCCESS;
	}

}
