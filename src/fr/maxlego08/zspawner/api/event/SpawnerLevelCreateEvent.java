package fr.maxlego08.zspawner.api.event;

import fr.maxlego08.zspawner.api.Level;

public class SpawnerLevelCreateEvent extends SpawnerEvent {

	private final Level level;

	public SpawnerLevelCreateEvent(Level level) {
		super();
		this.level = level;
	}

	/**
	 * @return the level
	 */
	public Level getLevel() {
		return level;
	}

}
