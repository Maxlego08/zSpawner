package fr.maxlego08.zspawner.api.event.economy;

import fr.maxlego08.zspawner.api.event.SpawnerEvent;

public class EconomyCurrencyEvent extends SpawnerEvent {

	private String currency = "$";

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
