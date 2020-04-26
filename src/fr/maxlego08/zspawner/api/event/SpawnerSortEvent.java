package fr.maxlego08.zspawner.api.event;

import java.util.Comparator;

import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.enums.Sort;

public class SpawnerSortEvent extends SpawnerEvent {

	private final PlayerSpawner playerSpawner;
	private Sort type;
	private Comparator<Spawner> comparator;

	/**
	 * 
	 * @param playerSpawner
	 * @param type
	 */
	public SpawnerSortEvent(PlayerSpawner playerSpawner, Sort type) {
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
	public Sort getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Sort type) {
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
