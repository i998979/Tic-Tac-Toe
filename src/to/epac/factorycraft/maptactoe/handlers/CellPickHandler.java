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
import to.epac.factorycraft.maptactoe.tictactoe.BoardState;
import to.epac.factorycraft.maptactoe.tictactoe.Game;
import to.epac.factorycraft.maptactoe.tictactoe.GamePlayer;
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
			if (Utils.locContains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), g.btm, g.top)) {
				game = g;
				break;
			}
		}
		
		if (game == null) return;
		
		// Now we have the game of the button clicked
		
		if (game.playerA instanceof GamePlayer) {
			GamePlayer gp = (GamePlayer) game.playerA;
			
			event.setCancelled(true);
			
			if (gp.getUniqueId().equals(player.getUniqueId())) {
				if (game.turn == BoardState.X) {
					game.place(loc, BoardState.X);
					game.swap();
					game.attemptAiMove();
				}
				else
					player.sendMessage("��cThis is not your turn!");
			}
			else {
				player.sendMessage("��cYou are not participant of this game. Find another one.");
			}
		}
		else if (game.playerB instanceof GamePlayer) {
			GamePlayer gp = (GamePlayer) game.playerB;

			event.setCancelled(true);
			
			if (gp.getUniqueId().equals(player.getUniqueId())) {
				if (game.turn == BoardState.O) {
					game.place(loc, BoardState.O);
					game.swap();
					game.attemptAiMove();
				}
				else
					player.sendMessage("��cThis is not your turn!");
			}
			else {
				player.sendMessage("��cYou are not participant of this game. Find another one.");
			}
		}
		else {
			player.sendMessage("��cYou are not participant of this game. Find another one.");
		}
	}
}
