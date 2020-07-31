package to.epac.factorycraft.maptactoe.tictactoe.participants;

public class GameAI implements GameParticipant {
	
	private String symbol;
	private int diff = 1;
	private int delay = 20;
	private boolean startFirst;
	
	
	
	public GameAI(String symbol, int difficulty, int delay, boolean startFirst) {
		this.symbol = symbol;
		this.diff = difficulty;
		this.delay = delay;
		this.startFirst = startFirst;
	}
	
	
	
	
	
	
	public int getDifficulty() {
		return diff;
	}
	public int getDelay() {
		return delay;
	}
	public boolean isStartFirst() {
		return startFirst;
	}
	
	
	
	@Override
	public String getSymbol() {
		return symbol;
	}
}
