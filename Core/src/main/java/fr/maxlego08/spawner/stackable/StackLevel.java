package fr.maxlego08.spawner.stackable;

import org.bukkit.block.CreatureSpawner;

public class StackLevel {
    private final int stackAmount;
    private final int delay;
    private final int minSpawnDelay;
    private final int maxSpawnDelay;
    private final int spawnCount;
    private final int maxNearbyEntities;
    private final int requiredPlayerRange;
    private final int spawnRange;

    public StackLevel(int stackAmount, int delay, int minSpawnDelay, int maxSpawnDelay, int spawnCount, int maxNearbyEntities, int requiredPlayerRange, int spawnRange) {
        this.stackAmount = stackAmount;
        this.delay = delay;
        this.minSpawnDelay = minSpawnDelay;
        this.maxSpawnDelay = maxSpawnDelay;
        this.spawnCount = spawnCount;
        this.maxNearbyEntities = maxNearbyEntities;
        this.requiredPlayerRange = requiredPlayerRange;
        this.spawnRange = spawnRange;
    }

    public int getStackAmount() {
        return stackAmount;
    }

    public int getDelay() {
        return delay;
    }

    public int getMinSpawnDelay() {
        return minSpawnDelay;
    }

    public int getMaxSpawnDelay() {
        return maxSpawnDelay;
    }

    public int getSpawnCount() {
        return spawnCount;
    }

    public int getMaxNearbyEntities() {
        return maxNearbyEntities;
    }

    public int getRequiredPlayerRange() {
        return requiredPlayerRange;
    }

    public int getSpawnRange() {
        return spawnRange;
    }

    public void updateSpawner(CreatureSpawner spawner) {

        spawner.setDelay(this.delay);

        // Bukkit valide min <= max à chaque appel : écrire directement un minSpawnDelay
        // supérieur au maxSpawnDelay actuel du bloc (800 en vanilla) lève une
        // IllegalArgumentException en plein BlockPlaceEvent. On passe donc par 0, qui est
        // toujours accepté, avant d'écrire la fenêtre définitive.
        int minDelay = Math.min(this.minSpawnDelay, this.maxSpawnDelay);
        int maxDelay = Math.max(this.minSpawnDelay, this.maxSpawnDelay);
        spawner.setMinSpawnDelay(0);
        spawner.setMaxSpawnDelay(maxDelay);
        spawner.setMinSpawnDelay(minDelay);

        spawner.setSpawnCount(this.spawnCount);
        spawner.setMaxNearbyEntities(this.maxNearbyEntities);
        spawner.setRequiredPlayerRange(this.requiredPlayerRange);
        spawner.setSpawnRange(this.spawnRange);

    }
}
