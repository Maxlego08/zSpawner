package fr.maxlego08.zspawner.depends;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;

import fr.maxlego08.zspawner.api.utils.FactionListener;

public class UUIDFaction extends FactionListener {

	public UUIDFaction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canBuild(Player player, Location location) {
		String tag = Board.getInstance().getFactionAt(new FLocation(location)).getTag();
		String playerTag = FPlayers.getInstance().getByPlayer(player).getFaction().getTag();
		return tag.equals(playerTag);
	}

}
