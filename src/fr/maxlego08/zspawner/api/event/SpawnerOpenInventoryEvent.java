package fr.maxlego08.zspawner.api.event;

import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.zcore.enums.Inventory;

public class SpawnerOpenInventoryEvent extends SpawnerEvent {

	private final Inventory inventory;
	private final PlayerSpawner playerSpawner;
	private final Player player;
	public SpawnerOpenInventoryEvent(Inventory inventory, PlayerSpawner playerSpawner, Player player) {
		super();
		this.inventory = inventory;
		this.playerSpawner = playerSpawner;
		this.player = player;
	}
	/**
	 * @return the inventory
	 */
	public Inventory getInventory() {
		return inventory;
	}
	/**
	 * @return the playerSpawner
	 */
	public PlayerSpawner getPlayerSpawner() {
		return playerSpawner;
	}
	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}
	
	
	
}
