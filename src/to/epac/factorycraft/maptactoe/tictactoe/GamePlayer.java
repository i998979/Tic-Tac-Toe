package to.epac.factorycraft.maptactoe.tictactoe;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class GamePlayer implements GameParticipant {
	
	OfflinePlayer player;
	
	public GamePlayer(OfflinePlayer player) {
		this.player = player;
	}

	@Override
	public String getName() {
		return player.getName();
	}
	
	public UUID getUniqueId() {
		return player.getUniqueId();
	}
	
}
