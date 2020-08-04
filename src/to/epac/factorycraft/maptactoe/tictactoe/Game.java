package to.epac.factorycraft.maptactoe.tictactoe;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.ItemUtil;

import net.md_5.bungee.api.ChatColor;
import to.epac.factorycraft.maptactoe.MapTacToe;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GameAI;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GameParticipant;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GamePlayer;

public class Game {
	
	private MapTacToe plugin = MapTacToe.inst();
	
	public class Move {
		int row;
		int col;
		int val;

		public Move(int row, int col, int val) {
			this.row = row;
			this.col = col;
			this.val = val;
		}
	}

	private String id;

	private int difficulty;

	private GameParticipant player1;
	private GameParticipant player2;

	private int win;
	private int time;
	private int expire;
	private int reset;
	
	private GameCommand cmd;

	private Location top;
	private Location btm;

	private int width;
	private int height;
	
	private BlockFace facing;



	public String[][] board;
	public Location[][] boardLoc;

	private CellState next = CellState.X;

	private long lastUpdate;
	
	private String self = "O";
	private String opponent = "X";



	public Game(String id, GameParticipant player1, GameParticipant player2, int win, int time, int expire,
			int reset, GameCommand cmd, Location top, Location btm, int width, int height, BlockFace facing) {
		
		this.id = id;
		this.player1 = player1;
		this.player2 = player2;
		this.win = win;
		this.time = time;
		this.expire = expire;
		this.reset = reset;
		this.cmd = cmd;
		this.top = top;
		this.btm = btm;
		this.width = width;
		this.height = height;
		this.facing = facing;

		this.board = new String[height][width];
		// Initialize board
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				board[i][j] = "";
			}
		}

		this.boardLoc = new Location[height][width];
		// Calculate relative locations
		for (int i = 0; i < height; i++) {
			//  addY = (TopY - BtmY + 2) / height * row
			int addY = (top.getBlockY() - btm.getBlockY() + 2) / height * i;

			for (int j = 0; j < width; j++) {
				//  addX = (Largest X - Smallest X + 2) / width * col
				int addX = (Math.max(top.getBlockX(), btm.getBlockX()) - Math.min(top.getBlockX(), btm.getBlockX()) + 2)
						/ width * j;

				//  addZ = (Largest Z - Smallest Z + 2) / width * col
				int addZ = (Math.max(top.getBlockZ(), btm.getBlockZ()) - Math.min(top.getBlockZ(), btm.getBlockZ()) + 2)
						/ width * j;
				
				Location loc = top.clone();
				
				loc.subtract(0, addY, 0);
				
				if (top.getBlockX() > btm.getBlockX())
					loc.subtract(addX, 0, 0);
				else
					loc.add(addX, 0, 0);
				
				if (top.getBlockZ() > btm.getBlockZ())
					loc.subtract(0, 0, addZ);
				else
					loc.add(0, 0, addZ);
				
				boardLoc[i][j] = loc;
			}
		}
	}
	
	
	
	
	
	
	/**
	 * Find the next best move for AI
	 * 
	 * @return true if there is a move, otherwise false
	 */
	public boolean attemptAiMove(int difficulty, CellState state, String symbol) {
		// If there are no moves left, can't place
		if (!isMovesLeft()) return false;
		
		try {
			// Find next best move and move
			Move move = findBestMove(difficulty);
			
			place(boardLoc[move.row][move.col], state, symbol);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Place input symbol on board
	 * 
	 * @param loc World location to place
	 * @param state Symbol to place
	 * @param symbol Symbol to draw
	 */
	public void place(Location loc, CellState state, String symbol) {

		// Place symbols into cell that match specified location
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
		ItemUtil.getMetaTag(item).putValue("State", symbol);

		ItemFrame frame = loc.getWorld().spawn(loc, ItemFrame.class);
		/*frame.setFacingDirection(facing, true);
		
		Bukkit.getScheduler().runTaskLater(plugin, runnable -> {
			frame.setItem(item);
		}, 5);*/
		frame.setItem(item);
	}
	
	/** Swap which symbol should be placed in the current turn */
	public void swap() {
		this.next = (this.next == CellState.X ? CellState.O : CellState.X);
	}
	
	
	
	/**
	 * Check if there are moves left, if no, then get board scores, determine win/lose/draw, then run commands
	 */
	public void runCommands() {
		List<String> gcmd = new ArrayList<>();
		
		int score = evaluate();
		
		String winner = "";
		String loser = "";
		String opponent = "";
		String p1 = "";
		String p2 = "";
		
		// X win
		if (score == -10) {
			
			if (player1 instanceof GameAI) {
				winner = "AI";
				gcmd = cmd.winAI;
			} else if (player1 instanceof GamePlayer) {
				winner = ((GamePlayer) player1).getPlayer().getName();
				gcmd = cmd.winPlayer;
			}
			
			
			if (player2 instanceof GameAI) {
				loser = "AI";
				gcmd = cmd.loseAI;
			} else if (player2 instanceof GamePlayer) {
				loser = ((GamePlayer) player2).getPlayer().getName();
				gcmd = cmd.losePlayer;
			}
		}
		// O win
		else if (score == 10) {
			if (player1 instanceof GameAI) {
				loser = "AI";
				gcmd = cmd.loseAI;
			} else if (player1 instanceof GamePlayer) {
				loser = ((GamePlayer) player1).getPlayer().getName();
				gcmd = cmd.losePlayer;
			}
			
			
			if (player2 instanceof GameAI) {
				winner = "AI";
				gcmd = cmd.winAI;
			} else if (player2 instanceof GamePlayer) {
				winner = ((GamePlayer) player2).getPlayer().getName();
				gcmd = cmd.winPlayer;
			}
		}
		// Draw
		else {
			if (player1 instanceof GamePlayer) {
				p1 = ((GamePlayer) player1).getPlayer().getName();
				
				// Player vs Player
				if (player2 instanceof GamePlayer) {
					p2 = ((GamePlayer) player2).getPlayer().getName();
					gcmd = cmd.drawPlayer;
				}
				// Player vs AI
				else if (player2 instanceof GameAI) {
					opponent = ((GamePlayer) player1).getPlayer().getName();
					gcmd = cmd.drawAI;
				}
			}
			if (player1 instanceof GameAI) {
				p1 = "AI";
				
				// AI vs Player
				if (player2 instanceof GamePlayer) {
					opponent = ((GamePlayer) player2).getPlayer().getName();
					gcmd = cmd.drawAI;
				}
			}
		}
		
		for (String cmd : gcmd) {
			cmd = cmd.replace("%id%", id)
					.replace("%winner%", winner)
					.replace("%loser%", loser)
					.replace("%opponent%", opponent)
					.replace("%p1%", p1)
					.replace("%p2%", p2);
			
			cmd = ChatColor.translateAlternateColorCodes('&', cmd);
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}

	public void reset() {
		if (player1 instanceof GamePlayer)
			((GamePlayer) player1).setUniqueId(null);
		if (player2 instanceof GamePlayer)
			((GamePlayer) player2).setUniqueId(null);
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				board[i][j] = "";
			}
		}
		
		next = CellState.X;
		
		plugin.getGameManager().initialize(this);
	}
	
	
	
	/**
	 * Check whether the board is empty
	 * 
	 * @return True if the board is empty, otherwise false
	 */
	public boolean isBoardEmpty() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (!board[i][j].isEmpty()) return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Check whether there is a space to place move
	 * 
	 * @return True if there is a space, otherwise false
	 */
	public boolean isMovesLeft() {
		// X/O Win: If X/O has won the game already, no more moves left
		if (evaluate() != 0) return false;
		
		// Ongoing: If no one has won the game, check board empty cells
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (board[i][j].isEmpty()) return true;
			}
		}
		
		// Draw: If no empty cells
		return false;
	}
	
	
	
	/**
	 * Find the best row&col to place
	 * 
	 * @return Best move
	 */
	public Move findBestMove(int diff) {
		Move move = new Move(-1, -1, -1000);

		if (height == 3 && width == 3) {
			int m = (height - 1) / 2;

			if (board[m][m].isEmpty()) {
				move.row = m;
				move.col = m;
				move.val = 10;

				MapTacToe.inst().getLogger().info("(M) Next best move of " + id +
						" is (" + move.row + ", " + move.col + ") with score " + move.val);
				return move;
			}
		}

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {

				if (board[i][j].isEmpty()) {

					board[i][j] = self;
					int moveVal = minimax(diff, false);
					board[i][j] = "";

					if (moveVal > move.val) {
						move.row = i;
						move.col = j;
						move.val = moveVal;
					}
				}
			}
		}

		MapTacToe.inst().getLogger().info("Next best move of " + id +
				" is (" + move.row + ", " + move.col + ") with score " + move.val);

		return move;
	}

	/**
	 * Maximize/minimize the score of self/opponent
	 * 
	 * @param depth How many steps will assume
	 * @param isMax Maximize the score or not
	 * @return Calculated score
	 */
	private int minimax(int depth, boolean isMax) {
		int score = evaluate();

		if (score == 10 || score == -10)
			return score;

		if (!isMovesLeft())
			return 0;

		if (depth == 0)
			return 0;

		if (isMax) {
			int best = -1000;

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {

					if (board[i][j].isEmpty()) {

						board[i][j] = self;
						best += Math.max(best, minimax(depth - 1, !isMax));
						board[i][j] = "";
					}
				}
			}
			return best;
		} else {
			int best = 1000;

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {

					if (board[i][j].isEmpty()) {

						board[i][j] = opponent;
						best += Math.min(best, minimax(depth - 1, isMax));
						board[i][j] = "";
					}
				}
			}
			return best;
		}
	}

	/**
	 * Evaluate the score of the moves
	 * 
	 * @return Score evaluated
	 */
	private int evaluate() {

		// Check horizontal
		for (int row = 0; row < height; row++) {
			int swin = 0;
			int owin = 0;

			for (int i = 0; i < width; i++) {
				if (board[row][i].equals(self)) {
					swin++;
					owin = 0;
				}
				if (board[row][i].equals(opponent)) {
					swin = 0;
					owin++;
				}
				if (swin == win)
					return 10;
				if (owin == win)
					return -10;
			}
		}

		// Check vertical
		for (int col = 0; col < width; col++) {
			int swin = 0;
			int owin = 0;

			for (int i = 0; i < height; i++) {
				if (board[i][col].equals(self)) {
					swin++;
					owin = 0;
				}
				if (board[i][col].equals(opponent)) {
					swin = 0;
					owin++;
				}
				if (swin == win)
					return 10;
				if (owin == win)
					return -10;
			}
		}

		// Check diagonal
		int n = height;
		if (width < height) n = width;

		int swin = 0;
		int owin = 0;

		for (int i = 0; i < n; i++) {
			if (board[i][i].equals(self)) {
				swin++;
				owin = 0;
			}
			if (board[i][i].equals(opponent)) {
				swin = 0;
				owin++;
			}
			if (swin == win)
				return 10;
			if (owin == win)
				return -10;
		}

		if (swin != win) swin = 0;
		if (owin != win) owin = 0;

		// Check anti-diagonal
		for (int i = 0; i < n; i++) {
			if (board[i][n - i - 1].equals(self)) {
				swin++;
				owin = 0;
			}
			if (board[i][n - i - 1].equals(opponent)) {
				swin = 0;
				owin++;
			}
			if (swin == win)
				return 10;
			if (owin == win)
				return -10;
		}

		return 0;
	}






	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	

	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	
	
	
	public GameParticipant getPlayer1() {
		return player1;
	}
	public void setPlayer1(GameParticipant player1) {
		this.player1 = player1;
	}
	
	
	
	public GameParticipant getPlayer2() {
		return player2;
	}
	public void setPlayer2(GameParticipant player2) {
		this.player2 = player2;
	}
	
	
	
	public int getWin() {
		return win;
	}
	public void setWin(int win) {
		this.win = win;
	}
	
	
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
	
	
	public int getExpire() {
		return expire;
	}
	public void setExpire(int expire) {
		this.expire = expire;
	}
	
	
	
	public int getReset() {
		return reset;
	}
	public void setReset(int reset) {
		this.reset = reset;
	}
	
	
	
	public GameCommand getGameCommand() {
		return cmd;
	}
	public void setGameCommand(GameCommand cmd) {
		this.cmd = cmd;
	}
	
	
	
	public Location getTop() {
		return top;
	}
	public void setTop(Location top) {
		this.top = top;
	}
	
	
	
	public Location getBottom() {
		return btm;
	}
	public void setBottom(Location btm) {
		this.btm = btm;
	}
	
	
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	
	
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	
	
	public BlockFace getFacing() {
		return facing;
	}
	public void setFacing(BlockFace facing) {
		this.facing = facing;
	}
	
	
	
	public CellState getNext() {
		return next;
	}
	public void setNext(CellState next) {
		this.next = next;
	}
	
	
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
	
	// Symbol represent Player1 & Player2
	public String getSelf() {
		return self;
	}
	public void setSelf(String self) {
		this.self = self;
	}
	
	
	
	public String getOpponent() {
		return opponent;
	}
	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}
}