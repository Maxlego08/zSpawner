package fr.maxlego08.zspawner.nms;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.api.NMS;
import fr.maxlego08.zspawner.api.SimpleLevel;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import fr.maxlego08.zspawner.zcore.utils.builder.ItemBuilder;
import net.minecraft.server.v1_11_R1.MobSpawnerAbstract;
import net.minecraft.server.v1_11_R1.NBTTagCompound;

public class NMS_1_11 extends ZUtils implements NMS {

	@Override
	public ItemStack set(ItemStack itemStack, String key, EntityType type) {

		net.minecraft.server.v1_11_R1.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();
		compound.setString(key, type.name());
		itemStackNMS.setTag(compound);

		return CraftItemStack.asBukkitCopy(itemStackNMS);
	}

	@Override
	public EntityType get(ItemStack itemStack, String key) {

		net.minecraft.server.v1_11_R1.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();
		String typeAsString = compound.getString(key);

		return EntityType.valueOf(typeAsString);
	}

	@Override
	public boolean has(ItemStack itemStack, String key) {

		net.minecraft.server.v1_11_R1.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();

		return compound.hasKey(key);
	}

	@Override
	public ItemStack set(ItemStack itemStack, String key, boolean value) {

		net.minecraft.server.v1_11_R1.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();
		compound.setBoolean(key, value);
		itemStackNMS.setTag(compound);

		return CraftItemStack.asBukkitCopy(itemStackNMS);
	}

	@Override
	public ItemStack set(ItemStack itemStack, String key, int value) {
		net.minecraft.server.v1_11_R1.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();
		compound.setInt(key, value);
		itemStackNMS.setTag(compound);
		return CraftItemStack.asBukkitCopy(itemStackNMS);
	}

	@Override
	public int getInteger(ItemStack itemStack, String key) {
		net.minecraft.server.v1_11_R1.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();
		return compound.getInt(key);
	}
	
	@Override
	public ItemStack fromSpawner(Spawner spawner) {
		EntityType finalType = spawner.getType();

		ItemBuilder builder = new ItemBuilder(getMaterial(52), 1,
				Config.itemName.replace("%type%", name(finalType.name())));
		List<String> lore = Config.itemLoreSpawner.stream().map(str -> str.replace("%type%", name(finalType.name())))
				.collect(Collectors.toList());
		builder.setLore(lore);

		ItemStack itemStack = this.set(builder.build(), KEY_TYPE, finalType);
		itemStack = this.set(itemStack, KEY_ADD, true);
		return itemStack;
	}

	@Override
	public Spawner toSpawner(ItemStack itemStack) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSpawner(Spawner spawner) {
		
		Location location = spawner.getLocation();
		SimpleLevel level = spawner.getLevel();
		
		if (!location.getBlock().getType().equals(getMaterial(52)))
			return;
		
		if (level == null)
			return;
		
		CreatureSpawner creatureSpawner = (CreatureSpawner) location.getBlock().getState();
		
		
		CraftCreatureSpawner craftCreatureSpawner = (CraftCreatureSpawner) creatureSpawner;
		MobSpawnerAbstract mobSpawnerAbstract = craftCreatureSpawner.getTileEntity().getSpawner();
		NBTTagCompound nbt = new NBTTagCompound();
		mobSpawnerAbstract.b(nbt);
		
		if (level.getMinDelay() != 0)
			nbt.setShort("MinSpawnDelay", (short) level.getMinDelay());
		if (level.getMaxDelay() != 0)
			nbt.setShort("MaxSpawnDelay", (short) level.getMaxDelay());
		if (level.getRequiredPlayerRange() != 0)
			nbt.setShort("RequiredPlayerRange", (short) level.getRequiredPlayerRange());
		if (level.getSpawnRange() != 0)
			nbt.setShort("SpawnRange", (short) level.getSpawnRange());
		if (level.getMaxNearbyEntities() != 0)
			nbt.setShort("MaxNearbyEntities", (short) level.getMaxNearbyEntities());
		if (level.getSpawnCount() != 0)
			nbt.setShort("SpawnCount", (short) level.getSpawnCount());
		
		mobSpawnerAbstract.a(nbt);
	}
	
}
