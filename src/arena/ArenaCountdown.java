package arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import arena.manager.GoldManager;
import config.LobbyFile;
import config.Messages;
import murdermystery.Main;
import scoreboard.ScoreboardManager;
import utils.ActionBar;

public class ArenaCountdown {
	
	private Map<String, Integer> arenaCountdowns = new HashMap<>();
    private Map<String, BukkitRunnable> countdownTasks = new HashMap<>();
    String actionbarMessage = "";

    public void startCountdown(String arenaName, int seconds, ArenaState nextState) {
        if (!arenaCountdowns.containsKey(arenaName)) {
            arenaCountdowns.put(arenaName, seconds);
            createCountdownTask(arenaName, seconds, () -> completeCountdown(arenaName, nextState));
            System.out.println("Started countdown for " + arenaName + " with " + seconds + " seconds");
        }
    }

    public void cancelCountdown(String arenaName) {
        arenaCountdowns.remove(arenaName);
        BukkitRunnable countdownTask = countdownTasks.remove(arenaName);
        if (countdownTask != null) {
            countdownTask.cancel();
        }
    }

    public int getCountdown(String arenaName) {
        return arenaCountdowns.getOrDefault(arenaName, 0);
    }

    private void completeCountdown(String arenaName, ArenaState nextState) {
        arenaCountdowns.remove(arenaName);
        Arena arena = Main.getInstance().getArena();

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            switch (nextState) {
                case PREGAME:
                    arena.setArenaState(arenaName, ArenaState.PREGAME);
                    arena.teleportPlayersToArenaSpawn(arenaName);
                    startCountdown(arenaName, 15, ArenaState.INGAME);
                    break;

                case INGAME:
                    arena.setArenaState(arenaName, ArenaState.INGAME);
                    startCountdown(arenaName, 120, ArenaState.ENDGAME);
                    arena.giveIronAxeToMurderers(arena.getMurderer(arenaName));
                    break;

                case ENDGAME:
                    arena.setArenaState(arenaName, ArenaState.ENDGAME);
                    startCountdown(arenaName, 15, ArenaState.WAITING);
                    LobbyFile lobbyFile = Main.getInstance().getFileManager().getLobbyFile();
                    arena.removeAllPlayersFromArena(arenaName, lobbyFile.getLocation());
                    break;

                case WAITING:
                    arena.setArenaState(arenaName, ArenaState.WAITING);
                    break;

                default:
                    break;
            }
        }, 1L);
    }

    private void createCountdownTask(String arenaName, int seconds, Runnable onComplete) {
        BukkitRunnable countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!arenaCountdowns.containsKey(arenaName)) {
                    cancelCountdown(arenaName);
                    return;
                }

                int remainingSeconds = arenaCountdowns.get(arenaName);
                if (remainingSeconds <= 0) {
                    onComplete.run();
                    cancelCountdown(arenaName);
                    return;
                }

                List<Player> playersInArena = getPlayersInArena(arenaName);
                for (Player player : playersInArena) {
                    if (remainingSeconds % 60 == 0 || remainingSeconds == 30 || remainingSeconds == 15 || remainingSeconds <= 5) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
                        ActionBar actionBar = new ActionBar();
                        actionBar.sendActionbar(player, Messages.getActionBarMessage(arenaName).replace("%timer%", String.valueOf(remainingSeconds)));
                    }
                    
                    if (remainingSeconds % 10 == 0) {
                        GoldManager goldManager = new GoldManager();
                        goldManager.spawnGoldAtLocations(arenaName, true);
                    }
                    
                    ScoreboardManager scoreboardManager = Main.getInstance().getScoreboardManager();
                    Scoreboard scoreboard = scoreboardManager.createScoreboard();
                    scoreboardManager.updatePlayerGameScoreboard(player, scoreboard, arenaName);
                }

                remainingSeconds--;
                arenaCountdowns.put(arenaName, remainingSeconds);
            }
        };

        countdownTasks.put(arenaName, countdownTask);
        countdownTask.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    private List<Player> getPlayersInArena(String arenaName) {
        return new ArrayList<>(Main.getInstance().getArena().getPlayersInArena(arenaName));
    }
}