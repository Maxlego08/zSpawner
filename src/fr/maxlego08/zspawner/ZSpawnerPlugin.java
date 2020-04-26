package fr.maxlego08.zspawner;

import fr.maxlego08.zspawner.api.Board;
import fr.maxlego08.zspawner.api.SpawnerManager;
import fr.maxlego08.zspawner.command.CommandManager;
import fr.maxlego08.zspawner.command.commands.CommandSpawner;
import fr.maxlego08.zspawner.inventory.InventoryManager;
import fr.maxlego08.zspawner.inventory.inventories.InventorySpawner;
import fr.maxlego08.zspawner.inventory.inventories.InventorySpawnerPaginate;
import fr.maxlego08.zspawner.listener.AdapterListener;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.ZPlugin;
import fr.maxlego08.zspawner.zcore.enums.Inventory;
import fr.maxlego08.zspawner.zcore.utils.builder.CooldownBuilder;

/**
 * System to create your plugins very simply Projet:
 * https://github.com/Maxlego08/TemplatePlugin
 * 
 * @author Maxlego08
 *
 */
public class ZSpawnerPlugin extends ZPlugin {

	private SpawnerManager spawner;
	private Board board;

	@Override
	public void onEnable() {

		preEnable();

		commandManager = new CommandManager(this);

		if (!isEnabled())
			return;
		inventoryManager = InventoryManager.getInstance();

		board = new BoardObject();
		spawner = new ZSpawnerManager(board, this);

		/* Commands */
		registerCommand("zspawners", new CommandSpawner(), "spawners", "spawner");

		/* Inventory */
		registerInventory(Inventory.INVENTORY_SPAWNER, new InventorySpawner());
		registerInventory(Inventory.INVENTORY_SPAWNER_PAGINATE, new InventorySpawnerPaginate());

		/* Add Listener */
		addListener(new AdapterListener(this));
		addListener(inventoryManager);
		addListener(new SpawnerListener(spawner));

		/* Add Saver */
		addSave(Config.getInstance());
		addSave(new CooldownBuilder());
		addSave(board);
		addSave(spawner);

		getSavers().forEach(saver -> saver.load(getPersist()));

		postEnable();

	}

	@Override
	public void onDisable() {

		preDisable();

		getSavers().forEach(saver -> saver.save(getPersist()));

		postDisable();

	}
	
	public SpawnerManager getSpawner() {
		return spawner;
	}

}
