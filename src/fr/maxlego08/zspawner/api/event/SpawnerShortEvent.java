package fr.maxlego08.zspawner.api.event;

import java.util.Comparator;

import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.enums.Short;

public class SpawnerShortEvent extends SpawnerEvent {

	private final PlayerSpawner playerSpawner;
	private Short type;
	private Comparator<Spawner> comparator;

	/**
	 * 
	 * @param playerSpawner
	 * @param type
	 */
	public SpawnerShortEvent(PlayerSpawner playerSpawner, Short type) {
		super();
		this.playerSpawner = playerSpawner;
		this.type = type;
		this.comparator = type.getComparator();
	}

	/**
	 * @return the playerSpawner
	 */
	public PlayerSpawner getPlayerSpawner() {
		return playerSpawner;
	}

	/**
	 * @return the type
	 */
	public Short getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Short type) {
		this.type = type;
	}

	/**
	 * @return the comparator
	 */
	public Comparator<Spawner> getComparator() {
		return comparator;
	}

	/**
	 * @param comparator
	 *            the comparator to set
	 */
	public void setComparator(Comparator<Spawner> comparator) {
		this.comparator = comparator;
	}

}
