package fr.maxlego08.zspawner.command.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerRemove extends VCommand {

	public CommandSpawnerRemove() {
		this.setPermission(Permission.ZSPAWNER_REMOVE);
		this.setDescription(Message.DESCRIPTION_REMOVE);

		this.DEBUG = true;
		
		this.addSubCommand("remove");

		this.addRequireArg("player");
		this.addRequireArg("type");
		this.addOptionalArg("number");
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {

		OfflinePlayer player = argAsOfflinePlayer(0);
		EntityType entityType = argAsEntityType(1);
		int amount = argAsInteger(2, 1);
		amount = amount < 0 ? 1 : amount;
		
		manager.removeSpawner(sender, player, entityType, amount);

		return CommandType.SUCCESS;
	}

}
