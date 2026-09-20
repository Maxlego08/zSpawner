package fr.maxlego08.spawner.stackable;

import fr.maxlego08.spawner.SpawnerPlugin;
import fr.maxlego08.spawner.zcore.utils.storage.Persist;
import fr.maxlego08.spawner.zcore.utils.storage.Savable;
import fr.maxlego08.spawner.zcore.utils.yaml.YamlUtils;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.*;

public class StackableManager extends YamlUtils implements Savable {

    private final SpawnerPlugin plugin;
    private final List<StackLevel> levels = new ArrayList<>();
    private final Map<EntityType, Integer> limits = new HashMap<>();
    private boolean enable;
    private int globalLimit;
    private List<EntityType> blacklist = new ArrayList<>();
    private List<EntityType> whitelist = new ArrayList<>();
    private String hologram;

    public StackableManager(SpawnerPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }


    @Override
    public void save(Persist persist) {

    }

    @Override
    public void load(Persist persist) {

        FileConfiguration config = this.plugin.getConfig();

        hologram = config.getString("stackableSpawner.hologram", null);
        enable = config.getBoolean("stackableSpawner.enable", false);
        globalLimit = config.getInt("stackableSpawner.globalLimit", 5);

        this.limits.clear();
        List<?> limitsList = config.getList("stackableSpawner.limits");
        if (limitsList != null) {
            for (Object limitObject : limitsList) {
                if (limitObject instanceof Map<?, ?>) {
                    Map<String, Integer> limitMap = (Map<String, Integer>) limitObject;
                    limitMap.forEach((entity, amount) -> {
                        Arrays.stream(EntityType.values()).filter(e -> e.name().equalsIgnoreCase(entity)).findFirst().ifPresent(entityType -> this.limits.put(entityType, amount));
                    });
                }
            }
        }


        blacklist = loadEntityList("stackableSpawner.blacklist");
        whitelist = loadEntityList("stackableSpawner.whitelist");
        List<?> levelsList = config.getList("stackableSpawner.levels");
        this.levels.clear();
        if (levelsList != null) {
            for (Object levelObject : levelsList) {
                if (levelObject instanceof Map<?, ?>) {
                    Map<?, ?> levelMap = (Map<?, ?>) levelObject;
                    StackLevel level = new StackLevel(readInt(levelMap, "stackAmount", 1), readInt(levelMap, "delay", 20), readInt(levelMap, "minSpawnDelay", 200), readInt(levelMap, "maxSpawnDelay", 800), readInt(levelMap, "spawnCount", 4), readInt(levelMap, "maxNearbyEntities", 16), readInt(levelMap, "requiredPlayerRange", 16), readInt(levelMap, "spawnRange", 4));
                    this.levels.add(level);
                }
            }
        }

        // getLevel() parcourt la liste dans l'ordre : la configuration peut déclarer les
        // paliers dans n'importe quel ordre.
        this.levels.sort(Comparator.comparingInt(StackLevel::getStackAmount));
    }

    /**
     * Une clé absente ou d'un autre type levait une NullPointerException qui interrompait
     * tout le chargement et laissait la liste des paliers vide : le spawner retombait alors
     * silencieusement sur les valeurs vanilla. Les valeurs par défaut sont celles de vanilla.
     */
    private int readInt(Map<?, ?> map, String key, int defaultValue) {
        Object value = map.get(key);
        return value instanceof Number number ? number.intValue() : defaultValue;
    }


    @Override
    public String toString() {
        return "StackableManager{" + "plugin=" + plugin + ", levels=" + levels + ", enable=" + enable + ", globalLimit=" + globalLimit + ", limits=" + limits + ", blacklist=" + blacklist + ", whitelist=" + whitelist + ", hologram='" + hologram + '\'' + '}';
    }

    public List<StackLevel> getLevels() {
        return levels;
    }

    public boolean isEnable() {
        return enable;
    }

    public Map<EntityType, Integer> getLimits() {
        return limits;
    }

    public List<EntityType> getBlacklist() {
        return blacklist;
    }

    public List<EntityType> getWhitelist() {
        return whitelist;
    }

    /**
     * Renvoie le palier applicable à une pile de {@code amount} spawners : le plus haut
     * palier dont le stackAmount est atteint. L'égalité stricte utilisée auparavant ne
     * renvoyait rien dès que la configuration sautait des paliers (1, 5, 10 comme dans la
     * documentation) ou que la pile dépassait le dernier palier, et le bloc gardait alors
     * ses réglages vanilla.
     */
    public Optional<StackLevel> getLevel(int amount) {
        StackLevel result = null;
        for (StackLevel level : this.levels) { // trié par stackAmount croissant
            if (level.getStackAmount() > amount) break;
            result = level;
        }
        // En dessous du premier palier configuré on applique quand même le plus petit.
        if (result == null && !this.levels.isEmpty()) result = this.levels.get(0);
        return Optional.ofNullable(result);
    }

    public void updateSpawner(CreatureSpawner spawner, int amount) {
        getLevel(amount).ifPresent(stackLevel -> stackLevel.updateSpawner(spawner));
    }

    public String getHologram() {
        return hologram;
    }

    public int getGlobalLimit() {
        return globalLimit;
    }

    public int getLimit(EntityType entityType) {
        return this.limits.getOrDefault(entityType, this.globalLimit);
    }
}
