package to.epac.factorycraft.maptactoe.tictactoe;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import to.epac.factorycraft.maptactoe.MapTacToe;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GameAI;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GamePlayer;

public class GameManager {
	
	private ArrayList<Game> games;
	
	private MapTacToe plugin;
	
	public GameManager(MapTacToe plugin) {
		
		this.plugin = plugin;
		
		games = new ArrayList<>();
	}
	
	
	
	/**
	 * Load configurations into memory
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
            	
            	// TODO - Load incomplete game's data in world
            	
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
	 * Save incomplete game's data into yml
	 */
	public void save() {
		
	}
	
	/**
	 * Initialize game board, if playerA is GameAI, means AI has the first move, then place X for AI
	 */
	public void initialize() {
		plugin.getLogger().info("Initializing game boards...");
		
		for (Game game : plugin.getGameManager().getGames()) {
			
			// If AI has the first priority to move
			if (game.playerA instanceof GameAI) {
				game.attemptAiMove();
				game.swap();
			}
		}
	}
	
	/**
	 * Load datas from yml, place them in world again
	 */
	public void refresh() {
		
	}
	
	
	
	public ArrayList<Game> getGames() {
		return games;
	}
	
	public Game getGame(UUID uuid) {
		for (Game game : games) {
			
			if (game.playerA instanceof GamePlayer) {
				GamePlayer gp = (GamePlayer) game.playerA;
				if (gp.getUniqueId().equals(uuid))
					return game;
			}
			
			else if (game.playerB instanceof GamePlayer) {
				GamePlayer gp = (GamePlayer) game.playerB;
				if (gp.getUniqueId().equals(uuid))
					return game;
			}
		}
		
		return null;
	}
}
