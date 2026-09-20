package fr.maxlego08.spawner.command.commands;

import fr.maxlego08.spawner.SpawnerPlugin;
import fr.maxlego08.spawner.command.VCommand;
import fr.maxlego08.spawner.save.Config;
import fr.maxlego08.spawner.zcore.enums.Message;
import fr.maxlego08.spawner.zcore.enums.Permission;
import fr.maxlego08.spawner.zcore.utils.commands.CommandType;

public class CommandSpawnerReload extends VCommand {

    public CommandSpawnerReload(SpawnerPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZSPAWNER_RELOAD);
        this.addSubCommand("reload", "rl");
        this.setDescription(Message.DESCRIPTION_RELOAD);
    }

    @Override
    protected CommandType perform(SpawnerPlugin plugin) {

        plugin.reloadConfig();
        Config.getInstance().load(plugin);
        plugin.reloadFiles();
        plugin.loadPlacedSpawners();
        message(this.plugin, sender, Message.RELOAD);

        return CommandType.SUCCESS;
    }

}
