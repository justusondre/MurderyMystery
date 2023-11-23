package command;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import config.ArenaFile;
import murdermystery.Main;

public class TeleportWorld implements CommandExecutor {
	
	ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tpworld")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }

            if (args.length == 1) {
                Player player = (Player) sender;
                String worldName = args[0];
                teleportToWorld(player, worldName);
                return true;
            }
        }
        return false;
    }

	public void teleportToWorld(Player player, String worldName) {
        World targetWorld = Bukkit.getWorld(worldName);

        if (targetWorld == null) {
            WorldCreator worldCreator = new WorldCreator(worldName);
            targetWorld = worldCreator.createWorld();
            if (targetWorld != null) {
                targetWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                targetWorld.setGameRule(GameRule.DO_FIRE_TICK, false);
                clearMobs(targetWorld);
            }
        }

        if (targetWorld != null) {
            Location spawnLocation = targetWorld.getSpawnLocation();
            player.teleport(spawnLocation);
        } else {
            player.sendMessage("Failed to teleport to world: " + worldName);
        }
    }

    private void clearMobs(World world) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof LivingEntity) {
                entity.remove();
            }
        }
    }
}