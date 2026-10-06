package fr.maxlego08.spawner.storage;

import fr.maxlego08.spawner.SpawnerPlugin;
import fr.maxlego08.spawner.ZSpawner;
import fr.maxlego08.spawner.ZSpawnerItem;
import fr.maxlego08.spawner.ZSpawnerOption;
import fr.maxlego08.spawner.api.SpawnerItem;
import fr.maxlego08.spawner.api.SpawnerLocationHistory;
import fr.maxlego08.spawner.api.SpawnerOption;
import fr.maxlego08.spawner.api.SpawnerType;
import fr.maxlego08.spawner.api.dto.SpawnerDTO;
import fr.maxlego08.spawner.api.storage.ServerDataManager;
import fr.maxlego08.spawner.api.storage.ServerProfile;
import fr.maxlego08.spawner.api.storage.StorageManager;
import fr.maxlego08.spawner.zcore.utils.ZUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZServerDataManager extends ZUtils implements ServerDataManager, Listener {
    private final SpawnerPlugin plugin;
    private ServerProfile profile;
    // Spawners dont le monde n'est pas encore chargé (Multiverse, etc.), indexés par nom de monde en minuscule
    private final Map<String, List<PendingSpawner>> pendingByWorld = new ConcurrentHashMap<>();
    private boolean worldListenerRegistered;

    public ZServerDataManager(SpawnerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Optional<ServerProfile> getServerProfile() {
        return Optional.ofNullable(this.profile);
    }

    @Override
    public @NotNull ServerProfile getOrCreate() {
        return this.profile == null ? this.profile = new ZServerProfile(this.plugin, this.plugin.getStorageManager()) : this.profile;
    }

    @Override
    public void clearAll() {

    }

    @Override
    public void loadServerData() {
        StorageManager storageManager = this.plugin.getStorageManager();
        var spawners = storageManager.loadSpawners();
        var options = storageManager.loadOptions();
        Map<UUID, SpawnerOption> spawnerOptions = new HashMap<>();
        for (var optionDTO : options) {
            spawnerOptions.put(optionDTO.spawner_id(), new ZSpawnerOption(storageManager,optionDTO.spawner_id(),optionDTO.distance(), optionDTO.experience_multiplier(), optionDTO.loot_multiplier(), optionDTO.auto_kill(), optionDTO.auto_sell(), optionDTO.max_entity(), optionDTO.min_delay(), optionDTO.max_delay(), optionDTO.min_spawn(), optionDTO.max_spawn(), optionDTO.mob_per_minute(), optionDTO.drop_loots(), optionDTO.location_enabled(), optionDTO.remaining(), optionDTO.min_location_time(), optionDTO.max_location_time(), optionDTO.location_price()));
        }
        var items = storageManager.loadItems();
        Map<UUID, List<SpawnerItem>> itemsBySpawnerId = new HashMap<>();
        for (var item : items) {
            itemsBySpawnerId.computeIfAbsent(item.spawner_id(), k -> new ArrayList<>()).add(new ZSpawnerItem(item.unique_id(),item.item_stack(),item.amount(), storageManager, item.spawner_id()));
        }
        var locationHistories = storageManager.loadLocationHistories();
        Map<UUID, List<SpawnerLocationHistory>> locationHistoriesBySpawnerId = new HashMap<>();
        for (var historyDTO : locationHistories) {
            locationHistoriesBySpawnerId.computeIfAbsent(historyDTO.spawner_id(), k -> new ArrayList<>()).add(new ZSpawnerLocationHistory(storageManager,historyDTO.spawner_id(), historyDTO.timestamp(), historyDTO.duration(), historyDTO.player_id(), historyDTO.price()));
        }
        ServerProfile serverProfile = this.getOrCreate();
        for (SpawnerDTO spawnerDTO : spawners) {
            var pending = new PendingSpawner(spawnerDTO, spawnerOptions.get(spawnerDTO.spawner_id()), itemsBySpawnerId.getOrDefault(spawnerDTO.spawner_id(), Collections.emptyList()), locationHistoriesBySpawnerId.get(spawnerDTO.spawner_id()));

            String location = spawnerDTO.location();
            if (location != null) {
                String worldName = location.split(",")[0];
                // Le monde n'est pas encore chargé : on attend son WorldLoadEvent, sinon la location aurait un monde null
                if (Bukkit.getWorld(worldName) == null) {
                    this.pendingByWorld.computeIfAbsent(worldName.toLowerCase(Locale.ROOT), k -> Collections.synchronizedList(new ArrayList<>())).add(pending);
                    continue;
                }
            }

            serverProfile.loadSpawner(createSpawner(pending));
        }

        if (!this.pendingByWorld.isEmpty()) {
            this.pendingByWorld.forEach((world, list) -> this.plugin.getLogger().info(list.size() + " spawner(s) in world '" + world + "' are waiting for that world to load."));
            if (!this.worldListenerRegistered) {
                this.worldListenerRegistered = true;
                Bukkit.getPluginManager().registerEvents(this, this.plugin);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        List<PendingSpawner> pending = this.pendingByWorld.remove(event.getWorld().getName().toLowerCase(Locale.ROOT));
        if (pending == null) return;

        ServerProfile serverProfile = this.getOrCreate();
        synchronized (pending) {
            for (PendingSpawner pendingSpawner : pending) {
                ZSpawner spawner = createSpawner(pendingSpawner);
                serverProfile.loadSpawner(spawner);
                // Les chunks déjà chargés n'auront pas de ChunkLoadEvent, il faut charger le spawner manuellement
                if (spawner.getType() != SpawnerType.VIRTUAL && spawner.isPlace() && spawner.isChunkLoaded()) {
                    this.plugin.getFoliaManager().runAtLocation(spawner.getLocation(), spawner::load);
                }
            }
        }

        this.plugin.getLogger().info("Loaded " + pending.size() + " spawner(s) for world '" + event.getWorld().getName() + "'.");
    }

    private ZSpawner createSpawner(PendingSpawner pending) {
        SpawnerDTO spawnerDTO = pending.dto();
        Location location = spawnerDTO.location() == null ? null : changeStringLocationToLocation(spawnerDTO.location());
        ZSpawner spawner = new ZSpawner(this.plugin, spawnerDTO.spawner_id(), spawnerDTO.owner(), spawnerDTO.type(), spawnerDTO.entity_type(), spawnerDTO.placed_at(), location, spawnerDTO.amount(), spawnerDTO.block_face(), spawnerDTO.last_location_user(), spawnerDTO.last_location_time());
        spawner.setLastLocationStartTime(spawnerDTO.last_location_start_time());
        spawner.setItems(pending.items());
        if (pending.option() != null) {
            spawner.setOption(pending.option());
        }
        var histories = pending.histories();
        if (histories != null) {
            histories.sort(Comparator.comparingLong(SpawnerLocationHistory::getStartTime)); // Most recent at the end
            spawner.setLocationHistory(histories);
        }
        return spawner;
    }

    private record PendingSpawner(SpawnerDTO dto, SpawnerOption option, List<SpawnerItem> items, List<SpawnerLocationHistory> histories) {
    }
}
