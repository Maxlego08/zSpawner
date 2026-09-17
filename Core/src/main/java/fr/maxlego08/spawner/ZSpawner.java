package fr.maxlego08.spawner;

import fr.maxlego08.spawner.api.*;
import fr.maxlego08.spawner.api.storage.StorageManager;
import fr.maxlego08.spawner.api.utils.Cuboid;
import fr.maxlego08.spawner.save.Config;
import fr.maxlego08.spawner.stackable.StackableManager;
import fr.maxlego08.spawner.storage.Updatable;
import fr.maxlego08.spawner.zcore.logger.Logger;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ZSpawner extends Updatable implements Spawner {

    private final SpawnerPlugin plugin;
    private final StorageManager storageManager;
    private final UUID uniqueId;
    private final UUID ownerId;
    private final SpawnerType spawnerType;
    private final EntityType entityType;
    private final BlockFace blockFace;
    private final List<Entity> deadEntities = new ArrayList<>();
    private SpawnerOption spawnerOption;
    private long placedAt;
    private Location location;
    private int amount = 0;
    private ArmorStand stackArmorstand;
    private LivingEntity livingEntity;
    private long lastSpawnAt;
    private Cuboid cuboid;
    private List<SpawnerItem> items = new ArrayList<>();
    private long lastEntityKill;
    private UUID lastLocationUser;
    private long lastLocationTime;
    private long lastLocationStartTime;
    private List<SpawnerLocationHistory> locationHistories = new ArrayList<>();

    public ZSpawner(SpawnerPlugin plugin, UUID uniqueId, UUID ownerId, SpawnerType spawnerType, EntityType entityType, long placedAt, Location location, int amount, BlockFace blockFace, UUID lastLocationUser, long lastLocationTime) {
        this.plugin = plugin;
        this.uniqueId = uniqueId;
        this.ownerId = ownerId;
        this.spawnerType = spawnerType;
        this.entityType = entityType;
        this.placedAt = placedAt;
        this.location = location;
        this.amount = amount;
        this.blockFace = blockFace;
        this.lastLocationUser = lastLocationUser;
        this.lastLocationTime = lastLocationTime;
        this.spawnerOption = this.plugin.getManager().getDefaultOption().cloneOption();
        this.storageManager = plugin.getStorageManager();
    }

    public ZSpawner(SpawnerPlugin plugin, UUID spawnerId, UUID ownerId, SpawnerType spawnerType, EntityType entityType, BlockFace blockFace) {
        this(plugin, spawnerId, ownerId, spawnerType, entityType, 0, null, 0, blockFace, null, 0);
    }

    public ZSpawner(SpawnerPlugin plugin, UUID ownerId, SpawnerType spawnerType, EntityType entityType, BlockFace blockFace) {
        this(plugin, UUID.randomUUID(), ownerId, spawnerType, entityType, blockFace);
    }

    @Override
    public @NotNull UUID getOwner() {
        return this.ownerId;
    }

    @Override
    public UUID getSpawnerId() {
        return this.uniqueId;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public SpawnerType getType() {
        return this.spawnerType;
    }

    @Override
    public long getPlacedAt() {
        return this.placedAt;
    }

    @Override
    public SpawnerOption getOption() {
        return this.spawnerOption;
    }

    @Override
    public void setOption(SpawnerOption spawnerOption) {
        this.spawnerOption = spawnerOption;
    }

    @Override
    public void addLocationHistory(SpawnerLocationHistory spawnerLocationHistory) {
        this.locationHistories.add(spawnerLocationHistory);
        this.canUpdate();
    }

    @Override
    public void setLocationHistory(List<SpawnerLocationHistory> spawnerLocationHistory) {
        this.locationHistories = spawnerLocationHistory;
    }

    @Override
    public List<SpawnerLocationHistory> getLocationHistory() {
        return this.locationHistories;
    }

    @Override
    public EntityType getEntityType() {
        return this.entityType;
    }

    @Override
    public boolean isPlace() {
        return this.location != null;
    }

    @Override
    public boolean sameChunk(int x, int z) {
        if (!this.isPlace()) return false;
        int chunkX = this.location.getBlockX() >> 4;
        int chunkZ = this.location.getBlockZ() >> 4;
        return x == chunkX && z == chunkZ;
    }

    @Override
    public void place(Location location) {

        this.placedAt = System.currentTimeMillis();
        this.location = location;
        Block block = location.getBlock();

        if (this.spawnerType == SpawnerType.VIRTUAL) {

            block.setType(Config.virtualMaterial, true);
            spawnEntity();

            if (Config.breakUpVirtualSpawner) {

                Location maxLocation = this.location.clone().add(0, this.livingEntity == null ? 2 : Math.ceil(this.livingEntity.getHeight()), 0);
                Cuboid cuboid = new Cuboid(this.location.clone().add(0, 1, 0), maxLocation);

                cuboid.forEach(cuboidBlock -> {
                    if (cuboidBlock.getType() != Material.BEDROCK && cuboidBlock.getType() != Config.virtualMaterial) {
                        cuboidBlock.breakNaturally(true, true);
                    }
                });
            }

        } else {

            block.setType(Material.SPAWNER, true);

            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            spawner.setSpawnedType(this.entityType);
            spawner.update(true);
        }

        this.canUpdate();
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
        this.canUpdate();

        if (amount <= 1) removeHologram();
        else this.spawnHologram();
    }

    @Override
    public void updateSpawner() {

        if (!isPlace()) return;

        Block block = this.location.getBlock();

        if (this.spawnerType == SpawnerType.VIRTUAL) {

            this.spawnEntity();
        } else {

            CreatureSpawner spawner = (CreatureSpawner) block.getState();

            StackableManager stackableManager = this.plugin.getStackableManager();
            if (stackableManager.isEnable()) {
                stackableManager.updateSpawner(spawner, this.amount);
                this.spawnHologram();
            }

            spawner.update(true);
        }
    }

    @Override
    public void load() {

        if (!isPlace()) return;

        Block block = this.location.getBlock();

        if (this.spawnerType == SpawnerType.VIRTUAL) {

            spawnEntity();
            if (block.getType() != Config.virtualMaterial) {
                block.setType(Config.virtualMaterial);
            }
        } else {
            spawnHologram();
            if (block.getType() != Material.SPAWNER) {
                block.setType(Material.SPAWNER, true);
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                spawner.setSpawnedType(this.entityType);
                spawner.update(true);
                this.updateSpawner();
            }
        }
    }

    @Override
    public Location getSpawnedEntityLocation() {
        Location location = this.location.clone().add(0.5, 1, 0.5);
        if (this.blockFace.equals(BlockFace.SOUTH)) location.setYaw(180.f);
        if (this.blockFace.equals(BlockFace.WEST)) location.setYaw(-90.f);
        if (this.blockFace.equals(BlockFace.EAST)) location.setYaw(90.f);
        return location;
    }

    @Override
    public @Nullable UUID getLastLocationUser() {
        return this.lastLocationUser;
    }

    @Override
    public void setLastLocationUser(@Nullable UUID uuid) {
        this.lastLocationUser = uuid;
        this.canUpdate();
    }

    @Override
    public long getLastLocationTime() {
        return this.lastLocationTime;
    }

    @Override
    public void setLastLocationTime(long time) {
        this.lastLocationTime = time;
        this.canUpdate();
    }

    @Override
    public long getLastLocationStartTime() {
        return this.lastLocationStartTime;
    }

    @Override
    public void setLastLocationStartTime(long time) {
        this.lastLocationStartTime = time;
        this.canUpdate();
    }

    private void spawnEntity() {

        if (this.livingEntity != null) {

            if (this.livingEntity.isValid()) {
                this.updateEntity();
                return;
            }

            this.livingEntity.remove();
        }

        Location location = getSpawnedEntityLocation();

        World world = location.getWorld();
        world.getNearbyEntities(location, 0.5, 0.5, 0.5).forEach(entity -> {
            if (entity.getType() != this.entityType) return;
            if (!entity.getPersistentDataContainer().has(this.plugin.getSpawnerKey(), PersistentDataType.STRING))
                return;
            String spawnerId = entity.getPersistentDataContainer().get(this.plugin.getSpawnerKey(), PersistentDataType.STRING);
            if (spawnerId == null || !spawnerId.equals(this.uniqueId.toString())) return;
            entity.remove();
        });

        Class<? extends Entity> entityClass = this.entityType.getEntityClass();
        if (entityClass == null) {
            Logger.info("Error with entity class for " + this.entityType, Logger.LogType.ERROR);
            return;
        }
        this.livingEntity = (LivingEntity) world.spawn(location, entityClass, e -> {
            e.getPersistentDataContainer().set(this.plugin.getSpawnerKey(), PersistentDataType.STRING, this.uniqueId.toString());
            if (e instanceof LivingEntity currentLiving) {
                currentLiving.setAI(false);
                currentLiving.setCollidable(false);
                currentLiving.setCustomNameVisible(true);
                currentLiving.setVisualFire(false);
                currentLiving.setSwimming(false);
                currentLiving.setSilent(true);
                currentLiving.setCanPickupItems(false);
                if (currentLiving.isInsideVehicle()) {
                    var vehicle = currentLiving.getVehicle();
                    if (vehicle != null) {
                        vehicle.remove();
                    }
                }
            }
        });

        if (this.livingEntity instanceof Ageable ageable) {
            ageable.setAdult();
        }

        if (this.livingEntity instanceof Slime slime) {
            slime.setSize(1);
        }

        if (this.livingEntity instanceof ZombieVillager) {
            this.livingEntity.remove();
            this.livingEntity = null;
            this.spawnEntity();
            return;
        }

        this.updateEntity();
    }

    private Location getHologramLocation() {
        return this.location.clone().add(0.5, 1.0, 0.5);
    }

    /**
     * Récupère les armor stands qui servent réellement d'hologramme à ce spawner.
     * La référence {@link #stackArmorstand} est uniquement en mémoire : après un redémarrage
     * ou un rechargement de chunk elle est nulle ou périmée alors que l'entité, elle, existe
     * toujours dans le monde. Sans cette relecture on crée un doublon et on n'arrive plus
     * jamais à supprimer l'ancien hologramme.
     */
    private List<ArmorStand> getHologramCandidates() {

        // Un spawner VIRTUAL n'a pas d'hologramme et son mob occupe exactement cette position.
        if (!isPlace() || this.spawnerType == SpawnerType.VIRTUAL) return Collections.emptyList();

        World world = this.location.getWorld();
        if (world == null) return Collections.emptyList();
        if (!world.isChunkLoaded(this.location.getBlockX() >> 4, this.location.getBlockZ() >> 4))
            return Collections.emptyList();
        // Sur Folia la lecture des entités n'est possible que depuis le thread de la région.
        if (!this.plugin.getFoliaManager().isOwnedByCurrentRegion(this.location)) return Collections.emptyList();

        List<ArmorStand> armorStands = new ArrayList<>();
        for (Entity entity : world.getNearbyEntities(getHologramLocation(), 0.5, 0.5, 0.5)) {

            if (!(entity instanceof ArmorStand armorStand)) continue;

            String spawnerId = armorStand.getPersistentDataContainer().get(this.plugin.getSpawnerKey(), PersistentDataType.STRING);
            if (spawnerId != null) {
                if (spawnerId.equals(this.uniqueId.toString())) armorStands.add(armorStand);
                continue;
            }

            // Hologrammes créés avant l'ajout du tag : la signature exacte de spawnHologram()
            // est le seul moyen de les reconnaitre pour nettoyer les serveurs existants.
            if (armorStand.isMarker() && armorStand.isInvisible() && armorStand.isCustomNameVisible()) {
                armorStands.add(armorStand);
            }
        }
        return armorStands;
    }

    private void spawnHologram() {

        StackableManager stackableManager = this.plugin.getStackableManager();
        if (!stackableManager.isEnable() || this.amount <= 1 || !isPlace()) return;

        if (this.stackArmorstand != null && this.stackArmorstand.isValid()) {
            this.updateHologram();
            return;
        }

        this.stackArmorstand = null;
        for (ArmorStand armorStand : getHologramCandidates()) {
            if (this.stackArmorstand == null) this.stackArmorstand = armorStand;
            else armorStand.remove(); // doublons laissés par les versions précédentes
        }

        if (this.stackArmorstand == null) {
            Location spawnLocation = getHologramLocation();
            this.stackArmorstand = spawnLocation.getWorld().spawn(spawnLocation, ArmorStand.class, armorStand -> {
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setMarker(true);
                armorStand.setCustomNameVisible(true);
                armorStand.setCustomName("");
                armorStand.getPersistentDataContainer().set(this.plugin.getSpawnerKey(), PersistentDataType.STRING, this.uniqueId.toString());
            });
        } else {
            // On retague l'hologramme récupéré pour ne plus dépendre de la signature.
            this.stackArmorstand.getPersistentDataContainer().set(this.plugin.getSpawnerKey(), PersistentDataType.STRING, this.uniqueId.toString());
        }

        this.updateHologram();
    }

    private void removeHologram() {

        ArmorStand armorStand = this.stackArmorstand;
        this.stackArmorstand = null;

        if (armorStand != null && armorStand.isValid()) {
            this.plugin.getFoliaManager().runAtEntity(armorStand, armorStand::remove);
        }

        // La référence en mémoire peut être périmée : on supprime aussi ce qui traine
        // réellement au dessus du spawner, sinon l'hologramme reste après la casse.
        for (ArmorStand orphan : getHologramCandidates()) {
            this.plugin.getFoliaManager().runAtEntity(orphan, orphan::remove);
        }
    }

    private void updateHologram() {
        StackableManager stackableManager = this.plugin.getStackableManager();
        if (this.stackArmorstand == null || !stackableManager.isEnable()) return;

        String hologram = stackableManager.getHologram();
        if (hologram == null) return;

        this.stackArmorstand.setCustomName(color(getMessage(hologram, "%amount%", this.amount, "%entity%", name(this.entityType.name()))));
    }

    private void updateEntity() {
        if (this.livingEntity != null) {
            this.livingEntity.setCustomName(color(getMessage(Config.virtualName, "%amount%", this.amount)));
        }
    }

    @Override
    public void disable() {
        removeHologram();
        if (this.livingEntity != null) {
            LivingEntity entity = this.livingEntity;
            this.livingEntity = null;
            this.plugin.getFoliaManager().runAtEntity(entity, entity::remove);
        }
    }

    @Override
    public void breakBlock() {
        if (!this.isPlace()) return;

        this.location.getBlock().setType(Material.AIR);

        // Avant de perdre la position : elle est nécessaire pour retrouver l'hologramme.
        removeHologram();
        if (this.livingEntity != null) {
            LivingEntity entity = this.livingEntity;
            this.livingEntity = null;
            this.plugin.getFoliaManager().runAtEntity(entity, entity::remove);
        }

        this.location = null;
        this.cuboid = null;
        this.placedAt = 0;

        this.canUpdate();
    }

    @Override
    public int comparePlace() {
        return isPlace() ? 1 : 0;
    }

    @Override
    public int compareNotPlace() {
        return isPlace() ? 0 : 1;
    }

    @Override
    public LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    @Override
    public List<Entity> getDeadEntities() {
        return this.deadEntities;
    }

    @Override
    public void entityDeath() {

        this.amount -= 1;
        this.canUpdate();
        this.updateEntity();

        if (this.livingEntity != null && this.livingEntity.isValid() && ((this.livingEntity.getLocation().getBlockX() != this.location.getBlockX() || this.livingEntity.getLocation().getBlockZ() != this.location.getBlockZ()))) {
            this.livingEntity.teleport(getSpawnedEntityLocation());
        }
    }

    @Override
    public void addItems(List<ItemStack> itemStacks) {

        itemStacks.forEach(itemStack -> {

            if (Config.blacklistMaterials.contains(itemStack.getType())) return;

            Optional<SpawnerItem> optional = getSpawnerItem(itemStack);
            if (optional.isPresent()) {
                SpawnerItem spawnerItem = optional.get();
                spawnerItem.addAmount(itemStack.getAmount());
            } else {
                SpawnerItem spawnerItem = new ZSpawnerItem(itemStack, itemStack.getAmount(), this.storageManager, this.uniqueId);
                this.items.add(spawnerItem);
            }
        });

        this.canUpdate();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.plugin.getInventoryManager().updateInventory(onlinePlayer, plugin);
        }
    }

    @Override
    public boolean isChunkLoaded() {
        if (this.location == null) return false;
        World world = this.location.getWorld();
        if (world == null) return false;
        return world.isChunkLoaded(this.location.getChunk());
    }

    @Override
    public double getDistance() {
        return spawnerOption.getDistance();
    }

    private boolean isInValid() {
        return this.livingEntity == null || !this.livingEntity.isValid() || this.livingEntity.isDead();
    }

    @Override
    public void tick() {

        if (isInValid()) {
            this.livingEntity = null;
            this.spawnEntity();
        }

        // No more entities available
        if (spawnerOption.getRemainingEntity() == 0) return;

        if (System.currentTimeMillis() > this.lastSpawnAt && this.amount < spawnerOption.getMaxEntity()) {

            long ms = ThreadLocalRandom.current().nextLong(Math.min(spawnerOption.getMinDelay(), spawnerOption.getMaxDelay()), Math.max(spawnerOption.getMinDelay(), spawnerOption.getMaxDelay()));
            this.lastSpawnAt = System.currentTimeMillis() + ms;

            int addedEntities = getNumberBetween(Math.min(spawnerOption.getMinSpawn(), spawnerOption.getMaxSpawn()), Math.max(spawnerOption.getMinSpawn(), spawnerOption.getMaxSpawn()));

            addedEntities = Math.min(addedEntities, spawnerOption.getRemainingEntity());

            this.amount += addedEntities;
            spawnerOption.removeRemainingEntity(addedEntities);

            if (this.amount > spawnerOption.getMaxEntity()) this.amount = spawnerOption.getMaxEntity();
            this.canUpdate();
            this.updateEntity();
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return this.blockFace;
    }

    @Override
    public Cuboid getCuboid() {
        if (this.cuboid != null) return this.cuboid;
        Location maxLocation = this.spawnerType == SpawnerType.VIRTUAL ? this.location.clone().add(0, this.livingEntity == null ? 2 : Math.ceil(this.livingEntity.getHeight()), 0) : this.location.clone();
        return this.cuboid = new Cuboid(this.location.clone(), maxLocation);
    }

    @Override
    public List<SpawnerItem> getItems() {
        return this.items;
    }

    @Override
    public void setItems(List<SpawnerItem> items) {
        this.items = items;
    }

    @Override
    public Optional<SpawnerItem> getSpawnerItem(ItemStack itemStack) {
        return this.items.stream().filter(spawnerItem -> spawnerItem.isSimilar(itemStack)).findFirst();
    }

    @Override
    public void removeItem(SpawnerItem spawnerItem) {
        this.items.remove(spawnerItem);
        this.plugin.getStorageManager().deleteItem(spawnerItem, this.uniqueId);
    }

    @Override
    public String getSpawnerKey() {
        return this.uniqueId.toString().substring(0, 6) + "_" + entityType.name().toLowerCase();
    }

    @Override
    public void autoKill() {
        int entityPerMinute = spawnerOption.getMobPerMinute();
        double entityPerSecond = (double) entityPerMinute / 60d;

        if (entityPerSecond < 1) {

            double secondWait = 1d / entityPerSecond;

            if (System.currentTimeMillis() >= this.lastEntityKill) {

                this.lastEntityKill = System.currentTimeMillis() + (1000L * (int) secondWait);
                this.killEntity(1);
            }
        } else this.killEntity((int) entityPerSecond);
    }

    private void killEntity(int count) {

        if (this.amount <= 0 || count <= 0 || this.isInValid()) {
            return;
        }

        World world = this.location.getWorld();
        LivingEntity clonedEntity = (LivingEntity) world.spawn(getSpawnedEntityLocation(), Objects.requireNonNull(this.livingEntity.getType().getEntityClass()));
        clonedEntity.setAI(false);

        if (clonedEntity instanceof Slime slime) {
            slime.setSize(1);
        }

        this.getDeadEntities().add(clonedEntity);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.ownerId);
        if (offlinePlayer.isOnline()) {
            clonedEntity.setKiller(offlinePlayer.getPlayer());
        }

        clonedEntity.damage(livingEntity.getHealth() * 2);

        this.entityDeath();
        this.killEntity(count - 1);
    }

    @Override
    public void save() {
        this.storageManager.upsertSpawner(this);
    }
}
