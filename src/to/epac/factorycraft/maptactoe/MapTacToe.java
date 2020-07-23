package to.epac.factorycraft.maptactoe;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import to.epac.factorycraft.maptactoe.commands.Commands;
import to.epac.factorycraft.maptactoe.handlers.CellPickHandler;
import to.epac.factorycraft.maptactoe.tictactoe.GameManager;

public class MapTacToe extends JavaPlugin {
	
	private static MapTacToe inst;
	
	private GameManager gameManager;
	
	public void onEnable() {
		inst = this;
		
		gameManager = new GameManager(this);
		gameManager.load();
		gameManager.initialize();
			
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new CellPickHandler(), this);
		
		getCommand("MapTacToe").setExecutor(new Commands());
		
		
		
		
		
	}
	
	public void onDisable() {
		inst = null;
		
		
		
		
	}
	
	public static MapTacToe inst() {
		return inst;
	}
	
	public GameManager getGameManager() {
		return this.gameManager;
	}
}
