package command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import config.ArenaFile;
import murdermystery.Main;

public class SetGoldSpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("setgoldspawn")) {
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Wrong usage: /setgoldspawn <arena> <number>");
                    return true;
                }

                try {
                    String arenaName = args[0].toLowerCase();
                    int number = Integer.parseInt(args[1]);

                    ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

                    if (arenaFile.config.contains("instance." + arenaName)) {
                        // Get the player's current location as the gold spawn
                        Location goldSpawnLocation = player.getLocation();
                        arenaFile.setGoldSpawn(arenaName, number, goldSpawnLocation);
                        player.sendMessage(ChatColor.GRAY + "You have set the gold spawnpoint " + ChatColor.GREEN + number + ChatColor.GRAY + " for " + ChatColor.GREEN + arenaName);
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