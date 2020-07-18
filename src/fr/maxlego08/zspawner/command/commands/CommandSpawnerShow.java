package fr.maxlego08.zspawner.command.commands;

import org.bukkit.OfflinePlayer;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerShow extends VCommand {

	public CommandSpawnerShow() {
		this.setPermission(Permission.ZSPAWNER_SHOW);
		this.setDescription(Message.DESCRIPTION_SHOW);
		this.addSubCommand("show");
		this.addRequireArg("player");
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {

		OfflinePlayer player = argAsOfflinePlayer(0);
		manager.showInventory(this.player, player);

		return CommandType.SUCCESS;
	}

}
