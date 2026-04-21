package fr.maxlego08.spawner;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.loader.NoneLoader;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.spawner.api.*;
import fr.maxlego08.spawner.api.enums.Sort;
import fr.maxlego08.spawner.api.storage.ServerDataManager;
import fr.maxlego08.spawner.api.storage.ServerProfile;
import fr.maxlego08.spawner.api.utils.PlayerSpawner;
import fr.maxlego08.spawner.api.utils.SpawnerResult;
import fr.maxlego08.spawner.buttons.ShowButton;
import fr.maxlego08.spawner.buttons.gui.SortButton;
import fr.maxlego08.spawner.buttons.gui.SpawnersButton;
import fr.maxlego08.spawner.buttons.virtual.*;
import fr.maxlego08.spawner.drop.CustomVirtualDrop;
import fr.maxlego08.spawner.drop.VirtualDrop;
import fr.maxlego08.spawner.loader.*;
import fr.maxlego08.spawner.materials.SpawnerItemLoader;
import fr.maxlego08.spawner.materials.SpawnerOptionItemLoader;
import fr.maxlego08.spawner.zcore.enums.Message;
import fr.maxlego08.spawner.zcore.enums.Permission;
import fr.maxlego08.spawner.zcore.logger.Logger;
import fr.maxlego08.spawner.zcore.utils.compatibility.FoliaCompatibilityManager;
import fr.maxlego08.spawner.zcore.utils.storage.Persist;
import fr.maxlego08.spawner.zcore.utils.storage.Savable;
import fr.maxlego08.spawner.zcore.utils.yaml.YamlUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnerManager extends YamlUtils implements Savable, Runnable {

    private final SpawnerPlugin plugin;
    private final FoliaCompatibilityManager foliaManager;
    private final ServerProfile serverProfile;
    private final NamespacedKey spawnerEntityKey;
    private final NamespacedKey spawnerTypeKey;
    private final NamespacedKey spawnerUuidKey;
    private final Map<UUID, PlayerSpawner> playerSpawners = new ConcurrentHashMap<>();
    private final Map<SpawnerType, MenuItemStack> spawnerTypeItemStacks = new HashMap<>();
    private final Map<EntityType, VirtualDrop> customVirtualDrops = new HashMap<>();
    private Map<EntityType, String> entitiesMaterials = new HashMap<>();
    private List<Material> blacklistMaterials = new ArrayList<>();
    private SpawnerOption defaultSpawnerOption;

    public SpawnerManager(SpawnerPlugin plugin, FoliaCompatibilityManager foliaManager, ServerDataManager serverDataManager) {
        super(plugin);
        this.plugin = plugin;
        this.foliaManager = foliaManager;
        this.serverProfile = serverDataManager.getOrCreate();
        this.spawnerTypeKey = new NamespacedKey(plugin, "type");
        this.spawnerEntityKey = new NamespacedKey(plugin, "entity");
        this.spawnerUuidKey = new NamespacedKey(plugin, "level");
    }

    public void addSpawner(CommandSender sender, Player target, EntityType entityType, boolean silent) {
        Spawner spawner = new ZSpawner(this.plugin, target.getUniqueId(), SpawnerType.GUI, entityType, BlockFace.NORTH);
        this.plugin.getServerDataManager().getOrCreate().addSpawner(spawner);

        message(this.plugin, sender, Message.ADD_SENDER, "%target%", target.getName(), "%entity%", name(entityType.name()));
        if (!silent) {
            message(this.plugin, target, Message.ADD_PLAYER, "%entity%", name(entityType.name()));
        }
    }

    public boolean inventoryIsFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public void breakSpawner(Player player, Spawner spawner, int page) {
        spawner.breakBlock();
        openSpawner(player, page);
    }

    public void fillInventoryWithLoot(Player player, Spawner spawner, SpawnerItem spawnerItem) {
        ItemStack itemStack = spawnerItem.getItemStack().clone();
        long amount = spawnerItem.getAmount();
        while (amount > 0 && player.getInventory().firstEmpty() != -1) {
            int toAdd = (int) Math.min(amount, itemStack.getMaxStackSize());
            ItemStack currentItemStack = itemStack.clone();
            currentItemStack.setAmount(toAdd);
            player.getInventory().addItem(currentItemStack);
            amount -= toAdd;
        }

        if (amount <= 0) spawner.removeItem(spawnerItem);
        else spawnerItem.setAmount(amount);
    }

    public SpawnerOption getDefaultOption() {
        return this.defaultSpawnerOption;
    }

    public Optional<SpawnerResult> getSpawnerResult(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return Optional.empty();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        if (persistentDataContainer.has(this.spawnerEntityKey) && persistentDataContainer.has(this.spawnerTypeKey)) {
            SpawnerType spawnerType = SpawnerType.valueOf(persistentDataContainer.get(this.spawnerTypeKey, PersistentDataType.STRING));
            EntityType entityType = EntityType.valueOf(persistentDataContainer.get(this.spawnerEntityKey, PersistentDataType.STRING));
            UUID spawnerId = UUID.fromString(persistentDataContainer.getOrDefault(this.spawnerUuidKey, PersistentDataType.STRING, UUID.randomUUID().toString()));
            return Optional.of(new SpawnerResult(spawnerType, entityType, spawnerId));
        }
        return Optional.empty();
    }

    public void registerPlaceholders(Placeholders placeholders, Spawner spawner) {

        SpawnerOption spawnerOption = spawner == null ? this.defaultSpawnerOption : spawner.getOption();

        placeholders.register("auto-kill", spawnerOption.enableAutoKill() ? Message.YES.msg() : Message.NO.msg());
        placeholders.register("auto-sell", spawnerOption.enableAutoSell() ? Message.YES.msg() : Message.NO.msg());
        placeholders.register("max-entity", format(spawnerOption.getMaxEntity()));
        placeholders.register("distance", format(spawnerOption.getDistance()));
        placeholders.register("experience-multiplier", format(spawnerOption.getExperienceMultiplier()));
        placeholders.register("loot-multiplier", format(spawnerOption.getLootMultiplier()));
        placeholders.register("min-delay", format(spawnerOption.getMinDelay()));
        placeholders.register("min-delay-second", format(spawnerOption.getMinDelay() / 1000.0));
        placeholders.register("max-delay", format(spawnerOption.getMaxDelay()));
        placeholders.register("max-delay-second", format(spawnerOption.getMaxDelay() / 1000.0));
        placeholders.register("min-spawn", String.valueOf(spawnerOption.getMinSpawn()));
        placeholders.register("max-spawn", String.valueOf(spawnerOption.getMaxSpawn()));
        placeholders.register("mob-per-minute", String.valueOf(spawnerOption.getMobPerMinute()));
        placeholders.register("remaining-entities", format(spawnerOption.getRemainingEntity()));
        placeholders.register("location-enabled", spawnerOption.isLocationEnabled() ? Message.YES.msg() : Message.NO.msg());
        placeholders.register("min-location-time", String.valueOf(spawnerOption.getMinLocationTime()));
        placeholders.register("max-location-time", String.valueOf(spawnerOption.getMaxLocationTime()));
        placeholders.register("location-price", String.valueOf(spawnerOption.getLocationPrice()));

        if (spawner != null) {
            placeholders.register("entity-type", name(spawner.getEntityType().name()));
            placeholders.register("spawner-owner", this.plugin.getServer().getOfflinePlayer(spawner.getOwner()).getName());
            placeholders.register("spawner-key", spawner.getSpawnerKey());
            int nbRentals = 0;
            double rentalAmount = 0;
            for (SpawnerLocationHistory spawnerLocationHistory : spawner.getLocationHistory()) {
                rentalAmount += spawnerLocationHistory.getPrice();
                nbRentals++;
            }
            placeholders.register("location-total-rentals", String.valueOf(nbRentals));
            placeholders.register("location-total-earned", format(rentalAmount));
            placeholders.register("location-average-earned", format(nbRentals == 0 ? 0 : rentalAmount / nbRentals));
        }
    }

    public ItemStack getSpawnerItemStack(Player player, SpawnerType spawnerType, EntityType entityType, Spawner spawner) {

        MenuItemStack menuItemStack = this.spawnerTypeItemStacks.get(spawnerType);
        Placeholders placeholders = new Placeholders();

        placeholders.register("type", name(entityType.name()));
        placeholders.register("translation", entityType.translationKey());
        registerPlaceholders(placeholders, spawner);

        ItemStack itemStack = menuItemStack.build(player, false, placeholders);
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(this.spawnerTypeKey, PersistentDataType.STRING, spawnerType.name());
        persistentDataContainer.set(this.spawnerEntityKey, PersistentDataType.STRING, entityType.name());
        if (spawner != null) {
            persistentDataContainer.set(this.spawnerUuidKey, PersistentDataType.STRING, spawner.getSpawnerId().toString());
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public Map<EntityType, String> getEntitiesMaterials() {
        return entitiesMaterials;
    }

    public List<Material> getBlacklistMaterials() {
        return blacklistMaterials;
    }

    public Sort getPlayerSort(Player player) {
        PlayerSpawner playerSpawner = this.playerSpawners.get(player.getUniqueId());
        return playerSpawner == null ? Sort.PLACE : playerSpawner.getTypeShort();
    }

    public Map<UUID, PlayerSpawner> getPlayerSpawners() {
        return playerSpawners;
    }

    public void giveSpawner(CommandSender sender, Player target, SpawnerType spawnerType, EntityType entityType, boolean silent) {

        ItemStack itemStack = getSpawnerItemStack(target, spawnerType, entityType, null);

        this.plugin.getPlayerGive().give(target, itemStack);
        message(this.plugin, sender, Message.GIVE_SENDER, "%target%", target.getName(), "%type%", name(spawnerType.name()), "%entity%", name(entityType.name()), "%translation%", entityType.translationKey());

        if (!silent) {
            message(this.plugin, target, Message.GIVE_PLAYER, "%type%", name(spawnerType.name()), "%entity%", name(entityType.name()), "%translation%", entityType.translationKey());
        }
    }

    public void load(Persist persist) {
        this.spawnerTypeItemStacks.clear();
        File file = new File(this.plugin.getDataFolder(), "config.yml");
        if (!file.exists()) this.plugin.saveDefaultConfig();
        YamlConfiguration configuration = (YamlConfiguration) this.plugin.getConfig();
        ConfigurationSection configurationSection = configuration.getConfigurationSection("items");
        if (configurationSection != null) {
            configurationSection.getKeys(false).forEach(type -> {
                try {
                    SpawnerType spawnerType = SpawnerType.valueOf(type);
                    MenuItemStack menuItemStack = this.plugin.getInventoryManager().loadItemStack(configuration, "items." + type + ".", file);
                    this.spawnerTypeItemStacks.put(spawnerType, menuItemStack);
                } catch (Exception exception) {
                    Logger.showException(Configuration.enableDebug, "invalid spawner type", exception);
                }
            });
        }
        this.entitiesMaterials = loadEntityMaterials();
        this.blacklistMaterials = loadBlacklist();
        this.defaultSpawnerOption = loadDefaultSpawnerOption();
        this.customVirtualDrops.clear();
        this.customVirtualDrops.putAll(loadCustomVirtualDrops(configuration, file));
        this.loadInventories();
    }

    public List<ItemStack> generateCustomVirtualDrops(EntityType entityType, Player player) {
        VirtualDrop virtualDrops = this.customVirtualDrops.get(entityType);

        if (virtualDrops == null || virtualDrops.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemStack> itemStacks = new ArrayList<>();
        for (CustomVirtualDrop drop : virtualDrops.drops()) {
            drop.generate(player).ifPresent(itemStacks::add);
        }

        return itemStacks;
    }

    public void loadButtons() {
        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        inventoryManager.registerMaterialLoader(new SpawnerItemLoader(this.plugin));
        inventoryManager.registerMaterialLoader(new SpawnerOptionItemLoader(this.plugin));

        ButtonManager buttonManager = this.plugin.getButtonManager();
        buttonManager.register(new NoneLoader(this.plugin, SpawnersButton.class, "zspawner_spawners"));
        buttonManager.register(new NoneLoader(this.plugin, SortButton.class, "zspawner_sort"));
        buttonManager.register(new NoneLoader(this.plugin, ItemsButton.class, "zspawner_items"));
        buttonManager.register(new NoneLoader(this.plugin, RemoveButton.class, "zspawner_remove"));
        buttonManager.register(new NoneLoader(this.plugin, ShowButton.class, "zspawner_show"));
        buttonManager.register(new NoneLoader(this.plugin, LocationHistoryButton.class, "zspawner_location_history"));
        buttonManager.register(new NoneLoader(this.plugin, ShopButton.class, "zspawner_shop"));
        buttonManager.register(new NoneLoader(this.plugin, InfoButton.class, "zspawner_info"));
        buttonManager.register(new NoneLoader(this.plugin, LocationPriceDisplayButton.class, "zspawner_location_price_display"));
        buttonManager.register(new ToggleDropLoader(this.plugin));
        buttonManager.register(new ToggleLocationLoader(this.plugin));
        buttonManager.register(new PlayerPurchaseLocationButtonLoader(this.plugin));
        buttonManager.registerAction(new MinLocationTimeActionLoader(this.plugin));
        buttonManager.registerAction(new MaxLocationTimeActionLoader(this.plugin));
        buttonManager.registerAction(new LocationPriceActionLoader(this.plugin));
        buttonManager.registerAction(new PlayerLocationPriceActionLoader(this.plugin));
    }

    public void loadInventories() {
        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        inventoryManager.deleteInventories(this.plugin);
        try {
            inventoryManager.loadInventoryOrSaveResource(this.plugin, "inventories/gui/spawners.yml");
            inventoryManager.loadInventoryOrSaveResource(this.plugin, "inventories/virtual/virtual.yml");
            inventoryManager.loadInventoryOrSaveResource(this.plugin, "inventories/virtual/manage-location.yml");
            inventoryManager.loadInventoryOrSaveResource(this.plugin, "inventories/virtual/player-location.yml");
            inventoryManager.loadInventoryOrSaveResource(this.plugin, "inventories/virtual/location-history.yml");
            inventoryManager.loadInventoryOrSaveResource(this.plugin, "inventories/show.yml");
        } catch (InventoryException exception) {
            Logger.showException(Configuration.enableDebug, "loading inventories", exception);
        }
    }

    public void openSpawner(Player player, int page) {
        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        inventoryManager.getInventory(this.plugin, "spawners").ifPresent(inventory -> inventoryManager.openInventory(player, inventory, page));
    }

    public void openVirtualSpawner(Player player, Spawner spawner, int page) {
        PlayerSpawner playerSpawner = this.playerSpawners.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerSpawner());
        playerSpawner.setVirtualSpawner(spawner);
        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        inventoryManager.getInventory(this.plugin, "virtual").ifPresent(inventory -> inventoryManager.openInventory(player, inventory, page));
    }

    public void openManageLocationSpawner(Player player, Spawner spawner, int page) {
        PlayerSpawner playerSpawner = this.playerSpawners.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerSpawner());
        playerSpawner.setVirtualSpawner(spawner);
        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        inventoryManager.getInventory(this.plugin, "manage-location").ifPresent(inventory -> inventoryManager.openInventory(player, inventory, page));
    }


    public void openPlayerLocationSpawner(Player player, Spawner spawner, int page) {
        PlayerSpawner playerSpawner = this.playerSpawners.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerSpawner());
        playerSpawner.setVirtualSpawner(spawner);
        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        inventoryManager.getInventory(this.plugin, "player-location").ifPresent(inventory -> inventoryManager.openInventory(player, inventory, page));
    }

    @Override
    public void run() {
        Collection<Spawner> spawners = this.serverProfile.getSpawners(SpawnerType.VIRTUAL);
        if (spawners.isEmpty()) {
            return;
        }

        Map<World, List<Location>> playerLocationsByWorld = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation();
            World world = location.getWorld();
            if (world == null) continue;
            playerLocationsByWorld.computeIfAbsent(world, ignored -> new ArrayList<>()).add(location);
        }

        for (Spawner spawner : spawners) {
            if (!spawner.isChunkLoaded()) continue;
            Location location = spawner.getLocation();
            World world = location == null ? null : location.getWorld();
            if (world == null) continue;

            List<Location> playerLocations = playerLocationsByWorld.get(world);
            if (playerLocations != null && !playerLocations.isEmpty()) {
                double maxDistanceSquared = spawner.getDistance() * spawner.getDistance();
                for (Location playerLocation : playerLocations) {
                    if (playerLocation.distanceSquared(location) <= maxDistanceSquared) {
                        spawner.tick();
                        break;
                    }
                }
            }

            if (spawner.getOption().enableAutoKill()) spawner.autoKill();
        }
    }

    public void removeSpawner(CommandSender sender, Player target, Spawner spawner, boolean silent) {
        spawner.breakBlock();
        this.serverProfile.deleteSpawner(spawner);
        message(this.plugin, sender, Message.REMOVE_SENDER, "%target%", target.getName(), "%spawnerKey%", spawner.getSpawnerKey());
        if (!silent) {
            message(this.plugin, target, Message.REMOVE_PLAYER);
        }
    }

    public void removeStackLoot(Player player, Spawner spawner, SpawnerItem spawnerItem, int removeAmount) {
        if (inventoryIsFull(player)) return;
        ItemStack itemStack = spawnerItem.getItemStack().clone();
        long amount = spawnerItem.getAmount();
        int available = (int) Math.min(Math.min(itemStack.getMaxStackSize(), removeAmount), amount);
        itemStack.setAmount(available);
        player.getInventory().addItem(itemStack);
        spawnerItem.removeAmount(available);
        if (spawnerItem.getAmount() <= 0) spawner.removeItem(spawnerItem);
    }

    public void removeVirtualSpawner(Player player) {

        PlayerSpawner playerSpawner = this.playerSpawners.get(player.getUniqueId());
        if (playerSpawner == null) return;

        Spawner spawner = playerSpawner.getVirtualSpawner();
        if (spawner == null) return;

        if (!spawner.getOwner().equals(player.getUniqueId()) && !hasPermission(player, Permission.ZSPAWNER_BYPASS)) {
            message(this.plugin, player, Message.COMMAND_NO_PERMISSION);
            return;
        }

        if (!spawner.getItems().isEmpty()) {
            message(this.plugin, player, Message.VIRTUAL_REMOVE_ERROR_EMPTY);
            return;
        }

        if (inventoryIsFull(player)) {
            message(this.plugin, player, Message.VIRTUAL_REMOVE_ERROR_FULL);
            return;
        }
        spawner.breakBlock();

        ItemStack itemStack = getSpawnerItemStack(player, spawner.getType(), spawner.getEntityType(), spawner);
        this.plugin.getPlayerGive().give(player, itemStack);

        this.foliaManager.runAsync(() -> this.serverProfile.deleteSpawner(spawner));
        message(this.plugin, player, Message.VIRTUAL_REMOVE_SUCCESS);
    }

    public void startPlacement(Player player, Spawner spawner) {

        player.closeInventory();
        PlayerSpawner playerSpawner = this.playerSpawners.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerSpawner());
        playerSpawner.setPlacingSpawner(spawner);
        message(this.plugin, player, Message.PLACE_START);
    }

    @Override
    public void save(Persist persist) {
        // Implementation not provided
    }

    public void showSpawners(Player player, OfflinePlayer offlinePlayer, int page) {

        PlayerSpawner playerSpawner = this.playerSpawners.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerSpawner());
        playerSpawner.setTargetPlayer(offlinePlayer);
        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        inventoryManager.getInventory(this.plugin, "show").ifPresent(inventory -> inventoryManager.openInventory(player, inventory, page));
    }

    public void removeSpawnerGui(Spawner spawner, Player player, OfflinePlayer target, int page) {

        spawner.breakBlock();
        this.serverProfile.deleteSpawner(spawner);
        message(this.plugin, player, Message.REMOVE_SENDER, "%target%", target.getName(), "%spawnerKey%", spawner.getSpawnerKey());

        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        inventoryManager.getInventory(this.plugin, "show").ifPresent(inventory -> inventoryManager.openInventory(player, inventory, page));
    }

    public boolean sellSpawnerInventory(Player player) {

        ShopAction action = this.plugin.getShopAction();
        if (action == null) {
            player.closeInventory();
            message(this.plugin, player, Message.SELL_ERROR);
            return false;
        }

        PlayerSpawner playerSpawner = this.plugin.getManager().getPlayerSpawners().get(player.getUniqueId());
        Spawner spawner = playerSpawner == null ? null : playerSpawner.getVirtualSpawner() == null ? null : playerSpawner.getVirtualSpawner();
        if (spawner == null) {
            player.closeInventory();
            message(this.plugin, player, Message.SELL_ERROR);
            return false;
        }

        var isSuccess = false;
        var iterator = spawner.getItems().iterator();
        while (iterator.hasNext()) {
            var spawnerItem = iterator.next();
            if (action.deposit(player, spawnerItem.getItemStack(), spawnerItem.getAmount())) {
                iterator.remove();
//                this.plugin.getStorage().deleteSpawnerItem(spawner, spawnerItem);
                this.plugin.getStorageManager().deleteItem(spawnerItem, spawner.getSpawnerId());
                isSuccess = true;
            }
        }

        openVirtualSpawner(player, spawner, 1);
        return isSuccess;
    }

    private Map<EntityType, VirtualDrop> loadCustomVirtualDrops(YamlConfiguration configuration, File file) {
        Map<EntityType, VirtualDrop> drops = new HashMap<>();
        InventoryManager inventoryManager = this.plugin.getInventoryManager();

        List<Map<?, ?>> customDrops = configuration.getMapList("custom-virtual-drops");

        for (Map<?, ?> map : customDrops) {

            var entity = EntityType.valueOf((String) map.get("entity"));
            var cancelDefaultDrop = map.containsKey("cancel-default-drop") && (boolean) map.get("cancel-default-drop");
            List<Map<?, ?>> mapDrops = (List<Map<?, ?>>) map.get("drops");
            List<CustomVirtualDrop> customVirtualDrops = new ArrayList<>();
            for (Map<?, ?> mapDrop : mapDrops) {
                TypedMapAccessor accessor = new TypedMapAccessor((Map<String, Object>) mapDrop);
                double chance = accessor.getDouble("chance", 100.0);
                int min = accessor.getInt("min", 1);
                int max = accessor.getInt("max", min);
                MenuItemStack menuItemStack = inventoryManager.loadItemStack(file, "", (Map<String, Object>) accessor.getObject("item"));
                if (menuItemStack == null) {
                    plugin.getLogger().warning("Warning: Item not found for custom virtual drops and will be ignored.");
                    continue;
                }
                customVirtualDrops.add(new CustomVirtualDrop(menuItemStack, chance, min, max));
            }

            drops.put(entity, new VirtualDrop(cancelDefaultDrop, customVirtualDrops));
        }

        return drops;
    }
}