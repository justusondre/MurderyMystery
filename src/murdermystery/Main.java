package murdermystery;

import org.bukkit.plugin.java.JavaPlugin;

import arena.Arena;
import arena.ArenaCountdown;
import arena.ArenaListener;
import arena.manager.GoldManager;
import arena.manager.SpectateManager;
import command.AddKill;
import command.ArenaInfo;
import command.CreateArena;
import command.DeathTest;
import command.JoinArena;
import command.SetArenaLobby;
import command.SetGoldSpawn;
import command.SetMainLobby;
import command.SetSpawn;
import command.Spectate;
import command.TeleportWorld;
import config.MySQLFile;
import config.manager.FileManager;
import listener.ClickListener;
import listener.JoinListener;
import listener.AxeListener;
import scoreboard.ScoreboardManager;
import user.data.MySQLConnection;
import user.data.MySQLDatabase;

public class Main extends JavaPlugin {
	
	private static Main instance;
    private FileManager fileManager;
    private ArenaCountdown countdownManager;
    private Arena arena;
    private ScoreboardManager scoreboardManager;
    private GoldManager goldManager;
    private MySQLConnection mysqlConnection;
    private MySQLDatabase mysqlDatabase;
    private MySQLFile sqlFile;
    private JoinListener joinListener;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize the SQL file first
        this.sqlFile = new MySQLFile(this);

        // Create and start the MySQL connection using the SQL file
        mysqlConnection = new MySQLConnection(this, sqlFile);
        mysqlConnection.getConnection(); // This method should establish the connection
        this.mysqlDatabase = new MySQLDatabase(this.mysqlConnection);

        // Initialize other components after the SQL connection is established
        this.fileManager = new FileManager();
        this.countdownManager = new ArenaCountdown();
        this.arena = new Arena();
        this.scoreboardManager = new ScoreboardManager();
        this.goldManager = new GoldManager();

        // Register commands
        getCommand("createarena").setExecutor(new CreateArena());
        getCommand("setspawn").setExecutor(new SetSpawn());
        getCommand("joinarena").setExecutor(new JoinArena());
        getCommand("leavearena").setExecutor(new JoinArena());
        getCommand("setarenalobby").setExecutor(new SetArenaLobby());
        getCommand("setmainlobby").setExecutor(new SetMainLobby());
        getCommand("arenainfo").setExecutor(new ArenaInfo());
        getCommand("tpworld").setExecutor(new TeleportWorld());
        getCommand("createworld").setExecutor(new TeleportWorld());
        getCommand("spectate").setExecutor(new Spectate());
        getCommand("setgoldspawn").setExecutor(new SetGoldSpawn());
        getCommand("addkill").setExecutor(new AddKill(this, mysqlDatabase));
        getCommand("deathtest").setExecutor(new DeathTest());

        // Register Events  
        getServer().getPluginManager().registerEvents(new ArenaListener(), this);
        getServer().getPluginManager().registerEvents(new ClickListener(), this);
        getServer().getPluginManager().registerEvents(new Spectate(), this);
        getServer().getPluginManager().registerEvents(new AxeListener(), this);
        getServer().getPluginManager().registerEvents(new SpectateManager(), this);
             
        // Create lobby world and load arena configurations
        fileManager.getLobbyFile().createLobbyWorld(fileManager.getLobbyFile().getWorldName());
        fileManager.getArenaFile().createArenasFromConfig();
        
        // Register the JoinListener with the MySQLConnection
        joinListener = new JoinListener(mysqlConnection);
        getServer().getPluginManager().registerEvents(joinListener, this);      
    }

    @Override
    public void onDisable() {
        if (mysqlConnection != null) {
            mysqlConnection.close();
        }

        // Clean up any other resources if needed

        getLogger().info("Your plugin has been disabled!");
    }

    public static Main getInstance() {
        return instance;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public ArenaCountdown getCountdownManager() {
        return countdownManager;
    }

    public Arena getArena() {
        return arena;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public GoldManager getGoldManager() {
        return goldManager;
    }

    public MySQLDatabase getMysqlDatabase() {
        return mysqlDatabase;
    }

    public MySQLConnection getMysqlConnection() {
        return mysqlConnection;
    }

    public MySQLFile getSqlFile() {
        return sqlFile;
    }
}