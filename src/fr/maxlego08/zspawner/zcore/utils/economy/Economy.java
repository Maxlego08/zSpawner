package fr.maxlego08.zspawner.zcore.utils.economy;

import org.bukkit.Bukkit;

import fr.maxlego08.zspawner.api.event.economy.EconomyCurrencyEvent;

public enum Economy {

	VAULT, PLAYERPOINT, TOKENMANAGER, MYSQLTOKEN, CUSTOM,

	;

	public static Economy getOrDefault(String string, Economy eco) {
		for (Economy economy : values())
			if (string.equalsIgnoreCase(economy.name()))
				return economy;
		return eco;
	}

	public String toCurrency() {
		switch (this) {
		case PLAYERPOINT:
			return "P";
		case VAULT:
			return "$";
		case TOKENMANAGER:
			return "T";
		case MYSQLTOKEN:
			return "M";
		case CUSTOM:
			EconomyCurrencyEvent event = new EconomyCurrencyEvent();
			Bukkit.getPluginManager().callEvent(event);
			return event.getCurrency();
		default:
			return "$";
		}
	}

}
