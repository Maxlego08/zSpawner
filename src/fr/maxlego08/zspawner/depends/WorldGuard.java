package fr.maxlego08.zspawner.depends;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import fr.maxlego08.zspawner.api.utils.FactionListener;
import fr.maxlego08.zspawner.zcore.ZPlugin;

public class WorldGuard extends FactionListener {

	@Override
	public boolean canBuild(Player player, Location location) {

		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		String version = plugin.getDescription().getVersion();

		// API pour la 1.13 et +
		if (version.contains("7.")) {

			LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
			RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform()
					.getRegionContainer();
			RegionQuery query = container.createQuery();

			com.sk89q.worldedit.util.Location location2 = BukkitAdapter.adapt(location);
			return query.testBuild(location2, localPlayer, Flags.BUILD);

		} else {
			// return ZPlugin.z().getWorldguard().canBuild(player, location);

			WorldGuardPlugin guardPlugin = ZPlugin.z().getWorldguard();

			try {
				Method method = guardPlugin.getClass().getMethod("canBuild", Player.class, Location.class);
				boolean b = (boolean) method.invoke(guardPlugin, player, location);

				return b;

			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				return false;
			}

		}

	}

}
