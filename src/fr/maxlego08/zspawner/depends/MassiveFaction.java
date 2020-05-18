package fr.maxlego08.zspawner.depends;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.massivecore.ps.PS;

import fr.maxlego08.zspawner.api.utils.FactionListener;

public class MassiveFaction extends FactionListener{

	@Override
	public boolean canBuild(Player player, Location location) {
		String tag = BoardColl.get().getFactionAt(PS.valueOf(location)).getName();
		String playerTag = MPlayerColl.get().get(player).getFaction().getName();
		return tag.equals(playerTag);
	}

}
