package fr.maxlego08.zspawner.inventory.inventories;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.inventory.PaginateInventory;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.utils.inventory.ItemButton;

public class InventorySendSpawner extends PaginateInventory<Spawner> {

	private PlayerSpawner playerSpawner;
	private Player receiver;

	public InventorySendSpawner() {
		super(Config.inventoryName, Config.inventorySize);
	}

	@Override
	public ItemStack buildItem(Spawner object) {
		return object.getItemStack();
	}

	@Override
	public void onClick(Spawner object, ItemButton button) {
		player.closeInventory();
		manager.sendSpawner(player, receiver, object);
	}

	@Override
	public List<Spawner> preOpenInventory() {
		
		playerSpawner = (PlayerSpawner) args[0];
		receiver = (Player) args[1];
		
		return playerSpawner.getShortSpawners();
	}

	@Override
	public void postOpenInventory() {

	}

	@Override
	protected void onClose(InventoryCloseEvent event, ZSpawnerPlugin plugin, Player player) {

	}

	@Override
	protected void onDrag(InventoryDragEvent event, ZSpawnerPlugin plugin, Player player) {

	}

	@Override
	protected InventorySendSpawner clone() {
		return new InventorySendSpawner();
	}

}
