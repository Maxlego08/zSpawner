package fr.maxlego08.spawner.zcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public final class OfflinePlayerCache {
    // clearCache() tourne sur le scheduler async pendant que getOfflinePlayer est
    // appelé depuis le thread de jeu : une HashMap se corrompait sous ces accès croisés.
    private static final Map<UUID, OfflinePlayer> cache = new ConcurrentHashMap<>();

    public static OfflinePlayer getOfflinePlayer(UUID uuid) {
        return cache.computeIfAbsent(uuid, Bukkit::getOfflinePlayer);
    }

    public static void clearCache() {
        cache.clear();
    }
}
