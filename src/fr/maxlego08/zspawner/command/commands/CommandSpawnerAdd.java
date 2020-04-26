package fr.maxlego08.zspawner.command.commands;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerAdd extends VCommand {

	public CommandSpawnerAdd() {
		this.setPermission(Permission.ZSPAWNER_ADD);
		this.setDescription(Message.DESCRIPTION_ADD);

		this.addSubCommand("add");

		this.addRequireArg("player");
		this.addRequireArg("type");
		this.addOptionalArg("number");
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {

		Player player = argAsPlayer(0);
		EntityType entityType = argAsEntityType(1);
		int amount = argAsInteger(2, 1);
		amount = amount < 0 ? 1 : amount;
		
		manager.addSpawner(sender, player, entityType, amount);

		return CommandType.SUCCESS;
	}

}
