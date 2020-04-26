package fr.maxlego08.zspawner.api;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public interface PlayerSpawner {

	/**
	 * 
	 * @return uuid of player
	 */
	UUID getUser();

	/**
	 * 
	 * @return spawners list
	 */
	List<Spawner> getSpawners();

	/**
	 * 
	 * @return spawners sort
	 */
	List<Spawner> getShortSpawners();

	/**
	 * 
	 * @return
	 */
	boolean isPlacing();

	/**
	 * 
	 * @return spawners size
	 */
	int spawnerSize();

	/**
	 * 
	 * @return
	 */
	Spawner getCurrentPlacingSpawner();

	/**
	 * 
	 * @param spawner
	 */
	void setCurrentPlacingSpawner(Spawner spawner);

	/**
	 * 
	 * @param spawner
	 */
	void addSpawner(Spawner spawner);

	/**
	 * 
	 * @param spawner
	 */
	void removeSpawner(Spawner spawner);

	/**
	 * 
	 * @param entityType
	 */
	void removeSpawner(EntityType type);

	/**
	 * 
	 * @param placeMs
	 */
	void setPlacing(long placeMs);

	/**
	 * 
	 * @param location
	 */
	void placeSpawner(Location location);
	
	/**
	 * Remove all spawners 
	 */
	void removeAll();
}
