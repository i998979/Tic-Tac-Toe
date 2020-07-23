package to.epac.factorycraft.maptactoe.tictactoe;

public class GameAI implements GameParticipant {
	
	String name = "AI";
	int difficulty = 1;
	
	public GameAI(String aiConfig) {
		aiConfig = aiConfig.replaceAll("\\s", "");
		
		if (aiConfig.startsWith("AI("))
			this.difficulty = Integer.parseInt(String.valueOf(aiConfig.charAt(3)));
	}
	public GameAI(String name, int difficulty) {
		this.name = name;
		this.difficulty = difficulty;
	}
	
	public int getDifficulty() {
		return difficulty;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
