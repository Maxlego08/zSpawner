package fr.maxlego08.zspawner.zcore.utils.loader;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.maxlego08.zspawner.LevelObject;
import fr.maxlego08.zspawner.api.Level;
import fr.maxlego08.zspawner.api.manager.LevelManager;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import fr.maxlego08.zspawner.zcore.utils.economy.Economy;

public class LevelLoader extends ZUtils implements Loader<Level> {

	private final LevelManager manager;

	public LevelLoader(LevelManager manager) {
		super();
		this.manager = manager;
	}

	@Override
	public Level load(YamlConfiguration configuration, String path) {

		int level = configuration.getInt(path + "level");
		Economy economy = Economy.valueOf(configuration.getString(path + "economy", "VAULT"));
		double price = configuration.getDouble(path + ".price");
		int minDelay = configuration.getInt(path + "minDelay");
		int maxDelay = configuration.getInt(path + "maxDelay");
		int spawnCount = configuration.getInt(path + "spawnCount");
		int maxNearbyEntity = configuration.getInt(path + "maxNearbyEntity");
		int spawnRange = configuration.getInt(path + "spawnRange");
		int requiredPlayerRange = configuration.getInt(path + "requiredPlayerRange");

		return new LevelObject(level, economy, price, minDelay, maxDelay, spawnCount, maxNearbyEntity, spawnRange,
				requiredPlayerRange, manager);
	}

	@Override
	public void save(Level object, YamlConfiguration configuration, String path) {

		configuration.set(path + "level", object.getId());
		configuration.set(path + "economy", object.getEconomy().name());
		configuration.set(path + "price", object.getPrice());
		configuration.set(path + "maxDelay", object.getMaxDelay());
		configuration.set(path + "minDelay", object.getMinDelay());
		configuration.set(path + "maxNearbyEntity", object.getMaxNearbyEntities());
		configuration.set(path + "spawnCount", object.getSpawnCount());
		configuration.set(path + "spawnRange", object.getSpawnRange());
		configuration.set(path + "requiredPlayerRange", object.getRequiredPlayerRange());

	}

}
