package fr.maxlego08.zspawner.command.commands;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawner extends VCommand {

	private final CommandSpawnerHelp commandHelp = new CommandSpawnerHelp();
	
	public CommandSpawner() {
		
		this.setPermission(Permission.ZSPAWNER_USE);
		
		this.addSubCommand(commandHelp);
		
	}
	
	@Override
	protected CommandType perform(ZSpawnerPlugin plugin) {
		
		commandHelp.sender = sender;
		commandHelp.perform(plugin);
		
		return CommandType.SUCCESS;
	}

}
