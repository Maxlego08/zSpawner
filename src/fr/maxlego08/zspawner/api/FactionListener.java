package fr.maxlego08.zspawner.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.zcore.ZPlugin;

public abstract class FactionListener {

	/**
	 * @param player
	 * @param location
	 */
	public abstract boolean canBuild(Player player, Location location);

	/**
	 * 
	 * @param player
	 * @param location
	 * @return
	 */
	public boolean preBuild(Player player, Location location) {

		boolean worldguardBuild = ZPlugin.z().getWorldguard() != null
				? ZPlugin.z().getGuard().canBuild(player, location) : true;

		return canBuild(player, location) && worldguardBuild;
	}

}
