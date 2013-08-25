/*
 * Minesweeper Plugin v0.4 by covertbagel for CraftBukkit 1060
 * 5 Semptember 2011
 * Licensed Under GPLv3
 */

package io.github.nnubes256.minesweeperreloaded;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

public class GameBlockListener implements Listener {
	private final Minesweeper plugin;
	
	public GameBlockListener(Minesweeper instance) {
		super();
		plugin = instance;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();
		final MapArea mapArea = findArea(block);
		if (mapArea != null) {
			// check player permission
			final Player player = event.getPlayer();
			if (!player.hasPermission("minesweeper.play")) {
				player.sendMessage(ChatColor.RED + "You don't have permission to play!");
				event.setCancelled(true);
				return;
			}
			
			// switch on type of block broke
			switch (block.getType()) {
			case REDSTONE_TORCH_ON:
			case REDSTONE_TORCH_OFF:
			case TORCH:
				// these items are ok to have removed from an area
				break;
			default:
				// nothing else should be removed from an area
				player.sendMessage(ChatColor.RED + "You don't have permission to break that!");
				event.setCancelled(true);
                break;
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		final Block block = event.getBlock();
		final MapArea mapArea = findArea(block);
		if (mapArea != null) {
			// check player permission
			final Player player = event.getPlayer();
			if (!player.hasPermission("minesweeper.play")) {
				player.sendMessage(ChatColor.RED + "You don't have permission to play!");
				event.setCancelled(true);
				return;
			}
			
			// switch on type of block placed
			switch (block.getType()) {
			case REDSTONE_TORCH_ON:
			case REDSTONE_TORCH_OFF:
				// ok so a redstone torch has been placed by a player
				// in one of the worlds map areas now let's see if
				// the block underneath is sand or wool
				final Location location = block.getLocation();
				location.setY(location.getY() - 1);
				switch (location.getBlock().getType()) {
				case SAND:
					// go down one more block so we can call clearAbove()
					location.setY(location.getY() - 1);
					clearAbove(location, mapArea);
					
					// check if the map is clear
					if (mapArea.isClear()) {
						plugin.getServer().broadcastMessage(String.format(Minesweeper.ARENA_WIN, mapArea.getName()));
					}
					break;
				case WOOL:
					// a redstone torch was placed directly on wool. let's
					// clear any unflagged blocks directly adjacent to this one
					clearAdjacentBlocks(location, mapArea);
                    break;
				default:
					player.sendMessage(ChatColor.RED + "You don't have permission to place that!");
					event.setCancelled(true);
                    break;
				}
				break;
			case TORCH:
				// it's ok if normal torches are placed anywhere
				break;
			default:
				// nothing else should be placed in the area
				player.sendMessage(ChatColor.RED + "You don't have permission to place that!");
				event.setCancelled(true);
				break;
			}
		}
	}
	
	private void explodinate(final Location location, final MapArea mapArea) {
        // We lost, so we should set the lost variable to true, so
        // we'll prevent a mega-explosion(onEntityExplode will catch it).
        mapArea.setExploded(true);

		// ok we guess we need to blow this thing up
		// we'll start by getting rid of this TNT
		final Block block = location.getBlock();
		block.setType(Material.AIR);
		
		// And then we'll spawn a primed tnt entity and MAKE IT EXPLODE AND CONQUER THE UNIVERSE MWHAHAHAHAHAHAHHAHAH
        // No seriously, we'll just make a harmless explsion ;)
        location.getWorld().createExplosion(location, 0.0F);
		
		// broadcast a message describing the sad fate
		// of this game/arena
		plugin.getServer().broadcastMessage(String.format(Minesweeper.ARENA_LOSE, mapArea.getName()));
		
		if (mapArea.isArena()) {
			// let's schedule an auto-reset for this arena
			plugin.delayedArenaReset(mapArea);
		} else {
			// let's remove this map area from the list. since
			// it isn't an arena since it's not going to
			// be around anymore
            @SuppressWarnings("unused")
            BukkitTask removeTask = new DelayedArenaRemoveTask(this.plugin, mapArea, true).runTaskLater(this.plugin, 50);
			GameHandler.getMapAreas().remove(mapArea);
		}
	}
	
	private MapArea findArea(final Block block) {
		final List<MapArea> mapAreas = GameHandler.getMapAreas();
		if (mapAreas != null) {
			for (MapArea mapArea : mapAreas) {
				if (mapArea.inArea(block)) {
					return mapArea;
				}
			}
		}
		return null;
	}

    private MapArea findAreaTotal(final Block block) {
        final List<MapArea> mapAreas = GameHandler.getMapAreas();
        if (mapAreas != null) {
            for (MapArea mapArea : mapAreas) {
                if (mapArea.inAreaTotal(block)) {
                    return mapArea;
                }
            }
        }
        return null;
    }
	
	private void clearAdjacentBlocks(final Location location, final MapArea mapArea) {
		int i;
		final Location loc = location.clone();
		
		// go to 'top left'
		loc.setX(loc.getX() + 1);
		loc.setZ(loc.getZ() + 1);
		clearAbove(loc, mapArea);
		
		// go to 'top center' and 'top right'
		for (i = 0; i < 2; i++) {
			loc.setZ(loc.getZ() - 1);
			clearAbove(loc, mapArea);
		}
		
		// go to 'middle right' and 'bottom right'
		for (i = 0; i < 2; i++) {
			loc.setX(loc.getX() - 1);
			clearAbove(loc, mapArea);
		}
		
		// go to 'bottom center' and 'bottom left'
		for (i = 0; i < 2; i++) {
			loc.setZ(loc.getZ() + 1);
			clearAbove(loc, mapArea);
		}
		
		// go to 'middle left'
		loc.setX(loc.getX() + 1);
		clearAbove(loc, mapArea);
	}
	
	private void clearAbove(final Location location, final MapArea mapArea) {
		// check type of block
		final Block bottom = location.getBlock();
		final Material bottomType = bottom.getType();
		if (bottomType != Material.WOOL && bottomType != Material.TNT) {
			return;
		}

		// make sure top block is sand
		final Location loc = location.clone();
		loc.setY(loc.getY() + 1);
		final Block top = loc.getBlock();
		if (top.getType() != Material.SAND) {
			return;
		}

		// only clear if block above sand is a redstone torch
		loc.setY(loc.getY() + 1);
		if (loc.getBlock().getType() == Material.TORCH) {
			return;
		}

		// now check if the bottom block is TNT
		if (bottomType == Material.TNT) {
			explodinate(location, mapArea);
		}

		// clear the sand
		top.setType(Material.AIR);
		mapArea.blockCleared();

		// also check if this location should have adjacent blocks cleared
		if (bottom.getData() == Minesweeper.PALETTE[0].getDyeData()) {
			clearAdjacentBlocks(location, mapArea);
		}
	}
}