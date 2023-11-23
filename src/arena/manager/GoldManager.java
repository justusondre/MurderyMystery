package arena.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import config.ArenaFile;
import murdermystery.Main;

public class GoldManager {
	
	private ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

    public void spawnGoldAtLocations(String arenaName, boolean allAtOnce) {
        List<Location> goldSpawnPoints = new ArrayList<>();
        for (int i = 1; i <= getMaxGoldSpawnPoints(arenaName); i++) {
            Location goldSpawn = arenaFile.getGoldSpawn(arenaName, i);
            if (goldSpawn != null) {
                goldSpawnPoints.add(goldSpawn);
            }
        }

        if (goldSpawnPoints.isEmpty()) {
            Bukkit.getLogger().warning("No gold spawn points found for arena: " + arenaName);
            return;
        }

        if (allAtOnce) {
            // Spawn gold at all locations simultaneously
            spawnGoldAtLocations(goldSpawnPoints);
        } else {
            // Spawn gold at random locations among the gold spawn points
            spawnGoldRandomly(goldSpawnPoints);
        }
    }

    public void spawnGoldAtLocations(List<Location> locations) {
        for (Location location : locations) {
            // Drop a single gold ingot at the specified location
            spawnGoldIngot(location);
        }
    }

    public void spawnGoldRandomly(List<Location> locations) {
        // Check if there are any locations to spawn gold
        if (locations.isEmpty()) {
            Bukkit.getLogger().warning("No gold spawn points provided.");
            return;
        }

        boolean spawnHalf = true; // Change to false to spawn gold at every location

        if (spawnHalf) {
            Random random = new Random();
            int halfSize = locations.size() / 2;

            // Shuffle the list of locations
            for (int i = 0; i < locations.size(); i++) {
                int randomIndex = random.nextInt(locations.size());
                Location temp = locations.get(i);
                locations.set(i, locations.get(randomIndex));
                locations.set(randomIndex, temp);
            }

            // Only spawn gold at half of the shuffled locations
            for (int i = 0; i < halfSize; i++) {
                Location location = locations.get(i);
                spawnGoldIngot(location);
            }
        } else {
            // Spawn gold at every location
            for (Location location : locations) {
                spawnGoldIngot(location);
            }
        }
    }

    private void spawnGoldIngot(Location location) {
        ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT);
        Item goldIngotItem = location.getWorld().dropItem(location, goldIngot);
        goldIngotItem.setPickupDelay(0); // Allow players to pick up the gold ingot immediately

        // Automatically remove the gold ingot after a delay (e.g., 30 seconds)
        new BukkitRunnable() {
            @Override
            public void run() {
                goldIngotItem.remove();
            }
        }.runTaskLater(Main.getInstance(), 20 * 30); // 20 ticks per second, 30 seconds delay
    }

    public int getMaxGoldSpawnPoints(String arenaName) {
        ConfigurationSection arenaSection = arenaFile.config.getConfigurationSection("instance." + arenaName);
        if (arenaSection != null) {
            ConfigurationSection goldSpawnPointsSection = arenaSection.getConfigurationSection("goldSpawnPoints");
            if (goldSpawnPointsSection != null) {
                return goldSpawnPointsSection.getKeys(false).size();
            }
        }
        return 0;
    }
}