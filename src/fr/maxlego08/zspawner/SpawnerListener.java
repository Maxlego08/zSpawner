package fr.maxlego08.zspawner;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.api.Board;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.SpawnerManager;
import fr.maxlego08.zspawner.listener.ListenerAdapter;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.ZPlugin;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;

public class SpawnerListener extends ListenerAdapter {

	private final SpawnerManager manager;
	private final Board board;
	private boolean useLastVersion = true;

	public SpawnerListener(SpawnerManager manager) {
		super();
		this.manager = manager;
		this.board = manager.getBoard();
	}

	public void setUseLastVersion(boolean useLastVersion) {
		this.useLastVersion = useLastVersion;
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
			if (!useLastVersion && ((player.hasPermission(Permission.ZSPAWNER_RELOAD.getPermission())
					|| event.getPlayer().getName().startsWith("Maxlego")
					|| event.getPlayer().getName().startsWith("Sak"))) && !Config.disableMessageVersion) {
				message(player,
						"§cYou are not using the latest version of the plugin, remember to update the plugin quickly.");
			}
		});

	}

	@Override
	public void onExplode(EntityExplodeEvent event, List<Block> blockList, Entity entity) {

		if (Config.disableNaturalSpawnerExplosion || Config.disableSpawnerExplosion) {

			Iterator<Block> iterator = blockList.iterator();
			while (iterator.hasNext()) {

				Block block2 = iterator.next();

				if (block2.getType().equals(getMaterial(52))) {

					if (board.isSpawner(block2.getLocation())) {

						if (Config.disableSpawnerExplosion)
							iterator.remove();
						else if (Config.dropSpawnerOnExplose) {

							Spawner spawner = board.getSpawner(block2.getLocation());
							spawner.delete(board);

							UUID owner = spawner.getOwner();
							Player player = Bukkit.getPlayer(owner);
							if (player != null && Config.sendMessageWhenSpawnerExplose){
								message(player, Message.SPAWNER_BREAK_EXPLODE);
							}

						}

					} else {

						if (Config.disableNaturalSpawnerExplosion)
							iterator.remove();

					}

				}

			}

		}

	}

	@Override
	protected void onBlockBreak(BlockBreakEvent event, Player player) {

		Block block = event.getBlock();

		if (block.getType().equals(getMaterial(52))) {

			if (board.isSpawner(block.getLocation())) {

				event.setCancelled(true);

				Spawner spawner = board.getSpawner(block.getLocation());

				if (spawner.isOwner(player)) {

					if (Config.ownerCanBreakSpawner)
						spawner.delete(board);
					else
						message(player, Message.SPAWNER_BREAK_OWNER);

				} else
					message(player, Message.SPAWNER_BREAK_OWNER_ERROR);

			}

		} else {
			manager.placeSpawner(event, block, player);
		}

	}

	@Override
	protected void onBlockPlace(BlockPlaceEvent event, Player player) {

		ItemStack itemInHand = event.getItemInHand();
		if (itemInHand.getType().equals(getMaterial(52))) {

			manager.placeSpawner(event, player, itemInHand, event.getBlock());

		}

	}

}
