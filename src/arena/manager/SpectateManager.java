package arena.manager;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import arena.Arena;
import murdermystery.Main;

public class SpectateManager implements Listener {
	
	private int currentPage = 0; // Track the current page
	
	@SuppressWarnings("deprecation")
	public void openSpectateGUI(Player player) {
	    Inventory gui = Bukkit.createInventory(null, 6 * 9, "Spectate Menu (Page " + (currentPage + 1) + ")");

	    Arena arena = Main.getInstance().getArena();
	    List<Player> alivePlayers = arena.getAlivePlayersInArena(arena.getArenaName(player)); // Use getArenaName here

	    int[] slotsToPopulate = {10, 11, 12, 13, 14, 15, 16,
	                             19, 20, 21, 22, 23, 24, 25,
	                             28, 29, 30, 31, 32, 33, 34};

	    for (int i = 0; i < slotsToPopulate.length; i++) {
	        int slot = slotsToPopulate[i];
	        if (i < alivePlayers.size()) {
	            Player onlinePlayer = alivePlayers.get(i);

	            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
	            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
	            skullMeta.setOwningPlayer(onlinePlayer);
	            skullMeta.setDisplayName(ChatColor.YELLOW + "Spectate " + onlinePlayer.getName()); // Set display name
	            skull.setItemMeta(skullMeta);
	            gui.setItem(slot, skull);
	        } else {
	            gui.setItem(slot, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
	        }
	    }

        // Add arrow heads
        ItemStack arrowLeft = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta arrowLeftMeta = (SkullMeta) arrowLeft.getItemMeta();
        arrowLeftMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowLeft"));
        arrowLeftMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
        arrowLeft.setItemMeta(arrowLeftMeta);
        gui.setItem(47, arrowLeft);

        ItemStack arrowRight = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta arrowRightMeta = (SkullMeta) arrowRight.getItemMeta();
        arrowRightMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowRight"));
        arrowRightMeta.setDisplayName(ChatColor.GREEN + "Next Page");
        arrowRight.setItemMeta(arrowRightMeta);
        gui.setItem(51, arrowRight);

        // Add close button
        ItemStack closeBarrier = new ItemStack(Material.BARRIER);
        ItemMeta closeBarrierMeta = closeBarrier.getItemMeta();
        closeBarrierMeta.setDisplayName(ChatColor.RED + "Close Menu");
        closeBarrier.setItemMeta(closeBarrierMeta);
        gui.setItem(49, closeBarrier);

        player.openInventory(gui);
    }

	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("Spectate Menu")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null) {
                if (clickedItem.getType() == Material.BARRIER) {
                    // Implement close button logic
                    player.closeInventory();
                } else if (clickedItem.getType() == Material.PLAYER_HEAD) {
                    if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous Page")) {
                        if (currentPage > 0) {
                            currentPage--;
                            openSpectateGUI(player);
                        }

                    } else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next Page")) {
                        int totalPages = (int) Math.ceil((double) Bukkit.getOnlinePlayers().size() / 21);
                        if (currentPage < totalPages - 1) {
                            currentPage++;
                            openSpectateGUI(player);
                        }
                    } else {
                        // Spectate the clicked player
                        String playerName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()); // Remove color codes
                        Player spectatedPlayer = Bukkit.getPlayerExact(playerName);

                        if (spectatedPlayer != null) {
                            player.teleport(spectatedPlayer); // Teleport the spectator to the selected player
                            player.closeInventory(); // Close the spectate menu
                        } else {
                            player.sendMessage(ChatColor.RED + "Player not found or offline.");
                        }
                    }
                }
            }
        }
    }
	
	@EventHandler
    public void onCompassRightClick(PlayerInteractEvent event) {
        if (event.getAction().toString().contains("RIGHT")) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand(); // You can change this to the off hand if needed
            if (item != null && item.getType() == Material.COMPASS && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("Spectate Players")) {
                // Open the spectate menu
                openSpectateGUI(player);
            }
        }
    }
}