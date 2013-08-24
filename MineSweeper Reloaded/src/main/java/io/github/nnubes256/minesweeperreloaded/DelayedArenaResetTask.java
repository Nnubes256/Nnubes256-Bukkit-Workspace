package io.github.nnubes256.minesweeperreloaded;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayedArenaResetTask extends BukkitRunnable {
	
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	private final MapArea arena;
	
    public DelayedArenaResetTask(JavaPlugin plugin, MapArea arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

	public void run() {
		Minesweeper.arenaResetWorker(arena);
		Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "The Minesweeper arena " + ChatColor.GOLD + arena.getName() + ChatColor.AQUA + "has been reset.");
    }
}
