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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Minesweeper extends JavaPlugin {
	// map configs - mines and size
	private static final int[] SMALL = {10, 8};
	private static final int[] MEDIUM = {40, 16};
	private static final int[] LARGE = {90, 24};
	
	// used by map generator and placer
	private static final int CELL_NOT_SET = -2;
	private static final int CELL_IS_MINE = -1;
	
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
	
	private static final String PERMISSION_DENIED = "[MineSweeper] Permission denied";
	private static final String ARENA_NOT_FOUND = "[MineSweeper] Arena %s does not exist";
	private static final String ARENA_FOUND = "[MineSweeper] Arena %s already exists";
	private static final String ARENA_CREATED = "[MineSweeper] Arena %s created by %s";
	private static final String ARENA_RESET = "[MineSweeper] Arena %s reset by %s";
	private static final String ARENA_VITRIFIED = "[MineSweeper] Arena %s vitrified by %s";
	protected static final String ARENA_WIN = "[MineSweeper] Arena %s has been cleared";
	protected static final String ARENA_LOSE = "[MineSweeper] Arena %s has been exploded";
	private static final String ARENA_WILL_RESET = "[MineSweeper] Arena %s will automatically reset in %d seconds";
    private static final String[] ABOUT = {
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

    public static Boolean isWEEnabled;
    protected static WorldEditPlugin wep;
    private Plugin we;
	private GameBlockListener blockListener;
	private static final List<MapArea> mapAreas = new ArrayList<MapArea>();
	private static final HashMap<String, MapArea> arenas = new HashMap<String, MapArea>();
    private static Minesweeper instance;

    @Override
	public void onEnable() {
        setInstance(this);
		// setup block listener
		blockListener = new GameBlockListener(this);
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(blockListener, this);
        we = pluginManager.getPlugin("WorldEdit");
        if(we == null) {
            this.getLogger().warning("WorldEdit wasn't found. You won't be able to restore terrain when creating/destroying map areas.");
            isWEEnabled = false;
        } else {
            wep = ((WorldEditPlugin) we);
            this.getLogger().warning("WorldEdit was found. You will be able to restore terrain when creating/destroying map areas.");
            isWEEnabled = true;
        }
        LogUtils.init(this);
        DirectoryStructure.setup();
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info("Disabled.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String commandName = command.getName();
		boolean canPlay = sender.hasPermission("minesweeper.play");
		// Leeeets go!
		if(commandName.equalsIgnoreCase("ms")) {
			if(args.length == 0) {
                sender.sendMessage(ABOUT);
                return true;
			} else if(args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
				// TODO: Do the goddamit help
                sender.sendMessage(ChatColor.DARK_AQUA + "-------- " + ChatColor.GOLD + "Help" + ChatColor.DARK_AQUA + " --------");
                sender.sendMessage(ChatColor.GOLD + "/ms " + ChatColor.AQUA + "quickgame:" + ChatColor.GRAY + " QuickGame commands.");
                sender.sendMessage(ChatColor.GOLD + "/ms " + ChatColor.AQUA + "arena:" + ChatColor.GRAY + " Arena Commands.");
                sender.sendMessage(ChatColor.GOLD + "/ms " + ChatColor.AQUA + "items:" + ChatColor.GRAY + " Gives you the items to play");
                sender.sendMessage(ChatColor.DARK_AQUA + "--------------------");
                return true;
			// -----------------------
			// SUBCOMMAND 1: QUICKGAME
			// -----------------------
			} else if(args[0].equalsIgnoreCase("quickgame")) {
				// If there aren't more args...
				if(args.length == 1) {
					sender.sendMessage(ChatColor.DARK_AQUA + "----- " + ChatColor.GOLD + "Quick Game" + ChatColor.DARK_AQUA + " -----");
					sender.sendMessage(ChatColor.GOLD + "/ms " + ChatColor.AQUA + "quickgame create:" + ChatColor.GRAY + " create a quick game.");
					sender.sendMessage(ChatColor.DARK_AQUA + "----------------------");
					return true;
				// Oh, there were...what a shame XD
				// Create a goddamit quick game(use-and-trash)
				} else if(args[1].equalsIgnoreCase("create")){
					if (canPlay && sender.hasPermission("minesweeper.game.create") && sender instanceof Player) {
                        if(args[2] == null) {
                            return false;
                        }
						sender.sendMessage("Trying to create game...");
						String sizeOfQuickGame = args[2];
						return gameCreate(sender, sizeOfQuickGame);
					} else if(sender instanceof ConsoleCommandSender) {
                        sender.sendMessage("You can only execute this command as a player");
                        return true;
                    }
					sender.sendMessage("You haven't got permission!");
					return true;
				} else {
					sender.sendMessage(ChatColor.DARK_AQUA + "------ " + ChatColor.GOLD + "Quick Game" + ChatColor.DARK_AQUA + " ------");
					sender.sendMessage(ChatColor.GOLD + "/ms quickgame"+ ChatColor.AQUA + " create:" + ChatColor.GRAY + " create a quick game.");
					sender.sendMessage(ChatColor.DARK_AQUA + "----------------------");
					return true;
				}
			} else if(args[0].equalsIgnoreCase("arena")) {
				if(args.length == 1) {
                    sender.sendMessage(ChatColor.DARK_AQUA + "--- " + ChatColor.GOLD + "Arena Management" + ChatColor.DARK_AQUA + " ---");
                    sender.sendMessage(ChatColor.GOLD + "/ms arena" + ChatColor.AQUA + " create:" + ChatColor.GRAY + " create an arena.");
                    sender.sendMessage(ChatColor.GOLD + "/ms arena" + ChatColor.AQUA + " reset:" + ChatColor.GRAY + " reset an arena.");
                    sender.sendMessage(ChatColor.GOLD + "/ms arena" + ChatColor.AQUA + " tp:" + ChatColor.GRAY + " teleport to an arena.");
                    sender.sendMessage(ChatColor.GOLD + "/ms arena" + ChatColor.AQUA + " vitrify:" + ChatColor.GRAY + " vitrifies an arena.");
                    sender.sendMessage(ChatColor.DARK_AQUA + "----------------------");
					return true;
				} else if(args[1].equalsIgnoreCase("create")) {
					if (canPlay && sender.hasPermission("minesweeper.arena.create") && sender instanceof Player) {
                        if(args[2] == null) {
                            return false;
                        }
                        if(args[3] == null) {
                            return false;
                        }
						String[] arenaCreateArguments = {args[2],args[3]};
						return arenaCreate(sender, arenaCreateArguments);
                    } else if(sender instanceof ConsoleCommandSender) {
                        sender.sendMessage("You can only execute this command as a player");
                        return true;
                    }
					sender.sendMessage(PERMISSION_DENIED);
					return true;
				} else if(args[1].equalsIgnoreCase("reset")) {
					if (canPlay && sender.hasPermission("minesweeper.arena.reset")) {
                        if(args[2] == null) {
                            return false;
                        }
						String arenaToReset = args[2];
						return arenaReset(sender, arenaToReset);
					}
					sender.sendMessage(PERMISSION_DENIED);
					return true;
				} else if(args[1].equalsIgnoreCase("tp")){
					if(canPlay && sender.hasPermission("minesweeper.arena.tp") && sender instanceof Player) {
                        if(args[2] == null) {
                            return false;
                        }
						String arenaToTeleport = args[2];
						return arenaTeleport(sender, arenaToTeleport);
					} else if(sender instanceof ConsoleCommandSender) {
                        sender.sendMessage("You can only execute this command as a player");
                    }
                    sender.sendMessage(ChatColor.RED + PERMISSION_DENIED);
                    return true;
				} else if(args[1].equalsIgnoreCase("vitrify")) {
					if (canPlay && sender.hasPermission("minesweeper.arena.vitrify")) {
                        if(args[2] == null) {
                            return false;
                        }
						String arenaToVitrify = args[2];
						return arenaVitrify(sender, arenaToVitrify);
					}
					sender.sendMessage(ChatColor.RED + PERMISSION_DENIED);
					return true;
				} else return false;
			// -----------------------
			// SUBCOMMAND 3: ITEMS
			// -----------------------
			} else if(args[0].equalsIgnoreCase("items")) {
                if(sender instanceof Player) {
                    return givePlayerStuff((Player) sender);
                } else if(sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("You can only execute this command as a player");
                    return true;
                }
			}
		}
		return false;
	}

    private void setInstance(Minesweeper minesweeper) {
        instance = minesweeper;
    }

    public static Minesweeper getInstance() {
        return instance;
    }
	
	public static List<MapArea> getMapAreas() {
		return mapAreas;
	}

    protected static HashMap<String, MapArea> getArenas() {
        return arenas;
    }
	
	private boolean gameCreate(final CommandSender sender, final String argument) {
		// parse arguments
		final int[] info = parseSize(argument);
		if (info == null) {
			return false;
		}
		
		// get player location
		final Player player = (Player) sender;
		final Location location = fixLocation(player.getLocation());
		
		// Create a new MapArea instance and init it.
		MapArea newMapArea = new MapArea(location, info, ((Player) sender), false);
        newMapArea.init();
		
		// setup area
		newMapArea.setup(Material.STONE);
		
		// give player some normal and redstone torches
		givePlayerStuff(player);
		
		return true;
	}
	
	private boolean arenaCreate(final CommandSender sender, final String[] arguments) {
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
			sender.sendMessage(String.format(ARENA_FOUND, name));
			return true;
		}
		
		// get player location
		final Player player = (Player) sender;
		final Location location = fixLocation(player.getLocation());
		
		// Create map instance and init it
		MapArea mapArea = new MapArea(location.clone(), info, ((Player) sender), true, name);
		mapArea.init();
		
		// setup area
        HashMap<String, Boolean> createArgs = new HashMap<String, Boolean>();
        createArgs.put("withWalls", true);
		mapArea.setup(Material.OBSIDIAN, createArgs);
		
		// give player some normal and redstone torches
		givePlayerStuff(player);
		
		sender.getServer().broadcastMessage(String.format(ARENA_CREATED, name, player.getName()));
		return true;
	}
	
	private boolean arenaReset(CommandSender sender, String name) {
		// parse arguments;
		if (name == null) {
			return false;
		}
		
		// look for arena with this name
		final MapArea arena = arenas.get(name);
		if (arena == null) {
			sender.sendMessage(String.format(ARENA_NOT_FOUND, name));
			return true;
		}
		
		arenaResetWorker(arena);
		
		// give player some normal and redstone torches
		final Player player = (Player) sender;
		givePlayerStuff(player);
		
		getServer().broadcastMessage(String.format(ARENA_RESET, name, player.getName()));
		return true;
	}
	
	protected static void arenaResetWorker(final MapArea arena) {
		setupArea(Material.OBSIDIAN, arena.getLocation().clone(), arena.getInfo());
		arena.reset();
	}
	
	private boolean arenaVitrify(final CommandSender sender, final String name) {
		// parse arguments
		if (name == null) {
			return false;
		}
		
		// look for arena with this name
		final MapArea arena = arenas.get(name);
		if (arena == null) {
			sender.sendMessage(ARENA_NOT_FOUND);
			return true;
		}
		
		// remove this arena from the arenas list
		arenas.remove(name);
		
		// modify it so it will be removed from mapAreas list after explosion
		arena.vitrify();
		
		getServer().broadcastMessage(String.format(ARENA_VITRIFIED, name, sender.getName()));
		return true;
	}
	
	private boolean arenaTeleport(CommandSender sender, String name) {
		// parse arguments
		if (name == null) {
			return false;
		}
		
		// look for arena with this name
		final MapArea arena = arenas.get(name);
		if (arena == null) {
			sender.sendMessage(ARENA_NOT_FOUND);
			return true;
		}
		
		// move player to location
		final Player player = (Player) sender;
		player.teleport(arena.getLocation());
		
		// give player some normal and redstone torches
		givePlayerStuff(player);
		
		return true;
	}
	
	private Location fixLocation(final Location location) {
		location.setX(Math.floor(location.getX()) + 0.5);
		location.setY(Math.floor(location.getY()));
		location.setZ(Math.floor(location.getZ()) + 0.5);
		return location;
	}

    public static void setupArea(final Material material, final Location location, final int[] info) {
		// place lots of the specified material below
		for (int i = 0; i < 6; i++) {
			placeLayer(location.clone(), i, info[SIZE] + 4, material.getId(), (byte) 0);
		}
		
		// place lots of air above
		for (int i = 0; i < PALETTE.length; i++) {
			placeLayer(location.clone(), -i, info[SIZE] + 2, Material.AIR.getId(), (byte) 0);
		}
		
		// place torches around the outside of the area
		placeLayer(location.clone(), 0, info[SIZE] + 2, Material.TORCH.getId(), (byte) 5);
		placeLayer(location.clone(), 0, info[SIZE], Material.AIR.getId(), (byte) 0);
		
		// place bottom layer of TNT
		placeLayer(location.clone(), 3, info[SIZE], Material.TNT.getId(), (byte) 0);
		
		// generate and place random map
		final int[] map = generateMap(info[MINES], info[SIZE]);
		placeMap(location.clone(), 2, info[SIZE], map);
		
		// place top layer of sand
		placeLayer(location.clone(), 1, info[SIZE], Material.SAND.getId(), (byte) 0);
		
		// place wool blocks and signs at corners to identify colors
		placePalettes(location.clone(), info[SIZE]);
	}
	
	public static void placePalettes(final Location location, int size) {
		size += 2;
		location.setX(location.getX() - size / 2);
		location.setZ(location.getZ() - size / 2);
		placePalette(location.clone());
		location.setX(location.getX() + size - 1);
		placePalette(location.clone());
		location.setZ(location.getZ() + size - 1);
		placePalette(location.clone());
		location.setX(location.getX() - size + 1);
		placePalette(location);
	}
	
	public static void placePalette(final Location location) {
		Block block;
        for (DyeColor aPALETTE : PALETTE) {
            block = location.getBlock();
            block.setType(Material.WOOL);
            block.setData(aPALETTE.getDyeData());
            location.setY(location.getY() + 1);
        }
	}
	
	public static void placeMap(final Location location, final int depth, final int size, final int[] map) {
		int i, j, k;
		Block block;
		location.setY(location.getY() - depth);
		location.setX(location.getX() - size / 2);
		location.setZ(location.getZ() - size / 2);
		for (i = 0; i < size; i++) {
			for (j = 0; j < size; j++) {
				k = map[i * size + j];
				if (k == CELL_IS_MINE) {
					location.getBlock().setType(Material.TNT);
				} else if (k >= 0 && k <= 8) {
					block = location.getBlock();
					block.setType(Material.WOOL);
					block.setData(PALETTE[k].getDyeData());
				}
				location.setX(location.getX() + 1);
			}
			location.setX(location.getX() - size);
			location.setZ(location.getZ() + 1);
		}
	}
	
	public static void placeLayer(final Location location, final int depth, final int size, final int typeid, final byte data) {
		location.setY(location.getY() - depth);
		location.setX(location.getX() - size / 2);
		location.setZ(location.getZ() - size / 2);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				location.getBlock().setTypeIdAndData(typeid, data, false);
				location.setX(location.getX() + 1);
			}
			location.setX(location.getX() - size);
			location.setZ(location.getZ() + 1);
		}
	}
	
	public static int[] parseSize(final String size) {
		if (size.equalsIgnoreCase("small")) {
			return SMALL;
		} else if (size.equalsIgnoreCase("medium")) {
			return MEDIUM;
		} else if (size.equalsIgnoreCase("large")) {
			return LARGE;
		}
		return null;
	}
	
	public static int[] generateMap(final int mines, final int size) {
		// create and initialize array
		int i, j, k;
		final int[] map = new int[size * size];
		for (i = 0; i < map.length; i++) {
			map[i] = CELL_NOT_SET;
		}
		
		// add random mines
		for (i = 0; i < mines; i++) {
			do {
				j = (int) (Math.random() * map.length);
			} while (map[j] != CELL_NOT_SET);
			map[j] = CELL_IS_MINE;
		}
		
		// set other values
		for (i = 0; i < size; i++) {
			for (j = 0; j < size; j++) {
				k = i * size + j;
				if (map[k] == CELL_NOT_SET) {
					map[k] = 0;
					if (i > 0 && map[(i - 1) * size + j] == CELL_IS_MINE) {
						map[k]++;
					}
					if (i < size - 1 && map[(i + 1) * size + j] == CELL_IS_MINE) {
						map[k]++;
					}
					if (j > 0 && map[i * size + j - 1] == CELL_IS_MINE) {
						map[k]++;
					}
					if (j < size - 1 && map[i * size + j + 1] == CELL_IS_MINE) {
						map[k]++;
					}
					if (i > 0 && j > 0 && map[(i - 1) * size + j - 1] == CELL_IS_MINE) {
						map[k]++;
					}
					if (i > 0 && j < size - 1 && map[(i - 1) * size + j + 1] == CELL_IS_MINE) {
						map[k]++;
					}
					if (i < size - 1 && j > 0 && map[(i + 1) * size + j - 1] == CELL_IS_MINE) {
						map[k]++;
					}
					if (i < size - 1 && j < size - 1 && map[(i + 1) * size + j + 1] == CELL_IS_MINE) {
						map[k]++;
					}
				}
			}
		}
		
		return map;
	}
	
	public void delayedArenaReset(final MapArea arena) {
		int resetDelay = 100;
        getServer().broadcastMessage(String.format(ARENA_WILL_RESET, arena.getName(), resetDelay));
        @SuppressWarnings("unused")
		BukkitTask task = new DelayedArenaResetTask(this, arena).runTaskLater(this, resetDelay);
	}
	
	private static boolean givePlayerStuff(final Player player) {
		if (player.hasPermission("minesweeper.play") && player.hasPermission("minesweeper.items")) {
			Inventory inventory = player.getInventory();
			inventory.addItem(new ItemStack(Material.REDSTONE_TORCH_ON, 64));
			inventory.addItem(new ItemStack(Material.TORCH, 64));
			return true;
		}
		player.sendMessage(PERMISSION_DENIED);
		return true;
	}
}
