package fr.maxlego08.zspawner.zcore.enums;

public enum Permission {
	
	ZSPAWNER_USE, ZSPAWNER_HELP, ZSPAWNER_ADD, ZSPAWNER_RELOAD,

	;

	private String permission;

	private Permission() {
		this.permission = this.name().toLowerCase().replace("_", ".");
	}

	public String getPermission() {
		return permission;
	}

}
