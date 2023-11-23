package arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import config.ArenaFile;
import config.LobbyFile;
import murdermystery.Main;
import net.md_5.bungee.api.ChatColor;
import scoreboard.ScoreboardManager;
import utils.Title;

public class Arena {
	
	private Map<String, ArenaState> arenaStates;

	ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
	ArenaCountdown arenaCountdown = Main.getInstance().getCountdownManager();

	private Map<Player, String> playerArenas = new HashMap<>();
	private Map<Player, String> playerRoles = new HashMap<>();

	private List<Player> alivePlayers = new ArrayList<>();
	private List<Player> deadPlayers = new ArrayList<>();

	private List<Player> murderer = new ArrayList<>();
	private List<Player> detective = new ArrayList<>();
	private List<Player> innocent = new ArrayList<>();

	public Arena() {
		arenaStates = new HashMap<>();
	}

	public void setArenaState(String arenaName, ArenaState state) {
		arenaStates.put(arenaName, state);
	}

	public ArenaState getArenaState(String arenaName) {
		return arenaStates.getOrDefault(arenaName, ArenaState.WAITING);
	}
	
	public void joinArena(Player player, String arenaName) {
		if (!isPlayerInArena(player)) {
			this.playerArenas.put(player, arenaName);
			addToAlive(player);
			player.setHealth(20.0D);
			player.setFoodLevel(40);
			player.setGameMode(GameMode.ADVENTURE);
			player.setExp(0);
			player.setTotalExperience(0);
			player.getInventory().clear();
			ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
			player.teleport(arenaFile.getArenaLobby(arenaName));
			checkMinPlayers(arenaName);
			broadcastMessageToPlayersInArena(arenaName, ChatColor.GREEN + "▶ " + ChatColor.YELLOW + "" + player.getDisplayName().toString() + " has joined the game!");
			
		} else {
			player.sendMessage(ChatColor.RED + "You are already in an arena. Leave the current arena to join another one.");
		}
	}

	public void leaveArena(Player player) {
		if (isPlayerInArena(player)) {
			String arenaName = getArenaName(player);
			this.playerArenas.remove(player);
			removeFromAlive(player);
			removeFromDead(player);
			removeInnocent(player);
			removeInvestigator(player);
			removeMurderer(player);
			
			Arena arena = Main.getInstance().getArena();
			arena.resetArenaIfEmpty(arenaName);
			
			LobbyFile lobbyFile = Main.getInstance().getFileManager().getLobbyFile();
            Location spawnLocation = lobbyFile.getLocation();
            player.teleport(spawnLocation);
			
			ScoreboardManager scoreboardManager = Main.getInstance().getScoreboardManager();
        	scoreboardManager.clearScoreboard(player);
            Scoreboard scoreboard = scoreboardManager.createScoreboard();
            scoreboardManager.updateLobbyObjectives(player, scoreboard);
            
		} else {
			
			player.sendMessage("You are not in any arena.");
		}
	}

	public boolean isPlayerInArena(Player player) {
		return this.playerArenas.containsKey(player);
	}

	public List<Player> getPlayersInArena(String arenaName) {
		List<Player> playersInArena = new ArrayList<>();
		for (Map.Entry<Player, String> entry : this.playerArenas.entrySet()) {
			if (((String) entry.getValue()).equalsIgnoreCase(arenaName))
				playersInArena.add(entry.getKey());
		}
		return playersInArena;
	}

	public List<Player> getAlivePlayersInArena(String arenaName) {
		List<Player> alivePlayersInArena = new ArrayList<>();
		for (Player player : this.alivePlayers) {
			if (this.playerArenas.containsKey(player)
					&& ((String) this.playerArenas.get(player)).equalsIgnoreCase(arenaName))
				alivePlayersInArena.add(player);
		}
		return alivePlayersInArena;
	}

