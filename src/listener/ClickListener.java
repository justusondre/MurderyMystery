package listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import arena.Arena;
import arena.ArenaCountdown;
import config.ArenaFile;
import murdermystery.Main;

public class ClickListener implements Listener {
	
	ArenaCountdown countdown = Main.getInstance().getCountdownManager();
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
	    if (event.getView().getTitle().equals("Join an Arena")) {
	        event.setCancelled(true); // Prevent players from taking items from the menu

	        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
	            String arenaName = event.getCurrentItem().getItemMeta().getDisplayName();
	            Arena arena = Main.getInstance().getArena();
	            Player player = (Player) event.getWhoClicked();

	            if (!arena.isPlayerInArena(player)) {
	                ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
	                Location spawnLocation = arenaFile.getArenaLobby(arenaName);
	                if (spawnLocation != null) {
	                    arena.joinArena(player, arenaName);
	                    player.teleport(spawnLocation);
	                }
	                
	            } else {
	                player.sendMessage(ChatColor.RED + "You are already in an arena. Leave the current arena to join another one.");
	            }
	        }
	    }
	}
}
