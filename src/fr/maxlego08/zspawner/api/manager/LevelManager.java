package fr.maxlego08.zspawner.api.manager;

import java.util.List;

import org.bukkit.command.CommandSender;

import fr.maxlego08.zspawner.api.SimpleLevel;
import fr.maxlego08.zspawner.zcore.utils.storage.Saveable;

public interface LevelManager extends Saveable {

	/**
	 * 
	 * @param level
	 * @return
	 */
	SimpleLevel getLevel(int level);
	
	/**
	 * 
	 * @return
	 */
	List<String> toTabList();

	/**
	 * create a new level
	 * @param sender
	 * @param level
	 */
	void createLevel(CommandSender sender, int level);
	
	/**
	 * 
	 * @param sender
	 * @param level
	 */
	void show(CommandSender sender, int level);
	
}
