package fr.maxlego08.zspawner.api.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnerSilkEnchantEvent extends SpawnerEvent {

	private final Player player;
	private final Block block;
	private ItemStack itemStack;

	/**
	 * @param player
	 * @param block
	 * @param itemStack
	 */
	public SpawnerSilkEnchantEvent(Player player, Block block, ItemStack itemStack) {
		super();
		this.player = player;
		this.block = block;
		this.itemStack = itemStack;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the block
	 */
	public Block getBlock() {
		return block;
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
