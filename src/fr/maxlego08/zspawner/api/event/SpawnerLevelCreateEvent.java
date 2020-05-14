package fr.maxlego08.zspawner.api.event;

import fr.maxlego08.zspawner.api.SimpleLevel;

public class SpawnerLevelCreateEvent extends SpawnerEvent {

	private final SimpleLevel level;

	public SpawnerLevelCreateEvent(SimpleLevel level) {
		super();
		this.level = level;
	}

	/**
	 * @return the level
	 */
	public SimpleLevel getLevel() {
		return level;
	}

}
