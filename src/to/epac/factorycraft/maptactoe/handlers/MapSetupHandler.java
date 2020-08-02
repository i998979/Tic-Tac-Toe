package to.epac.factorycraft.maptactoe.handlers;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MapSetupHandler implements Listener {
	
	@EventHandler
	public void onClick(BlockBreakEvent event) {
		Location loc = event.getBlock().getLocation();
		
		
	}
}
