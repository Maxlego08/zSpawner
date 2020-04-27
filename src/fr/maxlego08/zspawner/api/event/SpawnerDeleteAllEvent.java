package fr.maxlego08.zspawner.api.event;

import fr.maxlego08.zspawner.api.PlayerSpawner;

/**
 * 
 * @author Maxlego08
 *
 */
public class SpawnerDeleteAllEvent extends SpawnerEvent {

	private final PlayerSpawner playerSpawner;

	public SpawnerDeleteAllEvent(PlayerSpawner playerSpawner) {
		super();
		this.playerSpawner = playerSpawner;
	}

	/**
	 * @return the playerSpawner
	 */
	public PlayerSpawner getPlayerSpawner() {
		return playerSpawner;
	}

}
