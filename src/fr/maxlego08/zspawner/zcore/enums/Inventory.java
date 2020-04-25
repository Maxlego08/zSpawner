package fr.maxlego08.zspawner.zcore.enums;

public enum Inventory {

	INVENTORY_TEST(1),
	
	;
	
	private final int id;

	private Inventory(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
