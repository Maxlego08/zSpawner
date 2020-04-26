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
		
		message(sender, "§eVersion du plugin§7: §a" + main.getDescription().getVersion());
		message(sender, "§eAuteur§7: §aMaxlego08");
		message(sender, "§eDiscord§7: §ahttps://discord.gg/p9Mdste");
		String user = "%%__USER__%%";
		message(sender, "§eUser account§7: §ahttps://www.spigotmc.org/members/" + user);
		
		return CommandType.SUCCESS;
	}

}
