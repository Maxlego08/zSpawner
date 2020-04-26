package fr.maxlego08.zspawner;
import fr.maxlego08.zspawner.api.SpawnerManager;
import fr.maxlego08.zspawner.listener.ListenerAdapter;

public class SpawnerListener extends ListenerAdapter {

	private final SpawnerManager manager;

	public SpawnerListener(SpawnerManager manager) {
		super();
		this.manager = manager;
	}


}
