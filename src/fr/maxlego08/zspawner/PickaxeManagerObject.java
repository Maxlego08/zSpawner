package fr.maxlego08.zspawner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maxlego08.zspawner.api.manager.PickaxeManager;
import fr.maxlego08.zspawner.api.manager.SpawnerManager;
import fr.maxlego08.zspawner.zcore.logger.Logger;
import fr.maxlego08.zspawner.zcore.logger.Logger.LogType;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import fr.maxlego08.zspawner.zcore.utils.builder.ItemBuilder;
import fr.maxlego08.zspawner.zcore.utils.loader.ItemStackYAMLoader;
import fr.maxlego08.zspawner.zcore.utils.loader.Loader;
import fr.maxlego08.zspawner.zcore.utils.storage.Persist;

public class PickaxeManagerObject extends ZUtils implements PickaxeManager {

	private ItemStack pickaxe;
	private SpawnerManager manager;

	public void setManager(SpawnerManager manager) {
		this.manager = manager;
	}

	@Override
	public void save(Persist persist) {

		File file = new File(plugin.getDataFolder() + File.separator + "pickaxe.yml");
		if (file.exists())
			file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

		Loader<ItemStack> loader = new ItemStackYAMLoader();
		loader.save(pickaxe, configuration, "pickaxe.");

		try {
			configuration.save(file);
			Logger.info(file.getAbsolutePath() + " save successfully !", LogType.SUCCESS);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void load(Persist persist) {

		File file = new File(plugin.getDataFolder() + File.separator + "pickaxe.yml");
		if (!file.exists()) {
			saveDefault();
			return;
		}

		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
		Loader<ItemStack> loader = new ItemStackYAMLoader();

		pickaxe = loader.load(configuration, "pickaxe.");

		Logger.info(file.getAbsolutePath() + " loaded successfully !", LogType.SUCCESS);

	}

	public void saveDefault() {

		ItemBuilder builder = new ItemBuilder(getMaterial(285));

		builder.glow();
		builder.setName("§c§kII§4 Spawner Pickaxe §c§kII");
		builder.addLine("§7§m-------------------------------------");
		builder.addLine("§f§l» §7You can collect the spawners with this pickaxe.");
		builder.addLine("§f§l» §7Use: §f%use%§7/§b%maxUse%");
		builder.addLine("§7§m-------------------------------------");

		pickaxe = builder.build();

		save(null);

	}

	@Override
	public ItemStack getPickaxe(int durabilty, int maxDurabilty) {
		ItemStack itemStack = pickaxe.clone();

		ItemMeta itemMeta = itemStack.getItemMeta();

		if (itemMeta.hasDisplayName()) {

			String name = itemMeta.getDisplayName();
			name = name.replace("%use%", String.valueOf(durabilty));
			name = name.replace("%maxUse%", String.valueOf(maxDurabilty));
			itemMeta.setDisplayName(name);

		}

		if (itemMeta.hasLore()) {

			List<String> lore = itemMeta.getLore().stream().map(name -> {
				name = name.replace("%use%", String.valueOf(durabilty));
				name = name.replace("%maxUse%", String.valueOf(maxDurabilty));
				return name;
			}).collect(Collectors.toList());
			itemMeta.setLore(lore);

		}

		itemStack.setItemMeta(itemMeta);

		itemStack = manager.getNMS().set(itemStack, KEY_DURA, durabilty);
		itemStack = manager.getNMS().set(itemStack, KEY_MAX_DURA, maxDurabilty);

		return itemStack;
	}

}
