package to.epac.factorycraft.maptactoe.tictactoe;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.ItemUtil;

import to.epac.factorycraft.maptactoe.MapTacToe;

public class Game {
	
	public class Move {
		int row;
		int col;
	}
	
	/** Game board, indicates the cells are occupied or not */
	public String[][] board;
	/** Game board in world, represents real location of cells */
	public Location[][] boardLoc;
	
	/** Width of board */
	public int width = 3;
	/** Height of board */
	public int height = 3;
	
	/** Number of consecutive values */
	public int win = 3;
	
	/** Ticks allowed to pick cells */
	public int time = 100;
	/** Game expire time, after the game draws automatically */
	public int expire = 2400;
	
	/** 2 corners of the board */
	// TODO - Make sure it is same as width & height
	public Location top;
	public Location btm;
	
	/*
	 * ^^^^^^^^^^^^^^^^^^^^
	 * Load from config.yml
	 */
	
	public GameParticipant playerA = null;
	public GameParticipant playerB = null;
	
	public BoardState turn = BoardState.X;
	
	/** The game will expire and reset to default state if reached max play time */
	public long lastUpdate;
	
	public GameParticipant winner = null;
	
	
	
	public Game(String p1, String p2, int width, int height, int win, int time, int expire, Location top, Location btm) {
		this(width, height, win, time, expire, top, btm);
		
		if (p1 != null)
			if (p1.startsWith("AI"))
				playerA = new GameAI(p1);
			else
				playerA = new GamePlayer(Bukkit.getOfflinePlayer(UUID.fromString(p1)));
		
		if (p2 != null)
			if (p2.startsWith("AI"))
				playerB = new GameAI(p2);
			else
				playerB = new GamePlayer(Bukkit.getOfflinePlayer(UUID.fromString(p2)));
	}
	
	public Game(int width, int height, int win, int time, int expire, Location top, Location btm) {
		this.board = new String[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				board[i][j] = "";
			}
		}
		
		/*this.board = new String[][] {{ "", "", "" },
									 { "", "", "" },
									 { "", "", "" }};*/
									 
		this.boardLoc = new Location[width][height];
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
		
		this.width = width;
		this.height = height;
		this.win = win;
		this.time = time;
		this.top = top;
		this.btm = btm;
		this.expire = expire;
	}
	
	public void attemptAiMove() {
		// Do the logics and pick the next move
		Move move = findBestMove(board);
		
		place(boardLoc[move.row][move.col], turn);
		swap();
	}
	
	/** Swap which symbol should be placed in the current turn */
	public void swap() {
		this.turn = (this.turn == BoardState.X ? BoardState.O : BoardState.X);
	}
	
	public void place(Location loc, BoardState state) {
		// Place symbols into cell that match specified location
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				
				if (boardLoc[i][j].equals(loc))
					board[i][j] = state.toString();
			}
		}
		
		loc.getBlock().setType(Material.AIR);
		
		ItemStack item = GameDisplay.createMapItem(GameDisplay.class);
		ItemUtil.getMetaTag(item).putValue("State", state.toString());
		
		ItemFrame frame = loc.getWorld().spawn(loc, ItemFrame.class);
		frame.setItem(item);
	}
	
	public boolean isMovesLeft(String[][] board) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (board[i][j].isEmpty()) return true;
			}
		}
		return false;
	}
	
	
	String self = "O";
	String opponent = "X";
	
	// This is the evaluation function as discussed 
	// in the previous article ( http://goo.gl/sJgv68 ) 
	public int evaluate(String board[][]) {
	    // Checking for Rows for X or O victory. 
	    for (int row = 0; row < width; row++) {
	        if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
	            if (board[row][0].equals(self))
	                return +10;
	            else if (board[row][0].equals(opponent))
	                return -10;
	        }
	    }

	    // Checking for Columns for X or O victory. 
	    for (int col = 0; col < height; col++) {
	        if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
	            if (board[0][col].equals(self))
	                return +10;
	            else if (board[0][col].equals(opponent))
	                return -10;
	        }
	    }

	    // Checking for Diagonals for X or O victory. 
	    if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
	        if (board[0][0].equals(self))
	            return +10;
	        else if (board[0][0].equals(opponent))
	            return -10;
	    }

	    if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
	        if (board[0][2].equals(self))
	            return +10;
	        else if (board[0][2].equals(opponent))
	            return -10;
	    }

	    // Else if none of them have won then return 0 
	    return 0;
	}

	// This is the minimax function. It considers all 
	// the possible ways the game can go and returns 
	// the value of the board 
	public int minimax(String board[][], int depth, Boolean isMax) {
	    int score = evaluate(board);

	    // If Maximizer has won the game  
	    // return his/her evaluated score 
	    if (score == 10)
	        return score;

	    // If Minimizer has won the game  
	    // return his/her evaluated score 
	    if (score == -10)
	        return score;

	    // If there are no more moves and  
	    // no winner then it is a tie 
	    if (isMovesLeft(board) == false)
	        return 0;

	    // If this maximizer's move 
	    if (isMax) {
	        int best = -1000;

	        // Traverse all cells 
	        for (int i = 0; i < height; i++) {
	            for (int j = 0; j < width; j++) {
	                // Check if cell is empty 
	                if (board[i][j].isEmpty()) {
	                    // Make the move 
	                    board[i][j] = self;

	                    // Call minimax recursively and choose 
	                    // the maximum value 
	                    best = Math.max(best, minimax(board, depth + 1, isMax));

	                    // Undo the move 
	                    board[i][j] = "";
	                }
	            }
	        }
	        return best;
	    }

	    // If this minimizer's move 
	    else {
	        int best = 1000;

	        // Traverse all cells 
	        for (int i = 0; i < height; i++) {
	            for (int j = 0; j < width; j++) {
	                // Check if cell is empty 
	                if (board[i][j].isEmpty()) {
	                    // Make the move
	                    board[i][j] = opponent;

	                    // Call minimax recursively and choose 
	                    // the minimum value 
	                    best = Math.min(best, minimax(board, depth + 1, !isMax));

	                    // Undo the move 
	                    board[i][j] = "";
	                }
	            }
	        }
	        return best;
	    }
	}

	// This will return the best possible 
	// move for the player 
	public Move findBestMove(String board[][]) {
	    int bestVal = -1000;
	    Move move = new Move();
	    move.row = -1;
	    move.col = -1;

	    // Traverse all cells, evaluate minimax function  
	    // for all empty cells. And return the cell  
	    // with optimal value. 
	    for (int i = 0; i < height; i++) {
	        for (int j = 0; j < width; j++) {
	            // Check if cell is empty
	            if (board[i][j].isEmpty()) {
	                // Make the move 
	                board[i][j] = self;

	                // compute evaluation function for this 
	                // move. 
	                int moveVal = minimax(board, 0, false);

	                // Undo the move 
	                board[i][j] = "";
	                
	                // TODO - Save all scores, find the highest score,
	                // randomly choose moves to make it not place the same everytime
	                
	                // If the value of the current move is 
	                // more than the best value, then update 
	                // best/ 
	                if (moveVal > bestVal) {
	                    move.row = i;
	                    move.col = j;
	                    bestVal = moveVal;
	                }
	            }
	        }
	    }
	    
	    MapTacToe.inst().getLogger().info("Best val is: " + bestVal);
	    MapTacToe.inst().getLogger().info("The value of the best move is: " + move.row + ", " + move.col);
	    
	    return move;
	}
}