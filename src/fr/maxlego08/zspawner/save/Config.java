package fr.maxlego08.zspawner.save;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import fr.maxlego08.zspawner.api.enums.InventoryType;
import fr.maxlego08.zspawner.zcore.utils.inventory.Button;
import fr.maxlego08.zspawner.zcore.utils.storage.Persist;
import fr.maxlego08.zspawner.zcore.utils.storage.Saveable;

public class Config implements Saveable {

	public transient static InventoryType type = InventoryType.PAGINATE;
	public static int inventorySize = 54;
	
	public static boolean ownerCanBreakSpawner = false;
	public static boolean glowPlaceSpawner = true;
	public static boolean displayInformation = true;
	public static boolean displayRemoveAllButton = true;
	
	public static String inventoryName = "§ezSpawner §6%p%§7/§6%mp%";
	
	public static Button buttonPrevious = new Button(48, "§6» §ePrevious", Material.ARROW);
	public static Button buttonNext = new Button(50, "§6» §eNext", Material.ARROW);
	
	public static Button buttonInformation = new Button(49, Material.NETHER_STAR, "§eInformations",

			"§7§m-------------------------------------", 
			"§f§l» §7Welcome to the spawners inventory",
			"§f§l» §7all your spawners are stored here", 
			"§f§l» §7so you can manage them easily.", 
			"",
			"§f§l» §7To place a spawner just click on",
			"§f§l» §7a §bspawner §7that is not placed and then to §fbreak a block§7.", 
			"§f§l» §7To remove a spawner you must click on a spawner placed", 
			"",
			"§f§l» §7You have §b%spawners% §7spawners",
			"§f§l» §7Sort type§7: §b%sorting%",
			"§f§l» §7Click to §bchange §7the sort type", 
			"§7§m-------------------------------------");
	
	public static Button buttonRemoveAll = new Button(45, Material.ENDER_CHEST, "§eRemove all",
			
			"§7§m-------------------------------------", 
			"§f§l» §7Click here to remove all your spawners",
			"§7§m-------------------------------------");
	
	
	public static List<String> infos = new ArrayList<>();
	
	static {
		
		infos.add("§7§m--------------------------------------");
		infos.add("§8[§6!§8] §eInformation about your spawner");
		infos.add("  §6* §eType§7: §6%type%");
		infos.add("  §6* §eLocation§7: §6%location%");
		infos.add("§7§m--------------------------------------");
		
	}
	
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

	public void save(Persist persist) {
		persist.save(getInstance());
	}

	public void load(Persist persist) {
		persist.loadOrSaveDefault(getInstance(), Config.class);
	}

}
