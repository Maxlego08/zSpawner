package fr.maxlego08.zspawner.api;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface NMS {

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
	 * @return
	 */
	EntityType get(ItemStack itemStack, String key);
	
	/**
	 * 
	 * @param itemStack
	 * @param key
	 * @return
	 */
	boolean has(ItemStack itemStack, String key);

}
