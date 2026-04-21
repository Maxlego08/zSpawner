package fr.maxlego08.spawner;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.spawner.api.*;
import fr.maxlego08.spawner.api.item.UpgradeManager;
import fr.maxlego08.spawner.api.storage.ServerDataManager;
import fr.maxlego08.spawner.api.storage.ServerProfile;
import fr.maxlego08.spawner.api.storage.StorageManager;
import fr.maxlego08.spawner.api.team.TeamManager;
import fr.maxlego08.spawner.command.commands.CommandSpawner;
import fr.maxlego08.spawner.give.DefaultGive;
import fr.maxlego08.spawner.give.ZEssentialsGive;
import fr.maxlego08.spawner.item.ZUpgradeManager;
import fr.maxlego08.spawner.placeholder.LocalPlaceholder;
import fr.maxlego08.spawner.save.Config;
import fr.maxlego08.spawner.save.MessageLoader;
import fr.maxlego08.spawner.shop.ZShopAction;
import fr.maxlego08.spawner.stackable.StackableManager;
import fr.maxlego08.spawner.storage.StorageManagerImp;
import fr.maxlego08.spawner.storage.ZServerDataManager;
import fr.maxlego08.spawner.team.SuperiorTeamManager;
import fr.maxlego08.spawner.zcore.ZPlugin;
import fr.maxlego08.spawner.zcore.logger.Logger;
import fr.maxlego08.spawner.zcore.utils.OfflinePlayerCache;
import fr.maxlego08.spawner.zcore.utils.compatibility.FoliaCompatibilityManager;
import fr.maxlego08.spawner.zcore.utils.plugins.Metrics;
import fr.maxlego08.spawner.zcore.utils.plugins.Plugins;
import org.bukkit.NamespacedKey;

import java.util.*;

/**
 * System to create your plugins very simply Projet:
 * <a href="https://github.com/Maxlego08/TemplatePlugin">https://github.com/Maxlego08/TemplatePlugin</a>
 *
 * @author Maxlego08
 */
public class SpawnerPlugin extends ZPlugin {
    private final FoliaCompatibilityManager foliaManager = new FoliaCompatibilityManager(this);

    private final StorageManager storageManager = new StorageManagerImp(this, this.foliaManager);
    private final ServerDataManager serverDataManager = new ZServerDataManager(this);

    private final SpawnerManager manager = new SpawnerManager(this, this.foliaManager, this.serverDataManager);
    private final StackableManager stackableManager = new StackableManager(this);
    private final SpawnerPlaceholders spawnerPlaceholders = new SpawnerPlaceholders(this);
    private final UpgradeManager upgradeManager = new ZUpgradeManager(this);
    private InventoryManager inventoryManager;
    private ButtonManager buttonManager;
    private ShopAction shopAction;
    private PlayerGive playerGive = new DefaultGive();
    private NamespacedKey spawnerKey;
    private MetaUpdater metaUpdater;
    private final List<TeamManager> teamManagers = new ArrayList<>();

    @Override
    public void onEnable() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("zspawner");

        this.preEnable();
        this.saveDefaultConfig();

        this.storageManager.loadDatabase();

        this.spawnerKey = new NamespacedKey(this, "zspawner");

        this.registerCommand("zspawner", new CommandSpawner(this), "spawner", "sp", "spawners");

        this.inventoryManager = getProvider(InventoryManager.class);
        this.metaUpdater = this.inventoryManager.getMeta();
        this.buttonManager = getProvider(ButtonManager.class);

        this.addSave(this.manager);
        this.addSave(new MessageLoader(this));
        this.addSave(this.stackableManager);

        ServerProfile serverProfile = this.serverDataManager.getOrCreate();

        this.addListener(new SpawnerListener(this, this.foliaManager, serverProfile));
        this.addListener(new SpawnerListenerPaper(this));

        Config.getInstance().load(this);
        this.manager.loadButtons();
        this.upgradeManager.loadItems();
        this.loadFiles();
        this.serverDataManager.loadServerData();

        this.spawnerPlaceholders.register();

        this.foliaManager.runTimer(this.manager, 20, 20);

        new Metrics(this, 5365);

        if (this.isEnable(Plugins.ZSHOP)) {
            getLogger().info("Use zShop");
            this.shopAction = new ZShopAction(this);
        }

        if (this.isEnable(Plugins.ZESSENTIALS)) {
            getLogger().info("Use zEssentials");
            this.playerGive = new ZEssentialsGive();
        }

        if (this.isEnable(Plugins.SUPERIORSKYBLOCK2)) {
            getLogger().info("Use SuperiorSkyBlock2");
            this.registerTeamManager(new SuperiorTeamManager(this));
        }

        this.foliaManager.runTimerAsync(OfflinePlayerCache::clearCache, Config.offlinePlayerCacheDuration, Config.offlinePlayerCacheDuration);

        this.postEnable();
    }

    @Override
    public void onDisable() {

        this.preDisable();

        this.saveFiles();
        Logger.info("Saving spawners...");
        Collection<Spawner> spawners = this.serverDataManager.getOrCreate().getSpawners();
        for (Spawner spawner : spawners) {
            spawner.disable();
            spawner.save();
            for (SpawnerItem spawnerItem : spawner.getItems()) {
                spawnerItem.save();
            }
            spawner.getOption().save();
            for (SpawnerLocationHistory spawnerLocationHistory : spawner.getLocationHistory()){
                spawnerLocationHistory.save();
            }
        }
        this.storageManager.saveAllNow();

        Logger.info("Spawners saved.", Logger.LogType.SUCCESS);

        this.postDisable();
    }

    @Override
    public void reloadFiles() {
        super.reloadFiles();
        this.upgradeManager.loadItems();
    }

    public SpawnerManager getManager() {
        return manager;
    }

    public StackableManager getStackableManager() {
        return stackableManager;
    }

    public ButtonManager getButtonManager() {
        return buttonManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }

    public ShopAction getShopAction() {
        return shopAction;
    }

    public PlayerGive getPlayerGive() {
        return playerGive;
    }

    public NamespacedKey getSpawnerKey() {
        return spawnerKey;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public ServerDataManager getServerDataManager() {
        return serverDataManager;
    }

    public FoliaCompatibilityManager getFoliaManager() {return this.foliaManager;}

    public MetaUpdater getMetaUpdater() {return this.metaUpdater;}

    public void registerTeamManager(TeamManager teamManager) {
        if (teamManager != null) {
            this.teamManagers.add(teamManager);
        }
    }

    public List<TeamManager> getTeamManagers() {
        return Collections.unmodifiableList(this.teamManagers);
    }

    public boolean hasTeamAccess(UUID ownerId, UUID playerId) {
        if (ownerId == null || playerId == null) {
            return false;
        }
        return this.teamManagers.stream().anyMatch(teamManager -> teamManager.canAccess(ownerId, playerId));
    }
}
