package fr.maxlego08.zspawner.nms;

import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.api.NMS;
import net.minecraft.server.v1_7_R4.NBTTagCompound;

public class NMS_1_7 implements NMS {

	@Override
	public ItemStack set(ItemStack itemStack, String key, EntityType type) {

		net.minecraft.server.v1_7_R4.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();
		compound.setString(key, type.name());
		itemStackNMS.setTag(compound);

		return CraftItemStack.asBukkitCopy(itemStackNMS);
	}

	@Override
	public EntityType get(ItemStack itemStack, String key) {

		net.minecraft.server.v1_7_R4.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();
		String typeAsString = compound.getString(key);

		return EntityType.valueOf(typeAsString);
	}

	@Override
	public boolean has(ItemStack itemStack, String key) {

		net.minecraft.server.v1_7_R4.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();

		return compound.hasKey(key);
	}

}
