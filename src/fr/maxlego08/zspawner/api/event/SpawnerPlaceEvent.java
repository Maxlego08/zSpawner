package fr.maxlego08.zspawner.api.event;

import org.bukkit.Location;

import fr.maxlego08.zspawner.api.Spawner;

/**
 * 
 * @author Maxlego08
 *
 */
public class SpawnerPlaceEvent extends SpawnerEvent {

	private final Spawner spawner;

	/**
	 * 
	 * @param spawner
	 */
	public SpawnerPlaceEvent(Spawner spawner) {
		super();
		this.spawner = spawner;
	}
	
	/**
	 * 
	 * @return
	 */
	public Spawner getSpawner() {
		return spawner;
	}
	
	/**
	 * 
	 * @return
	 */
	public Location getLocation(){
		return spawner.getLocation();
	}
	
}
