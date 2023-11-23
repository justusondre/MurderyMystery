package arena;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ArenaListener implements Listener {
	
	@EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE && event.getTo().getY() < event.getFrom().getY()) {
            player.setFallDistance(0);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM &&
                !isHoldingWeapon(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null &&
            (event.getClickedBlock().getType() == Material.IRON_BLOCK ||
             event.getClickedBlock().getType() == Material.IRON_DOOR)) {
            if (event.getHand() == EquipmentSlot.HAND) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isHoldingWeapon(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        return itemInHand != null &&
               (itemInHand.getType() == Material.IRON_SWORD ||
                itemInHand.getType() == Material.DIAMOND_SWORD ||
                itemInHand.getType() == Material.BOW);
    }
    
    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }
}
