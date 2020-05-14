package fr.maxlego08.zspawner;

import fr.maxlego08.zspawner.api.Level;
import fr.maxlego08.zspawner.api.manager.LevelManager;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import fr.maxlego08.zspawner.zcore.utils.economy.Economy;

/**
 * 
 * @author Maxlego08
 *
 */
public class LevelObject extends ZUtils implements Level {

	private final int level;
	private Economy economy;
	private double price;
	private int minDelay;
	private int maxDelay;
	private int spawnCount;
	private int maxNearbyEntity;
	private int spawnRange;
	private int requiredPlayerRange;
	private final LevelManager levelManager;

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
			int maxNearbyEntity, int spawnRange, int requiredPlayerRange, LevelManager levelManager) {
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
		this.levelManager = levelManager;
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

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the maxNearbyEntity
	 */
	public int getMaxNearbyEntity() {
		return maxNearbyEntity;
	}

	/**
	 * @param economy
	 *            the economy to set
	 */
	public void setEconomy(Economy economy) {
		this.economy = economy;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @param minDelay
	 *            the minDelay to set
	 */
	public void setMinDelay(int minDelay) {
		this.minDelay = minDelay;
	}

	/**
	 * @param maxDelay
	 *            the maxDelay to set
	 */
	public void setMaxDelay(int maxDelay) {
		this.maxDelay = maxDelay;
	}

	/**
	 * @param spawnCount
	 *            the spawnCount to set
	 */
	public void setSpawnCount(int spawnCount) {
		this.spawnCount = spawnCount;
	}

	/**
	 * @param maxNearbyEntity
	 *            the maxNearbyEntity to set
	 */
	public void setMaxNearbyEntity(int maxNearbyEntity) {
		this.maxNearbyEntity = maxNearbyEntity;
	}

	/**
	 * @param spawnRange
	 *            the spawnRange to set
	 */
	public void setSpawnRange(int spawnRange) {
		this.spawnRange = spawnRange;
	}

	/**
	 * @param requiredPlayerRange
	 *            the requiredPlayerRange to set
	 */
	public void setRequiredPlayerRange(int requiredPlayerRange) {
		this.requiredPlayerRange = requiredPlayerRange;
	}

	@Override
	protected Level clone() {
		try {
			return (Level) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void flush() {
		levelManager.save(null);
	}

}
