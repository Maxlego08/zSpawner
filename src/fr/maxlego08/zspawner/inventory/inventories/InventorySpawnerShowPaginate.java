package fr.maxlego08.zspawner.inventory.inventories;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.inventory.PaginateInventory;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.enums.Inventory;
import fr.maxlego08.zspawner.zcore.utils.inventory.Button;
import fr.maxlego08.zspawner.zcore.utils.inventory.ItemButton;

public class InventorySpawnerShowPaginate extends PaginateInventory<Spawner> {

	private PlayerSpawner playerSpawner;

	public InventorySpawnerShowPaginate() {
		super(Config.inventoryNameShow, Config.inventorySize);
	}

	@Override
	public ItemStack buildItem(Spawner object) {
		return object.getItemStack();
	}

	@Override
	public void onClick(int slot, Spawner object, ItemButton button) {

	}

	@Override
	public List<Spawner> preOpenInventory() {
		playerSpawner = (PlayerSpawner) args[0];

		this.inventoryName = inventoryName.replace("%player%",
				Bukkit.getOfflinePlayer(playerSpawner.getUser()).getName());

		return playerSpawner.getShortSpawners();
	}

	@Override
	public void postOpenInventory() {

		if (Config.displayInformation) {
			Button button = Config.buttonInformation;
			int slot1 = button.getSlot() > inventorySize ? infoSlot : button.getSlot();
			addItem(slot1, button.toItemStack(playerSpawner)).setClick(event -> {
				playerSpawner.toggleShort();
				createInventory(player, Inventory.INVENTORY_SPAWNER_SHOW, getPage(), playerSpawner);
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
	protected InventorySpawnerShowPaginate clone() {
		return new InventorySpawnerShowPaginate();
	}

}
