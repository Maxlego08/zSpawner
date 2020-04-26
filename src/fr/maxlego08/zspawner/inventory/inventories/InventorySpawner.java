package fr.maxlego08.zspawner.inventory.inventories;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.exceptions.InventoryOpenException;
import fr.maxlego08.zspawner.inventory.VInventory;
import fr.maxlego08.zspawner.zcore.utils.inventory.InventoryResult;

public class InventorySpawner extends VInventory {

	@Override
	public InventoryResult openInventory(ZSpawnerPlugin main, Player player, int page, Object... args)
			throws InventoryOpenException {
		
		PlayerSpawner playerSpawner = (PlayerSpawner) args[0];
		
		createInventory("§eMy inventory");
		
		return InventoryResult.SUCCESS;
	}

	@Override
	protected void onClose(InventoryCloseEvent event, ZSpawnerPlugin plugin, Player player) {

	}

	@Override
	protected void onDrag(InventoryDragEvent event, ZSpawnerPlugin plugin, Player player) {

	}

}
