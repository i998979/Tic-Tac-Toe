package to.epac.factorycraft.maptactoe.handlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import to.epac.factorycraft.maptactoe.MapTacToe;
import to.epac.factorycraft.maptactoe.tictactoe.CellState;
import to.epac.factorycraft.maptactoe.tictactoe.Game;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GameAI;
import to.epac.factorycraft.maptactoe.tictactoe.participants.GamePlayer;
import to.epac.factorycraft.maptactoe.utils.Utils;

public class CellPickHandler implements Listener {
	
	private MapTacToe plugin = MapTacToe.inst();
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		
		if (action != Action.RIGHT_CLICK_BLOCK) return;
		
		Block block = event.getClickedBlock();
		Location loc = block.getLocation();
		
		if(block.getType() != Material.STONE_BUTTON) return;
		
		
		
		Game game = null;
		
		// Loop all games
		for (Game g : plugin.getGameManager().getGames()) {
			// Check if clicked button is in looping game's arena
			if (Utils.locContains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), g.getBottom(), g.getTop())) {
				game = g;
				break;
			}
		}
		
		if (game == null) return;
		
		// Now we have the game of the button clicked
		
		
		
		if (game.getPlayer1() instanceof GamePlayer) {
			GamePlayer gp = (GamePlayer) game.getPlayer1();
			
			event.setCancelled(true);
			
			if (gp.getUniqueId().equals(player.getUniqueId())) {
				if (game.getNext() == CellState.X) {
					game.place(loc, CellState.X, gp.getSymbol());
					game.swap();
					
					if (game.getPlayer2() instanceof GameAI)
						game.attemptAiMove((GameAI) game.getPlayer2());
				}
				else
					player.sendMessage("¡±cThis is not your turn!");
			}
			else {
				player.sendMessage("¡±cYou are not participant of this game. Find another one.");
			}
		}
		else if (game.getPlayer2() instanceof GamePlayer) {
			GamePlayer gp = (GamePlayer) game.getPlayer2();

			event.setCancelled(true);
			
			if (gp.getUniqueId().equals(player.getUniqueId())) {
				if (game.getNext() == CellState.O) {
					game.place(loc, CellState.O, gp.getSymbol());
					game.swap();
					
					if (game.getPlayer2() instanceof GameAI)
						game.attemptAiMove((GameAI) game.getPlayer1());
				}
				else
					player.sendMessage("¡±cThis is not your turn!");
			}
			else {
				player.sendMessage("¡±cYou are not participant of this game. Find another one.");
			}
		}
		else {
			player.sendMessage("¡±cYou are not participant of this game. Find another one.");
		}
	}
}
