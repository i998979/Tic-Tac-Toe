package to.epac.factorycraft.tictactoe.tictactoe.participants;

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
	public GamePlayer(String symbol) {
		this.symbol = symbol;
	}
	
	
	
	
	
	
	public UUID getUniqueId() {
		return uuid;
	}
	public void setUniqueId(UUID uuid) {
		this.uuid = uuid;
	}
	
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}
	
	
	
	@Override
	public String getSymbol() {
		return symbol;
	}
	@Override
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
