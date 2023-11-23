package command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import arena.Arena;
import arena.ArenaCountdown;
import config.ArenaFile;
import murdermystery.Main;

public class JoinArena implements CommandExecutor {
	
	ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
	ArenaCountdown countdownManager = Main.getInstance().getCountdownManager();
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
	    if (sender instanceof Player) {
	        Player player = (Player) sender;
	    	Arena arena = Main.getInstance().getArena();
	        
	    	if (cmd.getName().equalsIgnoreCase("joinarena")) {
	    	    if (args.length != 1) {
	    	        player.sendMessage("Usage: /joinarena <arena>");
	    	        return true;
	    	    }

	    	    String arenaName = args[0].toLowerCase();

	    	    List<String> arenaNames = arenaFile.getArenasList();
	    	    if (!arenaNames.contains(arenaName)) {
	    	        player.sendMessage(ChatColor.RED + "The specified arena does not exist.");
	    	        return true;
	    	    }

	    	    if (arena.isPlayerInArena(player)) {
	    	        player.sendMessage(ChatColor.RED + "You are already in an arena. Leave the current arena to join another one.");
	    	    } else {
	    	        arena.joinArena(player, arenaName);
	    	        
	    	    }
	            
	        } else if (cmd.getName().equalsIgnoreCase("leavearena")) {
	            if (arena.isPlayerInArena(player)) {
	            	arena.leaveArena(player);
	            } else {
	                player.sendMessage(ChatColor.RED + "You are not in any arena.");
	            }
	        }
	    }
	    return true;
	}
}