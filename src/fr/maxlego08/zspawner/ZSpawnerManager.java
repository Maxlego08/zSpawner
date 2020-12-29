package fr.maxlego08.zspawner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.zspawner.api.Board;
import fr.maxlego08.zspawner.api.FakeSpawner;
import fr.maxlego08.zspawner.api.NMS;
import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.SimpleLevel;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.enums.InventoryType;
import fr.maxlego08.zspawner.api.event.SpawnerAddEvent;
import fr.maxlego08.zspawner.api.event.SpawnerGiveEvent;
import fr.maxlego08.zspawner.api.event.SpawnerOpenInventoryEvent;
import fr.maxlego08.zspawner.api.event.SpawnerOpenShowInventoryEvent;
import fr.maxlego08.zspawner.api.event.SpawnerPlaceEvent;
import fr.maxlego08.zspawner.api.event.SpawnerPreUpgradeEvent;
import fr.maxlego08.zspawner.api.event.SpawnerRegisterEvent;
import fr.maxlego08.zspawner.api.event.SpawnerRemoveAllEvent;
import fr.maxlego08.zspawner.api.event.SpawnerRemoveEvent;
import fr.maxlego08.zspawner.api.event.SpawnerSendEvent;
import fr.maxlego08.zspawner.api.event.SpawnerSilkEnchantEvent;
import fr.maxlego08.zspawner.api.event.SpawnerSilkEvent;
import fr.maxlego08.zspawner.api.manager.LevelManager;
import fr.maxlego08.zspawner.api.manager.PickaxeManager;
import fr.maxlego08.zspawner.api.manager.SpawnerManager;
import fr.maxlego08.zspawner.api.utils.FactionListener;
import fr.maxlego08.zspawner.api.utils.Key;
import fr.maxlego08.zspawner.depends.LegacyFaction;
import fr.maxlego08.zspawner.depends.MassiveFaction;
import fr.maxlego08.zspawner.depends.NoFaction;
import fr.maxlego08.zspawner.depends.SuperiorSkyblock2;
import fr.maxlego08.zspawner.depends.UUIDFaction;
import fr.maxlego08.zspawner.nms.NMS_1_10;
import fr.maxlego08.zspawner.nms.NMS_1_11;
import fr.maxlego08.zspawner.nms.NMS_1_12;
import fr.maxlego08.zspawner.nms.NMS_1_13;
import fr.maxlego08.zspawner.nms.NMS_1_14;
import fr.maxlego08.zspawner.nms.NMS_1_15;
import fr.maxlego08.zspawner.nms.NMS_1_16_R1;
import fr.maxlego08.zspawner.nms.NMS_1_16_R2;
import fr.maxlego08.zspawner.nms.NMS_1_7;
import fr.maxlego08.zspawner.nms.NMS_1_8;
import fr.maxlego08.zspawner.nms.NMS_1_9;
import fr.maxlego08.zspawner.objects.FakeSpawnerObject;
import fr.maxlego08.zspawner.objects.PlayerObject;
import fr.maxlego08.zspawner.objects.SpawnerObject;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.enums.EnumVersion;
import fr.maxlego08.zspawner.zcore.enums.Inventory;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.logger.Logger;
import fr.maxlego08.zspawner.zcore.logger.Logger.LogType;
import fr.maxlego08.zspawner.zcore.utils.ItemDecoder;
import fr.maxlego08.zspawner.zcore.utils.economy.EconomyUtils;
import fr.maxlego08.zspawner.zcore.utils.storage.Persist;

public class ZSpawnerManager extends EconomyUtils implements SpawnerManager, Key {

	private final transient Board board;
	private transient FactionListener factionListener;
	private transient final NMS nms;
	private transient final LevelManager levelManager;
	private transient final PickaxeManager pickaxeManager;
	private transient final double version = ItemDecoder.getNMSVersion();
	private transient Map<UUID, PlayerSpawner> players = new HashMap<UUID, PlayerSpawner>();

