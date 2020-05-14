package fr.maxlego08.zspawner.api.manager;

import org.bukkit.command.CommandSender;

import fr.maxlego08.zspawner.api.Level;
import fr.maxlego08.zspawner.zcore.utils.storage.Saveable;

public interface LevelManager extends Saveable {

	/**
	 * 
	 * @param level
	 * @return
	 */
	Level getLevel(int level);

	/**
	 * create a new level
	 * @param sender
	 * @param level
	 */
	void createLevel(CommandSender sender, int level);
	
}
