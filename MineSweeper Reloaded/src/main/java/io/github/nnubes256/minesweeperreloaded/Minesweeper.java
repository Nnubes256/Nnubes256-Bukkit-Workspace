/*
    Copyright 2013 Nnubes256, formely made by covertbagel(notice below).

    Minesweeper Reloaded is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Minesweeper Reloaded is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * Minesweeper Plugin v0.4 by covertbagel for CraftBukkit 1060
 * 5 Semptember 2011
 * Licensed Under GPLv3
 */

package io.github.nnubes256.minesweeperreloaded;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.desht.dhutils.LogUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Minesweeper extends JavaPlugin {
	// map configs - mines and size
	public static final int[] SMALL = {10, 8};
	public static final int[] MEDIUM = {40, 16};
	public static final int[] LARGE = {90, 24};
	
	// indexes for the map configs
	public static final int MINES = 0;
	public static final int SIZE = 1;
	
	public static final DyeColor[] PALETTE = {
		DyeColor.WHITE,
		DyeColor.LIGHT_BLUE,
		DyeColor.LIME,
		DyeColor.RED,
		DyeColor.BLUE,
		DyeColor.GREEN,
		DyeColor.PINK,
		DyeColor.CYAN,
		DyeColor.BLACK
	};
	
	public static final String PERMISSION_DENIED = "[MineSweeper] Permission denied";
	protected static final String ARENA_NOT_FOUND = "[MineSweeper] Arena %s does not exist";
	protected static final String ARENA_FOUND = "[MineSweeper] Arena %s already exists";
	protected static final String ARENA_CREATED = "[MineSweeper] Arena %s created by %s";
	protected static final String ARENA_RESET = "[MineSweeper] Arena %s reset by %s";
	protected static final String ARENA_VITRIFIED = "[MineSweeper] Arena %s vitrified by %s";
	protected static final String ARENA_WIN = "[MineSweeper] Arena %s has been cleared";
	protected static final String ARENA_LOSE = "[MineSweeper] Arena %s has been exploded";
	protected static final String ARENA_WILL_RESET = "[MineSweeper] Arena %s will automatically reset in %d seconds";
    protected static final String[] ABOUT = {
            ChatColor.DARK_AQUA + "-----------------" + ChatColor.GOLD +" About " + ChatColor.DARK_AQUA + "-----------------",
            ChatColor.GOLD + "MineSweeper Reloaded" + ChatColor.AQUA +" v0.0.1 "+ ChatColor.GRAY +" by "+ ChatColor.AQUA + "Nnubes256"+ ChatColor.GRAY +".",
            ChatColor.GRAY + "A rewrite of " + ChatColor.GOLD + "MineSweeper" + ChatColor.GRAY + " by " + ChatColor.GOLD + "covertbagel " + ChatColor.GRAY + ".",
            ChatColor.GOLD + "      :::" + ChatColor.BLUE + " ::::    ::::   ::::::::  ",
            ChatColor.GOLD + "     :+:" + ChatColor.BLUE + "  +:+:+: :+:+:+ :+:    :+: ",
            ChatColor.GOLD + "    +:+" + ChatColor.BLUE + "   +:+ +:+:+ +:+ +:+        ",
            ChatColor.GOLD + "   +#+" + ChatColor.BLUE + "    +#+  +:+  +#+ +#++:++#++ ",
            ChatColor.GOLD + "  +#+" + ChatColor.BLUE + "     +#+       +#+        +#+ ",
            ChatColor.GOLD + " #+#" + ChatColor.BLUE + "      #+#       #+# #+#    #+# ",
            ChatColor.GOLD + "###" + ChatColor.BLUE + "       ###       ###  ########  ",
            ChatColor.GRAY + "For help and commands, type " + ChatColor.GOLD + "/ms " + ChatColor.BLUE + "?",
            ChatColor.DARK_AQUA + "-----------------------------------------"
    };

    protected static CommandHandling ch = null;
    protected static GameHandler gh = null;

    public static Boolean isWEEnabled;
    public static WorldEditPlugin wep;
    private Plugin we;

	private GameBlockListener blockListener;
    private static Minesweeper instance;

    @Override
	public void onEnable() {

        // First, we set an instance of the plugin, so we can make
        // instances of our classes
        setInstance(this);

        // Then, we make the instances
        if(gh == null) gh = new GameHandler(getInstance());
        if(ch == null) ch = new CommandHandling(getInstance(), getGHInstance());

		// And we set up the block listener
		blockListener = new GameBlockListener(this);

        // We get the plugin manager for the next things
		PluginManager pluginManager = getServer().getPluginManager();

        // Now, lets register our block listener
		pluginManager.registerEvents(blockListener, this);

        // And now the WorldEdit support
        // First, we try to get the plugin
        we = pluginManager.getPlugin("WorldEdit");

        // We couldn't find the plugin...
        if(we == null) {

            // Warn console and set to false a boolean used
            // for check if the plugin can use WorldEdit
            this.getLogger().warning("WorldEdit wasn't found. You won't be able to restore terrain when creating/destroying map areas.");
            isWEEnabled = false;
        // Or we could?
        } else {

            // Get an instance of WorldEdit, notify
            // console and set to true the boolean
            // explained above
            wep = ((WorldEditPlugin) we);
            this.getLogger().info("WorldEdit was found. You will be able to restore terrain when creating/destroying map areas.");
            isWEEnabled = true;
        }

        // Init necessary library(so we don't get NullPointerExceptions when working with MapArea's cuboids)
        LogUtils.init(this);

        // Generate the folder of schematics and that stuff.
        // My favorite class of the plugin. What a shame I didn't made it :(
        // Thanks to GNU licenses for give me a form to use this code :D
        // TODO: I don't find a more awersome way to load configs, so I should use this class for accomplish that.
        DirectoryStructure.setup();
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info("Disabled.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return CommandHandling.parseMSCommand(sender, command, label, args);
    }

    private void setInstance(Minesweeper minesweeper) {
        instance = minesweeper;
    }

    public static Minesweeper getInstance() {
        return instance;
    }

    public GameHandler getGHInstance() {
        return gh;
    }

    protected CommandHandling getCHInstance() {
        return ch;
    }
	
	public void delayedArenaReset(final MapArea arena) {
		int resetDelay = 100;
        getServer().broadcastMessage(String.format(ARENA_WILL_RESET, arena.getName(), resetDelay));
        @SuppressWarnings("unused")
		BukkitTask task = new DelayedArenaResetTask(this, arena).runTaskLater(this, resetDelay);
	}
}
