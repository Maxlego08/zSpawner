package fr.maxlego08.zspawner.depends;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;

import fr.maxlego08.zspawner.api.utils.FactionListener;

public class SuperiorSkyblock2 extends FactionListener {

	private SuperiorSkyblock plugin = SuperiorSkyblockAPI.getSuperiorSkyblock();

	private Island getIsland(Player player) {
		if (plugin == null)
			plugin = SuperiorSkyblockAPI.getSuperiorSkyblock();
		return plugin.getGrid().getIsland(SuperiorSkyblockAPI.getPlayer(player));
	}

	@Override
	public boolean canBuild(Player player, Location location) {

		if (plugin == null)
			plugin = SuperiorSkyblockAPI.getSuperiorSkyblock();

		Island island = plugin.getGrid().getIslandAt(location);
		Island playerIsland = getIsland(player);

		return island.getOwner().getUniqueId().equals(playerIsland.getOwner().getUniqueId());
	}

}
