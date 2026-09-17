package fr.maxlego08.spawner.buttons.virtual;

import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.Pagination;
import fr.maxlego08.spawner.SpawnerManager;
import fr.maxlego08.spawner.SpawnerPlugin;
import fr.maxlego08.spawner.api.Spawner;
import fr.maxlego08.spawner.api.SpawnerItem;
import fr.maxlego08.spawner.api.utils.PlayerSpawner;
import fr.maxlego08.spawner.placeholder.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemsButton extends PaginateButton {

    private final SpawnerPlugin plugin;

    public ItemsButton(Plugin plugin) {
        this.plugin = (SpawnerPlugin) plugin;
    }

    private List<SpawnerItem> getSpawnerItems(Player player) {
        PlayerSpawner playerSpawner = this.plugin.getManager().getPlayerSpawners().get(player.getUniqueId());
        return playerSpawner != null ? playerSpawner.getVirtualSpawner() != null ? playerSpawner.getVirtualSpawner().getItems() : new ArrayList<>() : new ArrayList<>();
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {

        PlayerSpawner playerSpawner = this.plugin.getManager().getPlayerSpawners().get(player.getUniqueId());
        Spawner spawner = playerSpawner == null ? null : playerSpawner.getVirtualSpawner() == null ? null : playerSpawner.getVirtualSpawner();
        if (spawner == null) return;

        List<SpawnerItem> items = getSpawnerItems(player);
        Pagination<SpawnerItem> pagination = new Pagination<>();
        List<SpawnerItem> paginatedItems = pagination.paginate(items, this.slots.size(), inventory.getPage());
        List<String> lore = this.getItemStack().getLore();

        for (int index = 0; index != Math.min(this.slots.size(), paginatedItems.size()); index++) {

            int slot = this.slots.get(index);
            SpawnerItem spawnerItem = paginatedItems.get(index);

            ItemStack itemStack = spawnerItem.getItemStack().clone();
            itemStack.setAmount(1);

            ItemMeta itemMeta = itemStack.getItemMeta();
            plugin.getInventoryManager().getMeta().updateLore(itemMeta, lore.stream().map(string -> {
                string = string.replace("%quantity%", plugin.getManager().format(spawnerItem.getAmount()));
                return Placeholder.getPlaceholder().setPlaceholders(player, string);
            }).collect(Collectors.toList()), player);
            itemStack.setItemMeta(itemMeta);

            inventory.addItem(slot, itemStack).setClick(event -> onClick(spawnerItem, spawner, player, event, inventory.getPage()));
        }
    }

    private void onClick(SpawnerItem spawnerItem, Spawner spawner, Player player, InventoryClickEvent event, int page) {

        SpawnerManager manager = plugin.getManager();
        if (event.getClick() == ClickType.SHIFT_LEFT) {
            manager.fillInventoryWithLoot(player, spawner, spawnerItem);
        } else if (event.isRightClick()) {
            manager.removeStackLoot(player, spawner, spawnerItem, 1);
        } else if (event.isLeftClick()) {
            manager.removeStackLoot(player, spawner, spawnerItem, 64);
        }

        // Seuls les autres joueurs regardant ce même spawner ont besoin d'un rafraichissement.
        manager.getPlayerSpawners().forEach((uuid, playerSpawner) -> {
            if (playerSpawner.getVirtualSpawner() != spawner || uuid.equals(player.getUniqueId())) return;
            Player viewer = Bukkit.getPlayer(uuid);
            if (viewer != null) this.plugin.getInventoryManager().updateInventory(viewer, this.plugin);
        });
        manager.openVirtualSpawner(player, spawner, page);
    }

    @Override
    public boolean isPermanent() {
        return true;
    }

    @Override
    public int getPaginationSize(Player player) {
        List<SpawnerItem> items = getSpawnerItems(player);
        return items.size();
    }
}
