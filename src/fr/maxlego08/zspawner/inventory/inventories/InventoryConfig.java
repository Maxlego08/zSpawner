package fr.maxlego08.zspawner.inventory.inventories;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.ZSpawnerPlugin;
import fr.maxlego08.zspawner.api.enums.Option;
import fr.maxlego08.zspawner.inventory.PaginateInventory;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.utils.builder.ItemBuilder;
import fr.maxlego08.zspawner.zcore.utils.inventory.InventorySize;
import fr.maxlego08.zspawner.zcore.utils.inventory.ItemButton;

public class InventoryConfig extends PaginateInventory<Option> {

	public InventoryConfig() {
		super("§6zSpawner §7- §eConfig", InventorySize.FULL_INVENTORY);
	}

	@Override
	public ItemStack buildItem(Option object) {
		String isBuild = object.getValue() ? "§aenable" : "§cdisable";
		ItemBuilder itemStack = new ItemBuilder(Material.BOOK, "§e" + name(object.name()) + " §8(" + isBuild + "§8)");
		if (object.getValue()) {
			itemStack.glow();
			itemStack.setMaterial(Material.ENCHANTED_BOOK);
		}
		return itemStack.build();
	}

	@Override
	public void onClick(int slot, Option object, ItemButton button) {
		object.setValue(!object.getValue());

		String isBuild = object.getValue() ? "§aenable" : "§cdisable";
		ItemBuilder itemStack = new ItemBuilder(Material.BOOK, "§e" + name(object.name()) + " §8(" + isBuild + "§8)");
		if (object.getValue()) {
			itemStack.glow();
			itemStack.setMaterial(Material.ENCHANTED_BOOK);
		}
		inventory.setItem(slot, itemStack.build());

		Config.getInstance().save(plugin.getPersist());
	}

	@Override
	public List<Option> preOpenInventory() {
		return Arrays.asList(Option.values());
	}

	@Override
	public void postOpenInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onClose(InventoryCloseEvent event, ZSpawnerPlugin plugin, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDrag(InventoryDragEvent event, ZSpawnerPlugin plugin, Player player) {
		// TODO Auto-generated method stub

	}

}
