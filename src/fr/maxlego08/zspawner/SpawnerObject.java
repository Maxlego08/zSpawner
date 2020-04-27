package fr.maxlego08.zspawner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.event.SpawnerDeleteEvent;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.utils.ItemDecoder;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;

public class SpawnerObject extends ZUtils implements Spawner {

	private final UUID uuid;
	private final EntityType type;
	private UUID owner;
	private Location location;

	public SpawnerObject(UUID owner, EntityType type) {
		super();
		this.uuid = UUID.randomUUID();
		this.type = type;
		this.owner = owner;
	}

	public SpawnerObject(UUID uuid, EntityType type, UUID owner, Location location) {
		super();
		this.uuid = uuid;
		this.type = type;
		this.owner = owner;
		this.location = location;
	}

	@Override
	public UUID getOwner() {
		return owner;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public EntityType getType() {
		return type;
	}

	@Override
	public ItemStack getItemStack() {
		@SuppressWarnings("deprecation")
		ItemStack itemStack = new ItemStack(getMaterial(383), 1, type.getTypeId());
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = new ArrayList<>();
		Config.infos.forEach(string -> {
			lore.add(string.replace("%location%", location == null ? "non placé" : toLocation()).replace("%type%", type.name()));
		});
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public String toLocation() {
		return name(location.getWorld().getName()) + " - " + location.getBlockX() + ", " + location.getBlockY() + ", "
				+ location.getBlockZ();
	}

	@Override
	public boolean isPlace() {
		return location != null;
	}

	@Override
	public boolean sameChunk(int x, int z) {
		return location != null && location.getChunk().getX() == x && location.getChunk().getZ() == z;
	}

	@Override
	public void delete() {
		if (location != null) {

			SpawnerDeleteEvent event = new SpawnerDeleteEvent(this);
			event.callEvent();

			if (event.isCancelled())
				return;

			location.getBlock().setType(Material.AIR);
			location = null;
		}
	}

	@Override
	public void place(Location location) {
		
		location.getBlock().setType(getMaterial(52));
		CreatureSpawner creatureSpawner = (CreatureSpawner) location.getBlock().getState();
		creatureSpawner.setSpawnedType(type);
		if (ItemDecoder.getNMSVersion() != 1.8 && ItemDecoder.getNMSVersion() != 1.7)
			creatureSpawner.update();
		this.location = location;
	}

	public int comparePlace(){
		return isPlace() ? 1 : 0;
	}
	
	public int compareNotPlace(){
		return isPlace() ? 0 : 1;
	}
	
}
