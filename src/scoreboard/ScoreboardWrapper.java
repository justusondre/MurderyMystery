package scoreboard;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import arena.Arena;
import arena.ArenaCountdown;
import arena.ArenaState;
import murdermystery.Main;
import utils.MathUtils;

public class ScoreboardWrapper {
    private Scoreboard scoreboard;
    
    ArenaCountdown arenaCountdown = Main.getInstance().getCountdownManager();

    public ScoreboardWrapper(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void setScore(String playerName, int score) {
        Score playerScore = getPlayerScore(playerName);
        playerScore.setScore(score);
    }

    public void setTeam(Player player) {
        Team team = getOrCreateTeam(player.getName()); 
        team.addEntry(player.getName());
    }
    
    private Score getPlayerScore(String playerName) {
        Objective objective = getOrCreateObjective("exampleObjective", ChatColor.YELLOW + "" + ChatColor.BOLD + "MURDER MYSTERY");
        return objective.getScore(playerName);
    }

    private Team getOrCreateTeam(String teamName) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.setPrefix("§r"); 
        }
        return team;
    }

    public void updatePlayerScoreboard(Player player, Scoreboard scoreboard, String arenaName, String gameState, String role) {
        ScoreboardWrapper scoreboardWrapper = new ScoreboardWrapper(scoreboard);
        scoreboardWrapper.setTeam(player);
    }

    public void addBlankLine(Objective objective, int score) {
        String entryName = ChatColor.RESET.toString() + ChatColor.values()[score];
        Team blankTeam = getOrCreateTeam("blank");
        objective.getScore(blankTeam.getPrefix() + entryName).setScore(score);
    }

    @SuppressWarnings("deprecation")
	private Objective getOrCreateObjective(String objectiveName, String displayName) {
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(objectiveName, "dummy", displayName);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        return objective;
    }
    
    public void clearScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        for (Team team : scoreboard.getTeams()) {
            if (!team.getName().equals("scoreboard")) {
                team.unregister();
            }
        }

        Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        player.setScoreboard(newScoreboard);
        
    }
    
    public void updateTitle(String newTitle) {
        Objective objective = getOrCreateObjective("gameStatus", newTitle);
        startGlitchingTitleAnimation(objective); 
    }
    
    public void updateLobbyObjectives(Player player) {
    	Objective objective = getOrCreateObjective("gameStatus", ChatColor.BOLD + "Game Status");
        startGlitchingTitleAnimation(objective); 

        String formattedDate = new SimpleDateFormat("dd/MM/YY").format(new Date());
        
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }   
        
        objective.getScore(ChatColor.DARK_GRAY + formattedDate + " " + ChatColor.GRAY + "" + ChatColor.ITALIC + "" + "Statistics").setScore(10);
        addBlankLine(objective, 9); 
        objective.getScore(ChatColor.WHITE + "Total Wins: " + ChatColor.GREEN + "100").setScore(8);
        objective.getScore(ChatColor.WHITE + "Total Kills: " + ChatColor.GREEN + "100").setScore(7);
        addBlankLine(objective, 6); 
        objective.getScore(ChatColor.WHITE + "Murderer Wins: " + ChatColor.GREEN + "50").setScore(5);
        objective.getScore(ChatColor.WHITE + "Detective Wins: " + ChatColor.GREEN + "50").setScore(4);
        addBlankLine(objective, 3); 
        objective.getScore(ChatColor.WHITE + "Coins: " + ChatColor.GREEN + "3,000").setScore(2);
        addBlankLine(objective, 1); 
        objective.getScore(ChatColor.YELLOW + "yourserver.com ").setScore(0);
        
    }
    
    public void updateGameObjectives(String arenaId) {
        Objective objective = getOrCreateObjective("gameStatus", ChatColor.BOLD + "Game Status");

        String formattedDate = new SimpleDateFormat("dd/MM/YY").format(new Date());
        
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        Arena arena = Main.getInstance().getArena();
        
        if (arena.getArenaState(arenaId).equals(ArenaState.WAITING)) {
        	objective.getScore(ChatColor.WHITE + "Lobby Time: ").setScore(3);
            objective.getScore(ChatColor.GREEN + MathUtils.formatTime(arenaCountdown.getCountdown(arenaId), ChatColor.GREEN, ChatColor.GREEN)).setScore(2);
            
        } else if (arena.getArenaState(arenaId).equals(ArenaState.INGAME)) {
        	objective.getScore(ChatColor.WHITE + "Game Time: ").setScore(3);
            objective.getScore(ChatColor.GREEN + MathUtils.formatTime(arenaCountdown.getCountdown(arenaId), ChatColor.GREEN, ChatColor.GREEN)).setScore(2);
            
        } else if (arena.getArenaState(arenaId).equals(ArenaState.ENDGAME)) {
            objective.getScore(ChatColor.GREEN + "End Game").setScore(8);
            
        }
        
        objective.getScore(ChatColor.DARK_GRAY + formattedDate + " " + ChatColor.GRAY + "" + ChatColor.ITALIC + "" + arenaId).setScore(9);
        addBlankLine(objective, 8); 
        objective.getScore(ChatColor.WHITE + "Map: " + ChatColor.GREEN + arenaId).setScore(7);
        addBlankLine(objective, 6); 
        objective.getScore(ChatColor.WHITE + "Players: " + ChatColor.GREEN + "1" + ChatColor.GRAY + "/" + ChatColor.GREEN + "16").setScore(5);
        addBlankLine(objective, 4); 
        addBlankLine(objective, 1); 
        objective.getScore(ChatColor.YELLOW + "yourserver.com ").setScore(0);
    }
    
    public void startGlitchingTitleAnimation(Objective objective) {
        BukkitRunnable animationTask = new BukkitRunnable() {
            int tick = 0;
            boolean isGlitching = false;

            @Override
            public void run() {
                tick++;

                if (tick % 200 == 0) { // Every 10 seconds (200 ticks)
                    isGlitching = true;
                }

                if (isGlitching) {
                    if (tick % 20 >= 0 && tick % 20 < 10) { // Glitch for 1 second (20 ticks)
                        if (tick % 2 == 0) { // Switch every 2 ticks
                            objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.ITALIC + "" + ChatColor.BOLD + " MURDER MYSTERY ");
                        } else {
                            objective.setDisplayName(ChatColor.RED + "" + ChatColor.ITALIC + "" + ChatColor.BOLD + " MURDER MYSTERY ");
                        }
                    } else {
                        objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + " MURDER MYSTERY ");
                        isGlitching = false;
                    }
                } else {
                    objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + " MURDER MYSTERY ");
                }
            }
        };

        animationTask.runTaskTimer(Main.getInstance(), 0L, 1L); // Run every tick
    }
}