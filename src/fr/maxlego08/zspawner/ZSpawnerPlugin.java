package fr.maxlego08.zspawner;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.maxlego08.zspawner.api.Board;
import fr.maxlego08.zspawner.api.manager.LevelManager;
import fr.maxlego08.zspawner.api.manager.SpawnerManager;
import fr.maxlego08.zspawner.command.CommandManager;
import fr.maxlego08.zspawner.command.commands.CommandSpawner;
import fr.maxlego08.zspawner.inventory.InventoryManager;
import fr.maxlego08.zspawner.inventory.inventories.InventorySendSpawner;
import fr.maxlego08.zspawner.inventory.inventories.InventorySpawner;
import fr.maxlego08.zspawner.inventory.inventories.InventorySpawnerPaginate;
import fr.maxlego08.zspawner.listener.AdapterListener;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.ZPlugin;
import fr.maxlego08.zspawner.zcore.enums.Inventory;
import fr.maxlego08.zspawner.zcore.logger.Logger;
import fr.maxlego08.zspawner.zcore.logger.Logger.LogType;
import fr.maxlego08.zspawner.zcore.utils.Metrics;
import fr.maxlego08.zspawner.zcore.utils.UpdateChecker;
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
	private LevelManager levelManager;
	private Board board;
	private SpawnerListener listener;
	private boolean isEnable = false;

	@Override
	public void onEnable() {

		isEnable = false;

		preEnable();

		commandManager = new CommandManager(this);

		if (!isEnabled())
			return;
		inventoryManager = InventoryManager.getInstance();

		board = new BoardObject();
		levelManager = new LevelManagerObject();
		spawner = new ZSpawnerManager(board, this);

		/* Commands */
		registerCommand("zspawners", new CommandSpawner(), "spawners", "spawner");

		/* Inventory */
		registerInventory(Inventory.INVENTORY_SPAWNER, new InventorySpawner());
		registerInventory(Inventory.INVENTORY_SPAWNER_PAGINATE, new InventorySpawnerPaginate());
		registerInventory(Inventory.INVENTORY_SPAWNER_SEND, new InventorySendSpawner());

		/* Add Listener */
		addListener(new AdapterListener(this));
		addListener(inventoryManager);
		addListener(listener = new SpawnerListener(spawner));

		/* Add Saver */
		addSave(Config.getInstance());
		addSave(new CooldownBuilder());
		addSave(spawner);
		addSave(levelManager);

		getSavers().forEach(saver -> saver.load(getPersist()));

		if (Config.autoSave)
			autoSave();

		isEnable = true;

		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SingleLineChart("total_number_of_spawners", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return spawner.count();
			}
		}));

		UpdateChecker checker = new UpdateChecker(this, 69465);
		AtomicBoolean atomicBoolean = new AtomicBoolean();
		checker.getVersion(version -> {
			atomicBoolean.set(this.getDescription().getVersion().equalsIgnoreCase(version));
			listener.setUseLastVersion(atomicBoolean.get());
			if (atomicBoolean.get())
				Logger.info("There is not a new update available.");
			else
				Logger.info("There is a new update available. Your version: " + this.getDescription().getVersion()
						+ ", Laste version: " + version);
		});

		postEnable();
	}

	@Override
	public void onDisable() {

		preDisable();

		if (isEnable)
			getSavers().forEach(saver -> saver.save(getPersist()));
		else {
			getLog().log("Unable to save files, plugin did not load well.", LogType.WARNING);
		}

		postDisable();

	}

	public SpawnerManager getSpawner() {
		return spawner;
	}

	private void autoSave() {
		final Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				spawner.save(getPersist());
			}
		}, 1000 * 60 * Config.autoSaveTime, 1000 * 60 * Config.autoSaveTime);
	}
	
	public LevelManager getLevelManager() {
		return levelManager;
	}

}
