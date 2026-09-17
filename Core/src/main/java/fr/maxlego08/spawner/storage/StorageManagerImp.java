package fr.maxlego08.spawner.storage;

import fr.maxlego08.sarah.*;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.maxlego08.sarah.database.Schema;
import fr.maxlego08.sarah.logger.JULogger;
import fr.maxlego08.sarah.logger.Logger;
import fr.maxlego08.spawner.SpawnerPlugin;
import fr.maxlego08.spawner.ZSpawnerOption;
import fr.maxlego08.spawner.api.Spawner;
import fr.maxlego08.spawner.api.SpawnerItem;
import fr.maxlego08.spawner.api.SpawnerLocationHistory;
import fr.maxlego08.spawner.api.SpawnerOption;
import fr.maxlego08.spawner.api.dto.ItemDTO;
import fr.maxlego08.spawner.api.dto.OptionDTO;
import fr.maxlego08.spawner.api.dto.SpawnerDTO;
import fr.maxlego08.spawner.api.dto.SpawnerLocationHistoryDTO;
import fr.maxlego08.spawner.api.storage.StorageManager;
import fr.maxlego08.spawner.migrations.ItemMigration;
import fr.maxlego08.spawner.migrations.OptionMigration;
import fr.maxlego08.spawner.migrations.SpawnerLocationHistoryMigration;
import fr.maxlego08.spawner.migrations.SpawnerMigration;
import fr.maxlego08.spawner.zcore.enums.Tables;
import fr.maxlego08.spawner.zcore.utils.GlobalDatabaseConfiguration;
import fr.maxlego08.spawner.zcore.utils.ZUtils;
import fr.maxlego08.spawner.zcore.utils.compatibility.FoliaCompatibilityManager;
import fr.maxlego08.spawner.zcore.utils.nms.Base64ItemStack;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StorageManagerImp extends ZUtils implements StorageManager {
    private final SpawnerPlugin plugin;
    private final TypeSafeCache cache = new TypeSafeCache();
    private final Object flushLock = new Object();
    private final FoliaCompatibilityManager foliaManager;
    private RequestHelper requestHelper;
    private boolean isEnable = true;

    public StorageManagerImp(SpawnerPlugin plugin, FoliaCompatibilityManager foliaManager) {
        this.plugin = plugin;
        this.foliaManager = foliaManager;
    }

    @Override
    public void loadDatabase() {
        MigrationManager.setMigrationTableName("zspawner_migrations");
        MigrationManager.registerMigration(new SpawnerMigration());
        MigrationManager.registerMigration(new ItemMigration());
        MigrationManager.registerMigration(new OptionMigration());
        MigrationManager.registerMigration(new SpawnerLocationHistoryMigration());

        File file = new File(this.plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        GlobalDatabaseConfiguration globalDatabaseConfiguration = new GlobalDatabaseConfiguration(config);
        String user = globalDatabaseConfiguration.getUser();
        String password = globalDatabaseConfiguration.getPassword();
        String host = globalDatabaseConfiguration.getHost();
        String dataBase = globalDatabaseConfiguration.getDatabase();
        String prefix = globalDatabaseConfiguration.getTablePrefix();
        int port = globalDatabaseConfiguration.getPort();
        boolean enableDebug = globalDatabaseConfiguration.isDebug();

        String storageType = config.getString("storage-type", "SQLITE");
        Logger logger = JULogger.from(this.plugin.getLogger());

        DatabaseConnection databaseConnection;
        if (storageType.equalsIgnoreCase("SQLITE")) {
            databaseConnection = new SqliteConnection(new DatabaseConfiguration(prefix, user, password, port, host, dataBase, enableDebug, DatabaseType.SQLITE), this.plugin.getDataFolder(), logger);
        } else {
            databaseConnection = new HikariDatabaseConnection(new DatabaseConfiguration(prefix, user, password, port, host, dataBase, enableDebug, storageType.equalsIgnoreCase("MYSQL") ? DatabaseType.MYSQL : DatabaseType.MARIADB), logger);
        }

        this.requestHelper = new RequestHelper(databaseConnection, logger);
        if (!databaseConnection.isValid()) {
            fr.maxlego08.spawner.zcore.logger.Logger.info("The database connection could not be established, disabling zspawner...", fr.maxlego08.spawner.zcore.logger.Logger.LogType.ERROR);
            this.isEnable = false;
            this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            return;
        }
        fr.maxlego08.spawner.zcore.logger.Logger.info("The database connection has been established successfully.", fr.maxlego08.spawner.zcore.logger.Logger.LogType.SUCCESS);

        MigrationManager.setDatabaseConfiguration(databaseConnection.getDatabaseConfiguration());
        MigrationManager.execute(databaseConnection, logger);

        this.startBatchTask(config.getInt("batch-task", 10));
    }

    private void startBatchTask(int seconds) {
        if (seconds <= 0) return;
        if (!this.isEnable) return;
        this.plugin.getFoliaManager().runTimerAsync(w -> {
            this.saveAllNow();
        }, seconds, seconds, TimeUnit.SECONDS);
    }

    private static Object itemKey(UUID spawnerId, UUID itemId) {
        return List.of(spawnerId, itemId);
    }

    private static Object historyKey(UUID spawnerId, long startTime) {
        return List.of(spawnerId, startTime);
    }

    /**
     * Sérialise les suppressions avec le flush du cache : les deux touchent les mêmes lignes.
     */
    private void runLocked(Runnable runnable) {
        synchronized (this.flushLock) {
            runnable.run();
        }
    }

    private void storeData() {
        synchronized (this.flushLock) {
            storeDataLocked();
        }
    }

    private void storeDataLocked() {
        List<Schema> schemas = new ArrayList<>();
        List<Schema> schemasWithLocation = new ArrayList<>();
        for (Spawner spawner : this.cache.drain(Spawner.class)) {
            if (spawner == null || spawner.getOwner() == null || spawner.getSpawnerId() == null) continue;
            SpawnerDTO spawnerDTO = toSpawnerDTO(spawner);
            spawnerToSchema(spawnerDTO.last_location_user() != null ? schemasWithLocation : schemas, spawnerDTO);
        }
        if (!schemas.isEmpty()) {
            upsertMultiple(schemas);
        }
        if (!schemasWithLocation.isEmpty()) {
            upsertMultiple(schemasWithLocation);
        }
        List<OptionDTO> optionDTOList = this.cache.drain(OptionDTO.class);
        for (OptionDTO optionDTO : optionDTOList) {
            if (optionDTO != null) {
                if (optionDTO.spawner_id() == null) continue;
                schemas.add(SchemaBuilder.upsert(Tables.OPTIONS.getTableName(), table -> {
                    table.uuid("spawner_id", optionDTO.spawner_id()).primary();
                    table.decimal("distance", optionDTO.distance());
                    table.decimal("experience_multiplier", optionDTO.experience_multiplier());
                    table.decimal("loot_multiplier", optionDTO.loot_multiplier());
                    table.bool("auto_kill", optionDTO.auto_kill());
                    table.bool("auto_sell", optionDTO.auto_sell());
                    table.bigInt("max_entity", optionDTO.max_entity());
                    table.bigInt("min_delay", optionDTO.min_delay());
                    table.bigInt("max_delay", optionDTO.max_delay());
                    table.bigInt("min_spawn", optionDTO.min_spawn());
                    table.bigInt("max_spawn", optionDTO.max_spawn());
                    table.bigInt("mob_per_minute", optionDTO.mob_per_minute());
                    table.bool("drop_loots", optionDTO.drop_loots());
                    table.bigInt("remaining", optionDTO.remaining());
                    table.bool("location_enabled", optionDTO.location_enabled());
                    table.bigInt("min_location_time", optionDTO.min_location_time());
                    table.bigInt("max_location_time", optionDTO.max_location_time());
                    table.decimal("location_price", optionDTO.location_price());
                }));
            }
        }
        upsertMultiple(schemas);
        for (SpawnerItem spawnerItem : this.cache.drain(SpawnerItem.class)) {
            if (spawnerItem != null) {
                ItemDTO itemDTO = new ItemDTO(spawnerItem.getUniqueId(), spawnerItem.getSpawnerUUID(), Base64ItemStack.encode(spawnerItem.getItemStack()), spawnerItem.getAmount());
                if (itemDTO.item_stack() == null || itemDTO.item_stack().isEmpty()) continue;
                schemas.add(SchemaBuilder.upsert(Tables.ITEMS.getTableName(), table -> {
                    table.uuid("unique_id", itemDTO.unique_id()).primary();
                    table.uuid("spawner_id", itemDTO.spawner_id()).primary();
                    table.string("item_stack", itemDTO.item_stack());
                    table.bigInt("amount", itemDTO.amount());
                }));
            }
        }
        upsertMultiple(schemas);

        List<SpawnerLocationHistoryDTO> locationHistoryDTOList = this.cache.drain(SpawnerLocationHistoryDTO.class);
        for (SpawnerLocationHistoryDTO locationHistoryDTO : locationHistoryDTOList) {
            if (locationHistoryDTO != null) {
                if (locationHistoryDTO.spawner_id() == null) continue;
                schemas.add(SchemaBuilder.upsert(Tables.SPAWNER_LOCATION_HISTORY.getTableName(), table -> {
                    table.uuid("spawner_id", locationHistoryDTO.spawner_id()).primary();
                    table.bigInt("timestamp", locationHistoryDTO.timestamp()).primary();
                    table.bigInt("duration", locationHistoryDTO.duration());
                    table.uuid("player_id", locationHistoryDTO.player_id());
                    table.decimal("price", locationHistoryDTO.price());
                }));
            }
        }
        upsertMultiple(schemas);
    }

    private SpawnerDTO toSpawnerDTO(Spawner spawner) {
        return new SpawnerDTO(spawner.getOwner(), spawner.getSpawnerId(), spawner.getLocation() == null ? null : changeLocationToString(spawner.getLocation()), spawner.getType(), spawner.getPlacedAt(), spawner.getEntityType(), spawner.getAmount(), spawner.getLastLocationUser(), spawner.getLastLocationTime(), spawner.getLastLocationStartTime(), spawner.getBlockFace());
    }

    private void spawnerToSchema(List<Schema> schemasList, SpawnerDTO spawnerDTO) {
        schemasList.add(SchemaBuilder.upsert(Tables.SPAWNERS.getTableName(), table -> {
            table.uuid("owner", spawnerDTO.owner()).primary();
            table.uuid("spawner_id", spawnerDTO.spawner_id()).primary();
            table.string("location", spawnerDTO.location());
            table.string("type", spawnerDTO.type().name());
            table.bigInt("placed_at", spawnerDTO.placed_at());
            table.string("entity_type", spawnerDTO.entity_type().name());
            table.string("block_face", spawnerDTO.block_face().name());
            if (spawnerDTO.last_location_user() != null) {
                table.uuid("last_location_user", spawnerDTO.last_location_user());
            }
            table.bigInt("last_location_time", spawnerDTO.last_location_time());
            table.bigInt("last_location_start_time", spawnerDTO.last_location_start_time());
            table.bigInt("amount", spawnerDTO.amount());
        }));
    }

    private void upsertMultiple(List<Schema> schemas) {
        if (schemas.isEmpty()) return;
        this.requestHelper.upsertMultiple(schemas);
        schemas.clear();
    }

    @Override
    public boolean isEnable() {
        return this.isEnable;
    }

    @Override
    public List<SpawnerDTO> loadSpawners() {
        return this.selectAll(Tables.SPAWNERS.getTableName(), SpawnerDTO.class);
    }

    @Override
    public List<OptionDTO> loadOptions() {
        return this.selectAll(Tables.OPTIONS.getTableName(), OptionDTO.class);
    }

    @Override
    public List<ItemDTO> loadItems() {
        return this.selectAll(Tables.ITEMS.getTableName(), ItemDTO.class);
    }

    @Override
    public List<SpawnerLocationHistoryDTO> loadLocationHistories() {
        return this.selectAll(Tables.SPAWNER_LOCATION_HISTORY.getTableName(), SpawnerLocationHistoryDTO.class);
    }

    @Override
    public void upsertSpawner(Spawner spawner) {
        if (!this.isEnable) return;

        // On mémorise le spawner lui-même : le DTO n'est construit qu'au flush, ce qui replie
        // les N mutations d'un même spawner sur une seule sérialisation.
        this.cache.put(Spawner.class, spawner.getSpawnerId(), spawner);
    }

    @Override
    public void upsertOption(SpawnerOption spawnerOption, UUID spawnerId) {
        if (!this.isEnable) return;

        this.cache.put(OptionDTO.class, spawnerId, new OptionDTO(spawnerId, spawnerOption.getDistance(), spawnerOption.getExperienceMultiplier(), spawnerOption.getLootMultiplier(), spawnerOption.enableAutoKill(), spawnerOption.enableAutoSell(), spawnerOption.getMaxEntity(), spawnerOption.getMinDelay(), spawnerOption.getMaxDelay(), spawnerOption.getMinSpawn(), spawnerOption.getMaxSpawn(), spawnerOption.getMobPerMinute(), spawnerOption.dropLoots(), spawnerOption.isLocationEnabled(), spawnerOption.getMinLocationTime(), spawnerOption.getMaxLocationTime(), spawnerOption.getLocationPrice(), spawnerOption.getRemainingEntity()));
    }

    @Override
    public void upsertItem(SpawnerItem spawnerItem, UUID spawnerId) {
        if (!this.isEnable) return;

        // Idem : l'encodage Base64 de l'ItemStack est différé au flush au lieu d'être refait
        // à chaque ajout de loot.
        this.cache.put(SpawnerItem.class, itemKey(spawnerId, spawnerItem.getUniqueId()), spawnerItem);
    }

    @Override
    public void upsertLocationHistory(SpawnerLocationHistory spawnerLocationHistory, UUID spawnerId) {
        if (!this.isEnable) return;

        this.cache.put(SpawnerLocationHistoryDTO.class, historyKey(spawnerId, spawnerLocationHistory.getStartTime()), new SpawnerLocationHistoryDTO(spawnerId, spawnerLocationHistory.getStartTime(), spawnerLocationHistory.getDuration(), spawnerLocationHistory.getRentalPlayer(), spawnerLocationHistory.getPrice()));
    }

    @Override
    public Optional<SpawnerOption> getOption(UUID spawnerId) {
        var options = this.requestHelper.select(Tables.OPTIONS.getTableName(), OptionDTO.class, table -> table.where("spawner_id", spawnerId));
        return options.isEmpty() ? Optional.empty() : Optional.of(toOption(options.getFirst(), spawnerId));
    }

    private SpawnerOption toOption(OptionDTO optionDTO, UUID spawnerId) {
        return new ZSpawnerOption(this, spawnerId, optionDTO.distance(), optionDTO.experience_multiplier(), optionDTO.loot_multiplier(), optionDTO.auto_kill(), optionDTO.auto_sell(), optionDTO.max_entity(), optionDTO.min_delay(), optionDTO.max_delay(), optionDTO.min_spawn(), optionDTO.max_spawn(), optionDTO.mob_per_minute(), optionDTO.drop_loots(), optionDTO.location_enabled(), optionDTO.remaining(), optionDTO.min_location_time(), optionDTO.max_location_time(), optionDTO.location_price());
    }

    @Override
    public void deleteSpawner(Spawner spawner) {
        if (!this.isEnable) return;

        this.cache.remove(Spawner.class, spawner.getSpawnerId());
        this.cache.remove(OptionDTO.class, spawner.getSpawnerId());
        // Le verrou empêche la suppression de s'intercaler dans un flush en cours : sans lui
        // un upsert déjà extrait du cache pouvait être réécrit après le DELETE, et le spawner
        // supprimé réapparaissait au redémarrage.
        this.foliaManager.runAsync(() -> runLocked(() -> {
            this.requestHelper.delete(Tables.SPAWNER_LOCATION_HISTORY.getTableName(), table -> table.where("spawner_id", spawner.getSpawnerId()));
            this.requestHelper.delete(Tables.OPTIONS.getTableName(), table -> table.where("spawner_id", spawner.getSpawnerId()));
            this.requestHelper.delete(Tables.ITEMS.getTableName(), table -> table.where("spawner_id", spawner.getSpawnerId()));
            this.requestHelper.delete(Tables.SPAWNERS.getTableName(), table -> table.where("spawner_id", spawner.getSpawnerId()));
        }));
    }

    @Override
    public void deleteOption(SpawnerOption spawnerOption, UUID spawnerId) {
        if (!this.isEnable) return;

        this.cache.remove(OptionDTO.class, spawnerId);
        this.foliaManager.runAsync(() -> runLocked(() -> this.requestHelper.delete(Tables.OPTIONS.getTableName(), table -> table.where("spawner_id", spawnerId))));
    }

    @Override
    public void deleteItem(SpawnerItem spawnerItem, UUID spawnerId) {
        if (!this.isEnable) return;

        this.cache.remove(SpawnerItem.class, itemKey(spawnerId, spawnerItem.getUniqueId()));
        this.foliaManager.runAsync(() -> runLocked(() -> this.requestHelper.delete(Tables.ITEMS.getTableName(), table -> table.where("spawner_id", spawnerId).where("unique_id", spawnerItem.getUniqueId()))));
    }

    @Override
    public void saveAllNow() {
        if (!this.isEnable) return;
        this.storeData();
    }


    private <T> List<T> selectAll(String tableName, Class<T> dtoClass) {
        return this.requestHelper.selectAll(tableName, dtoClass);
    }
}
