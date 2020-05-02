package fr.maxlego08.zspawner.command.commands;

import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerSend extends VCommand {

	public CommandSpawnerSend() {
		this.setPermission(Permission.ZSPAWNER_SEND);
		this.addSubCommand("send");
		this.setDescription(Message.DESCRIPTION_SEND);
		this.setConsoleCanUse(false);
		this.addRequireArg("player");
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {

		Player target = argAsPlayer(0);
		manager.openSendInventory(player, target);

		return CommandType.SUCCESS;
	}

}
