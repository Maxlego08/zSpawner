package fr.maxlego08.zspawner.api.enums;

import java.util.Comparator;

import fr.maxlego08.zspawner.api.Spawner;

public enum Sort {

	PLACE("spawners placed", Comparator.comparingInt(Spawner::comparePlace)),

	PLACE_NO("spawners not placed", Comparator.comparingInt(Spawner::compareNotPlace)),

	;

	private final String name;
	private final Comparator<Spawner> comparator;

	private Sort(String name, Comparator<Spawner> comparator) {
		this.name = name;
		this.comparator = comparator;

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the comparator
	 */
	public Comparator<Spawner> getComparator() {
		return comparator;
	}

	public Sort next(){
		switch (this) {
		case PLACE:
			return Sort.PLACE_NO;
		case PLACE_NO:
			return Sort.PLACE;
		default:
			break;
		}
		return Sort.PLACE;
	}
	
}
