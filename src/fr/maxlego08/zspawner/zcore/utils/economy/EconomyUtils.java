package fr.maxlego08.zspawner.zcore.utils.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.maxlego08.zspawner.api.event.economy.EconomyDepositEvent;
import fr.maxlego08.zspawner.api.event.economy.EconomyHasMoneyEvent;
import fr.maxlego08.zspawner.api.event.economy.EconomyWithdrawMoney;
import fr.maxlego08.zspawner.zcore.ZPlugin;
import fr.maxlego08.zspawner.zcore.utils.ZUtils;
import me.bukkit.mTokens.Inkzzz.Tokens;
import me.realized.tokenmanager.api.TokenManager;

public abstract class EconomyUtils extends ZUtils {

	/**
	 * @param economy
	 * @param player
	 * @param price
	 * @return
	 */
	protected boolean hasMoney(Economy economy, Player player, double price) {
		switch (economy) {
		case MYSQLTOKEN:
			return Tokens.getInstance().getAPI().getTokens(player) >= price;
		case TOKENMANAGER:
			TokenManager tokenManager = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");
			return tokenManager.getTokens(player).getAsLong() >= price;
		case PLAYERPOINT:
			return ZPlugin.z().getPlayerPointsAPI().look(player.getUniqueId()) >= (int) price;
		case VAULT:
			return super.getBalance(player) >= price;
		case CUSTOM:
			EconomyHasMoneyEvent event = new EconomyHasMoneyEvent(player, price);
			Bukkit.getPluginManager().callEvent(event);
			return event.hasMoney();
		default:
			return false;
		}
	}

	/**
	 * @param economy
	 * @param player
	 * @param value
	 */
	protected void depositMoney(Economy economy, Player player, double value) {
		switch (economy) {
		case MYSQLTOKEN:
			Tokens.getInstance().getAPI().giveTokens(player, (int) value);
			break;
		case TOKENMANAGER:
			TokenManager tokenManager = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");
			tokenManager.addTokens(player, (long) value);
			break;
		case PLAYERPOINT:
			ZPlugin.z().getPlayerPointsAPI().give(player.getUniqueId(), (int) value);
			break;
		case VAULT:
			super.depositMoney(player, value);
			break;
		case CUSTOM:
			EconomyDepositEvent event = new EconomyDepositEvent(player, value);
			Bukkit.getPluginManager().callEvent(event);
			break;
		default:
			break;
		}
	}

	/**
	 * @param economy
	 * @param player
	 * @param value
	 */
	protected void withdrawMoney(Economy economy, Player player, double value) {
		switch (economy) {
		case MYSQLTOKEN:
			Tokens.getInstance().getAPI().takeTokens(player, (int) value);
			break;
		case TOKENMANAGER:
			TokenManager tokenManager = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");
			tokenManager.removeTokens(player, (long) value);
			break;
		case PLAYERPOINT:
			ZPlugin.z().getPlayerPointsAPI().take(player.getUniqueId(), (int) value);
			break;
		case VAULT:
			super.withdrawMoney(player, value);
			break;
		case CUSTOM:
			EconomyWithdrawMoney event = new EconomyWithdrawMoney(player, value);
			Bukkit.getPluginManager().callEvent(event);
			break;
		default:
			break;
		}
	}

}
