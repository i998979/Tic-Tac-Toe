package to.epac.factorycraft.tictactoe.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.epac.factorycraft.tictactoe.TicTacToe;
import to.epac.factorycraft.tictactoe.tictactoe.Game;

public class Commands implements CommandExecutor {
	
	private TicTacToe plugin = TicTacToe.inst();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("TicTacToe.Admin")) {
			sender.sendMessage("¡±cYou don't have permission to perform this command.");
			return false;
		}
		
		if (args.length == 0) {
			helpPage(sender);
			return false;
		}
		
		if (args[0].equalsIgnoreCase("help")) {
			helpPage(sender);
		}
		// /tictactoe reset 3x3AI
		else if (args[0].equalsIgnoreCase("reset")) {
			if (args.length >= 2) {
				Game game = plugin.getGameManager().getGameById(args[1]);
				if (game != null) {
					game.reset();
					sender.sendMessage("¡±eTicTacToe board \"¡±a" + game.getId() + "¡±e\" was reset.");
				}
				else
					sender.sendMessage("¡±cThe specified board does not exist.");
			}
			else
				sender.sendMessage("¡±cPlease enter board id to reset.");
		}
		// /tictactoe setup 3x3AI top
		else if (args[0].equalsIgnoreCase("setup")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				Location loc = player.getLocation().getBlock().getLocation().clone();
				loc.setYaw(0); loc.setPitch(0);
				
				
				if (args.length >= 3) {
					Game game = plugin.getGameManager().getGameById(args[1]);
					if (game != null) {
						if (args[2].equalsIgnoreCase("top")) {
							plugin.getConfig().set("TicTacToe." + game.getId() + ".Locations.Top", loc);
							plugin.saveConfig();
							game.setTop(loc);
							player.sendMessage("¡±aTop-left corner of \"¡±e" + game.getId() + "¡±a\" was set to your location.");
						}
						else if (args[2].equalsIgnoreCase("bottom")) {
							plugin.getConfig().set("TicTacToe." + game.getId() + ".Locations.Bottom", loc);
							plugin.saveConfig();
							game.setBottom(loc);
							player.sendMessage("¡±aBottom-right corner of \"¡±e" + game.getId() + "¡±a\" was set to your location.");
						}
					}
					else
						sender.sendMessage("¡±cThe specified board does not exist.");
				}
				else if (args.length == 2)
					sender.sendMessage("¡±cPlease enter \"¡±etop¡±c\" for top-left corner or \"¡±ebottom¡±c\" for bottom-right corner of the board.");
				else
					sender.sendMessage("¡±cPlease enter board id to setup.");
			}
			else
				sender.sendMessage("¡±cYou must be a player to execute this command.");
		}
		else {
			helpPage(sender);
		}
		
		
		return false;
	}
	
	
	
	public void helpPage(CommandSender sender) {
		sender.sendMessage("¡±e----------¡±9Tic ¡±3Tac ¡±bToe¡±e----------");
		sender.sendMessage("¡±7Main command: ¡±f/ttt");
		sender.sendMessage("¡±b/ttt reset <board>: ¡±3Reset specified game board.");
		sender.sendMessage("¡±b/ttt setup <board> <top/bottom>: ¡±3Set top-left or bottom-right corner of specified game board.");
		sender.sendMessage("¡±b/ttt help <board>: ¡±3Show the help page.");
		sender.sendMessage("¡±e----------¡±9Tic ¡±3Tac ¡±bToe¡±e----------");
	}
}
