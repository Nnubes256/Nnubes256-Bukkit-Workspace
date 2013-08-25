package io.github.nnubes256.minesweeperreloaded;

import io.github.nnubes256.minesweeperreloaded.utils.MiscUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandHandling {
    private static Minesweeper MSInstance;
    private static GameHandler GHInstance;

    public CommandHandling(Minesweeper msinstance, GameHandler ghinstance) {
        MSInstance = msinstance;
        GHInstance = ghinstance;
    }

    public static boolean parseMSCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName();
        boolean canPlay = sender.hasPermission("minesweeper.play");
        // Leeeets go!
        if(commandName.equalsIgnoreCase("ms")) {
            if(args.length == 0) {
                sender.sendMessage(Minesweeper.ABOUT);
                return true;
            } else if(args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
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
                    sender.sendMessage(ChatColor.DARK_AQUA + "------ " + ChatColor.GOLD + "Quick Game" + ChatColor.DARK_AQUA + " ------");
                    sender.sendMessage(ChatColor.GOLD + "/ms quickgame" + ChatColor.AQUA + " create:" + ChatColor.GRAY + " create a quick game.");
                    sender.sendMessage(ChatColor.DARK_AQUA + "----------------------");
                    return true;
                    // Oh, there were...what a shame XD
                    // Create a goddamit quick game(use-and-trash)
                } else if(args[1].equalsIgnoreCase("create")){
                    if (canPlay && sender.hasPermission("minesweeper.game.create") && sender instanceof Player) {
                        if(args.length == 3) return false;
                        sender.sendMessage("Trying to create game...");
                        String sizeOfQuickGame = args[2];
                        return GHInstance.gameCreate(sender, sizeOfQuickGame);
                    } else if(sender instanceof ConsoleCommandSender) {
                        sender.sendMessage("You can only execute this command as a player");
                        return true;
                    }
                    sender.sendMessage("You haven't got permission!");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.DARK_AQUA + "------ " + ChatColor.GOLD + "Quick Game" + ChatColor.DARK_AQUA + " ------");
                    sender.sendMessage(ChatColor.GOLD + "/ms quickgame" + ChatColor.AQUA + " create:" + ChatColor.GRAY + " create a quick game.");
                    sender.sendMessage(ChatColor.DARK_AQUA + "----------------------");
                    return true;
                }
                // -----------------------
                // SUBCOMMAND 2: ARENA
                // -----------------------
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
                        if(args.length == 4) return false;
                        String[] arenaCreateArguments = {args[2],args[3]};
                        return GHInstance.arenaCreate(sender, arenaCreateArguments);
                    } else if(sender instanceof ConsoleCommandSender) {
                        sender.sendMessage("You can only execute this command as a player");
                        return true;
                    }
                    sender.sendMessage(Minesweeper.PERMISSION_DENIED);
                    return true;
                } else if(args[1].equalsIgnoreCase("reset")) {
                    if (canPlay && sender.hasPermission("minesweeper.arena.reset")) {
                        if(args.length == 2) return false;
                        String arenaToReset = args[2];
                        return GHInstance.arenaReset(sender, arenaToReset);
                    }
                    sender.sendMessage(Minesweeper.PERMISSION_DENIED);
                    return true;
                } else if(args[1].equalsIgnoreCase("tp")){
                    if(canPlay && sender.hasPermission("minesweeper.arena.tp") && sender instanceof Player) {
                        if(args.length == 2) return false;
                        String arenaToTeleport = args[2];
                        return GHInstance.arenaTeleport(sender, arenaToTeleport);
                    } else if(sender instanceof ConsoleCommandSender) {
                        sender.sendMessage("You can only execute this command as a player");
                    }
                    sender.sendMessage(ChatColor.RED + Minesweeper.PERMISSION_DENIED);
                    return true;
                } else if(args[1].equalsIgnoreCase("vitrify")) {
                    if (canPlay && sender.hasPermission("minesweeper.arena.vitrify")) {
                        if(args.length == 2) return false;
                        String arenaToVitrify = args[2];
                        return GHInstance.arenaVitrify(sender, arenaToVitrify);
                    }
                    sender.sendMessage(ChatColor.RED + Minesweeper.PERMISSION_DENIED);
                    return true;
                } else return false;
                // -----------------------
                // SUBCOMMAND 3: ITEMS
                // -----------------------
            } else if(args[0].equalsIgnoreCase("items")) {
                if(sender instanceof Player) {
                    return MiscUtils.givePlayerStuff((Player) sender);
                } else if(sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("You can only execute this command as a player");
                    return true;
                }
            }
        }
        return false;

    }
}
