package fr.maxlego08.zspawner.depends;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.utils.FactionListener;

public class NoFaction extends FactionListener {

	public NoFaction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canBuild(Player player, Location location) {
		return true;
	}

}
