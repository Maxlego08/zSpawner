package fr.maxlego08.zspawner.api.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnerSilkEvent extends SpawnerEvent {

	private final int dura;
	private final int maxDura;
	private final Player player;
	private final Block block;
	private ItemStack itemStack;
	private int newDura;

	/**
	 * @param dura
	 * @param maxDura
	 * @param player
	 * @param block
	 * @param itemStack
	 * @param newDura
	 */
	public SpawnerSilkEvent(int dura, int maxDura, Player player, Block block, ItemStack itemStack, int newDura) {
		super();
		this.dura = dura;
		this.maxDura = maxDura;
		this.player = player;
		this.block = block;
		this.itemStack = itemStack;
		this.newDura = newDura;
	}

	/**
	 * @return the dura
	 */
	public int getDura() {
		return dura;
	}

	/**
	 * @return the maxDura
	 */
	public int getMaxDura() {
		return maxDura;
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
	 * @return the newDura
	 */
	public int getNewDura() {
		return newDura;
	}

	/**
	 * @param itemStack
	 *            the itemStack to set
	 */
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	/**
	 * @param newDura
	 *            the newDura to set
	 */
	public void setNewDura(int newDura) {
		this.newDura = newDura;
	}

}
