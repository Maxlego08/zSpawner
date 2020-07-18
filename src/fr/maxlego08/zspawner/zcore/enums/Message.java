package fr.maxlego08.zspawner.zcore.enums;

public enum Message {

	PREFIX("§8(§6zSpawner§8)", true),
	PREFIX_END("§8(§6zSpawner§8)", false),
	
	TELEPORT_MOVE("§cVous ne devez pas bouger !", false),
	TELEPORT_MESSAGE("§7Téléportatio dans §3%s §7secondes !", false),
	TELEPORT_ERROR("§cVous avez déjà une téléportation en cours !", false),
	TELEPORT_SUCCESS("§7Téléportation effectué !", false),
	
	INVENTORY_NULL("§cImpossible de trouver l'inventaire avec l'id §6%s§c.", false),
	INVENTORY_CLONE_NULL("§cLe clone de l'inventaire est null !", false),
	INVENTORY_OPEN_ERROR("§cUne erreur est survenu avec l'ouverture de l'inventaire §6%s§c.", false),
	INVENTORY_BUTTON_PREVIOUS("§f» §7Page précédente", false),
	INVENTORY_BUTTON_NEXT("§f» §7Page suivante", false),
	
	TIME_DAY("%02d jour(s) %02d heure(s) %02d minute(s) %02d seconde(s)", true),
	TIME_HOUR("%02d heure(s) %02d minute(s) %02d seconde(s)", true),
	TIME_HOUR_SIMPLE("%02d:%02d:%02d", true),
	TIME_MINUTE("%02d minute(s) %02d seconde(s)", true),
	TIME_SECOND("%02d seconde(s)", true),
	
	COMMAND_SYNTAXE_ERROR("§cYou must execute the command like this§7: §2%command%"),
	COMMAND_NO_PERMISSION("§cYou don't have permission !"),
	COMMAND_NO_CONSOLE("§cOnly player can execute this command"),
	COMMAND_NO_ARG("§cThis argument does not exist!"),
	COMMAND_SYNTAXE_HELP("§f%s §7» §b%s"),
	
	
	DESCRIPTION_HELP("Show commands"),
	DESCRIPTION_RELOAD("Reload plugin"),
	DESCRIPTION_VERSION("Show plugin version"),
	DESCRIPTION_ADD("Add spawner to player"),
	DESCRIPTION_GIVE("Give spawner to player"),
	DESCRIPTION_REMOVE("Remove spawner to player"),
	DESCRIPTION_REMOVE_ALL("Remove all spawners to player"),
	DESCRIPTION_INFO("See the spawner information that you pointed"),
	DESCRIPTION_SEND("Send spawner to player"),
	DESCRIPTION_LEVEL("Show level commands"),
	DESCRIPTION_LEVEL_CREATE("Create a new level"),
	DESCRIPTION_LEVEL_EDIT("Edit value for a level"),
	DESCRIPTION_LEVEL_SHOW("Show information about a level"),
	DESCRIPTION_LEVEL_EDIT_MIN_DELAY("Change min delay for spawn (in tick)"),
	DESCRIPTION_LEVEL_EDIT_MAX_DELAY("Change max delay for spawn (in tick)"),
	DESCRIPTION_LEVEL_EDIT_SPAWN_COUNT("Change spawn count"),
	DESCRIPTION_LEVEL_EDIT_SPAWN_RANGE("Change spawn range"),
	DESCRIPTION_LEVEL_EDIT_MAX_ENTITIES("Change entity limit for spawn"),
	DESCRIPTION_LEVEL_EDIT_PLAYER("Change required player range"),
	DESCRIPTION_PICKAXE("Give pickaxe to player"),
	DESCRIPTION_HEADER("§8[§b?§8] §7Commands lists:"),
	DESCRIPTION_CONFIG("Config plugin in game"),
	DESCRIPTION_SHOW("Show player spawner"),
	
