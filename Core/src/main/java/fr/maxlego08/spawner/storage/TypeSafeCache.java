package fr.maxlego08.spawner.storage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tampon d'écriture différée, indexé par type de DTO puis par clé primaire.
 * <p>
 * L'implémentation précédente stockait des {@link ArrayList} non synchronisées alimentées
 * depuis le thread de jeu et vidées depuis le thread du batch : en plus des accès concurrents,
 * le vidage remplaçait la liste, ce qui faisait disparaître sans trace toute écriture arrivée
 * pendant le flush. Chaque upsert était par ailleurs un {@code removeIf} en O(n).
 * <p>
 * Ici une écriture est un simple {@code put} en O(1) et {@link #drain(Class)} retire les
 * entrées une par une de façon atomique : une écriture concurrente est soit emportée par le
 * flush en cours, soit conservée pour le suivant, mais jamais perdue.
 */
public class TypeSafeCache {

    private final Map<Class<?>, Map<Object, Object>> cache = new ConcurrentHashMap<>();

    /**
     * Ajoute ou remplace l'entrée identifiée par {@code key} pour ce type.
     */
    public void put(@NotNull Class<?> type, @NotNull Object key, @NotNull Object value) {
        this.cache.computeIfAbsent(type, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    /**
     * Retire l'écriture en attente identifiée par {@code key}, si elle existe.
     */
    public void remove(@NotNull Class<?> type, @NotNull Object key) {
        Map<Object, Object> typeCache = this.cache.get(type);
        if (typeCache != null) typeCache.remove(key);
    }

    /**
     * Retire et retourne toutes les entrées en attente pour ce type.
     */
    @SuppressWarnings("unchecked")
    public @NotNull <T> List<T> drain(@NotNull Class<T> type) {
        Map<Object, Object> typeCache = this.cache.get(type);
        if (typeCache == null || typeCache.isEmpty()) return Collections.emptyList();

        List<T> values = new ArrayList<>(typeCache.size());
        for (Object key : new ArrayList<>(typeCache.keySet())) {
            Object value = typeCache.remove(key);
            if (value != null) values.add((T) value);
        }
        return values;
    }

    public void clearAll() {
        this.cache.clear();
    }
}
