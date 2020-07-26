package to.epac.factorycraft.maptactoe.tictactoe;

import java.io.File;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapTexture;

import to.epac.factorycraft.maptactoe.MapTacToe;

public class GameDisplay extends MapDisplay {
	
	private MapTacToe plugin = MapTacToe.inst();
	
	@Override
	public void onTick() {
		
		if (this.properties.containsKey("State", String.class)) {
			String symbol = this.properties.get("State", String.class);
			
			MapTexture img = null;
			
			switch (symbol) {
				case "X":
					img = MapTexture.fromImageFile(plugin.getDataFolder() + File.separator + "cross.png");
					break;
				case "O":
					img = MapTexture.fromImageFile(plugin.getDataFolder() + File.separator + "circle.png");
					break;
				default:
					// If trying to place something special (player's head), retrieve from internet, async thread
					img = MapTexture.fromImageFile(plugin.getDataFolder() + File.separator + symbol);
					break;
			}
			
			this.getLayer().draw(img, 0, 0);
		}
	}
}
