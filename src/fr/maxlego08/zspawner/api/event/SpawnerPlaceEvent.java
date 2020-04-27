package fr.maxlego08.zspawner.api.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;

/**
 * 
 * @author Maxlego08
 *
 */
public class SpawnerPlaceEvent extends SpawnerEvent {

	private final Player player;
	private final PlayerSpawner playerSpawner;
	private final Spawner spawner;
	private Location location;

	/**
	 * 
	 * @param player
	 * @param playerSpawner
	 * @param spawner
	 * @param location
	 */
	public SpawnerPlaceEvent(Player player, PlayerSpawner playerSpawner, Spawner spawner, Location location) {
		super();
		this.player = player;
		this.playerSpawner = playerSpawner;
		this.spawner = spawner;
		this.location = location;
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
	 * @return the spawner
	 */
	public Spawner getSpawner() {
		return spawner;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

}
