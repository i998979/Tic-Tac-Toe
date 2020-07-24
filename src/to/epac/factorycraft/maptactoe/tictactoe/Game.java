package to.epac.factorycraft.maptactoe.tictactoe;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.ItemUtil;

import to.epac.factorycraft.maptactoe.tictactoe.participants.GameAI;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GameParticipant;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GamePlayer;

public class Game {
	
	public class Move {
		int row;
		int col;
	}
	
	private String id;
	
	private int difficulty;
	
	private GameParticipant player1;
	private GameParticipant player2;
	
	private int win;
	private int time;
	private long expire;
	
	private Location top;
	private Location btm;
	
	private int width;
	private int height;
	
	
	
	public String[][] board;
	public Location[][] boardLoc;
	
	public BoardState next = BoardState.X;
	
	private long lastUpdate;
	
	private GameParticipant winner;
	
	private String self = "O";
	private String opponent = "X";
	
	
	
	public Game(String id, int difficulty, String player1, String player2, int win, int time, long expire,
			Location top, Location btm, int width, int height) {
		
		this(id, difficulty, win, time, expire, top, btm, width, height);
		
		GameParticipant p1 = null;
		GameParticipant p2 = null;
		
		if (player1 != null)
			if (player1.startsWith("AI"))
				p1 = new GameAI(player1);
			else
				p1 = new GamePlayer(Bukkit.getOfflinePlayer(UUID.fromString(player1)));
		
		if (player2 != null)
			if (player2.startsWith("AI"))
				p2 = new GameAI(player2);
			else
				p2 = new GamePlayer(Bukkit.getOfflinePlayer(UUID.fromString(player2)));
		
		this.player1 = p1;
		this.player2 = p2;
	}
	public Game(String id, int difficulty, int win, int time, long expire, Location top, Location btm, int width, int height) {
		this.id = id;
		this.difficulty = difficulty;
		this.win = win;
		this.time = time;
		this.expire = expire;
		this.top = top;
		this.btm = btm;
		this.width = width;
		this.height = height;
		
		this.board = new String[width][height];
		// Initialize board
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				board[i][j] = "";
			}
		}
		/*this.board = new String[][] {{"", "", ""},
									 {"", "", ""},
									 {"", "", ""}};*/
		
		this.boardLoc = new Location[width][height];
		// Calculate relative locations
		for (int i = 0; i < height; i++) {
			//  addY = (TopY - BtmY + 1) / height * row
			int addY = (top.getBlockY() - btm.getBlockY() + 1) / height * i;
			
			for (int j = 0; j < width; j++) {
				//  addX = (Largest X - Smallest X + 1) / width * col
				int addX = (Math.max(top.getBlockX(), btm.getBlockX()) - Math.min(top.getBlockX(), btm.getBlockX()) + 1) / width * j;
				
				//  addZ = (Largest Z - Smallest Z + 1) / width * col
				int addZ = (Math.max(top.getBlockZ(), btm.getBlockZ()) - Math.min(top.getBlockZ(), btm.getBlockZ()) + 1) / width * j;
				
				boardLoc[i][j] = top.clone().subtract(addX, addY, addZ);
			}
		}
	}
	
	/** Swap which symbol should be placed in the current turn */
	public void swap() {
		this.next = (this.next == BoardState.X ? BoardState.O : BoardState.X);
	}
	
	/**
	 * Find the next best move for AI
	 * 
	 * @return true if there is a move, otherwise false
	 */
	public boolean attemptAiMove() {
		try {
			// TODO - Find next best move and move
			Move move = new Move();
			// Move move = findBestMove();
			
			place(boardLoc[move.row][move.col], next);
			
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Place input symbol on board
	 * 
	 * @param loc World location to place
	 * @param state Symbol to place
	 */
	public void place(Location loc, BoardState state) {
		// Place symbols into cell that match specified location
		// TODO - break labels to avoid unnecessary checks
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				
				if (boardLoc[i][j].equals(loc)) {
					board[i][j] = state.toString();
					break;
				}
			}
		}
		
		loc.getBlock().setType(Material.AIR);
		
		ItemStack item = GameDisplay.createMapItem(GameDisplay.class);
		ItemUtil.getMetaTag(item).putValue("State", state.toString());
		
		ItemFrame frame = loc.getWorld().spawn(loc, ItemFrame.class);
		frame.setItem(item);
	}
	
	/**
	 * Check whether there is a space to place move
	 * 
	 * @return True if there is a space, otherwise false
	 */
	public boolean isMovesLeft() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (board[i][j].isEmpty()) return true;
			}
		}
		return false;
	}
	
	
	
}
