package fr.maxlego08.zspawner.api.event;

import fr.maxlego08.zspawner.api.PlayerSpawner;

public class SpawnerRemoveAllEvent extends SpawnerEvent {

	private final PlayerSpawner playerSpawner;

	public SpawnerRemoveAllEvent(PlayerSpawner playerSpawner) {
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
