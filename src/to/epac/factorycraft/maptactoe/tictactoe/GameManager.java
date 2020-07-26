package to.epac.factorycraft.maptactoe.tictactoe;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import to.epac.factorycraft.maptactoe.MapTacToe;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GameAI;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GameParticipant;
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
            	GameParticipant p1 = null;
            	GameParticipant p2 = null;
            	
            	if (conf.contains("MapTacToe." + id + ".Participants.AI")) {
            		
            		// AI, if exist, check start first or not
            		String symbol = conf.getString("MapTacToe." + id + ".Participants.AI.Symbol");
            		int diff = conf.getInt("MapTacToe." + id + ".Participants.AI.Difficulty");
            		boolean startFirst = conf.getBoolean("MapTacToe." + id + ".Participants.AI.StartFirst");
            		
            		if (startFirst)
            			p1 = new GameAI(symbol, diff, startFirst);
            		else
            			p2 = new GameAI(symbol, diff, startFirst);
            	}
            	else {
            		
            		// PlayerB, if no AI, B must be P2
            		String symbol = conf.getString("MapTacToe." + id + ".Participants.PlayerB.Symbol");
                	String uuid = conf.getString("MapTacToe." + id + ".Participants.PlayerB.UUID");
                	
                	p2 = new GamePlayer(symbol, uuid);
            	}
            	
            	// PlayerA, P1 when P2 exist, P2 when AI exist
            	String symbol = conf.getString("MapTacToe." + id + ".Participants.PlayerA.Symbol");
            	String uuid = conf.getString("MapTacToe." + id + ".Participants.PlayerA.UUID");
            	
            	if (p1 == null)
            		p1 = new GamePlayer(symbol, uuid);
            	else
            		p2 = new GamePlayer(symbol, uuid);
            	
            	int width = conf.getInt("MapTacToe." + id + ".Settings.Width");
            	int height = conf.getInt("MapTacToe." + id + ".Settings.Height");
            	int win = conf.getInt("MapTacToe." + id + ".Settings.Win");
            	int time = conf.getInt("MapTacToe." + id + ".Settings.Time");
            	int expire = conf.getInt("MapTacToe." + id + ".Settings.Expire");
            	
            	Location top = conf.getLocation("MapTacToe." + id + ".Locations.Top");
            	Location btm = conf.getLocation("MapTacToe." + id + ".Locations.Bottom");
            	
            	// TODO - Load incomplete game's data in world
            	
            	Game game = new Game(id, p1, p2, win, time, expire, top, btm, width, height);
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
			if (game.getPlayer1() instanceof GameAI) {
				game.attemptAiMove((GameAI) game.getPlayer1());
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
			
			if (game.getPlayer1() instanceof GamePlayer) {
				GamePlayer gp = (GamePlayer) game.getPlayer1();
				if (gp.getUniqueId().equals(uuid))
					return game;
			}
			
			else if (game.getPlayer2() instanceof GamePlayer) {
				GamePlayer gp = (GamePlayer) game.getPlayer2();
				if (gp.getUniqueId().equals(uuid))
					return game;
			}
		}
		
		return null;
	}
}
