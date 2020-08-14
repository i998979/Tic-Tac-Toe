package to.epac.factorycraft.tictactoe.tictactoe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import to.epac.factorycraft.tictactoe.TicTacToe;
import to.epac.factorycraft.tictactoe.tictactoe.Game.CellState;
import to.epac.factorycraft.tictactoe.tictactoe.participants.GameAI;
import to.epac.factorycraft.tictactoe.tictactoe.participants.GameParticipant;
import to.epac.factorycraft.tictactoe.tictactoe.participants.GamePlayer;

public class GameManager {
	
	private ArrayList<Game> games;
	
	private TicTacToe plugin;
	
	public GameManager(TicTacToe plugin) {
		
		this.plugin = plugin;
		
		games = new ArrayList<>();
	}
	
	
	
	/**
	 * Load configurations into memory
	 */
	public void load() {
		plugin.getLogger().info("Loading GameManager...");
		
		FileConfiguration conf = plugin.getConfig();
        for (String id : conf.getConfigurationSection("TicTacToe").getKeys(false)) {
            try {
            	GameParticipant p1 = null;
            	GameParticipant p2 = null;
            	
            	if (conf.isSet("TicTacToe." + id + ".Participants.AI")) {
            		// AI, if exist, check start first or not
            		
            		String symbol = conf.getString("TicTacToe." + id + ".Participants.AI.Symbol", "circle.png");
            		int diff = conf.getInt("TicTacToe." + id + ".Participants.AI.Difficulty", 1);
            		int delay = conf.getInt("TicTacToe." + id + ".Participants.AI.Delay", 20);
            		boolean startFirst = conf.getBoolean("TicTacToe." + id + ".Participants.AI.StartFirst", false);
            		
            		if (startFirst)
            			p1 = new GameAI(symbol, diff, delay, startFirst);
            		else
            			p2 = new GameAI(symbol, diff, delay, startFirst);
            	}
            	else {
            		// PlayerB, if no AI, B must be P2
            		
            		String symbol = conf.getString("TicTacToe." + id + ".Participants.PlayerB.Symbol", "cross.png");
                	// String uuid = conf.getString("TicTacToe." + id + ".Participants.PlayerB.UUID");
                	
            		// p2 = new GamePlayer(symbol, uuid);
            		p2 = new GamePlayer(symbol);
            	}
            	
            	// PlayerA, P1 when P2 exist, P2 when AI exist
            	String symbol = conf.getString("TicTacToe." + id + ".Participants.PlayerA.Symbol", "circle.png");
            	// String uuid = conf.getString("TicTacToe." + id + ".Participants.PlayerA.UUID");
            	
            	if (p1 == null)
            		// p1 = new GamePlayer(symbol, uuid);
            		p1 = new GamePlayer(symbol);
            	else
            		// p2 = new GamePlayer(symbol, uuid);
            		p2 = new GamePlayer(symbol);
            	
            	int width = conf.getInt("TicTacToe." + id + ".Settings.Width", 3);
            	int height = conf.getInt("TicTacToe." + id + ".Settings.Height", 3);
            	BlockFace facing = BlockFace.valueOf(conf.getString("TicTacToe." + id + ".Settings.Facing", "EAST"));
            	int win = conf.getInt("TicTacToe." + id + ".Settings.Win", 3);
            	int time = conf.getInt("TicTacToe." + id + ".Settings.Time", 100);
            	int expire = conf.getInt("TicTacToe." + id + ".Settings.Expire", 2400);
            	int reset = conf.getInt("TicTacToe." + id + ".Settings.Reset", 100);
            	
            	List<String> winAI = conf.getStringList("TicTacToe." + id + ".Commands.Win.AI");
            	List<String> winPlayer = conf.getStringList("TicTacToe." + id + ".Commands.Win.Player");
            	
            	List<String> loseAI = conf.getStringList("TicTacToe." + id + ".Commands.Lose.AI");
            	List<String> losePlayer = conf.getStringList("TicTacToe." + id + ".Commands.Lose.Player");
            	
            	List<String> drawAI = conf.getStringList("TicTacToe." + id + ".Commands.Draw.AI");
            	List<String> drawPlayer = conf.getStringList("TicTacToe." + id + ".Commands.Draw.Player");
            	
            	GameCommand cmd = new GameCommand(winAI, winPlayer, loseAI, losePlayer, drawAI, drawPlayer);
            	
            	
            	Location top = conf.getLocation("TicTacToe." + id + ".Locations.Top");
            	Location btm = conf.getLocation("TicTacToe." + id + ".Locations.Bottom");
            	
            	// TODO - Load incomplete game's data in world
            	
            	Game game = new Game(id, p1, p2, win, time, expire, reset, cmd, top, btm, width, height, facing);
            	games.add(game);
            	
            } catch (Exception e) {
                plugin.getLogger().warning("Error loading TicTacToe id " + id + ".");
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
	public void initializeAll() {
		plugin.getLogger().info("Initializing games...");
		
		for (Game game : games) {
			initialize(game);
		}
	}
	public void initialize(Game game) {
		initBoard(game);
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				// If AI has the first priority to move
				if (game.getPlayer1() instanceof GameAI) {
					// Invert self and opponent symbol
					game.setSelf("X");
					game.setOpponent("O");
					
					GameAI ai = (GameAI) game.getPlayer1();
					game.attemptAiMove(ai.getDifficulty(), CellState.X, ai.getSymbol());
					game.swap();
				}
			}
		}, 10);
	}
	
	/**
	 * Place buttons in world for specified game
	 * @param game The game specified
	 */
	public void initBoard(Game game) {
		for (int i = 0; i < game.getHeight(); i++) {
			for (int j = 0; j < game.getWidth(); j++) {
				
				Collection<Entity> entities = game.getTop().getWorld().getNearbyEntities(game.boardLoc[i][j], 1, 1, 1);
				for (Entity entity : entities) {
					if (entity instanceof ItemFrame)
						entity.remove();
				}
				
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
	
	public Game getGameById(String id) {
		for (Game game : games) {
			if (game.getId().equalsIgnoreCase(id))
				return game;
		}
		
		return null;
	}
	
	public Game getGameByPlayer(UUID uuid) {
		for (Game game : games) {
			GamePlayer gp = null;
			
			if (game.getPlayer1() instanceof GamePlayer)
				gp = (GamePlayer) game.getPlayer1();
			
			else if (game.getPlayer2() instanceof GamePlayer)
				gp = (GamePlayer) game.getPlayer2();
			
			if (gp != null && gp.getUniqueId().equals(uuid))
				return game;
		}
		
		return null;
	}
}
