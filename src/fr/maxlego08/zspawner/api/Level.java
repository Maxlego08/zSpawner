package fr.maxlego08.zspawner.api;

import fr.maxlego08.zspawner.zcore.utils.economy.Economy;

public interface Level extends Cloneable{

	/**
	 * 
	 * @return level id
	 */
	int getId();

	/**
	 * 
	 * @return
	 */
	double getPrice();

	/**
	 * 
	 * @return
	 */
	Economy getEconomy();

	/**
	 * 
	 * @return min delay
	 */
	int getMinDelay();

	/**
	 * 
	 * @return max delay
	 */
	int getMaxDelay();

	/**
	 * 
	 * @return
	 */
	int getSpawnCount();

	/**
	 * 
	 * @return
	 */
	int getMaxNearbyEntities();

	/**
	 * 
	 * @return
	 */
	int getSpawnRange();

	/**
	 * 
	 * @return
	 */
	int getRequiredPlayerRange();

	/**
	 * 
	 * @param value
	 */
	void setMinDelay(int value);

	/**
	 * 
	 * @param value
	 */
	void setMaxDelay(int value);

	/**
	 * 
	 * @param spawnCount
	 */
	void setSpawnCount(int spawnCount);

	/**
	 * 
	 * @param maxNearbyEntity
	 */
	void setMaxNearbyEntity(int maxNearbyEntity);
	
	/**
	 * 
	 * @param spawnRange
	 */
	void setSpawnRange(int spawnRange);
	
	/**
	 * 
	 * @param requiredPlayerRange
	 */
	void setRequiredPlayerRange(int requiredPlayerRange);
	
	/**
	 * 
	 * @param price
	 */
	void setPrice(double price);
	
	/**
	 * 
	 * @param economy
	 */
	void setEconomy(Economy economy);

}
