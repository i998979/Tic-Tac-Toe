package to.epac.factorycraft.maptactoe.handlers;

import java.util.UUID;

import org.bukkit.Bukkit;
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
	public void onPick(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Action action = event.getAction();

		if (action != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		Location loc = block.getLocation();

		if (block.getType() != Material.STONE_BUTTON) return;

		Game game = null;

		// Loop all games
		for (Game g: plugin.getGameManager().getGames()) {
			// Check if clicked button is in looping game's arena
			if (Utils.locContains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), g.getBottom(), g.getTop())) {
				game = g;
				break;
			}
		}

		if (game == null) return;

		// Now we have the game of the button clicked

		event.setCancelled(true);
		
		if (!game.isMovesLeft()) return;
		
		

		// Set game player's uuid for new players
		if (game.getPlayer1() instanceof GamePlayer &&
				((GamePlayer) game.getPlayer1()).getUniqueId() == null) {
			
			GamePlayer p1 = (GamePlayer) game.getPlayer1();
				p1.setUniqueId(uuid);
		}
		else if (game.getPlayer2() instanceof GamePlayer &&
				((GamePlayer) game.getPlayer2()).getUniqueId() == null) {
			
			GamePlayer p2 = (GamePlayer) game.getPlayer2();
			p2.setUniqueId(uuid);
		}
		
		

		if (game.getPlayer1() instanceof GamePlayer &&
			((GamePlayer) game.getPlayer1()).getUniqueId().equals(uuid)) {
			
			GamePlayer gp = (GamePlayer) game.getPlayer1();

			if (game.getNext() == CellState.X) {
				game.place(loc, CellState.X, gp.getSymbol());

				if (game.isMovesLeft()) {

					// If Player2 is AI
					if (game.getPlayer2() instanceof GameAI) {
						Game g = game;
						GameAI ai = (GameAI) game.getPlayer2();

						// Delay, then attempt to place
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							@Override
							public void run() {
								g.swap();
								
								//  Attempt to place
								g.attemptAiMove(ai.getDifficulty(), CellState.O, ai.getSymbol());

								// If there are moves left
								if (g.isMovesLeft())
									// Swap to Player1
									g.swap();
								// If no moves left
								else {
									// Run end game commands
									g.runCommands();

									// Delay, then reset game
									Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
										@Override
										public void run() {
											g.reset();
										}
									}, g.getReset());
								}
							}
						}, ((GameAI) game.getPlayer2()).getDelay());
					}
				}
				else {
					// Run end game commands
					game.runCommands();

					// Delay, then reset game
					Game g = game;
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							g.reset();
						}
					}, game.getReset());
				}
			}
			else {
				player.sendMessage("¡±cThis is not your turn!");
			}
		}
		else if (game.getPlayer2() instanceof GamePlayer &&
			((GamePlayer) game.getPlayer2()).getUniqueId().equals(uuid)) {
			
			GamePlayer gp = (GamePlayer) game.getPlayer2();

			if (game.getNext() == CellState.O) {
				game.place(loc, CellState.O, gp.getSymbol());

				if (game.isMovesLeft()) {

					// If Player1 is AI
					if (game.getPlayer1() instanceof GameAI) {
						Game g = game;
						GameAI ai = (GameAI) game.getPlayer1();

						// Delay, then attempt to place
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							@Override
							public void run() {
								g.swap();
								
								//  Attempt to place
								g.attemptAiMove(ai.getDifficulty(), CellState.X, ai.getSymbol());

								// If there are moves left
								if (g.isMovesLeft())
									// Swap to Player2
									g.swap();
								// If no moves left
								else {
									// Run end game commands
									g.runCommands();

									// Delay, then reset game
									Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
										@Override
										public void run() {
											g.reset();
										}
									}, g.getReset());
								}
							}
						}, ((GameAI) game.getPlayer1()).getDelay());
					}
				}
				else {
					// Run end game commands
					game.runCommands();

					// Delay, then reset game
					Game g = game;
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							g.reset();
						}
					}, game.getReset());
				}
			}
			else {
				player.sendMessage("¡±cThis is not your turn!");
			}
		}
		else {
			player.sendMessage("¡±cYou are not particiapant of this game. Find a new one!");
		}
	}
}