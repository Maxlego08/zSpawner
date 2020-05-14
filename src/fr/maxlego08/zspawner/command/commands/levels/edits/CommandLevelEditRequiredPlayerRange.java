package fr.maxlego08.zspawner.command.commands.levels.edits;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.api.enums.Value;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandLevelEditRequiredPlayerRange extends VCommand {

	public CommandLevelEditRequiredPlayerRange() {
		this.setPermission(Permission.ZSPAWNER_LEVEL_EDIT);
		this.setDescription(Message.DESCRIPTION_LEVEL_EDIT_PLAYER);
		this.addSubCommand("requiredplayer");
		this.addRequireArg("level");
		this.addRequireArg("value");
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {
		
		int level = argAsInteger(0);
		int value = argAsInteger(1);
		
		manager.getLevelManager().updateLevel(sender, Value.PLAYERS, level, value);

		return CommandType.SUCCESS;
	}

}
