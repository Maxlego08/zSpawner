package fr.maxlego08.zspawner.command.commands.levels.edits;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.api.enums.Value;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandLevelEditSpawnCount extends VCommand {

	public CommandLevelEditSpawnCount() {
		this.setPermission(Permission.ZSPAWNER_LEVEL_EDIT);
		this.setDescription(Message.DESCRIPTION_LEVEL_EDIT_SPAWN_COUNT);
		this.addSubCommand("spawncount");
		this.addRequireArg("level");
		this.addRequireArg("value");
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {
		
		int level = argAsInteger(0);
		int value = argAsInteger(1);
		
		manager.getLevelManager().updateLevel(sender, Value.COUNT, level, value);

		return CommandType.SUCCESS;
	}

}
