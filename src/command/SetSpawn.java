package command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import config.ArenaFile;
import net.md_5.bungee.api.ChatColor;
import murdermystery.Main;

public class SetSpawn implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
	    if (sender instanceof Player) {
	        if (cmd.getName().equalsIgnoreCase("setspawn")) {
	            Player player = (Player) sender;

	            if (args.length != 2) {
	                player.sendMessage(ChatColor.RED + "Wrong usage: /setspawn <arena> <number>");
	                return true;
	            }

	            try {
	                String arenaName = args[0].toLowerCase();
	                int number = Integer.parseInt(args[1]);

	            	ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

	                if (arenaFile.config.contains("instance." + arenaName)) {
	                	arenaFile.setSpawn(arenaName, number, player.getLocation());
	                    player.sendMessage(ChatColor.GRAY + "You have added spawnpoint " + ChatColor.GREEN + number + ChatColor.GRAY + " for " + ChatColor.GREEN + arenaName);
	                } else {
	                    player.sendMessage(ChatColor.RED + "The specified arena does not exist!");
	                }
	            } catch (NumberFormatException ex) {
	                player.sendMessage(ChatColor.RED + "You need to identify a number!");
	            }
	        }
	    }
	    return true;
	}
}