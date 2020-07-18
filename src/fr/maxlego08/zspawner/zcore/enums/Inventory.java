package fr.maxlego08.zspawner.zcore.enums;

public enum Inventory {

	INVENTORY_SPAWNER_PAGINATE(1), 
	
	INVENTORY_SPAWNER(2), 
	INVENTORY_SPAWNER_SEND(3),
	INVENTORY_SPAWNER_UPGRADE(4),
	INVENTORY_SPAWNER_CONFIG(5), 
	INVENTORY_SPAWNER_SHOW(6),

	;

	private final int id;

	private Inventory(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
