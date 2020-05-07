package fr.maxlego08.zspawner.api.event;

import org.bukkit.inventory.ItemStack;

public class SpawnerExplodeNaturalEvent extends SpawnerEvent {

	private ItemStack itemStack;

	public SpawnerExplodeNaturalEvent(ItemStack itemStack) {
		super();
		this.itemStack = itemStack;
	}

	/**
	 * @return the itemStack
	 */
	public ItemStack getItemStack() {
		return itemStack;
	}

	/**
	 * @param itemStack
	 *            the itemStack to set
	 */
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

}
