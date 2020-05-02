package fr.maxlego08.zspawner.zcore.enums;

public enum Permission {
	
	ZSPAWNER_USE, ZSPAWNER_HELP, ZSPAWNER_ADD, ZSPAWNER_RELOAD, ZSPAWNER_REMOVE, ZSPAWNER_GIVE, ZSPAWNER_REMOVE_ALL, ZSPAWNER_INFO, ZSPAWNER_SEND,

	;

	private String permission;

	private Permission() {
		this.permission = this.name().toLowerCase().replace("_", ".");
	}

	public String getPermission() {
		return permission;
	}

}
