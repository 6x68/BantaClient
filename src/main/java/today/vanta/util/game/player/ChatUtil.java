package today.vanta.util.game.player;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import today.vanta.util.game.IMinecraft;

import java.util.regex.Matcher;

public class ChatUtil implements IMinecraft {
    private static void addChatMessage(IChatComponent component, int line) {
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(component, line);
    }

    public static void send(Prefix prefix, String message, Object... args) {
        for (Object arg : args) {
            message = message.replaceFirst("\\{}",
                    Matcher.quoteReplacement(String.valueOf(arg)));
        }

        message = prefix.prefix + message;
        message = message.replace('&', '§');

        addChatMessage(new ChatComponentText(message), 1);
    }

    public static void info(String message, Object... args) {
        send(Prefix.INFO, message, args);
    }

    public static void error(String message, Object... args) {
        send(Prefix.ERROR, message, args);
    }

    public static void warn(String message, Object... args) {
        send(Prefix.WARNING, message, args);
    }

    public enum Prefix {
        INFO("&dVanta &7:: &f"),
        ERROR("&cVanta &7:: &f"),
        WARNING("&eVanta &7:: &f");

        public final String prefix;

        Prefix(String prefix) {
            this.prefix = prefix;
        }
    }
}