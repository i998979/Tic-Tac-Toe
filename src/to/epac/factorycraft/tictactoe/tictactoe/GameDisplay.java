package to.epac.factorycraft.tictactoe.tictactoe;

import java.io.File;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapTexture;

import to.epac.factorycraft.tictactoe.TicTacToe;

public class GameDisplay extends MapDisplay {
	
	private TicTacToe plugin = TicTacToe.inst();
	
	@Override
	public void onTick() {
		if (this.properties.containsKey("State", String.class)) {
			
			String symbol = this.properties.get("State", String.class);
			
			MapTexture img = MapTexture.fromImageFile(plugin.getDataFolder() + File.separator + symbol);
			
			this.getLayer().draw(img, 0, 0);
		}
	}
}
