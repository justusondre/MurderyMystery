package scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {

    public Scoreboard createScoreboard() {
        return org.bukkit.Bukkit.getScoreboardManager().getNewScoreboard();
    }
    
    public void updateLobbyObjectives(Player player, Scoreboard scoreboard) {
        ScoreboardWrapper scoreboardWrapper = new ScoreboardWrapper(scoreboard);
        scoreboardWrapper.updateLobbyObjectives(player); // Utilize the existing method in ScoreboardWrapper
        player.setScoreboard(scoreboard);
    }

    public void updatePlayerGameScoreboard(Player player, Scoreboard scoreboard, String arenaId) {
        ScoreboardWrapper scoreboardWrapper = new ScoreboardWrapper(scoreboard);
        scoreboardWrapper.setTeam(player); 
        scoreboardWrapper.updateGameObjectives(arenaId);
        player.setScoreboard(scoreboard);
    }
    
    public void updateTitle(Player player, Scoreboard scoreboard) {
        ScoreboardWrapper scoreboardWrapper = new ScoreboardWrapper(scoreboard);
        scoreboardWrapper.setTeam(player);
        player.setScoreboard(scoreboard);
    }
    
    public void clearScoreboard(Player player) {
        ScoreboardWrapper scoreboardWrapper = new ScoreboardWrapper(player.getScoreboard());
        scoreboardWrapper.clearScoreboard(player);
    }
    
}