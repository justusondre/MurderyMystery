package command;

import arena.Arena;
import arena.ArenaState;
import murdermystery.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaInfo implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("This command can only be used by players.");
      return true;
    } 
    Player player = (Player)sender;
    Arena arena = Main.getInstance().getArena();
    if (args.length == 0) {
      if (arena.isPlayerInArena(player)) {
        String arenaName = arena.getArenaName(player);
        int playerCount = arena.getPlayersInArena(arenaName).size();
        int aliveCount = arena.getAlivePlayersInArena(arenaName).size();
        int deadCount = arena.getDeadPlayersInArena(arenaName).size();
        ArenaState gameState = arena.getArenaState(arenaName);
        
        player.sendMessage("Arena: " + arenaName);
        player.sendMessage("Players: " + playerCount);
        player.sendMessage("Alive: " + aliveCount);
        player.sendMessage("Dead: " + deadCount);
        player.sendMessage("Game State: " + gameState);
      } else {
        player.sendMessage("You are not in an arena.");
      } 
    } else {
      player.sendMessage("Usage: /arenainfo");
    } 
    return true;
  }
}
