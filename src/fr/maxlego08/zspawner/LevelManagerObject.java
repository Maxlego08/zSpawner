package fr.maxlego08.zspawner;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.maxlego08.zspawner.api.Level;
import fr.maxlego08.zspawner.api.event.SpawnerLevelCreateEvent;
import fr.maxlego08.zspawner.api.manager.LevelManager;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.logger.Logger;
import fr.maxlego08.zspawner.zcore.logger.Logger.LogType;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import fr.maxlego08.zspawner.zcore.utils.economy.Economy;
import fr.maxlego08.zspawner.zcore.utils.loader.LevelLoader;
import fr.maxlego08.zspawner.zcore.utils.loader.Loader;
import fr.maxlego08.zspawner.zcore.utils.storage.Persist;

public class LevelManagerObject extends ZUtils implements LevelManager {

	private final Map<Integer, Level> levels = new HashMap<Integer, Level>();

	public LevelManagerObject() {

		// Level par defaut;

		Level level = new LevelObject(1, Economy.VAULT, 100000, 150, 750, 5, 8, 4, 18, this);
		levels.put(1, level);

		level = new LevelObject(2, Economy.VAULT, 1000000, 100, 700, 8, 10, 4, 20, this);
		levels.put(2, level);

		level = new LevelObject(3, Economy.VAULT, 10000000, 50, 600, 10, 12, 4, 20, this);
		levels.put(3, level);

	}

	@Override
	public void save(Persist persist) {

		File file = new File(plugin.getDataFolder() + File.separator + "levels.yml");
		if (file.exists())
			file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

		Loader<Level> loader = new LevelLoader(this);
		this.levels.forEach((id, level) -> loader.save(level, configuration, "levels." + id + "."));

		try {
			configuration.save(file);
			Logger.info(file.getAbsolutePath() + " save successfully !", LogType.SUCCESS);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void load(Persist persist) {

		File file = new File(plugin.getDataFolder() + File.separator + "levels.yml");
		if (!file.exists()) {
			save(persist);
			return;
		}

		this.levels.clear();
		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
		Loader<Level> loader = new LevelLoader(this);

		for (String id : configuration.getConfigurationSection("levels.").getKeys(false)) {

			Level level = loader.load(configuration, "levels." + id + ".");
			this.levels.put(level.getId(), level);

		}
		Logger.info(file.getAbsolutePath() + " loaded successfully !", LogType.SUCCESS);

	}

	@Override
	public Level getLevel(int level) {
		return levels.getOrDefault(level, null);
	}

	@Override
	public void createLevel(CommandSender sender, int level) {

		Level levelObject = getLevel(level);
		if (levelObject != null) {

			message(sender, Message.LEVEL_CREATE_ERROR, level);
			return;

		}

		levelObject = new LevelObject(level, Economy.VAULT, 0, 200, 800, 4, 6, 4, 16, this);

		SpawnerLevelCreateEvent event = new SpawnerLevelCreateEvent(levelObject);
		event.callEvent();

		if (event.isCancelled())
			return;

		this.levels.put(level, levelObject);
		this.save(null);
		message(sender, Message.LEVEL_CREATE_SUCCESS, level);

	}

	@Override
	public void show(CommandSender sender, int level) {

		Level levelObject = getLevel(level);
		if (levelObject == null) {

			message(sender, Message.LEVEL_ERROR, level);
			return;

		}

		message(sender, "§eLevel§8: §f%s", levelObject.getId());
		message(sender, "§eEconomy§8: §f%s", name(levelObject.getEconomy().name()));
		message(sender, "§ePrice§8: §f%s", levelObject.getPrice());
		message(sender, "§eMin delay§8: §f%s", levelObject.getMinDelay());
		message(sender, "§eMax delay§8: §f%s", levelObject.getMaxDelay());
		message(sender, "§eSpawn count§8: §f%s", levelObject.getSpawnCount());
		message(sender, "§eSpawn range§8: §f%s", levelObject.getSpawnRange());
		message(sender, "§eMax nerby entities§8: §f%s", levelObject.getMaxNearbyEntities());
		message(sender, "§eRequired player range§8: §f%s", levelObject.getRequiredPlayerRange());

	}

	@Override
	public List<String> toTabList() {
		return levels.values().stream().map(level -> String.valueOf(level.getId())).collect(Collectors.toList());
	}

}
