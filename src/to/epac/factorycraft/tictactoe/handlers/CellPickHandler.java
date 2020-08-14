package to.epac.factorycraft.tictactoe.handlers;

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

import to.epac.factorycraft.tictactoe.TicTacToe;
import to.epac.factorycraft.tictactoe.tictactoe.Game;
import to.epac.factorycraft.tictactoe.tictactoe.Game.CellState;
import to.epac.factorycraft.tictactoe.tictactoe.participants.GameAI;
import to.epac.factorycraft.tictactoe.tictactoe.participants.GamePlayer;
import to.epac.factorycraft.tictactoe.utils.Utils;

public class CellPickHandler implements Listener {

	private TicTacToe plugin = TicTacToe.inst();

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
		
		// If the board has no moves left
		if (!game.isMovesLeft()) return;



		// Set game player's uuid for new players
		if (game.getPlayer1() instanceof GamePlayer && ((GamePlayer) game.getPlayer1()).getUniqueId() == null)
			((GamePlayer) game.getPlayer1()).setUniqueId(uuid);

		else if (game.getPlayer2() instanceof GamePlayer && ((GamePlayer) game.getPlayer2()).getUniqueId() == null)
			((GamePlayer) game.getPlayer2()).setUniqueId(uuid);


		// If Player1 is GamePlayer and player exist
		if (game.getPlayer1() instanceof GamePlayer && ((GamePlayer) game.getPlayer1()).getUniqueId().equals(uuid)) {
			GamePlayer gp = (GamePlayer) game.getPlayer1();
			
			// If next placer is X
			if (game.getNext() == CellState.X) {
				// Place symbol on map and database
				game.place(loc, CellState.X, gp.getSymbol());
				
				// If the board is not ended
				if (game.isMovesLeft()) {
					// Swap to opponent
					game.swap();

					// If Player2 is AI
					if (game.getPlayer2() instanceof GameAI) {
						GameAI ai = (GameAI) game.getPlayer2();

						Game g = game;
						// Delay, then attempt to place
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							@Override
							public void run() {
								//  Attempt to place
								g.attemptAiMove(ai.getDifficulty(), CellState.O, ai.getSymbol());
								
							 // If there are moves left
								if (g.isMovesLeft()) {
									// Swap to Player1
									g.swap();
								} else {
									// Run end game commands
									g.runCommands(g.evaluate());

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
				} else {
					// Run end game commands
					game.runCommands(game.evaluate());

					// Delay, then reset game
					Game g = game;
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							g.reset();
						}
					}, game.getReset());
				}
			} else {
				player.sendMessage("¡±cThis is not your turn!");
			}
		}
		
		
		// Player2
		else if (game.getPlayer2() instanceof GamePlayer && ((GamePlayer) game.getPlayer2()).getUniqueId().equals(uuid)) {
			GamePlayer gp = (GamePlayer) game.getPlayer2();

			if (game.getNext() == CellState.O) {
				game.place(loc, CellState.O, gp.getSymbol());

				if (game.isMovesLeft()) {
					game.swap();

					if (game.getPlayer1() instanceof GameAI) {
						Game g = game;
						GameAI ai = (GameAI) game.getPlayer1();

						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							@Override
							public void run() {
								g.attemptAiMove(ai.getDifficulty(), CellState.X, ai.getSymbol());

								if (g.isMovesLeft()) {
									g.swap();
								} else {
									g.runCommands(g.evaluate());

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
				} else {
					game.runCommands(game.evaluate());

					Game g = game;
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							g.reset();
						}
					}, game.getReset());
				}
			} else {
				player.sendMessage("¡±cThis is not your turn!");
			}
		} else {
			player.sendMessage("¡±cYou are not particiapant of this game. Find another game!");
		}
	}
}