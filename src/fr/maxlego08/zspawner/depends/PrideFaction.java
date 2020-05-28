package fr.maxlego08.zspawner.depends;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.utils.FactionListener;
import fr.pridenetwork.faction.Board;
import fr.pridenetwork.faction.FLocation;
import fr.pridenetwork.faction.FPlayers;

public class PrideFaction extends FactionListener {

	public PrideFaction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canBuild(Player player, Location location) {
		String tag = Board.getFactionAt(new FLocation(location)).getTag();
		String playerTag = FPlayers.getInstance().getByPlayer(player).getFaction().getTag();
		return tag.equals(playerTag);
	}

}