	private static Map<UUID, List<Spawner>> spawners = new HashMap<>();

	protected ZSpawnerManager(Board board, ZSpawnerPlugin plugin) {
		super();
		this.board = board;
		this.levelManager = plugin.getLevelManager();
		this.pickaxeManager = plugin.getPickaxeManager();

		for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {

			if (factionListener != null)
				continue;

			if (pl.getName().equalsIgnoreCase("LegacyFactions")) {
				factionListener = new LegacyFaction();
				Logger.info("LegacyFaction plugin detected successfully.", LogType.SUCCESS);
			} else if (pl.getName().equalsIgnoreCase("SuperiorSkyblock2")) {
				factionListener = new SuperiorSkyblock2();
				Logger.info("SuperiorSkyblock2 plugin detected successfully.", LogType.SUCCESS);
			} else if (pl.getName().equalsIgnoreCase("Factions")) {
				String author = pl.getDescription().getAuthors().toString();
				if (author.contains("Driftay")) {
					factionListener = new UUIDFaction();
					Logger.info("SaberFaction plugin detected successfully.", LogType.SUCCESS);
				} else if (author.contains("drtshock")) {
					factionListener = new UUIDFaction();
					Logger.info("FactionUUID plugin detected successfully.", LogType.SUCCESS);
				} else if (author.contains("Cayorion") && Bukkit.getPluginManager().isPluginEnabled("MassiveCore")) {
					factionListener = new MassiveFaction();
					Logger.info("Massivecraft Faction plugin detected successfully.", LogType.SUCCESS);
				}
			}
		}

		if (factionListener == null) {
			factionListener = new NoFaction();
			if (factionListener instanceof NoFaction)
				Logger.info("No faction plugin was detected.", LogType.SUCCESS);
		}
		SpawnerRegisterEvent event = new SpawnerRegisterEvent(factionListener);
		Bukkit.getPluginManager().callEvent(event);
		factionListener = event.getFactionListener();

		/**
		 * NMS
		 */

		if (version == 1.8) {
			nms = new NMS_1_8();
		} else if (version == 1.16) {
			nms = ItemDecoder.getVersion().equals(EnumVersion.V_16_R1) ? new NMS_1_16_R1() : new NMS_1_16_R2();
		} else if (version == 1.15) {
			nms = new NMS_1_15();
		} else if (version == 1.14) {
			nms = new NMS_1_14();
		} else if (version == 1.13) {
			nms = new NMS_1_13();
		} else if (version == 1.12) {
			nms = new NMS_1_12();
		} else if (version == 1.11) {
			nms = new NMS_1_11();
		} else if (version == 1.10) {
			nms = new NMS_1_10();
		} else if (version == 1.9) {
			nms = new NMS_1_9();
		} else if (version == 1.7) {
			nms = new NMS_1_7();
		} else
			nms = null;

		if (nms != null)
			Logger.info("Load " + nms.getClass().getName(), LogType.SUCCESS);
	}

	@Override
	public void save(Persist persist) {
		spawners = new HashMap<>();
		players.forEach((uuid, player) -> spawners.put(uuid, player.getSpawners()));
		persist.save(this, "spawners");
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(this, ZSpawnerManager.class, "spawners");
		players = new HashMap<UUID, PlayerSpawner>();
		spawners.forEach((uuid, list) -> players.put(uuid, new PlayerObject(uuid, list)));
		players.values().forEach(player -> player.getSpawners().forEach(spawner -> {
			if (spawner.isPlace())
				board.placeSpawner(spawner.getLocation(), spawner);
		}));
	}

	@Override
	public PlayerSpawner getPlayer(UUID uuid) {
		if (!players.containsKey(uuid)) {
			PlayerSpawner playerSpawner = new PlayerObject(uuid);
			players.put(uuid, playerSpawner);
		}
		return players.get(uuid);
	}

	@Override
	public boolean exit(UUID uuid) {
		return players.containsKey(uuid);
	}

