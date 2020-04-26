package fr.maxlego08.zspawner;

import fr.maxlego08.zspawner.command.CommandManager;
import fr.maxlego08.zspawner.inventory.InventoryManager;
import fr.maxlego08.zspawner.listener.AdapterListener;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.ZPlugin;
import fr.maxlego08.zspawner.zcore.utils.builder.CooldownBuilder;

/**
 * System to create your plugins very simply Projet:
 * https://github.com/Maxlego08/TemplatePlugin
 * 
 * @author Maxlego08
 *
 */
public class ZSpawnerPlugin extends ZPlugin {

	@Override
	public void onEnable() {

		preEnable();

		commandManager = new CommandManager(this);

		if (!isEnabled())
			return;
		inventoryManager = InventoryManager.getInstance();

//		scoreboardManager = new ScoreBoardManager(1000);
		
		/* Add Listener */

		addListener(new AdapterListener(this));
		addListener(inventoryManager);

		/* Add Saver */
		addSave(Config.getInstance());
		addSave(new CooldownBuilder());

		getSavers().forEach(saver -> saver.load(getPersist()));

		postEnable();

	}

	@Override
	public void onDisable() {

		preDisable();

		getSavers().forEach(saver -> saver.save(getPersist()));

		postDisable();

	}

}
