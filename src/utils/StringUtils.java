package utils;

import org.bukkit.entity.Player;

public class StringUtils {
	
	public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String lowerInput = input.toLowerCase();
        return lowerInput.substring(0, 1).toUpperCase() + lowerInput.substring(1);
    }
	
	public static void clearChat(Player player, int lines) {
        for (int i = 0; i < lines; i++) {
            player.sendMessage(""); // Sending empty messages to clear chat lines
        }
    }
}
