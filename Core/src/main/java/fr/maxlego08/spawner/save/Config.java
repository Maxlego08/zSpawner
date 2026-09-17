package fr.maxlego08.spawner.save;

import fr.maxlego08.spawner.SpawnerPlugin;
import fr.maxlego08.spawner.api.SpawnerType;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Config {

    public static boolean enableLimit = true;
    public static int globalLimit = 5;
    public static Map<EntityType, Integer> entityLimits = new HashMap<>();
    public static boolean enableDebug = true;
    public static boolean enableDebugTime = false;
    public static boolean disableNaturalSpawnerExplosion = false;
    public static boolean dropNaturalSpawnerOnExplose = false;
    public static boolean ownerCanBreakSpawner = false;
    public static Map<SpawnerType, Boolean> spawnerExplosion = new HashMap<>();
    public static Map<SpawnerType, Boolean> spawnerDrop = new HashMap<>();
    public static Material virtualMaterial = Material.LODESTONE;
    public static String virtualName = "&6x%amount%";

    public static boolean enableSilkSpawner = false;
    public static boolean needSilkTouchEnchant = false;
    public static boolean silkNaturalSpawner = false;
    // EnumSet : ces deux collections sont interrogées par item dropé et à chaque casse de
    // spawner, un contains() linéaire sur ArrayList y était inutilement coûteux.
    public static Set<Material> whitelistMaterialSilkSpawner = EnumSet.noneOf(Material.class);
    public static Set<Material> blacklistMaterials = EnumSet.noneOf(Material.class);
    public static SpawnerType naturelSpawnerInto = SpawnerType.CLASSIC;
    public static boolean breakUpVirtualSpawner;
    public static boolean givePlayerExperience;

    public static boolean enableSpawnerLocation = true;
    public static double minLocationPrice = 1000;
    public static double maxLocationPrice = 10000;

    public static long offlinePlayerCacheDuration = 300;

    /**
     * static Singleton instance.
     */
    private static volatile Config instance;


    /**
     * Private constructor for singleton.
     */
    private Config() {
    }

    /**
     * Return a singleton instance of Config.
     */
    public static Config getInstance() {
        // Double lock for thread safety.
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    public static boolean checkSpawnerExplosion() {
        return dropNaturalSpawnerOnExplose || disableNaturalSpawnerExplosion || spawnerExplosion.values().stream().anyMatch(Boolean::booleanValue) || spawnerDrop.values().stream().anyMatch(Boolean::booleanValue);
    }


    public void load(@NotNull SpawnerPlugin plugin) {

        FileConfiguration configuration = plugin.getConfig();

        enableDebug = configuration.getBoolean("enableDebug", false);
        enableDebugTime = configuration.getBoolean("enableDebugTime", false);
        ownerCanBreakSpawner = configuration.getBoolean("ownerCanBreakSpawner", true);
        offlinePlayerCacheDuration = configuration.getLong("cache-offline-player", 300);

        enableLimit = configuration.getBoolean("chunkLimit.enable", false);
        globalLimit = configuration.getInt("chunkLimit.global", 5);

        entityLimits.clear();
        List<?> limitsList = configuration.getList("stackableSpawner.limits");
        if (limitsList != null) {
            for (Object limitObject : limitsList) {
                if (limitObject instanceof Map<?, ?>) {
                    Map<String, Integer> limitMap = (Map<String, Integer>) limitObject;
                    limitMap.forEach((entity, amount) -> {
                        Arrays.stream(EntityType.values()).filter(e -> e.name().equalsIgnoreCase(entity)).findFirst().ifPresent(entityType -> entityLimits.put(entityType, amount));
                    });
                }
            }
        }

        breakUpVirtualSpawner = configuration.getBoolean("breakUpVirtualSpawner", true);
        givePlayerExperience = configuration.getBoolean("give-player-experience", false);
        dropNaturalSpawnerOnExplose = configuration.getBoolean("dropNaturalSpawnerOnExplose", true);
        disableNaturalSpawnerExplosion = configuration.getBoolean("disableNaturalSpawnerExplosion", true);

        spawnerExplosion.put(SpawnerType.GUI, configuration.getBoolean("disableSpawnerExplosion.GUI", false));
        spawnerExplosion.put(SpawnerType.CLASSIC, configuration.getBoolean("disableSpawnerExplosion.CLASSIC", false));

        spawnerDrop.put(SpawnerType.GUI, configuration.getBoolean("dropSpawnerOnExplose.GUI", false));
        spawnerDrop.put(SpawnerType.CLASSIC, configuration.getBoolean("dropSpawnerOnExplose.CLASSIC", false));

        virtualMaterial = Material.valueOf(configuration.getString("virtual.material", "LODESTONE"));
        virtualName = configuration.getString("virtual.name", "&6x%amount%");

        enableSilkSpawner = configuration.getBoolean("silkSpawner.enable", false);
        silkNaturalSpawner = configuration.getBoolean("silkSpawner.silkNaturalSpawner", false);
        needSilkTouchEnchant = configuration.getBoolean("silkSpawner.needSilkTouchEnchant", false);
        whitelistMaterialSilkSpawner = loadMaterials(plugin, configuration, "silkSpawner.whitelistMaterial");
        blacklistMaterials = loadMaterials(plugin, configuration, "blacklist-materials");
        naturelSpawnerInto = SpawnerType.valueOf(configuration.getString("silkSpawner.naturelSpawnerInto", "CLASSIC").toUpperCase());

        enableSpawnerLocation = configuration.getBoolean("spawner-location.enable", true);
        minLocationPrice = configuration.getDouble("spawner-location.minPrice", 1000);
        maxLocationPrice = configuration.getDouble("spawner-location.maxPrice", 10000);
    }

    /**
     * Un nom de matériau inconnu ne doit pas faire échouer tout le chargement de la config :
     * on ignore l'entrée en la signalant.
     */
    private static Set<Material> loadMaterials(SpawnerPlugin plugin, FileConfiguration configuration, String path) {
        Set<Material> materials = EnumSet.noneOf(Material.class);
        for (String name : configuration.getStringList(path)) {
            Material material = Material.matchMaterial(name);
            if (material == null) {
                plugin.getLogger().warning("Unknown material '" + name + "' in " + path + ", entry ignored.");
                continue;
            }
            materials.add(material);
        }
        return materials;
    }
}
