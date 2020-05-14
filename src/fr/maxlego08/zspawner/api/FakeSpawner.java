package fr.maxlego08.zspawner.api;

import org.bukkit.entity.EntityType;

public interface FakeSpawner extends Cloneable{

	/**
	 * 
	 * @return
	 */
	long createAt();


	/**
	 * 
	 * @return type
	 */
	EntityType getType();

	/**
	 * 
	 * @return level as id
	 */
	int getLevelId();
	
	/**
	 * 
	 * @return
	 */
	SimpleLevel getLevel();
	
}
