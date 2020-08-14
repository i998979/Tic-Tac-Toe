package to.epac.factorycraft.tictactoe;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import to.epac.factorycraft.tictactoe.commands.Commands;
import to.epac.factorycraft.tictactoe.handlers.CellPickHandler;
import to.epac.factorycraft.tictactoe.handlers.MapClickHandler;
import to.epac.factorycraft.tictactoe.tictactoe.GameManager;

public class TicTacToe extends JavaPlugin {
	
	private static TicTacToe inst;
	
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
		
		getCommand("TicTacToe").setExecutor(new Commands());
		
		
		
		configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "Configuration not found. Generating the default one.");

            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        
        File circle = new File(getDataFolder(), "circle.png");
		if (!circle.exists()) {
			saveResource("circle.png", false);
		}
		
		File cross = new File(getDataFolder(), "cross.png");
		if (!cross.exists()) {
			saveResource("cross.png", false);
		}
        
	}
	
	public void onDisable() {
		inst = null;
		
		
		
		
	}
	
	public static TicTacToe inst() {
		return inst;
	}
	
	public GameManager getGameManager() {
		return this.gameManager;
	}
}
