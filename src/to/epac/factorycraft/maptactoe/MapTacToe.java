package to.epac.factorycraft.maptactoe;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import to.epac.factorycraft.maptactoe.commands.Commands;
import to.epac.factorycraft.maptactoe.handlers.CellPickHandler;
import to.epac.factorycraft.maptactoe.handlers.MapClickHandler;
import to.epac.factorycraft.maptactoe.handlers.MapSetupHandler;
import to.epac.factorycraft.maptactoe.tictactoe.GameManager;

public class MapTacToe extends JavaPlugin {
	
	private static MapTacToe inst;
	
	private static File configFile;
	
	private GameManager gameManager;
	
	public void onEnable() {
		inst = this;
		
		gameManager = new GameManager(this);
		gameManager.load();
		gameManager.initializeAll();
			
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new CellPickHandler(), this);
		pm.registerEvents(new MapClickHandler(), this);
		pm.registerEvents(new MapSetupHandler(), this);
		
		getCommand("MapTacToe").setExecutor(new Commands());
		
		
		
		configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "Configuration not found. Generating the default one.");

            getConfig().options().copyDefaults(true);
            saveConfig();
        }
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