	public List<Player> getDeadPlayersInArena(String arenaName) {
		List<Player> deadPlayersInArena = new ArrayList<>();
		for (Player player : this.deadPlayers) {
			if (this.playerArenas.containsKey(player)
					&& ((String) this.playerArenas.get(player)).equalsIgnoreCase(arenaName))
				deadPlayersInArena.add(player);
		}
		return deadPlayersInArena;
	}
	
	public List<Player> getMurderer(String arenaName) {
        List<Player> murdererInArena = new ArrayList<>();
        for (Player player : this.murderer) {
            if (this.playerArenas.containsKey(player) && this.playerArenas.get(player).equalsIgnoreCase(arenaName)) {
                murdererInArena.add(player);
            }
        }
        return murdererInArena;
    }

    public List<Player> getInnocent(String arenaName) {
        List<Player> innocentInArena = new ArrayList<>();
        for (Player player : this.innocent) {
            if (this.playerArenas.containsKey(player) && this.playerArenas.get(player).equalsIgnoreCase(arenaName)) {
                innocentInArena.add(player);
            }
        }
        return innocentInArena;
    }

    public List<Player> getDetective(String arenaName) {
        List<Player> detectiveInArena = new ArrayList<>();
        for (Player player : this.detective) {
            if (this.playerArenas.containsKey(player) && this.playerArenas.get(player).equalsIgnoreCase(arenaName)) {
                detectiveInArena.add(player);
            }
        }
        return detectiveInArena;
    }
    
    public void checkMinPlayers(String arenaName) {
        List<Player> playersInArena = getPlayersInArena(arenaName);
        if (playersInArena.size() == 1) {
            arenaCountdown.startCountdown(arenaName, 15, ArenaState.PREGAME);
        }
    }
    
