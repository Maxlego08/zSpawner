package fr.maxlego08.zspawner.depends;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.utils.FactionListener;
import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.entity.FPlayerColl;

public class LegacyFaction extends FactionListener {

	public LegacyFaction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canBuild(Player player, Location location) {
		@SuppressWarnings("deprecation")
		String tag = net.redstoneore.legacyfactions.entity.Board.get().getFactionAt(new FLocation(location)).getTag();
		String playerTag = FPlayerColl.get(player).getFaction().getTag();
		return tag.equals(playerTag);
	}

}
