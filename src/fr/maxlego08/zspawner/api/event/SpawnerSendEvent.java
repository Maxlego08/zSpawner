package fr.maxlego08.zspawner.api.event;

import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;

public class SpawnerSendEvent extends SpawnerEvent {

	private final Player player;
	private final Player receiver;
	private final PlayerSpawner playerSpawner;
	private final PlayerSpawner receiverSpawner;
	private final Spawner spawner;
	private final Spawner newSpawner;

	public SpawnerSendEvent(Player player, Player receiver, PlayerSpawner playerSpawner, PlayerSpawner receiverSpawner,
			Spawner spawner, Spawner newSpawner) {
		super();
		this.player = player;
		this.receiver = receiver;
		this.playerSpawner = playerSpawner;
		this.receiverSpawner = receiverSpawner;
		this.spawner = spawner;
		this.newSpawner = newSpawner;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the receiver
	 */
	public Player getReceiver() {
		return receiver;
	}

	/**
	 * @return the playerSpawner
	 */
	public PlayerSpawner getPlayerSpawner() {
		return playerSpawner;
	}

	/**
	 * @return the receiverSpawner
	 */
	public PlayerSpawner getReceiverSpawner() {
		return receiverSpawner;
	}

	/**
	 * @return the spawner
	 */
	public Spawner getSpawner() {
		return spawner;
	}

	/**
	 * @return the newSpawner
	 */
	public Spawner getNewSpawner() {
		return newSpawner;
	}

}
