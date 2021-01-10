package fr.maxlego08.zspawner.depends;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.utils.FactionListener;
import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.PlayerManager;

public class FactionXHook extends FactionListener {

	public FactionXHook() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canBuild(Player player, Location location) {
		FPlayer fPlayer = PlayerManager.INSTANCE.getFPlayer(player);
		Faction factionAt = GridManager.INSTANCE.getFactionAt(location.getChunk());
		return factionAt.equals(fPlayer.getFaction());
	}

}
