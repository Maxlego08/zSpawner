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
	
	COMMAND_SYNTAXE_ERROR("§cVous devez exécuter la commande comme ceci§7: §a%s", true),
	COMMAND_NO_PERMISSION("§cVous n'avez pas la permission d'exécuter cette commande.", true),
	COMMAND_NO_CONSOLE("§cSeul un joueur peut exécuter cette commande.", true),
	COMMAND_NO_ARG("§cImpossible de trouver la commande avec ses arguments.", true),
	COMMAND_SYNTAXE_HELP("§a%s §b» §7%s", true),
	
	
	DESCRIPTION_HELP("Show commands"),
	DESCRIPTION_RELOAD("Reload plugin"),
	DESCRIPTION_VERSION("Show plugin version"),
	DESCRIPTION_ADD("Add spawner to player"),
	DESCRIPTION_REMOVE("Remove spawner to player"),
	
	NO_SPAWNER("§cYou have no spawners !"), 
	PLACING_SPAWNER("§cYou already place a spawner!"),
	
	ADD_SPAWNER_SENDER("§eYou have just given x§f%how% §6%type% §espawner to §f%player%§e."),
	ADD_SPAWNER_RECEIVER("§eYou have just received x§f%how% §6%type% §espawner."),
	
	PLACE_SPAWNER_SUCCESS("§aYou have just placed a spawner"), 
	PLACE_SPAWNER_START("§eYou have §f1 §eminute to place the spawner in your land !"),
	
	SPAWNER_BREAK_OWNER("§eYou can §6delete §eyour spawner with the command §f/spawners§e."),
	SPAWNER_BREAK_OWNER_ERROR("§cOnly the owner of the spawner can break it."),
	
	REMOVE_SPAWNER("§bYou have removed a spawner !"), 
	
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

