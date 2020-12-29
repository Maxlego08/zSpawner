package fr.maxlego08.zspawner.api.manager;

import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.api.utils.Key;
import fr.maxlego08.zspawner.zcore.utils.storage.Saveable;

public interface PickaxeManager extends Saveable, Key{

	/**
	 * Get pickaxe
	 * @param durabilty
	 * @return
	 */
	ItemStack getPickaxe(int durabilty, int maxDurabilty);

	/**
	 * 
	 * @param itemStack
	 * @return
	 */
	boolean isPickaxe(ItemStack itemStack);

	/**
	 * 
	 * @param itemStack
	 * @return
	 */
	boolean isEnchantPickaxe(ItemStack itemStack);
	
}
