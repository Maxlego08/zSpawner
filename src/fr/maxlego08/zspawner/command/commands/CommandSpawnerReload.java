package fr.maxlego08.zspawner.command.commands;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.command.VCommand;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.save.Lang;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.commands.CommandType;

public class CommandSpawnerReload extends VCommand {

	public CommandSpawnerReload() {
		this.setPermission(Permission.ZSPAWNER_RELOAD);
		this.addSubCommand("reload", "rl");
		this.setDescription(Message.DESCRIPTION_RELOAD);
	}

	@Override
	protected CommandType perform(ZSpawnerPlugin main) {

		long ms = System.currentTimeMillis();

		manager.save(main.getPersist());
		Config.getInstance().load(main.getPersist());
		Lang.getInstance().load(main.getPersist());
		manager.getLevelManager().load(main.getPersist());
		main.getPickaxeManager().load(null);

		long end = Math.abs(System.currentTimeMillis() - ms);

		message(sender, "§eReload in §6%s §ems", format(end));

		main.getInventoryManager().updateAllPlayer(1, 2);

		return CommandType.SUCCESS;
	}

}