	@Override
	public boolean hasSpawner(UUID uuid) {
		return exit(uuid) && getPlayer(uuid).spawnerSize() > 0;
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public void openInventory(Player player) {

		UUID uuid = player.getUniqueId();

		if (!exit(uuid))
			message(player, Message.NO_SPAWNER);
		else if (!hasSpawner(uuid))
			message(player, Message.NO_SPAWNER);
		else {

			PlayerSpawner playerSpawner = getPlayer(uuid);
			if (playerSpawner.isPlacing())
				message(player, Message.PLACING_SPAWNER);
			else {

				Inventory inventory = Config.type == InventoryType.PAGINATE ? Inventory.INVENTORY_SPAWNER_PAGINATE
						: Inventory.INVENTORY_SPAWNER;

				SpawnerOpenInventoryEvent event = new SpawnerOpenInventoryEvent(inventory, playerSpawner, player);
				event.callEvent();

				if (event.isCancelled())
					return;

				createInventory(player, inventory, 1, playerSpawner);
			}

		}

	}

	@Override
	public void openSendInventory(Player player, Player target) {

		UUID uuid = player.getUniqueId();

		if (!exit(uuid))
			message(player, Message.NO_SPAWNER);
		else if (!hasSpawner(uuid))
			message(player, Message.NO_SPAWNER);
		else {

			if (player.getName().equals(target.getName())) {
				message(player, Message.SEND_SPAWNER_ERROR);
				return;
			}

			PlayerSpawner playerSpawner = getPlayer(uuid);

			if (playerSpawner.isPlacing()) {
				message(player, Message.PLACING_SPAWNER);
				return;
			}

			SpawnerOpenInventoryEvent event = new SpawnerOpenInventoryEvent(Inventory.INVENTORY_SPAWNER_SEND,
					playerSpawner, player);
			event.callEvent();

			if (event.isCancelled())
				return;

			createInventory(player, event.getInventory(), 1, playerSpawner, target);

		}

	}

	@Override
	public void sendSpawner(Player player, Player target, Spawner spawner) {

		UUID uuid = player.getUniqueId();
		spawner.delete(board);

		PlayerSpawner playerSpawner = getPlayer(uuid);
		PlayerSpawner targetPlayerSpawner = getPlayer(target.getUniqueId());
		Spawner newSpawner = new SpawnerObject(target.getUniqueId(), spawner, this);

		SpawnerSendEvent event = new SpawnerSendEvent(player, target, playerSpawner, targetPlayerSpawner, spawner,
				newSpawner);
		event.callEvent();

		if (event.isCancelled())
			return;

		playerSpawner.removeSpawner(board, spawner);
		targetPlayerSpawner.addSpawner(newSpawner);

		String message = Message.SEND_SPAWNER_PLAYER.getMessage();
		message = message.replace("%type%", name(spawner.getType().name()));
		message = message.replace("%player%", target.getName());

		message(player, message);

		message = Message.SEND_SPAWNER_RECEIVER.getMessage();
		message = message.replace("%type%", name(spawner.getType().name()));
		message = message.replace("%sender%", player.getName());

		message(target, message);

	}

	@Override
	public void addSpawner(CommandSender sender, OfflinePlayer target, EntityType type, int number, int level) {

		PlayerSpawner playerSpawner = getPlayer(target.getUniqueId());

		SpawnerAddEvent event = new SpawnerAddEvent(sender, target, playerSpawner, type, number, level);
		event.callEvent();

		if (event.isCancelled())
			return;

		type = event.getType();
		number = event.getAmount();
		level = event.getLevel();

		if (number < 0)
			return;

		for (int a = 0; a < number; a++) {
			Spawner spawner = new SpawnerObject(target.getUniqueId(), type, level, this);
			playerSpawner.addSpawner(spawner);
		}

		String message = Message.ADD_SPAWNER_SENDER.getMessage();
		message = message.replace("%how%", String.valueOf(number));
		message = message.replace("%type%", name(type.name()));
		message = message.replace("%player%", target.getName());

		message(sender, message);

		message = Message.ADD_SPAWNER_RECEIVER.getMessage();
		message = message.replace("%how%", String.valueOf(number));
		message = message.replace("%type%", name(type.name()));
		if (target.isOnline())
			message(target.getPlayer(), message);

	}

	@Override
	public void giveSpawner(CommandSender sender, Player target, EntityType type, int number, int level) {

		SimpleLevel levelObject = levelManager.getLevel(level);
		if (levelObject == null && level != 0) {
			message(sender, Message.LEVEL_ERROR, level);
			return;
		}

		SpawnerGiveEvent event = new SpawnerGiveEvent(sender, target, type, number, level);
		event.callEvent();

		if (event.isCancelled())
			return;

		EntityType finalType = event.getType();
		number = event.getAmount();

		if (number < 0)
			return;

		FakeSpawner fakeSpawner = new FakeSpawnerObject(this, type, level);
		ItemStack itemStack = nms.fromSpawner(fakeSpawner);
		itemStack.setAmount(number);

		give(target, itemStack);

		String message = Message.GIVE_SPAWNER_SENDER.getMessage();
		message = message.replace("%how%", String.valueOf(number));
		message = message.replace("%type%", name(finalType.name()));
		message = message.replace("%player%", target.getName());
		message(sender, message);

		message = Message.GIVE_SPAWNER_RECEIVER.getMessage();
		message = message.replace("%how%", String.valueOf(number));
		message = message.replace("%type%", name(finalType.name()));
		message(target, message);

	}

	@Override
	public void removeSpawnerAll(CommandSender sender, OfflinePlayer target) {

		UUID uuid = target.getUniqueId();

		if (!hasSpawner(uuid)) {
			message(sender, Message.NO_SPAWNER_TARGET, target.getName());
			return;
		}

		PlayerSpawner playerSpawner = getPlayer(uuid);

		SpawnerRemoveAllEvent event = new SpawnerRemoveAllEvent(playerSpawner);
		event.callEvent();

		if (event.isCancelled())
			return;

		playerSpawner.removeAll(board);

		String message = Message.REMOVE_ALL_SPAWNER_SENDER.getMessage();
		message = message.replace("%player%", target.getName());

		message(sender, message);

		if (target.isOnline()) {
			message = Message.REMOVE_ALL_SPAWNER_RECEIVER.getMessage();
			message(target.getPlayer(), message);
		}

	}

	@Override
	public void removeSpawner(CommandSender sender, OfflinePlayer target, EntityType type, int number) {

		UUID uuid = target.getUniqueId();

		if (!hasSpawner(uuid)) {
			message(sender, Message.NO_SPAWNER_TARGET, target.getName());
			return;
		}

		PlayerSpawner playerSpawner = getPlayer(uuid);

		SpawnerRemoveEvent event = new SpawnerRemoveEvent(sender, target, playerSpawner, type, number);
		event.callEvent();

		if (event.isCancelled())
			return;

		type = event.getType();
		number = event.getAmount();

		if (number < 0)
			return;

		playerSpawner.removeSpawner(board, type, number);

		String message = Message.REMOVE_SPAWNER_SENDER.getMessage();
		message = message.replace("%how%", String.valueOf(number));
		message = message.replace("%type%", name(type.name()));
		message = message.replace("%player%", target.getName());

		message(sender, message);

		if (target.isOnline()) {

			message = Message.REMOVE_SPAWNER_RECEIVER.getMessage();
			message = message.replace("%how%", String.valueOf(number));
			message = message.replace("%type%", name(type.name()));

			message(target.getPlayer(), message);
		}

	}

	@Override
	public void placeSpawner(BlockBreakEvent event, Block block, Player player) {

		// Verif de la permission de casser ici

		UUID uuid = player.getUniqueId();
		if (!hasSpawner(uuid))
			return;

		PlayerSpawner playerSpawner = getPlayer(uuid);
		if (playerSpawner.isPlacing()) {

			event.setCancelled(true);
			if (!factionListener.preBuild(player, block.getLocation())) {
				message(player, Message.PLACE_SPAWNER_ERROR);
				return;
			}

			if (isBlacklist(block)) {
				message(player, Message.PLACE_SPAWNER_ERROR_BLACKLIST);
				return;
			}

			if (Config.limitSpawnerPerChunk
					&& board.countSpawners(block.getChunk()) > Config.limitSpawnerPerChunkAmount) {
				message(player, Message.PLACE_SPAWNER_ERROR_LIMIT);
				return;
			}

			Spawner spawner = playerSpawner.getCurrentPlacingSpawner();
			SpawnerPlaceEvent placeEvent = new SpawnerPlaceEvent(player, playerSpawner, spawner, block.getLocation());

			if (placeEvent.isCancelled())
				return;

			Location finalLocation = placeEvent.getLocation() == null ? block.getLocation() : placeEvent.getLocation();
			spawner.place(finalLocation);
			playerSpawner.placeSpawner();
			board.placeSpawner(finalLocation, spawner);

			message(player, Message.PLACE_SPAWNER_SUCCESS);

		}

	}

	@Override
	public int count() {
		AtomicInteger amount = new AtomicInteger();
		players.values().forEach(player -> amount.getAndAdd(player.spawnerSize()));
		return amount.get();
	}

	@Override
	public FactionListener getFactionListener() {
		return factionListener;
	}

	@Override
	public void setFactionListener(FactionListener factionListener) {
		this.factionListener = factionListener;
	}

	@Override
	public boolean isBlacklist(Block block) {
		return Config.blacklistMaterial.contains(block.getType());
	}

	@Override
	public NMS getNMS() {
		return nms;
	}

	@Override
	public void placeSpawner(BlockPlaceEvent event, Player player, ItemStack itemInHand, Block block) {

		if (event.isCancelled())
			return;

		if (!factionListener.preBuild(player, block.getLocation())) {
			event.setCancelled(true);
			message(player, Message.PLACE_SPAWNER_ERROR);
			return;
		}

		if (nms.has(itemInHand, KEY_TYPE)) {

			EntityType entityType = nms.get(itemInHand, KEY_TYPE);

			int level = 0;
			if (nms.has(itemInHand, KEY_LEVEL))
				level = nms.getInteger(itemInHand, KEY_LEVEL);

			block.setType(getSpawner());
			CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
			creatureSpawner.setSpawnedType(entityType);
			if (version != 1.8 && version != 1.7)
				creatureSpawner.update();

			if (level > 0) {

				nms.updateSpawner(new SpawnerObject(null, entityType, 0, null, block.getLocation(), level, this));

			}

			message(player, Message.PLACE_SPAWNER.getMessage().replace("%type%", name(entityType.name())));

		}

	}

	@Override
	public void remove(Spawner spawner) {

		UUID uuid = spawner.getOwner();
		spawner.delete(board);
		PlayerSpawner playerSpawner = getPlayer(uuid);
		playerSpawner.removeSpawner(board, spawner);

	}

	@Override
	public LevelManager getLevelManager() {
		return levelManager;
	}

	@Override
	public void givePickaxe(CommandSender sender, Player player, int durabilty, int maxDurabilty) {

		ItemStack itemStack = pickaxeManager.getPickaxe(durabilty, maxDurabilty);
		give(player, itemStack);

		message(player, Message.GIVE_PICKAXE_RECEIVER);
		message(sender, Message.GIVE_PICKAXE_SENDER.getMessage().replace("%player%", player.getName()));

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean breakSilkSpawner(BlockBreakEvent event, Player player, Block block) {

		ItemStack itemStack = player.getItemInHand();
		if (itemStack == null)
			return false;

		if (!factionListener.preBuild(player, block.getLocation())) {
			event.setCancelled(true);
			message(player, Message.BREAK_SPAWNER_ERROR);
			return false;
		}

		if (pickaxeManager.isPickaxe(itemStack)) {

			if (!factionListener.preBuild(player, block.getLocation()))
				return false;

			int dura = nms.getInteger(itemStack, KEY_DURA);
			int maxDura = nms.getInteger(itemStack, KEY_MAX_DURA);
			ItemStack spawner = nms.getLevelFromSpawnBlock(levelManager, block);

			SpawnerSilkEvent spawnerEvent = new SpawnerSilkEvent(dura, maxDura, player, block, spawner, dura - 1);
			spawnerEvent.callEvent();

			if (spawnerEvent.isCancelled())
				return false;

			int newDura = spawnerEvent.getNewDura();
			spawner = spawnerEvent.getItemStack();

			if (newDura <= 0)

				removeItemInHand(player);

			else {

				itemStack = pickaxeManager.getPickaxe(newDura, maxDura);
				player.setItemInHand(itemStack);

			}

			block.getWorld().dropItem(block.getLocation(), spawner);

			return true;

		} else if (Config.enableSilkPickaxeWithEnchant && pickaxeManager.isEnchantPickaxe(itemStack)) {
			if (!factionListener.preBuild(player, block.getLocation()))
				return false;

			ItemStack spawner = nms.getLevelFromSpawnBlock(levelManager, block);

			SpawnerSilkEnchantEvent spawnerEvent = new SpawnerSilkEnchantEvent(player, block, spawner);
			spawnerEvent.callEvent();

			if (spawnerEvent.isCancelled())
				return false;

			spawner = spawnerEvent.getItemStack();
			block.getWorld().dropItem(block.getLocation(), spawner);

			return true;
		}

		return false;

	}

	@Override
	public void upgradeSpawner(Player player, Spawner object, PlayerSpawner playerSpawner) {

		SimpleLevel currentLevel = object.getLevel();
		SimpleLevel newLevel = currentLevel == null ? levelManager.getLevel(1) : currentLevel.next();

		SpawnerPreUpgradeEvent event = new SpawnerPreUpgradeEvent(object, currentLevel, newLevel, player, playerSpawner,
				newLevel == null ? 0 : newLevel.getPrice());
		event.callEvent();

		if (event.isCancelled())
			return;

		newLevel = event.getNewLevel();
		double price = event.getPrice();

		if (newLevel == null) {
			message(player, Message.SPAWNER_UPGRADE_ERROR);
			return;
		}

		if (!hasMoney(newLevel.getEconomy(), player, price)) {
			message(player, Message.SPAWNER_UPGRADE_ERROR_MONEY);
			return;
		}

		withdrawMoney(newLevel.getEconomy(), player, price);
		object.setLevel(newLevel.getId());
		message(player,
				Message.SPAWNER_UPGRADE_SUCCESS.getMessage().replace("%level%", String.valueOf(newLevel.getId())));
		player.closeInventory();
	}

	@Override
	public void openConfig(Player player) {
		if (player != null)
			createInventory(player, Inventory.INVENTORY_SPAWNER_CONFIG);
	}

	@Override
	public void showInventory(Player player, OfflinePlayer target) {

		UUID uuid = target.getUniqueId();

		if (!exit(uuid))
			message(player, Message.NO_SPAWNER_TARGET, target.getName());
		else if (!hasSpawner(uuid))
			message(player, Message.NO_SPAWNER_TARGET, target.getName());
		else {

			PlayerSpawner playerSpawner = getPlayer(uuid);

			Inventory inventory = Inventory.INVENTORY_SPAWNER_SHOW;
			SpawnerOpenShowInventoryEvent event = new SpawnerOpenShowInventoryEvent(inventory, playerSpawner, player);
			event.callEvent();

			if (event.isCancelled())
				return;

			createInventory(player, inventory, 1, playerSpawner);

		}

	}
}
