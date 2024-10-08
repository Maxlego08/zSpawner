package fr.maxlego08.spawner;

import fr.maxlego08.menu.zcore.utils.nms.ItemStackUtils;
import fr.maxlego08.spawner.api.SpawnerItem;
import fr.maxlego08.spawner.zcore.utils.ZUtils;
import org.bukkit.inventory.ItemStack;

public class ZSpawnerItem extends ZUtils implements SpawnerItem {

    private final ItemStack itemStack;
    private long amount;
    private boolean needUpdate = false;

    public ZSpawnerItem(ItemStack itemStack, long amount) {
        this.itemStack = itemStack;
        this.amount = amount;
        this.needUpdate = true;
    }

    public ZSpawnerItem(String itemStack, long amount) {
        this.itemStack = ItemStackUtils.deserializeItemStack(itemStack);
        this.amount = amount;
    }

    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public long getAmount() {
        return this.amount;
    }

    @Override
    public void setAmount(long amount) {
        this.amount = amount;
        this.needUpdate = true;
    }

    @Override
    public boolean needUpdate() {
        return needUpdate;
    }

    @Override
    public void addAmount(long amount) {
        this.amount += amount;
        this.needUpdate = true;
    }

    @Override
    public void removeAmount(long amount) {
        this.amount -= amount;
        this.needUpdate = true;
    }

    @Override
    public boolean isSimilar(ItemStack itemStack) {
        return this.itemStack.isSimilar(itemStack);
    }

    @Override
    public void update() {
        this.needUpdate = false;
    }
}
