package fr.maxlego08.zspawner.command.commands;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.command.commands.levels.CommandSpawnerLevel;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawner extends VCommand {

	public CommandSpawner() {

		this.setPermission(Permission.ZSPAWNER_USE);
		this.setConsoleCanUse(false);

		this.DEBUG = true;

		this.addSubCommand(new CommandSpawnerAdd());
		this.addSubCommand(new CommandSpawnerRemove());
		this.addSubCommand(new CommandSpawnerRemoveAll());
		this.addSubCommand(new CommandSpawnerGive());
		this.addSubCommand(new CommandSpawnerVersion());
		this.addSubCommand(new CommandSpawnerReload());
		this.addSubCommand(new CommandSpawnerSend());
		this.addSubCommand(new CommandSpawnerHelp());
		this.addSubCommand(new CommandSpawnerInfo());
		this.addSubCommand(new CommandSpawnerPickaxe());
		this.addSubCommand(new CommandSpawnerLevel());
		this.addSubCommand(new CommandSpawnerConfig());

	}

	@Override
	protected CommandType perform(ZSpawnerPlugin plugin) {
		manager.openInventory(player);
		return CommandType.SUCCESS;
	}

}
