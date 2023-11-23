package config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import murdermystery.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

@SuppressWarnings("serial")
public class ArenaFile extends File {
	
  public File file = new File("plugins/" + Main.getInstance().getName(), "arena.yml");
  
  public FileConfiguration config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.file);
  
  public ArenaFile() {
    super("", "arena");
    writeDefaults();
  }
  
  public void writeDefaults() {
    this.config.options().copyDefaults(true);
    saveConfig();
  }
  
  public void createArena(String arenaId) {
	    String arenaPath = "instance." + arenaId.toLowerCase();
	    
	    config.set(arenaPath + ".mapName", "default");
	    config.set(arenaPath + ".minimumPlayers", 4);
	    config.set(arenaPath + ".maximumPlayers", 16);
	    setDefaultLocation(config, arenaPath + ".lobbyLocation");
	    setDefaultLocation(config, arenaPath + ".endLocation");
	    config.set(arenaPath + ".playerSpawnPoints", new ArrayList<>());
	    
	    saveConfig();
	}

	private void setDefaultLocation(FileConfiguration config, String path) {
	    String locationString = String.format("world, %.3f, %.3f, %.3f, %.3f, %.3f", 0.0, 0.0, 0.0, 0.0, 0.0);
	    config.set(path, locationString);
	}
  
	public Location getArena(String arenaName) {
	    Location loc = null;
	    try {
	        String lobbyLocationString = config.getString("instance." + arenaName + ".lobbyLocation");
	        String[] parts = lobbyLocationString.split(", ");

	        if (parts.length >= 6) {
	            World w = Bukkit.getWorld(parts[0]);
	            double x = Double.parseDouble(parts[1]);
	            double y = Double.parseDouble(parts[2]);
	            double z = Double.parseDouble(parts[3]);
	            float yaw = Float.parseFloat(parts[4]);
	            float pitch = Float.parseFloat(parts[5]);

	            loc = new Location(w, x, y, z, yaw, pitch);
	        }
	    } catch (Exception ex) {
	        loc = null;
	    }
	    return loc;
	}
  
  public void setArenaLobby(String arenaName, Location loc) {
	    String locationString = String.format(
	        "%s, %.3f, %.3f, %.3f, %.3f, %.3f",
	        loc.getWorld().getName(),
	        loc.getX(),
	        loc.getY(),
	        loc.getZ(),
	        loc.getYaw(),
	        loc.getPitch()
	    );

	    config.set("instance." + arenaName + ".lobbyLocation", locationString);
	    saveConfig();
	}

  public Location getArenaLobby(String arenaName) {
      Location loc;
      try {
          String lobbyLocationString = config.getString("instance." + arenaName + ".lobbyLocation");
          String[] parts = lobbyLocationString.split(", ");

          if (parts.length >= 6) {
              World w = Bukkit.getWorld(parts[0]);
              double x = Double.parseDouble(parts[1]);
              double y = Double.parseDouble(parts[2]);
              double z = Double.parseDouble(parts[3]);
              float yaw = Float.parseFloat(parts[4]);
              float pitch = Float.parseFloat(parts[5]);

              loc = new Location(w, x, y, z, yaw, pitch);
          } else {
              loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
          }
      } catch (Exception ex) {
          loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
      }
      return loc;
  }
  
  public void setSpawn(String arenaName, int number, Location loc) {
	    String locationString = String.format(
	        "%s, %.3f, %.3f, %.3f, %.3f, %.3f",
	        loc.getWorld().getName(),
	        loc.getX(),
	        loc.getY(),
	        loc.getZ(),
	        loc.getYaw(),
	        loc.getPitch()
	    );

	    config.set("instance." + arenaName + ".playerSpawnPoints." + number, locationString);
	    saveConfig();
	}

	public Location getSpawn(String arenaName, int number) {
	    Location loc;
	    try {
	        String spawnLocationString = config.getString("instance." + arenaName + ".playerSpawnPoints." + number);
	        String[] parts = spawnLocationString.split(", ");

	        if (parts.length >= 6) {
	            World w = Bukkit.getWorld(parts[0]);
	            double x = Double.parseDouble(parts[1]);
	            double y = Double.parseDouble(parts[2]);
	            double z = Double.parseDouble(parts[3]);
	            float yaw = Float.parseFloat(parts[4]);
	            float pitch = Float.parseFloat(parts[5]);

	            loc = new Location(w, x, y, z, yaw, pitch);
	        } else {
	            loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
	        }
	    } catch (Exception ex) {
	        loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
	    }
	    return loc;
	}
	
	public void setGoldSpawn(String arenaName, int number, Location loc) {
	    String locationString = String.format(
	        "%s, %.3f, %.3f, %.3f, %.3f, %.3f",
	        loc.getWorld().getName(),
	        loc.getX(),
	        loc.getY(),
	        loc.getZ(),
	        loc.getYaw(),
	        loc.getPitch()
	    );

	    config.set("instance." + arenaName + ".goldSpawnPoints." + number, locationString);
	    saveConfig();
	}

	public Location getGoldSpawn(String arenaName, int number) {
	    Location loc;
	    try {
	        String spawnLocationString = config.getString("instance." + arenaName + ".goldSpawnPoints." + number);
	        String[] parts = spawnLocationString.split(", ");

	        if (parts.length >= 6) {
	            World w = Bukkit.getWorld(parts[0]);
	            double x = Double.parseDouble(parts[1]);
	            double y = Double.parseDouble(parts[2]);
	            double z = Double.parseDouble(parts[3]);
	            float yaw = Float.parseFloat(parts[4]);
	            float pitch = Float.parseFloat(parts[5]);

	            loc = new Location(w, x, y, z, yaw, pitch);
	        } else {
	            loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
	        }
	    } catch (Exception ex) {
	        loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
	    }
	    return loc;
	}
  
	public void createArenasFromConfig() {
	    for (String key : config.getConfigurationSection("instance.").getKeys(false)) {
	        String worldName = config.getString("instance." + key + ".lobbyLocation").split(", ")[0];
	        if (worldName != null) {
	            createNewWorld(worldName);
	        } else {
	            Bukkit.getLogger().warning("No world name found for arena: " + key);
	        }
	    }
	}

	public World createNewWorld(String worldName) {
	    WorldCreator worldCreator = new WorldCreator(worldName);
	    World newWorld = Bukkit.createWorld(worldCreator);
	    return newWorld;
	}

	public void saveConfig() {
	    try {
	        config.save(file);
	    } catch (IOException ignored) {
	    }
	}

	public void getArenas() {
	    for (String key : config.getConfigurationSection("instance.").getKeys(false)) {
	        Bukkit.broadcastMessage(key);
	    }
	}

	public List<String> getArenasList() {
	    return new ArrayList<>(config.getConfigurationSection("instance.").getKeys(false));
	}
}