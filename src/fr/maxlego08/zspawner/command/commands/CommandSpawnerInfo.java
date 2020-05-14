package fr.maxlego08.zspawner.command.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import fr.maxlego08.zspawner.SpawnerObject;
import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerInfo extends VCommand {

	public CommandSpawnerInfo() {
		this.setPermission(Permission.ZSPAWNER_INFO);
		this.addSubCommand("info");
		this.setDescription(Message.DESCRIPTION_INFO);
		this.setConsoleCanUse(false);
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {

		Block block = null;
		try {
			block = player.getTargetBlock(null, 4);
		} catch (Exception e) {
		}

		if (block == null) {
			message(sender, Message.INFO_ERROR);
			return CommandType.DEFAULT;
		}

		if (!block.getType().equals(getMaterial(52))) {
			message(sender, Message.INFO_ERROR);
			return CommandType.DEFAULT;
		}

		if (!manager.getBoard().isSpawner(block.getLocation())) {
			message(sender, Message.INFO_ERROR_BOARD);
			return CommandType.DEFAULT;
		}

		Spawner spawner = manager.getBoard().getSpawner(block.getLocation());

		Config.commandInfos.forEach(string -> {

			SimpleDateFormat format = new SimpleDateFormat(Config.timeFormat);

			string = string.replace("%location%",
					spawner.getLocation() == null ? "non placé" : ((SpawnerObject) spawner).toLocation());
			string = string.replace("%type%", spawner.getType().name());
			string = string.replace("%create%", format.format(new Date(spawner.createAt())));
			string = string.replace("%level%", String.valueOf(spawner.getLevelId()));
			string = string.replace("%player%", Bukkit.getOfflinePlayer(spawner.getOwner()).getName());
			string = string.replace("%placed%",
					spawner.getLocation() == null ? "non placé" : format.format(new Date(spawner.placedAt())));

			message(sender, string);
		});

		return CommandType.SUCCESS;
	}

}
