package fr.maxlego08.spawner.listener;

import fr.maxlego08.spawner.SpawnerPlugin;
import fr.maxlego08.spawner.zcore.utils.ZUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

@SuppressWarnings("deprecation")
public class AdapterListener extends ZUtils implements Listener {

    private final SpawnerPlugin plugin;

    public AdapterListener(SpawnerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onConnect(SlimeSplitEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onSlimeSplit(event, event.getEntity()));
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onConnect(event, event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onQuit(event, event.getPlayer()));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onInventoryClick(event, (Player) event.getWhoClicked()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.isCancelled()) return;

        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onBlockBreak(event, event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.isCancelled()) return;

        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onBlockPlace(event, event.getPlayer()));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onEntityDeath(event, event.getEntity()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onInteract(event, event.getPlayer()));
    }

    @EventHandler
    public void onPlayerTalk(AsyncPlayerChatEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onPlayerTalk(event, event.getMessage()));
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onCraftItem(event));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onInventoryDrag(event, (Player) event.getWhoClicked()));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onInventoryClose(event, (Player) event.getPlayer()));
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onCommand(event, event.getPlayer(), event.getMessage()));
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onGamemodeChange(event, event.getPlayer()));
    }

    /*
     * @EventHandler public void onDrop(PlayerDropItemEvent event) {
     * this.plugin.getListenerAdapters().forEach(adapter ->
     * adapter.onDrop(event, event.getPlayer())); if (!Config.useItemFallEvent)
     * return; Item item = event.getItemDrop(); AtomicBoolean hasSendEvent = new
     * AtomicBoolean(false); scheduleFix(100, (task, isActive) -> { if
     * (!isActive) return; this.plugin.getListenerAdapters().forEach(adapter ->
     * adapter.onItemMove(event, event.getPlayer(), item, item.getLocation(),
     * item.getLocation().getBlock())); if (item.isOnGround() &&
     * !hasSendEvent.get()) { task.cancel(); hasSendEvent.set(true);
     * this.plugin.getListenerAdapters().forEach( adapter ->
     * adapter.onItemisOnGround(event, event.getPlayer(), item,
     * item.getLocation())); } }); }
     */

    @EventHandler
    public void onPick(PlayerPickupItemEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onPickUp(event, event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMobSpawn(CreatureSpawnEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onMobSpawn(event));
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onBlockExplode(event.blockList()));
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onBlockExplode(event.blockList()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPower(CreeperPowerEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onPower(event, event.getCause(), event.getEntity(), event.getLightning()));
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onPlayerInteractAtEntity(event, event.getPlayer(), event.getRightClicked()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onCombust(EntityCombustEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onCombust(event, event.getEntity()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            this.plugin.getListenerAdapters().forEach(adapter -> adapter.onEntityDamage(event, (LivingEntity) event.getEntity(), event.getCause(), event.getFinalDamage()));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (event.getEntity() instanceof LivingEntity && event.getDamager() instanceof LivingEntity) {
            this.plugin.getListenerAdapters().forEach(adapter -> adapter.onDamageByEntity(event, event.getCause(), event.getDamage(), (LivingEntity) event.getDamager(), (LivingEntity) event.getEntity()));
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            this.plugin.getListenerAdapters().forEach(adapter -> adapter.onPlayerDamagaByPlayer(event, event.getCause(), event.getDamage(), (Player) event.getDamager(), (Player) event.getEntity()));
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
            this.plugin.getListenerAdapters().forEach(adapter -> adapter.onPlayerDamagaByArrow(event, event.getCause(), event.getDamage(), (Projectile) event.getDamager(), (Player) event.getEntity()));
        }

        if (damager instanceof Player && entity instanceof LivingEntity) {
            this.plugin.getListenerAdapters().forEach(adapter -> adapter.onEntityDamageByPlayer(event, (Player) damager, (LivingEntity) entity));
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onPistonExtend(event, event.getBlock(), event.getBlocks()));
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onPistonRetract(event, event.getBlock(), event.getBlocks()));
    }

    @EventHandler
    public void onLoad(ChunkLoadEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onChunkLoad(event, event.getChunk(), event.getWorld()));
    }

    @EventHandler
    public void onUnLoad(ChunkUnloadEvent event) {
        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onChunkUnLoad(event, event.getChunk(), event.getWorld()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(EntityTeleportEvent event) {

        if (event.isCancelled()) return;

        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onTeleport(event, event.getEntity()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(EntityDropItemEvent event) {

        if (event.isCancelled()) return;

        this.plugin.getListenerAdapters().forEach(adapter -> adapter.onEntityDrop(event, event.getEntity(), event.getItemDrop()));
    }

}
