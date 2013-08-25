package io.github.nnubes256.minesweeperreloaded;

import io.github.nnubes256.minesweeperreloaded.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameHandler {

    private static Minesweeper instance;

    private static final List<MapArea> mapAreas = new ArrayList<MapArea>();
    private static final HashMap<String, MapArea> arenas = new HashMap<String, MapArea>();

    public GameHandler(Minesweeper instance) {
        GameHandler.instance = instance;
    }

    public static List<MapArea> getMapAreas() {
        return mapAreas;
    }

    public static HashMap<String, MapArea> getArenas() {
        return arenas;
    }

    public boolean gameCreate(final CommandSender sender, final String argument) {
        // parse arguments
        final int[] info = parseSize(argument);
        if (info == null) {
            return false;
        }

        // get player location
        final Player player = (Player) sender;
        final Location location = MiscUtils.fixLocation(player.getLocation());

        // Create a new MapArea instance and init it.
        MapArea newMapArea = new MapArea(location, info, ((Player) sender), false);
        newMapArea.init();

        // setup area
        newMapArea.setup(Material.STONE);

        // give player some normal and redstone torches
        MiscUtils.givePlayerStuff(player);

        return true;
    }

    public boolean arenaCreate(final CommandSender sender, final String[] arguments) {
        // parse arguments
        if (arguments.length != 2) {
            return false;
        }
        final String name = arguments[0];
        final int[] info = parseSize(arguments[1]);
        if (name == null || name.length() == 0 || info == null) {
            return false;
        }

        // try to look for arena with this name
        final MapArea arena = arenas.get(name);
        if (arena != null) {
            sender.sendMessage(String.format(Minesweeper.ARENA_FOUND, name));
            return true;
        }

        // get player location
        final Player player = (Player) sender;
        final Location location = MiscUtils.fixLocation(player.getLocation());

        // Create map instance and init it
        MapArea mapArea = new MapArea(location.clone(), info, ((Player) sender), true, name);
        mapArea.init();

        // setup area
        HashMap<String, Boolean> createArgs = new HashMap<String, Boolean>();
        createArgs.put("withWalls", true);
        mapArea.setup(Material.OBSIDIAN, createArgs);

        // give player some normal and redstone torches
        MiscUtils.givePlayerStuff(player);

        sender.getServer().broadcastMessage(String.format(Minesweeper.ARENA_CREATED, name, player.getName()));
        return true;
    }

    public boolean arenaReset(CommandSender sender, String name) {
        // parse arguments;
        if (name == null) {
            return false;
        }

        // look for arena with this name
        final MapArea arena = arenas.get(name);
        if (arena == null) {
            sender.sendMessage(String.format(Minesweeper.ARENA_NOT_FOUND, name));
            return true;
        }

        arenaResetWorker(arena);

        // give player some normal and redstone torches
        final Player player = (Player) sender;
        MiscUtils.givePlayerStuff(player);

        instance.getServer().broadcastMessage(String.format(Minesweeper.ARENA_RESET, name, player.getName()));
        return true;
    }

    protected static void arenaResetWorker(final MapArea arena) {
        arena.setup(Material.OBSIDIAN);
        arena.reset();
    }

    public boolean arenaTeleport(CommandSender sender, String name) {
        // parse arguments
        if (name == null) {
            return false;
        }

        // look for arena with this name
        final MapArea arena = arenas.get(name);
        if (arena == null) {
            sender.sendMessage(Minesweeper.ARENA_NOT_FOUND);
            return true;
        }

        // move player to location
        final Player player = (Player) sender;
        player.teleport(arena.getLocation());

        // give player some normal and redstone torches
        MiscUtils.givePlayerStuff(player);

        return true;
    }

    protected boolean arenaVitrify(final CommandSender sender, final String name) {

        if (name == null || name.length() == 0) {
            return false;
        }

        final MapArea arena = arenas.get(name);
        if (arena == null) {
            sender.sendMessage(Minesweeper.ARENA_NOT_FOUND);
            return true;
        }
        // remove this arena from the arenas list
        arenas.remove(name);

        // modify it so it will be removed from mapAreas list after explosion
        arena.vitrify();

        instance.getServer().broadcastMessage(String.format(Minesweeper.ARENA_VITRIFIED, name, sender.getName()));
        return true;
    }


    public static int[] parseSize(final String size) {
        if (size.equalsIgnoreCase("small")) {
            return Minesweeper.SMALL;
        } else if (size.equalsIgnoreCase("medium")) {
            return Minesweeper.MEDIUM;
        } else if (size.equalsIgnoreCase("large")) {
            return Minesweeper.LARGE;
        }
        return null;
    }
}
