package fr.maxlego08.zspawner.api.event;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnerGiveEvent extends SpawnerEvent {

	private final CommandSender sender;
	private final Player player;
	private EntityType type;
	private int amount;

	public SpawnerGiveEvent(CommandSender sender, Player player, EntityType type, int amount) {
		super();
		this.sender = sender;
		this.player = player;
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
	public Player getPlayer() {
		return player;
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
