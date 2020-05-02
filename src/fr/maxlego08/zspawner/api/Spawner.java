package fr.maxlego08.zspawner.api;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Spawner {

	/**
	 * 
	 * @return uuid of owner
	 */
	UUID getOwner();

	/**
	 * 
	 * @return uuid of spawner
	 */
	UUID getUniqueId();

	/**
	 * 
	 * @return
	 */
	long placedAt();
	
	/**
	 * 
	 * @return
	 */
	long createAt();
	
	/**
	 * 
	 * @return location
	 */
	Location getLocation();

	/**
	 * 
	 * @return type
	 */
	EntityType getType();

	/**
	 * 
	 * @return
	 */
	ItemStack getItemStack();

	/**
	 * 
	 * @return
	 */
	boolean isPlace();

	/**
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	boolean sameChunk(int x, int z);

	/**
	 * 
	 */
	void delete(Board board);

	/**
	 * 
	 * @param location
	 */
	void place(Location location);

	/**
	 * 
	 * @return
	 */
	int comparePlace();

	/**
	 * 
	 * @return
	 */
	int compareNotPlace();

	/**
	 * 
	 * @param player
	 * @return
	 */
	boolean isOwner(Player player);

}
