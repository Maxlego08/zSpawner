package fr.maxlego08.zspawner.command.commands;

import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerPickaxe extends VCommand {

	public CommandSpawnerPickaxe() {

		this.setPermission(Permission.ZSPAWNER_PICKAXE);
		this.setDescription(Message.DESCRIPTION_PICKAXE);
		this.addSubCommand("pickaxe");
		this.addRequireArg("player");
		this.addOptionalArg("durabilty");
		this.addOptionalArg("maxDurabilty");

	}

	@Override
	protected CommandType perform(ZSpawnerPlugin plugin) {

		Player player = argAsPlayer(0);
		int durabilty = argAsInteger(1, Config.defaultMaxUsePickaxe);
		int maxDurabilty = argAsInteger(2, Config.defaultMaxUsePickaxe);

		manager.givePickaxe(sender, player, durabilty, maxDurabilty);
		return CommandType.SUCCESS;
	}

}
