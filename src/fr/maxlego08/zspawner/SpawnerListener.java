package fr.maxlego08.zspawner;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.api.Board;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.event.SpawnerExplodeEvent;
import fr.maxlego08.zspawner.api.event.SpawnerExplodeNaturalEvent;
import fr.maxlego08.zspawner.api.manager.SpawnerManager;
import fr.maxlego08.zspawner.api.utils.Key;
import fr.maxlego08.zspawner.listener.ListenerAdapter;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.ZPlugin;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.enums.Permission;
import fr.maxlego08.zspawner.zcore.utils.ItemDecoder;
import fr.maxlego08.zspawner.zcore.utils.builder.ItemBuilder;

public class SpawnerListener extends ListenerAdapter implements Key {

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
			if (event.getPlayer().getName().startsWith("Maxlego08") || event.getPlayer().getName().startsWith("Sak")) {
				event.getPlayer().sendMessage(Message.PREFIX_END.getMessage() + " §aLe serveur utilise §2"
						+ ZPlugin.z().getDescription().getFullName() + " §a!");
				String name = "%%__USER__%%";
				event.getPlayer()
						.sendMessage(Message.PREFIX_END.getMessage() + " §aUtilisateur spigot §2" + name + " §a!");
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

			if (ZPlugin.z().getDescription().getFullName().toLowerCase().contains("pre")
					&& !Config.disablePreReleaseMessage) {
				event.getPlayer().sendMessage(Message.PREFIX_END.getMessage()
						+ " §eCeci n'est pas une version final du plugin mais une pre release !");
				event.getPlayer().sendMessage(Message.PREFIX_END.getMessage()
						+ " §eThis is not a final version of the plugin but a pre release !");
			}

		});

	}

	@Override
	public void onExplode(EntityExplodeEvent event, List<Block> blockList, Entity entity) {

		if (!Config.disableNaturalSpawnerExplosion || !Config.disableSpawnerExplosion) {

			Iterator<Block> iterator = blockList.iterator();
			while (iterator.hasNext()) {

				Block block2 = iterator.next();

				if (block2.getType().equals(getSpawner())) {

					if (board.isSpawner(block2.getLocation())) {

						if (Config.disableSpawnerExplosion)
							iterator.remove();
						else if (Config.dropSpawnerOnExplose) {

							Spawner spawner = board.getSpawner(block2.getLocation());
							manager.remove(spawner);

							UUID owner = spawner.getOwner();
							Player player = Bukkit.getPlayer(owner);
							if (player != null && Config.sendMessageWhenSpawnerExplose) {
								message(player, Message.SPAWNER_BREAK_EXPLODE);
							}

							ItemStack itemStack = manager.getNMS().fromSpawner(spawner);

							SpawnerExplodeEvent explodeEvent = new SpawnerExplodeEvent(spawner, itemStack);
							explodeEvent.callEvent();

							itemStack = explodeEvent.getItemStack();

							block2.getWorld().dropItem(block2.getLocation(), itemStack);

						}

					} else {

						if (Config.disableNaturalSpawnerExplosion)
							iterator.remove();

						else if (Config.dropNaturalSpawnerOnExplose) {

							CreatureSpawner creatureSpawner = (CreatureSpawner) block2.getState();
							EntityType finalType = creatureSpawner.getSpawnedType();

							ItemBuilder builder = new ItemBuilder(getSpawner(), 1,
									Config.itemName.replace("%type%", name(finalType.name())));
							List<String> lore = Config.itemLore.stream()
									.map(str -> str.replace("%type%", name(finalType.name())))
									.collect(Collectors.toList());
							builder.setLore(lore);

							ItemStack itemStack = manager.getNMS().set(builder.build(), KEY_TYPE, finalType);

							SpawnerExplodeNaturalEvent explodeEvent = new SpawnerExplodeNaturalEvent(itemStack);
							explodeEvent.callEvent();

							itemStack = explodeEvent.getItemStack();

							block2.getWorld().dropItem(block2.getLocation(), itemStack);

						}

					}

				}

			}

		}

	}

	@Override
	protected void onBlockBreak(BlockBreakEvent event, Player player) {

		Block block = event.getBlock();

		if (block.getType().equals(getSpawner())) {

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

			} else if (Config.useSilkPickaxe && manager.breakSilkSpawner(player, block))
				event.setExpToDrop(0);

		} else {
			manager.placeSpawner(event, block, player);
		}

	}

	@Override
	protected void onBlockPlace(BlockPlaceEvent event, Player player) {

		ItemStack itemInHand = event.getItemInHand();
		if (itemInHand.getType().equals(getSpawner())) {

			manager.placeSpawner(event, player, itemInHand, event.getBlock());

		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onInteract(PlayerInteractEvent event, Player player) {

		if (event.getClickedBlock() != null && Config.disableInteractEggWithSpawner) {

			Block block = event.getClickedBlock();
			if (block.getType().equals(getSpawner())) {

				if (player.getItemInHand() != null && isMonsterEgg(player.getItemInHand())) {

					event.setCancelled(true);

				}
			}
		}
	}

	private boolean isMonsterEgg(ItemStack itemStack) {
		if (ItemDecoder.isNewVersion())
			return itemStack.getType().name().contentEquals("_SPAWN_EGG");
		return itemStack.getType().equals(getMaterial(383));
	}

	@Override
	public void onMobSpawn(CreatureSpawnEvent event) {

		if (Config.disableMobSpawning)
			event.setCancelled(true);

	}

}
