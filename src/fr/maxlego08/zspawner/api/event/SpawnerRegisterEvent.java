/**
 * 
 */
package fr.maxlego08.zspawner.api.event;

import fr.maxlego08.zspawner.api.FactionListener;

/**
 * @author Maxlego08 Permet de register le faction listener
 */
public class SpawnerRegisterEvent extends SpawnerEvent {

	private FactionListener factionListener;

	public SpawnerRegisterEvent(FactionListener factionListener) {
		super();
		this.factionListener = factionListener;
	}

	/**
	 * @return the factionListener
	 */
	public FactionListener getFactionListener() {
		return factionListener;
	}

	/**
	 * @param factionListener
	 *            the factionListener to set
	 */
	public void setFactionListener(FactionListener factionListener) {
		this.factionListener = factionListener;
	}

}
