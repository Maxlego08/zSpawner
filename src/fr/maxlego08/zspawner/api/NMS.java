package fr.maxlego08.zspawner.api;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.api.utils.Key;

public interface NMS extends Key{
	
	/**
	 * 
	 * @param itemStack
	 * @param key
	 * @param type
	 * @return
	 */
	ItemStack set(ItemStack itemStack, String key, EntityType type);
	
	/**
	 * 
	 * @param itemStack
	 * @param key
	 * @param value
	 * @return
	 */
	ItemStack set(ItemStack itemStack, String key, boolean value);
	
	/**
	 * 
	 * @param itemStack
	 * @param key
	 * @param value
	 * @return
	 */
	ItemStack set(ItemStack itemStack, String key, int value);
	
	/**
	 * 
	 * @param itemStack
	 * @param key
	 * @return
	 */
	EntityType get(ItemStack itemStack, String key);
	
	/**
	 * 
	 * @param itemStack
	 * @param key
	 * @return
	 */
	int getInteger(ItemStack itemStack, String key);
	
	/**
	 * 
	 * @param itemStack
	 * @param key
	 * @return
	 */
	boolean has(ItemStack itemStack, String key);
	
	/**
	 * 
	 * @param spawner
	 * @return
	 */
	ItemStack fromSpawner(FakeSpawner spawner);
	
	/**
	 * 
	 * @param itemStack
	 * @return
	 */
	Spawner toSpawner(ItemStack itemStack);

	/**
	 * 
	 * @param spawnerObject
	 */
	void updateSpawner(Spawner spawner);

}
