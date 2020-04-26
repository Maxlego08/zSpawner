package fr.maxlego08.zspawner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.Board;
import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.SpawnerManager;
import fr.maxlego08.zspawner.api.event.SpawnerOpenInventoryEvent;
import fr.maxlego08.zspawner.zcore.enums.Inventory;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import fr.maxlego08.zspawner.zcore.utils.storage.Persist;

public class ZSpawnerManager extends ZUtils implements SpawnerManager {

	private final transient Board board;
	private final transient ZSpawnerPlugin plugin;

	private static Map<UUID, PlayerSpawner> players = new HashMap<UUID, PlayerSpawner>();

	public ZSpawnerManager(Board board, ZSpawnerPlugin plugin) {
		super();
		this.board = board;
		this.plugin = plugin;
	}

	@Override
	public void save(Persist persist) {
		persist.save(this, "spawners");
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(this, ZSpawnerManager.class, "spawners");
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
		else if (hasSpawner(uuid))
			message(player, Message.NO_SPAWNER);
		else {

			PlayerSpawner playerSpawner = getPlayer(uuid);
			if (playerSpawner.isPlacing())
				message(player, Message.PLACING_SPAWNER);
			else {

				SpawnerOpenInventoryEvent event = new SpawnerOpenInventoryEvent(Inventory.INVENTORY_SPAWNER,
						playerSpawner, player);
				event.callEvent();

				if (event.isCancelled())
					return;

				createInventory(player, Inventory.INVENTORY_SPAWNER, 1, playerSpawner);
			}

		}

	}

	@Override
	public void openSendInventory(Player player, Player target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendSpawner(Player player, Player target, Spawner spawner) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSpawner(CommandSender sender, Player target, EntityType type, int number) {
		// TODO Auto-generated method stub

	}

	@Override
	public void giveSpawner(CommandSender sender, Player target, EntityType type, int number) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSpawner(CommandSender sender, Player target, EntityType type, int number) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSpawners(CommandSender sender, Player target, EntityType type) {
		// TODO Auto-generated method stub

	}

}
