package fr.maxlego08.zspawner.api.enums;

import fr.maxlego08.zspawner.save.Config;

public enum Option {

	OWNER_CAN_BREAK_SPAWNER,

	GLOW_PLACE_SPAWNER, DISPLAY_INFORMATION,

	DISPLAY_REMOVE_ALL_BUTTON,

	DISABLE_MESSAGE_VERSION,

	DISABLE_SPAWNER_EXPLOSION,

	DISABLE_NATURAl_SPAWNER_EXPLOSION,

	DROP_NATURAL_SPAWNER_ON_EXPLOSION,

	DROP_SPAWNER_ON_EXPLOSION,

	SEND_MESSAGE_WHEN_SPAWNER_EXPLOSE,

	USE_SILK_PICKAXE,

	USE_SILK_PICKAXE_ON_SPAWNER,

	ALLOW_UPGRADE_SPAWNER_WITH_SHIFT_CLICK,

	AUTO_SAVE,

	DISABLE_MOB_SPAWNING,
	
	ENABLE_DEBUG_MODE,
	
	;

	public boolean getValue() {
		switch (this) {
		case ALLOW_UPGRADE_SPAWNER_WITH_SHIFT_CLICK:
			return Config.allowUpgradeSpawnerWithShiftClick;
		case AUTO_SAVE:
			return Config.autoSave;
		case DISABLE_MESSAGE_VERSION:
			return Config.disableMessageVersion;
		case DISABLE_NATURAl_SPAWNER_EXPLOSION:
			return Config.disableNaturalSpawnerExplosion;
		case DISABLE_SPAWNER_EXPLOSION:
			return Config.disableSpawnerExplosion;
		case DISPLAY_INFORMATION:
			return Config.displayInformation;
		case DISPLAY_REMOVE_ALL_BUTTON:
			return Config.displayRemoveAllButton;
		case DROP_NATURAL_SPAWNER_ON_EXPLOSION:
			return Config.dropNaturalSpawnerOnExplose;
		case DROP_SPAWNER_ON_EXPLOSION:
			return Config.dropSpawnerOnExplose;
		case GLOW_PLACE_SPAWNER:
			return Config.glowPlaceSpawner;
		case OWNER_CAN_BREAK_SPAWNER:
			return Config.ownerCanBreakSpawner;
		case SEND_MESSAGE_WHEN_SPAWNER_EXPLOSE:
			return Config.sendMessageWhenSpawnerExplose;
		case USE_SILK_PICKAXE:
			return Config.useSilkPickaxe;
		case USE_SILK_PICKAXE_ON_SPAWNER:
			return Config.useSilkPickaxeOnPlayerSpawner;
		case DISABLE_MOB_SPAWNING:
			return Config.disableMobSpawning;
		case ENABLE_DEBUG_MODE:
			return Config.enableDebugMode;
		default:
			return false;
		}
	}

	public void setValue(boolean value) {
		switch (this) {
		case ALLOW_UPGRADE_SPAWNER_WITH_SHIFT_CLICK:
			Config.allowUpgradeSpawnerWithShiftClick = value;
			break;
		case AUTO_SAVE:
			Config.autoSave = value;
			break;
		case DISABLE_MESSAGE_VERSION:
			Config.disableMessageVersion = value;
			break;
		case DISABLE_NATURAl_SPAWNER_EXPLOSION:
			Config.disableNaturalSpawnerExplosion = value;
			break;
		case DISABLE_SPAWNER_EXPLOSION:
			Config.disableSpawnerExplosion = value;
			break;
		case DISPLAY_INFORMATION:
			Config.displayInformation = value;
			break;
		case DISPLAY_REMOVE_ALL_BUTTON:
			Config.displayRemoveAllButton = value;
			break;
		case DROP_NATURAL_SPAWNER_ON_EXPLOSION:
			Config.dropNaturalSpawnerOnExplose = value;
			break;
		case DROP_SPAWNER_ON_EXPLOSION:
			Config.dropSpawnerOnExplose = value;
			break;
		case GLOW_PLACE_SPAWNER:
			Config.glowPlaceSpawner = value;
			break;
		case OWNER_CAN_BREAK_SPAWNER:
			Config.ownerCanBreakSpawner = value;
			break;
		case SEND_MESSAGE_WHEN_SPAWNER_EXPLOSE:
			Config.sendMessageWhenSpawnerExplose = value;
			break;
		case USE_SILK_PICKAXE:
			Config.useSilkPickaxe = value;
			break;
		case USE_SILK_PICKAXE_ON_SPAWNER:
			Config.useSilkPickaxeOnPlayerSpawner = value;
			break;
		case DISABLE_MOB_SPAWNING:
			Config.disableMobSpawning = value;
			break;
		case ENABLE_DEBUG_MODE:
			Config.enableDebugMode = value;
			break;
		default:
			break;
		}
	}

}
