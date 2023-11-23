package arena;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import arena.manager.SpectateManager;
import murdermystery.Main;
import net.md_5.bungee.api.ChatColor;

public class ArenaSpectator implements Listener {
	
	private Player player;

    public ArenaSpectator(Player player) {
        this.player = player;
        player.getInventory().clear();
        makeInvisible();
        disableInteractions();
        giveCompass();
        giveLeaveBed();
    }

    public void makeInvisible() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
    }

    public void disableInteractions() {
    	
    }

    public void giveCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Spectate Players");
        compass.setItemMeta(compassMeta);

        player.getInventory().setItem(0, compass);
    }

    public void giveLeaveBed() {
        ItemStack leaveBed = new ItemStack(Material.RED_BED);
        ItemMeta bedMeta = leaveBed.getItemMeta();
        bedMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Return to Lobby");
        leaveBed.setItemMeta(bedMeta);

        player.getInventory().setItem(8, leaveBed);
    }

    public void startLeavingCountdown(int seconds) {
        Arena arena = Main.getInstance().getArena();
        new BukkitRunnable() {
            int countdown = seconds;

            @Override
            public void run() {
                if (countdown <= 0) {
                    player.sendMessage("You are leaving the arena.");
                    cancel();
                    arena.leaveArena(player);
                } else {
                    player.sendMessage("Leaving in " + countdown + " seconds...");
                    countdown--;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
}
   