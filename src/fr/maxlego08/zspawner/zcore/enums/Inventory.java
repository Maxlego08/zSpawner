package fr.maxlego08.zspawner.zcore.enums;

public enum Inventory {

	INVENTORY_SPAWNER_PAGINATE(1),
	INVENTORY_SPAWNER(2),
	
	;
	
	private final int id;

	private Inventory(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
