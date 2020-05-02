package fr.maxlego08.zspawner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.zspawner.api.Board;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.api.event.SpawnerDeleteEvent;
import fr.maxlego08.zspawner.save.Config;
import fr.maxlego08.zspawner.zcore.utils.ItemDecoder;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import fr.maxlego08.zspawner.zcore.utils.builder.ItemBuilder;

public class SpawnerObject extends ZUtils implements Spawner {

	private final UUID uuid;
	private final EntityType type;
	private final long createAt;
	private long placedAt;
	private UUID owner;
	private Location location;

	public SpawnerObject(UUID owner, EntityType type) {
		super();
		this.uuid = UUID.randomUUID();
		this.type = type;
		this.owner = owner;
		this.createAt = System.currentTimeMillis();
	}

	public SpawnerObject(UUID uuid, EntityType type, long createAt, long placedAt, UUID owner, Location location) {
		super();
		this.uuid = uuid;
		this.type = type;
		this.createAt = createAt;
		this.placedAt = placedAt;
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
	@SuppressWarnings("deprecation")
	public ItemStack getItemStack() {

		ItemBuilder builder = new ItemBuilder(getMaterial(383), 1, type.getTypeId());
		if (Config.glowPlaceSpawner && location != null)
			builder.glow();
		Config.infos.forEach(string -> {

			SimpleDateFormat format = new SimpleDateFormat(Config.timeFormat);

			string = string.replace("%location%", location == null ? "non placé" : toLocation());
			string = string.replace("%type%", type.name());
			string = string.replace("%create%", format.format(new Date(createAt)));
			string = string.replace("%placed%", location == null ? "non placé" : format.format(new Date(placedAt)));

			builder.addLine(string);
		});
		return builder.build();
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
	public void delete(Board board) {
		if (location != null) {

			SpawnerDeleteEvent event = new SpawnerDeleteEvent(this);
			event.callEvent();

			if (event.isCancelled())
				return;

			board.removeSpawner(this);
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

		placedAt = System.currentTimeMillis();

		this.location = location;
	}

	public int comparePlace() {
		return isPlace() ? 1 : 0;
	}

	public int compareNotPlace() {
		return isPlace() ? 0 : 1;
	}

	@Override
	public boolean isOwner(Player player) {
		return player == null ? false : player.getUniqueId().equals(owner);
	}

	@Override
	public long placedAt() {
		return placedAt;
	}

	@Override
	public long createAt() {
		return createAt;
	}

}
