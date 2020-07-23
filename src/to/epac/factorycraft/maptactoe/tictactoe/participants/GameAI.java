package to.epac.factorycraft.maptactoe.tictactoe.participants;

public class GameAI implements GameParticipant {
	
	private String name = "AI";
	private int diff = 1;
	
	public GameAI(String aiConfig) {
		String conf = aiConfig.replaceAll("\\s", "");
		
		if (conf.startsWith("AI(")) {
			try {
				this.diff = Integer.parseInt(String.valueOf(conf.charAt(3)));
			} catch (NumberFormatException e) {
				this.diff = 1;
			}
		}
	}
	public GameAI(String name, int difficulty) {
		this.name = name;
		this.diff = difficulty;
	}
	
	
	
	public int getDifficulty() {
		return diff;
	}
	
	
	
	@Override
	public String getName() {
		return name;
	}
}
