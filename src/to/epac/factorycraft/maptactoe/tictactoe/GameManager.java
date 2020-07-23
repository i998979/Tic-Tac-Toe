package to.epac.factorycraft.maptactoe.tictactoe;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import to.epac.factorycraft.maptactoe.MapTacToe;

public class GameManager {
	
	private ArrayList<Game> games;
	
	private MapTacToe plugin;
	
	public GameManager(MapTacToe plugin) {
		
		this.plugin = plugin;
		
		games = new ArrayList<>();
	}
	
	
	
	/**
	 * Load configurations to memory
	 */
	public void load() {
		plugin.getLogger().info("Loading GameManager...");
		
		FileConfiguration conf = plugin.getConfig();
        for (String id : conf.getConfigurationSection("MapTacToe").getKeys(false)) {
            try {
            	String p1 = conf.getString("MapTacToe." + id + ".Participants.P1");
            	String p2 = conf.getString("MapTacToe." + id + ".Participants.P2");
            	
            	int width = conf.getInt("MapTacToe." + id + ".Settings.Width");
            	int length = conf.getInt("MapTacToe." + id + ".Settings.Height");
            	int win = conf.getInt("MapTacToe." + id + ".Settings.Win");
            	int time = conf.getInt("MapTacToe." + id + ".Settings.Time");
            	int expire = conf.getInt("MapTacToe." + id + ".Settings.Expire");
            	
            	Location top = conf.getLocation("MapTacToe." + id + ".Locations.Top");
            	Location btm = conf.getLocation("MapTacToe." + id + ".Locations.Bottom");
            	
            	// TODO - Load incomplete game's data on world
            	
            	Game game = new Game(p1, p2, width, length, win, time, expire, top, btm);
            	games.add(game);
            	
            } catch (Exception e) {
                plugin.getLogger().warning("Error loading MapTacToe id " + id + ".");
                e.printStackTrace();
                continue;
            }
        }
	}
	
	/**
	 * Initialize game board, if playerA is GameAI, means AI has the first move, then place X for AI
	 */
	public void initialize() {
		plugin.getLogger().info("Initializing game board...");
		
		for (Game g : plugin.getGameManager().getGames()) {
			
			// If AI has the first move priority
			if (g.playerA instanceof GameAI) {
				g.attemptAiMove();
				g.swap();
			}
		}
	}
	
	public void refresh() {
		
	}
	
	
	
	public ArrayList<Game> getGames() {
		return games;
	}
}
