package fr.maxlego08.zspawner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Location;

import fr.maxlego08.zspawner.api.Board;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.zcore.utils.storage.Persist;

public class BoardObject implements Board {

	private static Map<Location, Spawner> boards = new HashMap<Location, Spawner>();

	@Override
	public boolean isSpawner(Location location) {
		return boards.containsKey(location);
	}

	@Override
	public Spawner getSpawner(Location location) {
		return boards.getOrDefault(location, null);
	}

	@Override
	public List<Spawner> getSpawners(int x, int z) {
		return boards.values().stream().filter(spawner -> spawner.sameChunk(x, z)).collect(Collectors.toList());
	}

	@Override
	public int countSpawners(int x, int z) {
		return getSpawners(x, z).size();
	}

	@Override
	public void placeSpawner(Location location, Spawner spawner) {
		boards.put(location, spawner);
	}

	@Override
	public void removeSpawner(Location location) {
		boards.remove(location);
	}

	@Override
	public void save(Persist persist) {
		persist.save(this, "board");
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(this, BoardObject.class, "board");
	}

	@Override
	public void removeSpawner(Spawner spawner) {
		if (spawner.isPlace())
			removeSpawner(spawner.getLocation());
	}

	@Override
	public List<Spawner> getSpawners(Chunk chunk) {
		return getSpawners(chunk.getX(), chunk.getZ());
	}

	@Override
	public int countSpawners(Chunk chunk) {
		return countSpawners(chunk.getX(), chunk.getZ());
	}

}
