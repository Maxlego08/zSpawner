package fr.maxlego08.zspawner.api;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.EntityType;

import fr.maxlego08.zspawner.api.enums.Sort;

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
	 * @return
	 */
	Sort getShort();
	
	/**
	 * 
	 */
	void toggleShort();
	
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
	void removeSpawner(Board board, Spawner spawner);

	/**
	 * 
	 * @param entityType
	 */
	void removeSpawner(Board board,EntityType type);

	/**
	 * Remove all spawners 
	 */
	void removeAll(Board board);

	/**
	 * @param board 
	 * 
	 */
	void deleteAllSpawners(Board board);
	
	/**
	 * 
	 */
	void placeSpawner();
}
