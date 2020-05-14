package fr.maxlego08.zspawner.api.event;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.PlayerSpawner;

public class SpawnerAddEvent extends SpawnerEvent {

	private final CommandSender sender;
	private final Player player;
	private final PlayerSpawner playerSpawner;
	private EntityType type;
	private int amount;
	private int level;

	public SpawnerAddEvent(CommandSender sender, Player player, PlayerSpawner playerSpawner, EntityType type,
			int amount, int level) {
		super();
		this.sender = sender;
		this.player = player;
		this.playerSpawner = playerSpawner;
		this.type = type;
		this.amount = amount;
		this.level = level;
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
	public Player getPlayer() {
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

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

}
