package fr.maxlego08.zspawner.api;

import fr.maxlego08.zspawner.zcore.utils.economy.Economy;

public interface SimpleLevel extends Cloneable{

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

}
