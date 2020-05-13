package fr.maxlego08.zspawner;

import java.util.HashMap;
import java.util.Map;

import fr.maxlego08.zspawner.api.Level;
import fr.maxlego08.zspawner.api.manager.LevelManager;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import fr.maxlego08.zspawner.zcore.utils.economy.Economy;
import fr.maxlego08.zspawner.zcore.utils.storage.Persist;

public class LevelManagerObject extends ZUtils implements LevelManager {

	private final Map<Integer, Level> levels = new HashMap<Integer, Level>();

	public LevelManagerObject() {

		// Level par defaut;

		Level level = new LevelObject(1, Economy.VAULT, 100000, 150, 750, 5, 8, 4, 18);
		levels.put(1, level);

		level = new LevelObject(2, Economy.VAULT, 1000000, 100, 700, 8, 10, 4, 20);
		levels.put(2, level);

		level = new LevelObject(3, Economy.VAULT, 10000000, 50, 600, 10, 12, 4, 20);
		levels.put(3, level);

	}

	@Override
	public void save(Persist persist) {
		persist.save(this, "levels");
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(this, LevelManagerObject.class, "levels");
	}

	@Override
	public Level getLevel(int level) {
		return levels.getOrDefault(level, null);
	}

}
