package to.epac.factorycraft.maptactoe.tictactoe.participants;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class GamePlayer implements GameParticipant {
	
	OfflinePlayer player;
	
	public GamePlayer(OfflinePlayer player) {
		this.player = player;
	}
	
	
	
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	public UUID getUniqueId() {
		return player.getUniqueId();
	}
	
	
	
	@Override
	public String getName() {
		return player.getName();
	}
}
