package io.github.nnubes256.minesweeperreloaded;

import io.github.nnubes256.minesweeperreloaded.MapArea;
import io.github.nnubes256.minesweeperreloaded.Minesweeper;
import io.github.nnubes256.minesweeperreloaded.utils.TerrainBackup;
import me.desht.dhutils.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayedArenaRemoveTask extends BukkitRunnable {

    @SuppressWarnings("unused")
    private final JavaPlugin plugin;
    private final MapArea arena;
    private final Boolean globalNotice;

    public DelayedArenaRemoveTask(JavaPlugin plugin, MapArea arena, Boolean sayToEveryone) {
        this.plugin = plugin;
        this.arena = arena;
        this.globalNotice = sayToEveryone;
    }

    @Override
    public void run() {
        arenaRemoveWorker(arena);
        if(globalNotice) {
            if(arena.getName().equals("quickgame")) {
                Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "");
            }
            Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "Minesweeper zone " + arena.getName() + " has been removed.");
        }
    }

    protected void arenaRemoveWorker(final MapArea arena) {
        Cuboid area = arena.area;
        Boolean restored = false;
        if (Minesweeper.wep != null && Minesweeper.isWEEnabled) {
            // Let
            restored = TerrainBackup.reload(arena);
        }

        if (!restored) {
            // We couldn't restore the original terrain - just set the map's cuboid to air.
            area.fill(0, (byte) 0);
        }
    }
}
