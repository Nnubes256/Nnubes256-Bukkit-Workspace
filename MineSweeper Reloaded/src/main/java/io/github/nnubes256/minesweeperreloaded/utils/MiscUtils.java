package io.github.nnubes256.minesweeperreloaded.utils;

import io.github.nnubes256.minesweeperreloaded.Minesweeper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MiscUtils {

    public static boolean givePlayerStuff(final Player player) {
        if (player.hasPermission("minesweeper.play") && player.hasPermission("minesweeper.items")) {
            Inventory inventory = player.getInventory();
            inventory.addItem(new ItemStack(Material.REDSTONE_TORCH_ON, 64));
            inventory.addItem(new ItemStack(Material.TORCH, 64));
            return true;
        }
        player.sendMessage(Minesweeper.PERMISSION_DENIED);
        return true;
    }

    public static Location fixLocation(final Location location) {
        location.setX(Math.floor(location.getX()) + 0.5);
        location.setY(Math.floor(location.getY()));
        location.setZ(Math.floor(location.getZ()) + 0.5);
        return location;
    }
}
