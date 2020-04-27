package fr.maxlego08.zspawner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.EntityType;

import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.enums.Sort;
import fr.maxlego08.zspawner.api.event.SpawnerSortEvent;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;

public class PlayerObject extends ZUtils implements PlayerSpawner {

	private final UUID user;
	private final List<Spawner> spawners;
	private Sort typeShort = Sort.PLACE;
	private long placingCooldown = 0;
	private Spawner placingSpawner;

	/**
	 * 
	 * @param user
	 */
	public PlayerObject(UUID user) {
		this(user, new ArrayList<>());
	}

	/**
	 * 
	 * @param user
	 * @param spawners
	 */
	public PlayerObject(UUID user, List<Spawner> spawners) {
		super();
		this.user = user;
		this.spawners = spawners;
	}

	@Override
	public UUID getUser() {
		return user;
	}

	@Override
	public List<Spawner> getSpawners() {
		return spawners;
	}

	@Override
	public List<Spawner> getShortSpawners() {
		
		SpawnerSortEvent event = new SpawnerSortEvent(this, typeShort);
		event.callEvent();
		
		if (event.isCancelled())
			return spawners;
		
		typeShort = event.getType();
		
		List<Spawner> currentList = new ArrayList<>(spawners);
		
		Collections.sort(spawners, event.getComparator());
		
		return currentList;
	}

	@Override
	public boolean isPlacing() {
		return placingCooldown != 0 && placingCooldown > System.currentTimeMillis();
	}

	@Override
	public int spawnerSize() {
		return spawners.size();
	}

	@Override
	public Spawner getCurrentPlacingSpawner() {
		return placingSpawner;
	}

	@Override
	public void setCurrentPlacingSpawner(Spawner spawner) {
		this.placingSpawner = spawner;
		this.placingCooldown = System.currentTimeMillis() + 1000 * 60;
	}

	@Override
	public void addSpawner(Spawner spawner) {
		spawners.add(spawner);
	}

	@Override
	public void removeSpawner(Spawner spawner) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSpawner(EntityType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public Sort getShort() {
		return typeShort;
	}

	@Override
	public void toggleShort() {
		typeShort = typeShort.next();
	}

	@Override
	public void placeSpawner() {
		placingCooldown = 0;
		placingSpawner = null;
	}
	

}
