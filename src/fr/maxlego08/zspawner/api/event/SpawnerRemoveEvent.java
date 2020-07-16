package fr.maxlego08.zspawner.api.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import fr.maxlego08.zspawner.api.PlayerSpawner;

public class SpawnerRemoveEvent extends SpawnerEvent {

	private final CommandSender sender;
	private final OfflinePlayer player;
	private final PlayerSpawner playerSpawner;
	private EntityType type;
	private int amount;

	public SpawnerRemoveEvent(CommandSender sender, OfflinePlayer player, PlayerSpawner playerSpawner, EntityType type,
			int amount) {
		super();
		this.sender = sender;
		this.player = player;
		this.playerSpawner = playerSpawner;
		this.type = type;
		this.amount = amount;
	}

	/**
	 * @return the sender
	 */
	public CommandSender getSender() {
		return sender;
	}

	/**
	 * @return the player
	 */
	public OfflinePlayer getPlayer() {
		return player;
	}

	/**
	 * @return the playerSpawner
	 */
	public PlayerSpawner getPlayerSpawner() {
		return playerSpawner;
	}

	/**
	 * @return the type
	 */
	public EntityType getType() {
		return type;
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(EntityType type) {
		this.type = type;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

}
