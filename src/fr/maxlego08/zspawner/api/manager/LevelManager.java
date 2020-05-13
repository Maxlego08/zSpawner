package fr.maxlego08.zspawner.api.manager;

import fr.maxlego08.zspawner.api.Level;
import fr.maxlego08.zspawner.zcore.utils.storage.Saveable;

public interface LevelManager extends Saveable {

	/**
	 * 
	 * @param level
	 * @return
	 */
	Level getLevel(int level);
	
}
