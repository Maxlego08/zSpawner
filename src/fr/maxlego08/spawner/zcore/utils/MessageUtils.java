package fr.maxlego08.spawner.zcore.utils;

import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.spawner.SpawnerPlugin;
import fr.maxlego08.spawner.zcore.enums.Message;
import fr.maxlego08.spawner.zcore.utils.nms.NMSUtils;
import fr.maxlego08.spawner.zcore.utils.players.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Allows you to manage messages sent to players and the console
 *
 * @author Maxence
 */
public abstract class MessageUtils extends LocationUtils {

    private final transient static int CENTER_PX = 154;

    public static String removeColorCodes(String input) {
        input = input.replaceAll("#[0-9a-fA-F]{6}", "");
        input = input.replaceAll("§[0-9a-fA-Fk-oK-OrR]", "");
        return input.replaceAll("&[0-9a-fA-Fk-oK-OrR]", "");
    }

    /**
     * @param player
     * @param message
     * @param args
     */
    protected void messageWO(CommandSender player, Message message, Object... args) {
        player.sendMessage(getMessage(message, args));
    }

    public void messageWO(SpawnerPlugin plugin, CommandSender sender, Message message, Object... args) {
        MetaUpdater updater = plugin.getInventoryManager().getMeta();

        if (sender instanceof ConsoleCommandSender) {
            if (message.getMessages().size() > 0) {
                message.getMessages().forEach(msg -> sender.sendMessage(removeColorCodes(Message.PREFIX.msg() + getMessage(msg, args))));
            } else {
                sender.sendMessage(removeColorCodes(Message.PREFIX.msg() + getMessage(message, args)));
            }
        } else {
            Player player = (Player) sender;
            if (message.getMessages().size() > 0) {
                message.getMessages().forEach(msg -> updater.sendMessage(sender, this.papi(getMessage(msg, args), player)));
            } else {
                updater.sendMessage(sender, this.papi(getMessage(message, args), player));
            }
        }
    }

    /**
     * @param sender
     * @param message
     * @param args
     */
    protected void message(CommandSender sender, String message, Object... args) {
        sender.sendMessage(Message.PREFIX.msg() + getMessage(message, args));
    }

    /**
     * @param sender
     * @param message
     * @param args
     */
    public void message(SpawnerPlugin plugin, CommandSender sender, String message, Object... args) {
        plugin.getInventoryManager().getMeta().sendMessage(sender, Message.PREFIX.msg() + getMessage(message, args));
    }

    /**
     * Allows you to send a message to a command sender
     *
     * @param sender  User who sent the command
     * @param message The message - Using the Message enum for simplified message
     *                management
     * @param args    The arguments - The arguments work in pairs, you must put for
     *                example %test% and then the value
     */
    public void message(SpawnerPlugin plugin, CommandSender sender, Message message, Object... args) {

        MetaUpdater updater = plugin.getInventoryManager().getMeta();

        if (sender instanceof ConsoleCommandSender) {
            if (message.getMessages().size() > 0) {
                message.getMessages().forEach(msg -> sender.sendMessage(removeColorCodes(Message.PREFIX.msg() + getMessage(msg, args))));
            } else {
                sender.sendMessage(removeColorCodes(Message.PREFIX.msg() + getMessage(message, args)));
            }
        } else {

            Player player = (Player) sender;
            switch (message.getType()) {
                case CENTER:
                    if (message.getMessages().size() > 0) {
                        message.getMessages().forEach(msg -> updater.sendMessage(sender, this.getCenteredMessage(this.papi(getMessage(msg, args), player))));
                    } else {
                        updater.sendMessage(sender, this.getCenteredMessage(this.papi(getMessage(message, args), player)));
                    }

                    break;
                case ACTION:
                    this.actionMessage(player, message, args);
                    break;
                case TCHAT:
                    if (message.getMessages().size() > 0) {
                        message.getMessages().forEach(msg -> updater.sendMessage(sender, this.papi(Message.PREFIX.msg() + getMessage(msg, args), player)));
                    } else {
                        updater.sendMessage(sender, this.papi(Message.PREFIX.msg() + getMessage(message, args), player));
                    }
                    break;
                case TITLE:
                    // title message management
                    String title = message.getTitle();
                    String subTitle = message.getSubTitle();
                    int fadeInTime = message.getStart();
                    int showTime = message.getTime();
                    int fadeOutTime = message.getEnd();
                    this.title(player, this.papi(this.getMessage(title, args), player), this.papi(this.getMessage(subTitle, args), player), fadeInTime, showTime, fadeOutTime);
                    break;
                default:
                    break;

            }

        }
    }

    /**
     * @param player
     * @param message
     * @param args
     */
    protected void actionMessage(Player player, Message message, Object... args) {
        ActionBar.sendActionBar(player, this.papi(getMessage(message, args), player));
    }

    protected String getMessage(Message message, Object... args) {
        return getMessage(message.getMessage(), args);
    }

    protected String getMessage(String message, Object... args) {

        if (args.length % 2 != 0)
            throw new IllegalArgumentException("Number of invalid arguments. Arguments must be in pairs.");

        for (int i = 0; i < args.length; i += 2) {
            if (args[i] == null || args[i + 1] == null)
                throw new IllegalArgumentException("Keys and replacement values must not be null.");
            message = message.replace(args[i].toString(), args[i + 1].toString());
        }
        return message;
    }

    protected final Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send title to player
     *
     * @param player
     * @param title
     * @param subtitle
     * @param fadeInTime
     * @param showTime
     * @param fadeOutTime
     */
    protected void title(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {

        if (NmsVersion.nmsVersion.isNewMaterial()) {
            player.sendTitle(title, subtitle, fadeInTime, showTime, fadeOutTime);
            return;
        }

        try {
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + title + "\"}");
            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    fadeInTime, showTime, fadeOutTime);

            Object chatsTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + subtitle + "\"}");
            Constructor<?> timingTitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object timingPacket = timingTitleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null),
                    chatsTitle, fadeInTime, showTime, fadeOutTime);

            sendPacket(player, packet);
            sendPacket(player, timingPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param player
     * @param packet
     */
    protected final void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param message
     * @return message
     */
    protected String getCenteredMessage(String message) {
        if (message == null || message.equals(""))
            return "";
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                } else
                    isBold = false;
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString() + message;
    }

    protected void broadcastAction(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ActionBar.sendActionBar(player, papi(message, player));
        }
    }

}
