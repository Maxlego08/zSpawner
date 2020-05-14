package fr.maxlego08.zspawner;

import org.bukkit.entity.EntityType;

import fr.maxlego08.zspawner.api.FakeSpawner;
import fr.maxlego08.zspawner.api.SimpleLevel;
import fr.maxlego08.zspawner.api.manager.SpawnerManager;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;

public class FakeSpawnerObject extends ZUtils implements FakeSpawner {

	protected final SpawnerManager spawnerManager;
	protected final EntityType type;
	protected final long createAt;
	protected int levelId;

	/**
	 * 
	 * @param spawnerManager
	 * @param type
	 * @param levelId
	 */
	public FakeSpawnerObject(SpawnerManager spawnerManager, EntityType type, int levelId) {
		this(spawnerManager, type, System.currentTimeMillis(), levelId);
	}

	/**
	 * @param spawnerManager
	 * @param type
	 * @param createAt
	 * @param levelId
	 */
	public FakeSpawnerObject(SpawnerManager spawnerManager, EntityType type, long createAt, int levelId) {
		super();
		this.spawnerManager = spawnerManager;
		this.type = type;
		this.createAt = createAt;
		this.levelId = levelId;
	}

	@Override
	public long createAt() {
		return createAt;
	}

	@Override
	public EntityType getType() {
		return type;
	}

	@Override
	public int getLevelId() {
		return levelId;
	}

	@Override
	public SimpleLevel getLevel() {
		return spawnerManager.getLevelManager().getLevel(levelId);
	}

}
