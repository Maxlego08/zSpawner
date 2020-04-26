package fr.maxlego08.zspawner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;

public class PlayerObject extends ZUtils implements PlayerSpawner {

	private final UUID user;
	private final List<Spawner> spawners;

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
		return null;
	}

	@Override
	public boolean isPlacing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int spawnerSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Spawner getCurrentPlacingSpawner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurrentPlacingSpawner(Spawner spawner) {
		// TODO Auto-generated method stub

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
	public void setPlacing(long placeMs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void placeSpawner(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAll() {
		// TODO Auto-generated method stub

	}

}
