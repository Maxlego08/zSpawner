package fr.maxlego08.zspawner.command.commands;

import org.bukkit.OfflinePlayer;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerRemoveAll extends VCommand {

	public CommandSpawnerRemoveAll() {
		this.setPermission(Permission.ZSPAWNER_REMOVE_ALL);
		this.setDescription(Message.DESCRIPTION_REMOVE_ALL);

		this.DEBUG = true;
		
		this.addSubCommand("removeall");

		this.addRequireArg("player");
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {

		OfflinePlayer player = argAsOfflinePlayer(0);
		manager.removeSpawnerAll(sender, player);

		return CommandType.SUCCESS;
	}

}
