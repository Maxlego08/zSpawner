package fr.maxlego08.zspawner.api.event;

import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;

public class SpawnerPrePlaceEvent extends SpawnerEvent {

	private final Player player;
	private final Spawner spawner;
	private final PlayerSpawner playerSpawner;

	public SpawnerPrePlaceEvent(Player player, Spawner spawner, PlayerSpawner playerSpawner) {
		super();
		this.player = player;
		this.spawner = spawner;
		this.playerSpawner = playerSpawner;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the spawner
	 */
	public Spawner getSpawner() {
		return spawner;
	}

	/**
	 * @return the playerSpawner
	 */
	public PlayerSpawner getPlayerSpawner() {
		return playerSpawner;
	}

}
