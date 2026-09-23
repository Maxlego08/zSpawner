# Unreleased

- Fix stacked spawners counting one short: a freshly placed spawner started at 0, so the second spawner placed on it was lost and a stack of 5 only reached 4
- Fix the stack level (spawnCount, delays, ranges) never being written onto the spawner block: a placed spawner kept the vanilla settings instead of the ones from `stackableSpawner.levels`
- Fix the stack level being ignored when the configuration skips amounts (levels 1, 5, 10) or when the stack goes past the last configured level
- Fix stacked spawners losing their level and their hologram after a server restart
- Fix a stack level with a missing option aborting the whole stackable configuration load
- Fix a stack level whose `minSpawnDelay` is above the block's current `maxSpawnDelay` throwing while placing the spawner
- Fix `stackableSpawner.whitelist` having no effect: any entity outside the blacklist could be stacked even when a whitelist was set
- Fix `/zspawner reload` not re-applying `stackableSpawner.levels` to the spawners already placed
- Fix every pending change being lost on shutdown: removing a hologram or a virtual mob scheduled a task while the plugin was already disabled, which aborted the final save
- Fix stacked spawner holograms not being removed when the spawner is broken
- Fix duplicated holograms after a server restart or a chunk reload
- Fix spawners taken from a stack being impossible to place back ("The spawner already exists, you can't place it")
- Fix broken classic spawners (silk touch or explosion, including the last spawner of a stack) dropping an item that kept the broken spawner's identity in a `zspawner:level` tag: it did not stack with the other spawners and was not a default spawner. Classic spawner items dropped by previous versions are now placed as new spawners
- Fix the hologram being left behind when a stacked spawner is broken without silk touch
- Fix chunks holding a virtual spawner never being able to unload
- Fix pending spawner data being silently dropped: only one save out of five was queued, and writes landing during a batch flush were discarded
- Fix deleted spawners reappearing after a restart when the deletion overlapped a batch flush
- Fix an invalid material or entity name in the config aborting the whole plugin load
- Improve performance of entity events (damage, combustion, death, drops), which no longer scan every spawner on the server
- Improve performance of auto-kill, loot storage and spawner GUI refreshes

# 4.2.2

- Fix loading of default configuration
- Fix error with default spawner option

# 4.2.1

- Update to last zMenu and Sarah version

# 4.2.0

- Various code optimization and improvement
- Fix database implementation
- Improves inventories

# 4.1.1

- Added SuperiorSkyBlock tracker support.
- Added team-based spawner management when using SuperiorSkyBlock.
- Fixed virtual spawner placement issues.
- Added an option to give experience directly to the player from virtual spawners.

# 4.1.0

- Update to Sarah 1.20. Added MARIADB support
- Fix zShop API
- Fix sell button
- Change metadata key to persistant data container
- Add virtual spawner spawn limit
- Add custom drop for virtual spawner

# 4.0.9

- Update to zMenu 1.1.0.0

# 4.0.8

- Update to last zMenu version
- Fix slime virtual spawner
- Cancel virtual chicken laying

# 4.0.7

- Improvement and correction of the spawner option system
- Added placeholders to display the options in a spawner item
- Added an information button to display the options of a spawner
- Use Sarah for database management (you can now use MYSQL)
- Fixed slime spawn

# 4.0.6

- Add zEssentials mailbox on give commands (allows you to receive items in your mailbox if you are full)

# 4.0.5

- Added Shop button, allows to sell the content of the virtual spawner (only work with zshop for the moment)
- Fix error with default spawner option
- Added blacklist materials for virtual spawner
- Player with the permission "zspawner.bypass" can now open virtual spawner

# 4.0.4

- Some fixs

# 4.0.3

- Update auto kill, if the player is online then the mob will be killed by the owner of the spawner

# 4.0.2

- Add option ``breakUpVirtualSpawner``
- Fixed the possibility of placing a block at the entity position for a virtual spawner
- Fixed the autocompletion for ``/spawners option``

# 4.0.1

- Add Silk Spawner for natural Spawner
- Fix virtual spawner with random entity spawn
- Fix squid KnockBack
- Fix entity spawn on entity kill with Virtual Spawner
