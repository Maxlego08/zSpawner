package fr.maxlego08.spawner.storage;

import fr.maxlego08.spawner.api.Spawner;
import fr.maxlego08.spawner.api.SpawnerType;
import fr.maxlego08.spawner.api.storage.ServerProfile;
import fr.maxlego08.spawner.api.storage.StorageManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZServerProfile implements ServerProfile {
    private final StorageManager storageManager;
    private final Map<SpawnerType, Map<UUID, Spawner>> spawners = new ConcurrentHashMap<>();

    public ZServerProfile(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    public Optional<Spawner> getSpawner(UUID uuid, SpawnerType spawnerType) {
        if (this.spawners.containsKey(spawnerType)) {
            Map<UUID, Spawner> typeMap = this.spawners.get(spawnerType);
            if (typeMap != null && typeMap.containsKey(uuid)) {
                return Optional.of(typeMap.get(uuid));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Spawner> getSpawner(UUID uuid) {
        for (Map<UUID, Spawner> typeMap : this.spawners.values()) {
            if (typeMap.containsKey(uuid)) {
                return Optional.of(typeMap.get(uuid));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Spawner> getSpawner(SpawnerType spawnerType, Location location) {
        Collection<Spawner> spawnerCollection = this.getSpawners(spawnerType);
        for  (Spawner spawner : spawnerCollection) {
            if (spawner.isPlace() && spawner.getCuboid().contains(location)) {
                return Optional.of(spawner);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Spawner> getSpawner(Location location) {
        Collection<Spawner> spawnerCollection = this.getSpawners();
        for  (Spawner spawner : spawnerCollection) {
            if (spawner.isPlace() && spawner.getCuboid().contains(location)) {
                return Optional.of(spawner);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Spawner> getSpawnerByEntity(LivingEntity entity) {
        for (Spawner spawner : this.getSpawners()) {
            if (spawner.getLivingEntity() != null && spawner.getLivingEntity() == entity) {
                return Optional.of(spawner);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Spawner> getSpawnerByDeadEntity(Entity entity) {
        for (Spawner spawner : this.getSpawners()) {
            if (spawner.getDeadEntities().contains(entity)) {
                return Optional.of(spawner);
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<Spawner> getSpawners() {
        List<Spawner> allSpawners = new ArrayList<>();
        for (Map<UUID, Spawner> typeMap : this.spawners.values()) {
            allSpawners.addAll(typeMap.values());
        }
        return Collections.unmodifiableList(allSpawners);
    }

    @Override
    public Collection<Spawner> getSpawners(SpawnerType spawnerType) {
        if (this.spawners.containsKey(spawnerType)) {
            Map<UUID, Spawner> typeMap = this.spawners.get(spawnerType);
            if (typeMap != null) {
                return Collections.unmodifiableCollection(typeMap.values());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<Spawner> getSpawners(UUID ownerUUID) {
        List<Spawner> ownerSpawners = new ArrayList<>();
        for (Map<UUID, Spawner> typeMap : this.spawners.values()) {
            for (Spawner spawner : typeMap.values()) {
                if (spawner.getOwner().equals(ownerUUID)) {
                    ownerSpawners.add(spawner);
                }
            }
        }
        return Collections.unmodifiableList(ownerSpawners);
    }

    @Override
    public Collection<Spawner> getSpawners(UUID ownerUUID, SpawnerType spawnerType) {
        List<Spawner> ownerSpawners = new ArrayList<>();
        if (this.spawners.containsKey(spawnerType)) {
            Map<UUID, Spawner> typeMap = this.spawners.get(spawnerType);
            if (typeMap != null) {
                for (Spawner spawner : typeMap.values()) {
                    if (spawner.getOwner().equals(ownerUUID)) {
                        ownerSpawners.add(spawner);
                    }
                }
            }
        }
        return Collections.unmodifiableList(ownerSpawners);
    }

    @Override
    public long getSpawnersInChunkCount(int x, int z) {
        int count = 0;
        for (Spawner spawner : this.getSpawners()) {
            if (spawner.sameChunk(x, z)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public long getSpawnersInChunkCount(int x, int z, EntityType entityType) {
        int count = 0;
        for (Spawner spawner : this.getSpawners()) {
            if (spawner.sameChunk(x, z) && spawner.getEntityType().equals(entityType)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void addSpawner(Spawner spawner) {
        this.spawners.computeIfAbsent(spawner.getType(), k -> new ConcurrentHashMap<>()).put(spawner.getSpawnerId(), spawner);
        this.storageManager.upsertSpawner(spawner);
    }

    @Override
    public void deleteSpawner(Spawner spawner) {
        if (this.spawners.containsKey(spawner.getType())) {
            Map<UUID, Spawner> typeMap = this.spawners.get(spawner.getType());
            if (typeMap != null) {
                typeMap.remove(spawner.getSpawnerId());
            }
        }
        this.storageManager.deleteSpawner(spawner);
    }

    @Override
    public void deleteSpawner(Location location) {
        Optional<Spawner> spawnerOpt = this.getSpawner(location);
        spawnerOpt.ifPresent(this::deleteSpawner);
    }

    @Override
    public void loadSpawner(Spawner spawner) {
        this.spawners.computeIfAbsent(spawner.getType(), k -> new ConcurrentHashMap<>()).put(spawner.getSpawnerId(), spawner);
    }
}
