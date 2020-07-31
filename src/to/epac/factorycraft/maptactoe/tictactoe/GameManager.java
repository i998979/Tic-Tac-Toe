package to.epac.factorycraft.maptactoe.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
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
            		int delay = conf.getInt("MapTacToe." + id + ".Participants.AI.Delay");
            		boolean startFirst = conf.getBoolean("MapTacToe." + id + ".Participants.AI.StartFirst");
            		
            		if (startFirst)
            			p1 = new GameAI(symbol, diff, delay, startFirst);
            		else
            			p2 = new GameAI(symbol, diff, delay, startFirst);
            	}
            	else {
            		
            		// PlayerB, if no AI, B must be P2
            		String symbol = conf.getString("MapTacToe." + id + ".Participants.PlayerB.Symbol");
                	String uuid = conf.getString("MapTacToe." + id + ".Participants.PlayerB.UUID");
                	
                	p2 = new GamePlayer(symbol, uuid);
            	}
            	
            	// PlayerA, P1 when P2 exist, P2 when AI exist
            	String symbol = conf.getString("MapTacToe." + id + ".Participants.PlayerA.Symbol");
            	// TODO - Set UUID when cell first click
            	String uuid = conf.getString("MapTacToe." + id + ".Participants.PlayerA.UUID");
            	
            	if (p1 == null)
            		p1 = new GamePlayer(symbol, uuid);
            	else
            		p2 = new GamePlayer(symbol, uuid);
            	
            	int width = conf.getInt("MapTacToe." + id + ".Settings.Width");
            	int height = conf.getInt("MapTacToe." + id + ".Settings.Height");
            	BlockFace facing = BlockFace.valueOf(conf.getString("MapTacToe." + id + ".Settings.Facing"));
            	int win = conf.getInt("MapTacToe." + id + ".Settings.Win");
            	int time = conf.getInt("MapTacToe." + id + ".Settings.Time");
            	int expire = conf.getInt("MapTacToe." + id + ".Settings.Expire");
            	
            	List<String> winAI = conf.getStringList("MapTacToe." + id + ".Commands.Win.AI");
            	List<String> winPlayer = conf.getStringList("MapTacToe." + id + ".Commands.Win.Player");
            	
            	List<String> loseAI = conf.getStringList("MapTacToe." + id + ".Commands.Lose.AI");
            	List<String> losePlayer = conf.getStringList("MapTacToe." + id + ".Commands.Lose.Player");
            	
            	List<String> drawAI = conf.getStringList("MapTacToe." + id + ".Commands.Draw.AI");
            	List<String> drawPlayer = conf.getStringList("MapTacToe." + id + ".Commands.Draw.Player");
            	
            	GameCommand cmd = new GameCommand(winAI, winPlayer, loseAI, losePlayer, drawAI, drawPlayer);
            	
            	
            	Location top = conf.getLocation("MapTacToe." + id + ".Locations.Top");
            	Location btm = conf.getLocation("MapTacToe." + id + ".Locations.Bottom");
            	
            	// TODO - Load incomplete game's data in world
            	
            	Game game = new Game(id, p1, p2, win, time, expire, cmd, top, btm, width, height, facing);
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
		plugin.getLogger().info("Initializing games...");
		
		for (Game game : plugin.getGameManager().getGames()) {
			
			initBoard(game);
			
			// If AI has the first priority to move
			if (game.getPlayer1() instanceof GameAI) {
				game.attemptAiMove((GameAI) game.getPlayer1());
				game.swap();
			}
		}
	}
	
	public void initBoard(Game game) {
		for (int i = 0; i < game.getHeight(); i++) {
			for (int j = 0; j < game.getWidth(); j++) {
				
				Block button = game.boardLoc[i][j].getBlock();
				button.setType(Material.STONE_BUTTON);
				
				Directional data = (Directional) button.getBlockData();
				data.setFacing(game.getFacing());
				
				button.setBlockData(data);
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
