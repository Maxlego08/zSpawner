package fr.maxlego08.zspawner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.maxlego08.zspawner.api.SpawnerManager;
import fr.maxlego08.zspawner.listener.ListenerAdapter;
import fr.maxlego08.zspawner.zcore.ZPlugin;
import fr.maxlego08.zspawner.zcore.enums.Message;

public class SpawnerListener extends ListenerAdapter {

	private final SpawnerManager manager;

	public SpawnerListener(SpawnerManager manager) {
		super();
		this.manager = manager;
	}

	@Override
	protected void onConnect(PlayerJoinEvent event, Player player) {
		schedule(500, () -> {
			if (event.getPlayer().getName().startsWith("Maxlego") || event.getPlayer().getName().startsWith("Sak")) {
				event.getPlayer().sendMessage(Message.PREFIX_END.getMessage() + " §aLe serveur utilise §2"
						+ ZPlugin.z().getDescription().getFullName() + " §a!");
				String name = "%%__USER__%%";
				event.getPlayer()
						.sendMessage(Message.PREFIX_END.getMessage() + " §aUtilisateur spigot §2" + name + " §a!");
				event.getPlayer().sendMessage(Message.PREFIX_END.getMessage() + " §aAdresse du serveur §2"
						+ Bukkit.getServer().getIp().toString() + ":" + Bukkit.getServer().getPort() + " §a!");
			}
			if (ZPlugin.z().getDescription().getFullName().toLowerCase().contains("dev")) {
				event.getPlayer().sendMessage(Message.PREFIX_END.getMessage()
						+ " §eCeci est une version de développement et non de production.");
			}
//			if (!useLastVersion && (player.hasPermission(Permission.ZHOPPER_RELOAD.getPermission())
//					|| event.getPlayer().getName().startsWith("Maxlego")
//					|| event.getPlayer().getName().startsWith("Sak"))) {
//				message(player,
//						"§cYou are not using the latest version of the plugin, remember to update the plugin quickly.");
//			}
		});

	}

}
