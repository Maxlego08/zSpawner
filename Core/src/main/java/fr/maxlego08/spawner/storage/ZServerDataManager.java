package fr.maxlego08.spawner.storage;

import fr.maxlego08.spawner.SpawnerPlugin;
import fr.maxlego08.spawner.ZSpawner;
import fr.maxlego08.spawner.ZSpawnerItem;
import fr.maxlego08.spawner.ZSpawnerOption;
import fr.maxlego08.spawner.api.Spawner;
import fr.maxlego08.spawner.api.SpawnerItem;
import fr.maxlego08.spawner.api.SpawnerLocationHistory;
import fr.maxlego08.spawner.api.SpawnerOption;
import fr.maxlego08.spawner.api.dto.SpawnerDTO;
import fr.maxlego08.spawner.api.storage.ServerDataManager;
import fr.maxlego08.spawner.api.storage.ServerProfile;
import fr.maxlego08.spawner.api.storage.StorageManager;
import fr.maxlego08.spawner.zcore.utils.ZUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ZServerDataManager extends ZUtils implements ServerDataManager {
    private final SpawnerPlugin plugin;
    private ServerProfile profile;

    public ZServerDataManager(SpawnerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Optional<ServerProfile> getServerProfile() {
        return Optional.ofNullable(this.profile);
    }

    @Override
    public @NotNull ServerProfile getOrCreate() {
        return this.profile == null ? this.profile = new ZServerProfile(this.plugin.getStorageManager()) : this.profile;
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
            Spawner spawner = new ZSpawner(this.plugin, spawnerDTO.spawner_id(), spawnerDTO.owner(), spawnerDTO.type(),spawnerDTO.entity_type(), spawnerDTO.placed_at(), changeStringLocationToLocation(spawnerDTO.location()),spawnerDTO.amount(),spawnerDTO.block_face(),spawnerDTO.last_location_user(),spawnerDTO.last_location_time());
            spawner.setLastLocationStartTime(spawnerDTO.last_location_start_time());

            List<SpawnerItem> spawnerItems = itemsBySpawnerId.getOrDefault(spawnerDTO.spawner_id(), Collections.emptyList());
            spawner.setItems(spawnerItems);
            SpawnerOption spawnerOption = spawnerOptions.get(spawnerDTO.spawner_id());
            if (spawnerOption != null) {
                spawner.setOption(spawnerOption);
            }
            var histories = locationHistoriesBySpawnerId.get(spawnerDTO.spawner_id());
            if (histories != null) {
                histories.sort(Comparator.comparingLong(SpawnerLocationHistory::getStartTime)); // Most recent at the end
                spawner.setLocationHistory(histories);
            }
            serverProfile.loadSpawner(spawner);
        }
    }
}
