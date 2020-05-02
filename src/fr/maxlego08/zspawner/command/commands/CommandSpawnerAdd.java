package fr.maxlego08.zspawner.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
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

		if (!entityType.isAlive())
			return CommandType.SYNTAX_ERROR;

		int amount = argAsInteger(2, 1);
		amount = amount < 0 ? 1 : amount;

		manager.addSpawner(sender, player, entityType, amount);

		return CommandType.SUCCESS;
	}

	@Override
	public CommandType tabPerform(ZSpawnerPlugin plugin, CommandSender sender, String[] args) {
		return CommandType.SUCCESS;
	}

	@Override
	public List<String> toTab(ZSpawnerPlugin plugin, CommandSender sender, String[] args) {

		if (args.length == 3) {

			String startWith = args[2];

			List<String> entities = new ArrayList<String>();
			for (EntityType type : EntityType.values()) {
				if (type.isAlive() && !type.equals(EntityType.PLAYER)) {
					if (startWith.length() == 0 || type.name().toLowerCase().startsWith(startWith))
						entities.add(name(type.name()));
				}
			}

			return entities;

		}

		return null;
	}

	@Override
	protected String name(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
	}

}
