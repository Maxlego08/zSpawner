package fr.maxlego08.spawner;

import fr.maxlego08.spawner.api.Spawner;
import fr.maxlego08.spawner.api.SpawnerType;
import fr.maxlego08.spawner.api.storage.ServerProfile;
import fr.maxlego08.spawner.api.utils.PlayerSpawner;
import fr.maxlego08.spawner.api.utils.SpawnerResult;
import fr.maxlego08.spawner.listener.ListenerAdapter;
import fr.maxlego08.spawner.save.Config;
import fr.maxlego08.spawner.stackable.StackableManager;
import fr.maxlego08.spawner.zcore.enums.Message;
import fr.maxlego08.spawner.zcore.enums.Permission;
import fr.maxlego08.spawner.zcore.logger.Logger;
import fr.maxlego08.spawner.zcore.utils.MendingUtil;
import fr.maxlego08.spawner.zcore.utils.builder.TimerBuilder;
import fr.maxlego08.spawner.zcore.utils.compatibility.FoliaCompatibilityManager;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.Directional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class SpawnerListener extends ListenerAdapter {

    private final SpawnerPlugin plugin;
    private final FoliaCompatibilityManager foliaManager;
    private final ServerProfile serverProfile;

    public SpawnerListener(SpawnerPlugin plugin, FoliaCompatibilityManager foliaManager, ServerProfile serverProfile) {
        this.plugin = plugin;
        this.foliaManager = foliaManager;
        this.serverProfile = serverProfile;
    }

    private boolean checkBlockPlaceVirtualSpawner(BlockPlaceEvent event, Block block) {
        Optional<Spawner> optional = this.serverProfile.getSpawner(SpawnerType.VIRTUAL, block.getLocation());
        if (optional.isPresent()) {
            event.setCancelled(true);
            return true;
        }

        for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation().clone().add(0.5, 1, 0.5), 0.5, 1.5, 0.5)) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;
            if (!livingEntity.getPersistentDataContainer().has(this.plugin.getSpawnerKey(), PersistentDataType.STRING))
                continue;
            if (this.serverProfile.getSpawnerByEntity(livingEntity).isEmpty()) continue;
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    @Override
    protected void onBlockPlace(BlockPlaceEvent event, Player player) {

        ItemStack itemStack = event.getItemInHand();
        EquipmentSlot equipmentSlot = event.getHand();
        Block block = event.getBlock();

        Optional<SpawnerResult> optionalSpawner = this.plugin.getManager().getSpawnerResult(itemStack);
        if (optionalSpawner.isEmpty()) return;
        SpawnerResult spawnerResult = optionalSpawner.get();

        SpawnerType spawnerType = spawnerResult.spawnerType();
        EntityType entityType = spawnerResult.entityType();

        if (checkBlockPlaceVirtualSpawner(event, block)) {
            message(this.plugin, player, Message.PLACE_ERROR_SPAWNER);
            return;
        }

        if (this.serverProfile.getSpawner(spawnerResult.spawnerId()).isPresent()) {
            message(this.plugin, player, Message.PLACE_ERROR_EXIST);
            event.setCancelled(true);
            return;
        }

        StackableManager stackableManager = this.plugin.getStackableManager();

        if (stackableManager.isEnable() && spawnerType == SpawnerType.CLASSIC) {

            Block blockAgainst = event.getBlockAgainst();
            Optional<Spawner> optional = this.serverProfile.getSpawner(blockAgainst.getLocation());

            if (optional.isPresent()) {
                Spawner spawner = optional.get();

                if (spawner.getType() == spawnerType && spawner.getEntityType() == entityType) {

                    int limit = stackableManager.getLimit(entityType);
                    if (spawner.getAmount() < limit) {

                        List<EntityType> whitelist = stackableManager.getWhitelist();
                        if (!stackableManager.getBlacklist().contains(entityType) || (!whitelist.isEmpty() && whitelist.contains(entityType))) {

                            spawner.setAmount(spawner.getAmount() + 1);
                            event.setCancelled(true);

                            if (itemStack.getAmount() > 0) itemStack.setAmount(itemStack.getAmount() - 1);
                            else player.getInventory().setItem(equipmentSlot, new ItemStack(Material.AIR));

                            return;
                        }
                    }
                }
            }
        }

        if (Config.enableLimit) {

            Chunk chunk = block.getChunk();
            if (hasSpawnerLimit(event, player, entityType, chunk)) return;
        }

        BlockFace blockFace = getCardinalDirection(player);
        Spawner spawner = new ZSpawner(plugin, spawnerResult.spawnerId(), player.getUniqueId(), spawnerType, entityType, blockFace);
        spawner.place(block.getLocation());

        serverProfile.addSpawner(spawner);

        this.foliaManager.runAsync(() -> this.plugin.getStorageManager().getOption(spawner.getSpawnerId()).ifPresent(spawner::setOption));

    }

    @Override
    protected void onBlockBreak(BlockBreakEvent event, Player player) {

        Block block = event.getBlock();

        SpawnerManager manager = this.plugin.getManager();
        PlayerSpawner playerSpawner = manager.getPlayerSpawners().get(player.getUniqueId());
        if (playerSpawner != null && playerSpawner.isPlacingSpawner()) {
            placeSpawner(player, playerSpawner, event, manager, block);
            return;
        }

        if (block.getType() == Material.SPAWNER || block.getType() == Config.virtualMaterial) {

            StackableManager stackableManager = this.plugin.getStackableManager();

            Optional<Spawner> optional = this.serverProfile.getSpawner(block.getLocation());
            if (optional.isPresent()) {
                Spawner spawner = optional.get();

                event.setCancelled(true);

                if (spawner.getType() != SpawnerType.CLASSIC) {
                    breakOther(player, spawner);
                    return;
                }

                if (Config.enableSilkSpawner) {
                    if (cantSilkSpawner(player)) {
                        spawner.disable();
                        this.serverProfile.deleteSpawner(spawner.getLocation());
                        event.setCancelled(false);
                        return;
                    }
                }

                // Le spawner ne disparait que si c'est le dernier de la pile. Tant qu'il reste
                // en place il garde son identité : recopier son UUID sur l'item lâché rendrait
                // celui-ci impossible à poser (PLACE_ERROR_EXIST).
                boolean keepSpawner = stackableManager.isEnable() && spawner.getAmount() > 1;

                block.getWorld().dropItemNaturally(block.getLocation(), this.plugin.getManager().getSpawnerItemStack(player, spawner.getType(), spawner.getEntityType(), keepSpawner ? null : spawner));

                if (keepSpawner) {
                    spawner.setAmount(spawner.getAmount() - 1);
                    return;
                }

                spawner.disable();
                block.setType(Material.AIR);
                this.serverProfile.deleteSpawner(spawner.getLocation());

                return;
            }

            // On est dans le cas d'un spawner naturel
            if (block.getType() == Material.SPAWNER && Config.enableSilkSpawner && block.getState() instanceof CreatureSpawner spawner && Config.silkNaturalSpawner) {

                if (cantSilkSpawner(player)) return;

                block.getWorld().dropItemNaturally(block.getLocation(), this.plugin.getManager().getSpawnerItemStack(player, Config.naturelSpawnerInto, spawner.getSpawnedType(), null));
            }
        }
    }

    private boolean cantSilkSpawner(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (!Config.whitelistMaterialSilkSpawner.contains(itemStack.getType())) {
            return true;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        return Config.needSilkTouchEnchant && !itemMeta.hasEnchant(Enchantment.SILK_TOUCH);
    }

    private void breakOther(Player player, Spawner spawner) {
        if (spawner.getOwner().equals(player.getUniqueId())) {

            if (spawner.getType() == SpawnerType.GUI) {

                if (Config.ownerCanBreakSpawner) {
                    spawner.breakBlock();
                    message(this.plugin, player, Message.BREAK_GUI);
                    return;
                }

                message(this.plugin, player, Message.BREAK_ERROR_GUI);
                return;
            }

            if (spawner.getType() == SpawnerType.VIRTUAL) {

                message(this.plugin, player, Message.BREAK_ERROR_VIRTUAL);
                return;
            }
        }

        message(this.plugin, player, Message.BREAK_ERROR_OTHER);
    }

    private void placeSpawner(Player player, PlayerSpawner playerSpawner, BlockBreakEvent event, SpawnerManager manager, Block block) {

        event.setCancelled(true);

        if (manager.getBlacklistMaterials().contains(block.getType())) {
            message(this.plugin, player, Message.PLACE_ERROR_BLACKLIST);
            return;
        }

        Spawner spawner = playerSpawner.getPlacingSpawner();

        if (Config.enableLimit) {

            Chunk chunk = block.getChunk();
            if (hasSpawnerLimit(event, player, spawner.getEntityType(), chunk)) return;
        }

        playerSpawner.placeSpawner();
        spawner.place(block.getLocation());
        message(this.plugin, player, Message.PLACE_SUCCESS);
    }

    @Override
    protected void onBlockExplode(List<Block> blocks) {

        if (!Config.checkSpawnerExplosion()) return;

        blocks.removeIf(block -> {

            Optional<Spawner> optional = this.serverProfile.getSpawner(block.getLocation());
            if (optional.isPresent()) {
                Spawner spawner = optional.get();
                if (spawner.getType() == SpawnerType.VIRTUAL || Config.spawnerExplosion.get(spawner.getType())) {
                    return true;
                }

                if (Config.spawnerDrop.getOrDefault(spawner.getType(), false)) {

                    spawner.breakBlock();
                    this.serverProfile.deleteSpawner(spawner);

                    block.getWorld().dropItemNaturally(block.getLocation(), this.plugin.getManager().getSpawnerItemStack(null, spawner.getType(), spawner.getEntityType(), spawner));
                    return false;
                }
            }

            if (block.getType() == Material.SPAWNER && Config.disableNaturalSpawnerExplosion) {
                return true;
            }

            if (block.getType() == Material.SPAWNER && Config.dropNaturalSpawnerOnExplose) {
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                block.getWorld().dropItemNaturally(block.getLocation(), this.plugin.getManager().getSpawnerItemStack(null, SpawnerType.CLASSIC, spawner.getSpawnedType(), null));
                return false;
            }

            return false;
        });
    }

    @Override
    protected void onPower(CreeperPowerEvent event, CreeperPowerEvent.PowerCause cause, Creeper entity, LightningStrike lightning) {

        this.serverProfile.getSpawnerByEntity(entity).ifPresent(spawner -> event.setCancelled(true));
    }

    @Override
    protected void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event, Player player, Entity rightClicked) {

        if (rightClicked instanceof LivingEntity livingEntity) {
            if (!Objects.equals(event.getHand(), EquipmentSlot.HAND)) return;
            this.serverProfile.getSpawnerByEntity(livingEntity).ifPresent(spawner -> {
                event.setCancelled(true);
                this.openVirtualSpawner(spawner, player, event);
            });
        }
    }

    @Override
    protected void onTeleport(EntityTeleportEvent event, Entity entity) {
        if (entity.getPersistentDataContainer().has(this.plugin.getSpawnerKey())) event.setCancelled(true);
    }

    @Override
    protected void onEntityDamage(EntityDamageEvent event, LivingEntity entity, EntityDamageEvent.DamageCause cause, double finalDamage) {

        this.serverProfile.getSpawnerByEntity(entity).ifPresent(spawner -> {

            if (spawner.getAmount() <= 0 || cause == EntityDamageEvent.DamageCause.SUFFOCATION || cause == EntityDamageEvent.DamageCause.LAVA || cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || cause == EntityDamageEvent.DamageCause.DRYOUT || cause == EntityDamageEvent.DamageCause.POISON || cause == EntityDamageEvent.DamageCause.MAGIC || cause == EntityDamageEvent.DamageCause.DROWNING || cause == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
                event.setCancelled(true);
                return;
            }

            if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {

                Entity damager = damageByEntityEvent.getDamager();

                if (damager instanceof Wolf || damager instanceof IronGolem) {
                    event.setCancelled(true);
                    return;
                }

                if (spawner.getOption().isLocationEnabled() && spawner.getLastLocationUser() != null) {
                    boolean isLocationActive = spawner.getLastLocationStartTime() + spawner.getLastLocationTime() > System.currentTimeMillis();

                    if (isLocationActive) {
                        if (damager instanceof Player damagerPlayer) {
                            if (!damagerPlayer.getUniqueId().equals(spawner.getLastLocationUser())) {
                                event.setCancelled(true);
                                return;
                            }
                        } else {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }

            if (entity.getHealth() - finalDamage <= 0) {

                event.setDamage(0);
                entity.setHealth(entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue());

                Class<? extends Entity> entityClass = spawner.getEntityType().getEntityClass();

                if (entityClass == null) {
                    Logger.info("Error with entity class for " + spawner.getEntityType(), Logger.LogType.ERROR);
                    return;
                }

                LivingEntity clonedEntity = (LivingEntity) entity.getWorld().spawn(entity.getLocation(), entityClass, e -> {
                    if (e instanceof LivingEntity living) {

                        living.setAI(false);
                        living.setCollidable(false);
                        living.setVisualFire(false);
                        living.setSwimming(false);
                        living.setSilent(true);
                        living.setMetadata("zspawner-death", new FixedMetadataValue(this.plugin, true));

                        if (living instanceof Slime slime) {
                            slime.setSize(1);
                        }
                    }
                });

                spawner.getDeadEntities().add(clonedEntity);

                if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {

                    Entity damager = damageByEntityEvent.getDamager();

                    if (damager instanceof Wolf || damager instanceof IronGolem) {
                        return;
                    }

                    if (damager instanceof Player) clonedEntity.setKiller((Player) damager);
                    clonedEntity.damage(entity.getHealth() * 2, damager);

                } else {

                    clonedEntity.damage(entity.getHealth() * 2);
                }

                spawner.entityDeath();
            }
        });
    }

    @Override
    protected void onEntityDeath(EntityDeathEvent event, Entity entity) {
        this.serverProfile.getSpawnerByDeadEntity(entity).ifPresent(spawner -> {

            spawner.getDeadEntities().remove(entity);

            Player killer = event.getEntity().getKiller();

            boolean isLocationActive = false;
            if (spawner.getOption().isLocationEnabled() && spawner.getLastLocationUser() != null) {
                isLocationActive = spawner.getLastLocationStartTime() + spawner.getLastLocationTime() > System.currentTimeMillis();

                if (isLocationActive) {
                    if (killer == null || !killer.getUniqueId().equals(spawner.getLastLocationUser())) {
                        event.getDrops().clear();
                        event.setDroppedExp(0);
                        return;
                    }
                }
            }

            if (spawner.getType() == SpawnerType.VIRTUAL) {
                List<ItemStack> customDrops = this.plugin.getManager().generateCustomVirtualDrops(spawner.getEntityType(), killer);
                if (!customDrops.isEmpty()) {
                    event.getDrops().clear();
                    event.getDrops().addAll(customDrops);
                }

                if (Config.givePlayerExperience) {
                    if (killer != null) {
                        int droppedExp = MendingUtil.repairAllMainHandAndArmor(killer, event.getDroppedExp());
                        if (droppedExp > 0) {
                            event.setDroppedExp(0);
                            killer.giveExp(droppedExp);
                        }
                    }
                }
            }

            List<ItemStack> itemStacks = new ArrayList<>(event.getDrops());

            /*if (spawner.isEnableAutoSell()) {

                ZPlayer player = this.plugin.getZPlayer(Bukkit.getOfflinePlayer(spawner.getUniqueId()));
                Iterator<ItemStack> iterator = itemStacks.iterator();
                while (iterator.hasNext()) {
                    ItemStack itemStack = iterator.next();
                    Optional<ShopItem> optional2 = this.plugin.getShopManager().getItem(itemStack);
                    if (optional2.isPresent()) {

                        ShopItem item = optional2.get();
                        if (item.getSellPrice(player) > 0) {

                            double price = itemStack.getAmount() * item.getSellPrice(player);
                            player.deposit(price);

                            iterator.remove();

                        }

                    }
                }
            }*/

            if (isLocationActive) {
                event.getDrops().clear();

                for (ItemStack itemStack : itemStacks) {
                    Map<Integer, ItemStack> leftover = killer.getInventory().addItem(itemStack);

                    if (!leftover.isEmpty()) {
                        for (ItemStack item : leftover.values()) {
                            killer.getWorld().dropItemNaturally(killer.getLocation(), item);
                        }
                    }
                }
            } else if (!spawner.getOption().dropLoots()) {
                if (!itemStacks.isEmpty()) {
                    spawner.addItems(itemStacks);
                }

                event.getDrops().clear();
            }
        });
    }

    @Override
    protected void onCombust(EntityCombustEvent event, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            this.serverProfile.getSpawnerByEntity(livingEntity).ifPresent(spawner -> event.setCancelled(true));
        }
    }

    @Override
    public void onPistonExtend(BlockPistonExtendEvent event, Block block, List<Block> blocks) {
        Block finalBlock = null;
        for (Block b : blocks) {

            Optional<Spawner> optional = this.serverProfile.getSpawner(b.getLocation());
            if (optional.isPresent()) {
                event.setCancelled(true);
                return;
            }
            finalBlock = b;

        }

        Directional directional = (Directional) block.getBlockData();
        BlockFace blockFace = directional.getFacing();

        if (finalBlock != null) {

            finalBlock = finalBlock.getRelative(blockFace);
            Optional<Spawner> optional = this.serverProfile.getSpawner(finalBlock.getLocation());
            if (optional.isPresent()) {
                event.setCancelled(true);
                return;
            }

        }

        block = block.getRelative(blockFace);

        Optional<Spawner> optional = this.serverProfile.getSpawner(block.getLocation());
        if (optional.isPresent()) event.setCancelled(true);
    }

    @Override
    protected void onChunkLoad(ChunkLoadEvent event, Chunk chunk, World world) {
        Collection<Spawner> spawners = this.serverProfile.getSpawners(SpawnerType.VIRTUAL);
        for (Spawner spawner : spawners) {
            if (spawner.sameChunk(chunk.getX(), chunk.getZ())) {
                spawner.load();
            }
        }
    }

    @Override
    protected void onChunkUnLoad(ChunkUnloadEvent event, Chunk chunk, World world) {
        Collection<Spawner> spawners = this.serverProfile.getSpawners(SpawnerType.VIRTUAL);
        for (Spawner spawner : spawners) {
            if (spawner.sameChunk(chunk.getX(), chunk.getZ())) {
                spawner.disable();
            }
        }
    }

    @Override
    protected void onInteract(PlayerInteractEvent event, Player player) {

        Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_BLOCK)) {

            Block block = event.getClickedBlock();
            if (block == null) return;

            if (!Objects.equals(event.getHand(), EquipmentSlot.HAND)) return;

            this.serverProfile.getSpawner(block.getLocation()).ifPresent(spawner -> {
                if (spawner.getType() == SpawnerType.VIRTUAL && action.equals(Action.RIGHT_CLICK_BLOCK)) {
                    if (plugin.getUpgradeManager().applyUpgradeItem(spawner, player, event.getItem())) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (action == Action.RIGHT_CLICK_BLOCK) {
                    this.openLocationSpawner(spawner, player, event);
                } else {
                    this.openVirtualSpawner(spawner, player, event);
                }

            });
        }
    }
    private void openLocationSpawner(Spawner spawner, Player player, Cancellable event) {
        if (spawner.getType() == SpawnerType.VIRTUAL) {

            event.setCancelled(true);
            boolean playerHasAccess = hasAccess(player, spawner);

            if (playerHasAccess) {
                this.plugin.getManager().openManageLocationSpawner(player, spawner, 1);
            } else if (spawner.getOption().isLocationEnabled()) {

                UUID lastUser = spawner.getLastLocationUser();
                long startTime = spawner.getLastLocationStartTime();
                long duration = spawner.getLastLocationTime();
                long currentTime = System.currentTimeMillis();

                boolean isLocationActive = lastUser != null && startTime + duration > currentTime;

                if (!isLocationActive) {
                    spawner.setLastLocationUser(null);
                    spawner.setLastLocationStartTime(0);
                    spawner.setLastLocationTime(0);
                    this.plugin.getManager().openPlayerLocationSpawner(player, spawner, 1);
                }
                else if (lastUser.equals(player.getUniqueId())) {
                    message(this.plugin,player, Message.SPAWNER_LOCATION_RENTED_BY_YOU.getMessage(),"%time%", TimerBuilder.formatTimeAuto((startTime + duration)- currentTime));
                } else {
                    message(this.plugin,player, Message.SPAWNER_LOCATION_ALREADY_RENTED.getMessage());
                }
            }
        }
    }

    private void openVirtualSpawner(Spawner spawner, Player player, Cancellable event) {
        if (spawner.getType() == SpawnerType.VIRTUAL) {

            event.setCancelled(true);

            if (hasAccess(player,spawner)){
                this.plugin.getManager().openVirtualSpawner(player, spawner, 1);
            }
        }
    }

    private boolean hasAccess(Player player, Spawner spawner) {
        boolean hasAccess = spawner.getOwner().equals(player.getUniqueId()) || hasPermission(player, Permission.ZSPAWNER_BYPASS);

        if (!hasAccess) {
            hasAccess = this.plugin.hasTeamAccess(spawner.getOwner(), player.getUniqueId());
        }
        return hasAccess;
    }

    private boolean hasSpawnerLimit(Cancellable event, Player player, EntityType entityType, Chunk chunk) {

        if (Config.entityLimits.containsKey(entityType)) {

            int entityLimit = Config.entityLimits.get(entityType);
            long amount = this.serverProfile.getSpawnersInChunkCount(chunk.getX(), chunk.getZ(), entityType);
            if (amount >= entityLimit) {
                event.setCancelled(true);
                message(this.plugin, player, Message.LIMIT_ENTITY, "%amount%", entityLimit, "%type%", name(entityType.name()));
                return true;
            }
        } else {

            int entityLimit = Config.globalLimit;
            long amount = this.serverProfile.getSpawnersInChunkCount(chunk.getX(), chunk.getZ());
            if (amount >= entityLimit) {
                event.setCancelled(true);
                message(this.plugin, player, Message.LIMIT_GLOBAL, "%amount%", entityLimit);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onQuit(PlayerQuitEvent event, Player player) {
        this.plugin.getManager().getPlayerSpawners().remove(player.getUniqueId());
    }

    @Override
    protected void onSlimeSplit(SlimeSplitEvent event, Slime entity) {
        this.serverProfile.getSpawner(entity.getLocation()).ifPresent(spawner -> {
            event.setCancelled(true);
            entity.setSize(1);
        });
    }

    @Override
    public void onEntityDrop(EntityDropItemEvent event, Entity entity, Item itemDrop) {

        if (entity instanceof LivingEntity living) {
            this.serverProfile.getSpawnerByEntity(living).ifPresent(spawner -> event.setCancelled(true));
        }
    }
}
