/*
* This is a modification of the terrain backup handler of
* the Minecraft Bukkit plugin 'ChessCraft', plugin made by desht,
* and licensed under the GNU General Public License v3(GPLv3).
*
* TODO: License the whole project with same license.
*
* For a small talk about my opinion about licenses, go to DirectoryStructure.
*/

package io.github.nnubes256.minesweeperreloaded.utils;

import java.io.File;

import io.github.nnubes256.minesweeperreloaded.DirectoryStructure;
import io.github.nnubes256.minesweeperreloaded.MapArea;
import io.github.nnubes256.minesweeperreloaded.Minesweeper;
import me.desht.dhutils.cuboid.Cuboid;
import me.desht.dhutils.LogUtils;
import me.desht.dhutils.TerrainManager;

import org.bukkit.Location;

public class TerrainBackup {

    public static boolean save(MapArea mapArea) {
        boolean saved = false;
        try {
            TerrainManager tm = new TerrainManager(Minesweeper.wep, mapArea.getWorld());

            Cuboid c = mapArea.area;
            Location l1 = c.getLowerNE();
            Location l2 = c.getUpperSW();
            tm.saveTerrain(new File(DirectoryStructure.getSchematicsDirectory(), mapArea.getName()), l1, l2);
            saved = true;
        } catch (Exception e) {
            LogUtils.warning(e.getMessage());
        }
        return saved;
    }

    public static boolean reload(MapArea mapArea) {
        boolean restored = false;
        try {
            TerrainManager tm = new TerrainManager(Minesweeper.wep, mapArea.getWorld());
            tm.loadSchematic(new File(DirectoryStructure.getSchematicsDirectory(), mapArea.getName()));
            restored = true;
        } catch (Exception e) {
            LogUtils.warning(e.getMessage());
        }
        return restored;
    }
}
