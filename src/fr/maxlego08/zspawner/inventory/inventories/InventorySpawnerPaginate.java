package fr.maxlego08.zspawner.inventory.inventories;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.event.SpawnerPrePlaceEvent;
import fr.maxlego08.zspawner.inventory.PaginateInventory;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.enums.Inventory;
import fr.maxlego08.zspawner.zcore.enums.Message;
import fr.maxlego08.zspawner.zcore.utils.inventory.Button;
import fr.maxlego08.zspawner.zcore.utils.inventory.ItemButton;

public class InventorySpawnerPaginate extends PaginateInventory<Spawner> {

	private PlayerSpawner playerSpawner;

	public InventorySpawnerPaginate() {
		super(Config.inventoryName, Config.inventorySize);
	}

	@Override
	public ItemStack buildItem(Spawner object) {
		return object.getItemStack();
	}

	@Override
	public void onClick(Spawner object, ItemButton button) {

		if (object.isPlace()) {

			object.delete(manager.getBoard());
			message(player, Message.REMOVE_SPAWNER);
			createInventory(player, Inventory.INVENTORY_SPAWNER_PAGINATE, getPage(), playerSpawner);

			return;
		}

		if (Config.allowUpgradeSpawnerWithShiftClick && button.getEvent().getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)){
			
			manager.upgradeSpawner(player, object, playerSpawner);
			
			return;
		}
		

		SpawnerPrePlaceEvent event = new SpawnerPrePlaceEvent(player, object, playerSpawner);
		event.callEvent();

		if (event.isCancelled())
			return;

		playerSpawner.setCurrentPlacingSpawner(object);
		player.closeInventory();
		message(player, Message.PLACE_SPAWNER_START);

	}

	@Override
	public List<Spawner> preOpenInventory() {
		playerSpawner = (PlayerSpawner) args[0];
		return playerSpawner.getShortSpawners();
	}

	@Override
	public void postOpenInventory() {

		if (Config.displayInformation) {
			Button button = Config.buttonInformation;
			int slot1 = button.getSlot() > inventorySize ? infoSlot : button.getSlot();
			addItem(slot1, button.toItemStack(playerSpawner)).setClick(event -> {
				playerSpawner.toggleShort();
				createInventory(player, Inventory.INVENTORY_SPAWNER_PAGINATE, getPage(), playerSpawner);
			});
		}

		if (Config.displayRemoveAllButton) {
			Button button = Config.buttonRemoveAll;
			int slot1 = button.getSlot() > inventorySize ? removeAllSlot : button.getSlot();
			addItem(slot1, button.toItemStack(playerSpawner)).setClick(event -> {
				playerSpawner.deleteAllSpawners(manager.getBoard());
				createInventory(player, Inventory.INVENTORY_SPAWNER_PAGINATE, getPage(), playerSpawner);
			});
		}

	}

	@Override
	protected void onClose(InventoryCloseEvent event, ZSpawnerPlugin plugin, Player player) {

	}

	@Override
	protected void onDrag(InventoryDragEvent event, ZSpawnerPlugin plugin, Player player) {

	}

	@Override
	protected InventorySpawnerPaginate clone() {
		return new InventorySpawnerPaginate();
	}

}
