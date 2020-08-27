package fr.maxlego08.zspawner.nms;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_7_R4.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.api.FakeSpawner;
import fr.maxlego08.zspawner.api.NMS;
import fr.maxlego08.zspawner.api.SimpleLevel;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.manager.LevelManager;
import fr.maxlego08.zspawner.objects.LevelObject;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import fr.maxlego08.zspawner.zcore.utils.builder.ItemBuilder;
import net.minecraft.server.v1_7_R4.MobSpawnerAbstract;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.TileEntityMobSpawner;

public class NMS_1_7 extends ZUtils implements NMS {

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
		
		if (itemStackNMS == null)
			return false;
		NBTTagCompound compound = itemStackNMS.getTag();
		if (compound == null)
			return false;

		return compound.hasKey(key);
	}

	@Override
	public ItemStack set(ItemStack itemStack, String key, boolean value) {
		net.minecraft.server.v1_7_R4.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();
		compound.setBoolean(key, value);
		itemStackNMS.setTag(compound);

		return CraftItemStack.asBukkitCopy(itemStackNMS);
	}

	@Override
	public ItemStack set(ItemStack itemStack, String key, int value) {
		net.minecraft.server.v1_7_R4.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();
		compound.setInt(key, value);
		itemStackNMS.setTag(compound);
		return CraftItemStack.asBukkitCopy(itemStackNMS);
	}

	@Override
	public int getInteger(ItemStack itemStack, String key) {
		net.minecraft.server.v1_7_R4.ItemStack itemStackNMS = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = itemStackNMS.getTag();
		return compound.getInt(key);
	}

	@Override
	public ItemStack fromSpawner(FakeSpawner spawner) {

		EntityType finalType = spawner.getType();

		String name = Config.itemName.replace("%type%", name(finalType.name()));
		name = name.replace("%level%", String.valueOf(spawner.getLevelId()));

		ItemBuilder builder = new ItemBuilder(getSpawner(), 1, name);

		List<String> tmpList = spawner.getLevelId() == 0 ? Config.itemLoreSpawner : Config.itemLoreSpawnerLevel;
		List<String> lore = tmpList.stream().map(str -> {

			str = str.replace("%type%", name(finalType.name()));
			str = str.replace("%level%", String.valueOf(spawner.getLevelId()));
			return str;

		}).collect(Collectors.toList());

		builder.setLore(lore);

		ItemStack itemStack = this.set(builder.build(), KEY_TYPE, finalType);
		itemStack = this.set(itemStack, KEY_ADD, true);
		itemStack = this.set(itemStack, KEY_LEVEL, spawner.getLevelId());

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

		if (!location.getBlock().getType().equals(getSpawner()))
			return;

		if (level == null)
			return;

		CreatureSpawner creatureSpawner = (CreatureSpawner) location.getBlock().getState();
		CraftCreatureSpawner craftCreatureSpawner = (CraftCreatureSpawner) creatureSpawner;

		Field field;
		try {

			field = craftCreatureSpawner.getClass().getDeclaredField("spawner");
			field.setAccessible(true);

			TileEntityMobSpawner entityMobSpawner = (TileEntityMobSpawner) field.get(craftCreatureSpawner);
			MobSpawnerAbstract mobSpawnerAbstract = entityMobSpawner.getSpawner();

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

		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	@Override
	public ItemStack getLevelFromSpawnBlock(LevelManager levelManager, Block block) {

		CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
		CraftCreatureSpawner craftCreatureSpawner = (CraftCreatureSpawner) creatureSpawner;

		Field field;
		try {

			field = craftCreatureSpawner.getClass().getDeclaredField("spawner");
			field.setAccessible(true);

			TileEntityMobSpawner entityMobSpawner = (TileEntityMobSpawner) field.get(craftCreatureSpawner);
			MobSpawnerAbstract mobSpawnerAbstract = entityMobSpawner.getSpawner();
			
			NBTTagCompound nbt = new NBTTagCompound();
			mobSpawnerAbstract.b(nbt);

			int minDelay = nbt.getShort("MinSpawnDelay");
			int maxDelay = nbt.getShort("MaxSpawnDelay");
			int requiredPlayerRange = nbt.getShort("RequiredPlayerRange");
			int spawnRange = nbt.getShort("SpawnRange");
			int spawnCount = nbt.getShort("SpawnCount");
			int maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
			EntityType finalType = creatureSpawner.getSpawnedType();

			SimpleLevel level = levelManager.getLevelFromValue(new LevelObject(0, null, 0, minDelay, maxDelay,
					spawnCount, maxNearbyEntities, spawnRange, requiredPlayerRange, levelManager));

			String name = Config.itemName.replace("%type%", name(finalType.name()));
			if (level != null)
				name = name.replace("%level%", String.valueOf(level.getId()));

			ItemBuilder builder = new ItemBuilder(getSpawner(), 1, name);

			List<String> tmpList = level == null ? Config.itemLoreSpawner : Config.itemLoreSpawnerLevel;
			List<String> lore = tmpList.stream().map(str -> {

				str = str.replace("%type%", name(finalType.name()));
				str = str.replace("%level%", String.valueOf(level != null ? level.getId() : 0));
				return str;

			}).collect(Collectors.toList());

			builder.setLore(lore);

			ItemStack itemStack = this.set(builder.build(), KEY_TYPE, finalType);
			itemStack = this.set(itemStack, KEY_ADD, true);
			if (level != null)
				itemStack = this.set(itemStack, KEY_LEVEL, level.getId());

			return itemStack;
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
