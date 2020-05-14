package fr.maxlego08.zspawner;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import fr.maxlego08.zspawner.api.Board;
import fr.maxlego08.zspawner.api.FakeSpawner;
import fr.maxlego08.zspawner.api.manager.SpawnerManager;

public class FakeSpawnerObject extends SpawnerObject implements FakeSpawner {

	public FakeSpawnerObject(EntityType type, int levelId, SpawnerManager spawnerManager) {
		super(null, type, System.currentTimeMillis(), 0, null, null, levelId, spawnerManager);
	}

	@Override
	public void place(Location location) {
	}
	
	@Override
	public void delete(Board board) {
	}
	
}
