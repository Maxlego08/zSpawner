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
import fr.maxlego08.zspawner.zcore.utils.inventory.Button;
import fr.maxlego08.zspawner.zcore.utils.inventory.ItemButton;

public class InventorySpawnerPaginate extends PaginateInventory<Spawner> {

	public InventorySpawnerPaginate() {
		super(Config.inventoryName, Config.inventorySize);
	}

	@Override
	public ItemStack buildItem(Spawner object) {
		return object.getItemStack();
	}

	@Override
	public void onClick(Spawner object, ItemButton button) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Spawner> preOpenInventory() {
		PlayerSpawner playerSpawner = (PlayerSpawner) args[0];
		return playerSpawner.getShortSpawners();
	}

	@Override
	public void postOpenInventory() {

		Button button = Config.buttonInformation;
		addItem(button.getSlot(), button.toItemStack());
		
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
