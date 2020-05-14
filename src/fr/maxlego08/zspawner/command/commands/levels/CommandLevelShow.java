package fr.maxlego08.zspawner.command.commands.levels;

import java.util.List;

import org.bukkit.command.CommandSender;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandLevelShow extends VCommand {

	public CommandLevelShow() {
		this.setPermission(Permission.ZSPAWNER_LEVEL_SHOW);
		this.setDescription(Message.DESCRIPTION_LEVEL_SHOW);
		this.addSubCommand("show");
		this.addSubCommand("list");
		this.addRequireArg("level");
		this.setTabCompletor();
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {

		int level = argAsInteger(0);
		manager.getLevelManager().show(sender, level);

		return CommandType.SUCCESS;
	}

	@Override
	public CommandType tabPerform(ZSpawnerPlugin plugin, CommandSender sender, String[] args) {
		return CommandType.SUCCESS;
	}

	@Override
	public List<String> toTab(ZSpawnerPlugin plugin, CommandSender sender2, String[] args) {
		return generateList(plugin.getSpawner().getLevelManager().toTabList(), args[2]);
	}

}
