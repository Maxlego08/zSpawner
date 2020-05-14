package fr.maxlego08.zspawner.api.event;

import fr.maxlego08.zspawner.api.SimpleLevel;
import fr.maxlego08.zspawner.api.enums.Value;

public class SpawnerLevelUpdateEvent extends SpawnerEvent {

	private final SimpleLevel level;
	private Value type;
	private int value;

	/**
	 * 
	 * @param level
	 * @param type
	 * @param value
	 */
	public SpawnerLevelUpdateEvent(SimpleLevel level, Value type, int value) {
		super();
		this.level = level;
		this.type = type;
		this.value = value;
	}

	/**
	 * @return the level
	 */
	public SimpleLevel getLevel() {
		return level;
	}

	/**
	 * @return the type
	 */
	public Value getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Value type) {
		this.type = type;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

}