    public void teleportPlayersToArenaSpawn(String arenaName) {
		List<Player> playersInArena = getPlayersInArena(arenaName);
		for (int i = 0; i < playersInArena.size(); i++) {
			Player player = playersInArena.get(i);
			int spawnNumber = i + 1;
			Location spawnLocation = arenaFile.getSpawn(arenaName, spawnNumber);
			player.teleport(spawnLocation);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 4*20, 1));
			assignRolesRandomly(arenaName);
		}
	}

	public void resetArenaIfEmpty(String arenaName) {
		if (getPlayersInArena(arenaName).isEmpty()) {
			arenaCountdown.cancelCountdown(arenaName);
			setArenaState(arenaName, ArenaState.WAITING);
		}
	}
    
    public void removeAllPlayersFromArena(String arenaName, Location mainLobbyLocation) {
		List<Player> playersInArena = getPlayersInArena(arenaName);
		for (Player player : playersInArena) {
			this.playerArenas.remove(player);
			removeFromAlive(player);
			removeFromDead(player);
			removeInnocent(player);
			removeInvestigator(player);
			removeMurderer(player);
			player.sendMessage("You have left the arena.");
			player.teleport(mainLobbyLocation);
			
			ScoreboardManager scoreboardManager = Main.getInstance().getScoreboardManager();
        	scoreboardManager.clearScoreboard(player);
            Scoreboard scoreboard = scoreboardManager.createScoreboard();
            scoreboardManager.updateLobbyObjectives(player, scoreboard);
		}
		Arena arena = Main.getInstance().getArena();
		arena.resetArenaIfEmpty(arenaName);
	}

    public void assignRolesRandomly(String arenaName) {
        List<Player> playersInArena = getAlivePlayersInArena(arenaName);
        int numPlayers = playersInArena.size();
        
        if (numPlayers < 1) {
            return;
        }
        
        Random random = new Random();
        int murdererIndex = random.nextInt(numPlayers);
        Player murdererPlayer = playersInArena.get(murdererIndex);
        
        List<Player> potentialInnocents = new ArrayList<>(playersInArena);
        potentialInnocents.remove(murdererPlayer);

        for (Player player : playersInArena) {
            if (player == murdererPlayer) {
                addMurderer(player);
                this.playerRoles.put(player, "Murderer");
                Title.sendTitle(player, 0, 60, 20, ChatColor.RED + "ROLE: MURDERER", "Kill as many players as possible!");
                player.sendMessage("You are the Murderer!");
            } else if (numPlayers > 2) {
                int innocentIndex = random.nextInt(potentialInnocents.size());
                Player innocentPlayer = potentialInnocents.get(innocentIndex);
                potentialInnocents.remove(innocentPlayer);

                addInnocent(player);
                this.playerRoles.put(player, "Innocent");
                Title.sendTitle(player, 0, 60, 20, ChatColor.GREEN + "ROLE: INNOCENT", "Stay alive as long as possible!");
                player.sendMessage("You are the Innocent!");
            } else {
                addInvestigator(player);
                this.playerRoles.put(player, "Investigator");
                Title.sendTitle(player, 0, 60, 20, ChatColor.BLUE + "ROLE: INVESTIGATOR", "Find and kill the murderer!");
                player.sendMessage("You are an Investigator!");
            }
        }
    }
	
	public void giveIronAxeToMurderers(List<Player> murderers) {
	    ItemStack ironAxe = new ItemStack(Material.IRON_AXE);
	    ironAxe.addEnchantment(Enchantment.DAMAGE_ALL, 1);
	    
	    for (Player player : murderers) {
	    	ItemStack itemToSetAsCursor = new ItemStack(Material.AIR);
	    	player.getInventory().setHeldItemSlot(4);
	    	player.setItemOnCursor(itemToSetAsCursor);
	        player.getInventory().addItem(ironAxe);
	        
	    }
	}

	public void checkForWinners(String arenaName) {
		Arena arena = Main.getInstance().getArena();
		ArenaState arenaState = arena.getArenaState(arenaName);

		if (arenaState == ArenaState.INGAME) {
			List<Player> alivePlayersInArena = getAlivePlayersInArena(arenaName);
			List<Player> aliveMurderersInArena = new ArrayList<>(this.murderer);
			aliveMurderersInArena.retainAll(alivePlayersInArena);

			if (aliveMurderersInArena.size() == 1) {
				broadcastMessageToPlayersInArena(arenaName, ChatColor.RED + "The murderer has won!");
				Player murdererPlayer = aliveMurderersInArena.get(0);
				if (murdererPlayer != null)
					broadcastMessageToPlayersInArena(arenaName, ChatColor.RED + "Congratulations to " + murdererPlayer.getName() + " for being the murderer!");
				

			} else if (aliveMurderersInArena.size() == 0) {
				
				broadcastMessageToPlayersInArena(arenaName, ChatColor.GREEN + "The murderers have been defeated!");
				broadcastMessageToPlayersInArena(arenaName, ChatColor.GREEN + "Innocents and detectives win!");
			}
		}
	}

	private void broadcastMessageToPlayersInArena(String arenaName, String message) {
		List<Player> playersInArena = getPlayersInArena(arenaName);
		for (Player player : playersInArena)
			player.sendMessage(message);
	}

	public void addMurderer(Player player) {
		this.murderer.add(player);
	}

	public void removeMurderer(Player player) {
		this.murderer.remove(player);
	}

	public void addInvestigator(Player player) {
		this.detective.add(player);
	}

	public void removeInvestigator(Player player) {
		this.detective.remove(player);
	}

	public void addInnocent(Player player) {
		this.innocent.add(player);
	}

	public void removeInnocent(Player player) {
		this.innocent.remove(player);
	}

	public String getPlayerRole(Player player) {
		return this.playerRoles.get(player);
	}

	public String getArenaName(Player player) {
		return this.playerArenas.get(player);
	}

	private void addToAlive(Player player) {
		this.alivePlayers.add(player);
	}

	private void removeFromAlive(Player player) {
		this.alivePlayers.remove(player);
	}

	private void addToDead(Player player) {
		this.deadPlayers.add(player);
	}

	private void removeFromDead(Player player) {
		this.deadPlayers.remove(player);
	}
}