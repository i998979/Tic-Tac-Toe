package to.epac.factorycraft.maptactoe.tictactoe;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.ItemUtil;

import to.epac.factorycraft.maptactoe.MapTacToe;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GameAI;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GameParticipant;

public class Game {
	
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
	private long expire;

	private Location top;
	private Location btm;

	private int width;
	private int height;



	public String[][] board;
	public Location[][] boardLoc;

	private CellState next = CellState.X;

	private long lastUpdate;

	private GameParticipant winner;
	
	// TODO - Redo X/O representation
	private String self = "O";
	private String opponent = "X";



	public Game(String id, GameParticipant player1, GameParticipant player2, int win, int time, long expire,
			Location top, Location btm, int width, int height) {

		this.id = id;
		this.player1 = player1;
		this.player2 = player2;
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

		this.boardLoc = new Location[width][height];
		// Calculate relative locations
		for (int i = 0; i < height; i++) {
			//  addY = (TopY - BtmY + 1) / height * row
			int addY = (top.getBlockY() - btm.getBlockY() + 1) / height * i;

			for (int j = 0; j < width; j++) {
				//  addX = (Largest X - Smallest X + 1) / width * col
				int addX = (Math.max(top.getBlockX(), btm.getBlockX()) - Math.min(top.getBlockX(), btm.getBlockX()) + 1)
						/ width * j;

				//  addZ = (Largest Z - Smallest Z + 1) / width * col
				int addZ = (Math.max(top.getBlockZ(), btm.getBlockZ()) - Math.min(top.getBlockZ(), btm.getBlockZ()) + 1)
						/ width * j;

				boardLoc[i][j] = top.clone().subtract(addX, addY, addZ);
			}
		}
		
		// Default it is X start first, if P1 is AI, then make O start first
		if (player1 instanceof GameAI) {
			if (((GameAI) player1).isStartFirst()) {
				next = CellState.O;
			}
		}
	}

	/** Swap which symbol should be placed in the current turn */
	public void swap() {
		this.next = (this.next == CellState.X ? CellState.O : CellState.X);
	}

	/**
	 * Find the next best move for AI
	 * 
	 * @return true if there is a move, otherwise false
	 */
	public boolean attemptAiMove(GameAI ai) {
		try {
			// Get GameAI's difficulty
			int diff = ai.getDifficulty();

			// Find next best move and move
			Move move = findBestMove(diff);

			place(boardLoc[move.row][move.col], next, ai.getSymbol());

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
		found: for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {

				if (boardLoc[i][j].equals(loc)) {
					board[i][j] = state.toString();
					break found;
				}
			}
		}

		loc.getBlock().setType(Material.AIR);

		ItemStack item = GameDisplay.createMapItem(GameDisplay.class);
		ItemUtil.getMetaTag(item).putValue("State", symbol);

		ItemFrame frame = loc.getWorld().spawn(loc, ItemFrame.class);
		frame.setItem(item);
	}

	/**
	 * Check whether there is a space to place move
	 * 
	 * @return True if there is a space, otherwise false
	 */
	public boolean hasMovesLeft() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (board[i][j].isEmpty()) return true;
			}
		}
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
	public int minimax(int depth, boolean isMax) {
		int score = evaluate();

		if (score == 10 || score == -10)
			return score;

		if (!hasMovesLeft())
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
	public int evaluate() {

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

	public long getExpire() {
		return expire;
	}
	public void setExpire(long expire) {
		this.expire = expire;
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

	// Get winner of the game
	public GameParticipant getWinner() {
		return winner;
	}
	public void setWinner(GameParticipant winner) {
		this.winner = winner;
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