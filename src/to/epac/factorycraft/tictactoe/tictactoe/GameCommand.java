package to.epac.factorycraft.tictactoe.tictactoe;

import java.util.List;

public class GameCommand {
	
	/** AI won player */
	List<String> winAI;
	/** Player won player */
	List<String> winPlayer;
	
	/** AI lose player */
	List<String> loseAI;
	/** Player lose to player */
	List<String> losePlayer;
	
	/** AI draw player */
	List<String> drawAI;
	/** Player draw player */
	List<String> drawPlayer;
	
	public GameCommand(List<String> winAI, List<String> winPlayer,
			List<String> loseAI, List<String> losePlayer,
			List<String> drawAI, List<String> drawPlayer) {
		this.winAI = winAI;
		this.winPlayer = winPlayer;
		this.loseAI = loseAI;
		this.losePlayer = losePlayer;
		this.drawAI = drawAI;
		this.drawPlayer = drawPlayer;
	}
}
