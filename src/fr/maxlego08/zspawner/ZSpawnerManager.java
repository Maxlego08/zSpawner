package fr.maxlego08.zspawner;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.Board;
import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.SpawnerManager;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import fr.maxlego08.zspawner.zcore.utils.storage.Persist;

public class ZSpawnerManager extends ZUtils implements SpawnerManager {

	private final transient Board board;
	private final transient ZSpawnerPlugin plugin;

	public ZSpawnerManager(Board board, ZSpawnerPlugin plugin) {
		super();
		this.board = board;
		this.plugin = plugin;
	}

	@Override
	public void save(Persist persist) {

	}

	@Override
	public void load(Persist persist) {

	}

	@Override
	public PlayerSpawner getPlayer(UUID uuid) {
		return null;
	}

	@Override
	public boolean exit(UUID uuid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSpawner(UUID uuid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public void openInventory(Player player) {
		// TODO Auto-generated method stub

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
