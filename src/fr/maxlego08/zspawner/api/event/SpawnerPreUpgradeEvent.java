package fr.maxlego08.zspawner.api.event;

import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.SimpleLevel;
import fr.maxlego08.zspawner.api.Spawner;

public class SpawnerPreUpgradeEvent extends SpawnerEvent {

	private final Spawner spawner;
	private final SimpleLevel currentLevel;
	private SimpleLevel newLevel;
	private final Player player;
	private final PlayerSpawner playerSpawner;

	/**
	 * @param spawner
	 * @param currentLevel
	 * @param newLevel
	 * @param player
	 * @param playerSpawner
	 */
	public SpawnerPreUpgradeEvent(Spawner spawner, SimpleLevel currentLevel, SimpleLevel newLevel, Player player,
			PlayerSpawner playerSpawner) {
		super();
		this.spawner = spawner;
		this.currentLevel = currentLevel;
		this.newLevel = newLevel;
		this.player = player;
		this.playerSpawner = playerSpawner;
	}

	/**
	 * @return the spawner
	 */
	public Spawner getSpawner() {
		return spawner;
	}

	/**
	 * @return the currentLevel
	 */
	public SimpleLevel getCurrentLevel() {
		return currentLevel;
	}

	/**
	 * @return the newLevel
	 */
	public SimpleLevel getNewLevel() {
		return newLevel;
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
	 * @param newLevel
	 *            the newLevel to set
	 */
	public void setNewLevel(SimpleLevel newLevel) {
		this.newLevel = newLevel;
	}

}
