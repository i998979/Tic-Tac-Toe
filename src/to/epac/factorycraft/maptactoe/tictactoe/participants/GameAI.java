package to.epac.factorycraft.maptactoe.tictactoe.participants;

public class GameAI implements GameParticipant {
	
	private String symbol;
	private int diff = 1;
	private boolean startFirst;
	
	
	
	public GameAI(String symbol, int difficulty, boolean startFirst) {
		this.symbol = symbol;
		this.diff = difficulty;
		this.startFirst = startFirst;
	}
	
	
	
	
	
	
	public int getDifficulty() {
		return diff;
	}
	public boolean isStartFirst() {
		return startFirst;
	}
	
	
	
	@Override
	public String getSymbol() {
		return symbol;
	}
}
