package fr.maxlego08.zspawner.api.event;

import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.api.Spawner;

public class SpawnerExplodeEvent extends SpawnerEvent {

	private final Spawner spawner;
	private ItemStack itemStack;

	public SpawnerExplodeEvent(Spawner spawner, ItemStack itemStack) {
		super();
		this.spawner = spawner;
		this.itemStack = itemStack;
	}

	/**
	 * @return the spawner
	 */
	public Spawner getSpawner() {
		return spawner;
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
