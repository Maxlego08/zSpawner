package fr.maxlego08.zspawner.command.commands.levels;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandLevelCreate extends VCommand {

	public CommandLevelCreate() {
		this.setPermission(Permission.ZSPAWNER_LEVEL_CREATE);
		this.setDescription(Message.DESCRIPTION_LEVEL_CREATE);
		this.addSubCommand("create");
		this.addRequireArg("level");
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {
		
		int level = argAsInteger(0);
		manager.getLevelManager().createLevel(sender, level);
		
		return CommandType.SUCCESS;
	}

}
