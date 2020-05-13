package fr.maxlego08.zspawner;

import fr.maxlego08.zspawner.api.Level;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import fr.maxlego08.zspawner.zcore.utils.economy.Economy;

/**
 * 
 * @author Maxlego08
 *
 */
public class LevelObject extends ZUtils implements Level {

	private final int level;
	private final Economy economy;
	private final double price;
	private final int minDelay;
	private final int maxDelay;
	private final int spawnCount;
	private final int maxNearbyEntity;
	private final int spawnRange;
	private final int requiredPlayerRange;

	/**
	 * 
	 * @param level
	 * @param economy
	 * @param price
	 * @param minDelay
	 * @param maxDelay
	 * @param spawnCount
	 * @param maxNearbyEntity
	 * @param spawnRange
	 * @param requiredPlayerRange
	 */
	public LevelObject(int level, Economy economy, double price, int minDelay, int maxDelay, int spawnCount,
			int maxNearbyEntity, int spawnRange, int requiredPlayerRange) {
		super();
		this.level = level;
		this.economy = economy;
		this.price = price;
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		this.spawnCount = spawnCount;
		this.maxNearbyEntity = maxNearbyEntity;
		this.spawnRange = spawnRange;
		this.requiredPlayerRange = requiredPlayerRange;
	}

	/**
	 * @return the economy
	 */
	@Override
	public Economy getEconomy() {
		return economy;
	}

	/**
	 * @return the price
	 */
	@Override
	public double getPrice() {
		return price;
	}

	/**
	 * @return the minDelay
	 */
	@Override
	public int getMinDelay() {
		return minDelay;
	}

	/**
	 * @return the maxDelay
	 */
	@Override
	public int getMaxDelay() {
		return maxDelay;
	}

	/**
	 * @return the spawnCount
	 */
	@Override
	public int getSpawnCount() {
		return spawnCount;
	}

	/**
	 * @return the spawnRange
	 */
	@Override
	public int getSpawnRange() {
		return spawnRange;
	}

	/**
	 * @return the requiredPlayerRange
	 */
	@Override
	public int getRequiredPlayerRange() {
		return requiredPlayerRange;
	}

	@Override
	public int getId() {
		return level;
	}

	@Override
	public int getMaxNearbyEntities() {
		return maxNearbyEntity;
	}

}
