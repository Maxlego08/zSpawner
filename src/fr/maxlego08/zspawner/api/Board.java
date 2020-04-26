package fr.maxlego08.zspawner.api;

import java.util.List;

import org.bukkit.Location;

import fr.maxlego08.zspawner.zcore.utils.storage.Saveable;

public interface Board extends Saveable {

	/**
	 * 
	 * @param location
	 * @return
	 */
	boolean isSpawner(Location location);

	/**
	 * 
	 * @param location
	 * @return
	 */
	Spawner getSpawner(Location location);

	/**
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	List<Spawner> getSpawners(int x, int z);

	/**
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	int countSpawners(int x, int z);

	/**
	 * 
	 * @param location
	 * @param spawner
	 */
	void placeSpawner(Location location, Spawner spawner);

	/**
	 * 
	 * @param location
	 */
	void removeSpawner(Location location);

}
