package to.epac.factorycraft.maptactoe.tictactoe.participants;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class GamePlayer implements GameParticipant {
	
	private String symbol;
	private UUID uuid;
	
	
	
	public GamePlayer(String symbol, String uuid) {
		this(symbol, UUID.fromString(uuid));
	}
	public GamePlayer(String symbol, UUID uuid) {
		this.symbol = symbol;
		this.uuid = uuid;
	}
	
	
	
	
	
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}
	
	
	
	@Override
	public String getSymbol() {
		return symbol;
	}
}
