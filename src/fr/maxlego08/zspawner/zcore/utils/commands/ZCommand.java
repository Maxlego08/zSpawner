package fr.maxlego08.zspawner.zcore.utils.commands;

import java.util.function.BiConsumer;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;

public class ZCommand extends VCommand {

	private BiConsumer<VCommand, ZSpawnerPlugin> command;

	@Override
	public CommandType perform(ZSpawnerPlugin main) {
		
		if (command != null){
			command.accept(this, main);
		}

		return CommandType.SUCCESS;
	}

	public VCommand setCommand(BiConsumer<VCommand, ZSpawnerPlugin> command) {
		this.command = command;
		return this;
	}

	public VCommand sendHelp(String command) {
		this.command = (cmd, main) -> main.getCommandManager().sendHelp(command, cmd.getSender());
		return this;
	}

}