	NO_SPAWNER("§cYou have no spawners !"), 
	NO_SPAWNER_TARGET("§f%s §chave no spawners !"), 
	PLACING_SPAWNER("§cYou already place a spawner!"),
	PLACE_SPAWNER("§eYou just placed a §f%type% §espawner§e."),
	
	ADD_SPAWNER_SENDER("§eYou have just given x§f%how% §6%type% §espawner to §f%player%§e."),
	ADD_SPAWNER_RECEIVER("§eYou have just received x§f%how% §6%type% §espawner §8(§f/spawners§8)"),
	
	GIVE_SPAWNER_SENDER("§eYou have just given x§f%how% §6%type% §espawner to §f%player%§e."),
	GIVE_SPAWNER_RECEIVER("§eYou have just received x§f%how% §6%type% §espawner."),
	
	REMOVE_SPAWNER_SENDER("§eYou have just remove x§f%how% §6%type% §espawner to §f%player%§e."),
	REMOVE_SPAWNER_RECEIVER("§eYou have just lost x§f%how% §6%type% §espawner."),
	
	REMOVE_ALL_SPAWNER_SENDER("§eYou have just remove §fall §espawners to §f%player%§e."),
	REMOVE_ALL_SPAWNER_RECEIVER("§eyou just lost §fall §eyour spawners."),
	
	SEND_SPAWNER_PLAYER("§eYou just gave §fx1 §6%type% §eto §6%player%§e."),
	SEND_SPAWNER_ERROR("§cYou can't send a spawner to yourself."),
	SEND_SPAWNER_RECEIVER("§eYou have just received x§f1 §6%type% §espawner from §f%sender% §8(§f/spawners§8)"),
	
	PLACE_SPAWNER_SUCCESS("§aYou have just placed a spawner"), 
	PLACE_SPAWNER_START("§eYou have §f1 §eminute to place the spawner in your land !"),
	PLACE_SPAWNER_ERROR("§cYou do not have permission to place the spawner here."),
	PLACE_SPAWNER_ERROR_BLACKLIST("§cYou cannot place a spawner on this block"),
	PLACE_SPAWNER_ERROR_LIMIT("§cYou cannot place more than §f16 §cspawners in a chunk."),
	
	SPAWNER_BREAK_OWNER("§eYou can §6delete §eyour spawner with the command §f/spawners§e."),
	SPAWNER_BREAK_OWNER_ERROR("§cOnly the owner of the spawner can break it."),
	
	SPAWNER_BREAK_EXPLODE("§cYour spawner just explode ! you loose it !"),
	REMOVE_SPAWNER("§eYou have removed a spawner !"), 
	INFO_ERROR("§cYou must watch a block!"), 
	INFO_ERROR_BOARD("§cThis spawner is natural!"), 

	LEVEL_ERROR("§clevel §f%s §cdoesn't exists."),
	LEVEL_CREATE_ERROR("§clevel §f%s §calready exists."),
	LEVEL_CREATE_SUCCESS("§eYou just create level §6%s§e."),
	LEVEL_UPDATE("§eYou just update level §6%s§e."),	
	
	GIVE_PICKAXE_SENDER("§eYou have just given x§f1 §ePickaxe to §f%player%§e."),
	GIVE_PICKAXE_RECEIVER("§eYou have just received x§f1 §ePickaxe."), 
	SPAWNER_UPGRADE_ERROR("§cYou can upgrade your spawners."),
	SPAWNER_UPGRADE_ERROR_MONEY("§cYou don't have enought money."),
	SPAWNER_UPGRADE_SUCCESS("§eYou have just improved your spawner to level §f%level%§e."),
	
	;

	private String message;
	private boolean use = true;

	private Message(String message) {
		this.message = message;
		this.use = true;
	}

	private Message(String message, boolean use) {
		this.message = message;
		this.use = use;
	}

	public String getMessage() {
		return message;
	}

	public String toMsg() {
		return message;
	}

	public String msg() {
		return message;
	}
	public boolean isUse() {
		return use;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}

