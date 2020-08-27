package fr.maxlego08.zspawner.command.commands;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerVersion extends VCommand {

	public CommandSpawnerVersion() {
		this.addSubCommand("version");
		this.addSubCommand("v");
		this.addSubCommand("ver");
		this.setDescription(Message.DESCRIPTION_VERSION);
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {
		
		sender.sendMessage("§7(§bzSpawner§7) §aVersion du plugin§7: §2" + main.getDescription().getVersion());
		sender.sendMessage("§7(§bzSpawner§7) §aAuteur§7: §2Maxlego08");
		sender.sendMessage("§7(§bzSpawner§7) §aDiscord§7: §2http://discord.groupez.xyz/");
		sender.sendMessage("§7(§bzSpawner§7) §aBuy it for §d8€§7: §2https://www.spigotmc.org/resources/69465/");
		String user = "%%__USER__%%";
		sender.sendMessage("§7(§bzSpawner§7) §aUser account§7: §2https://www.spigotmc.org/members/" + user);
		
		return CommandType.SUCCESS;
	}

}
