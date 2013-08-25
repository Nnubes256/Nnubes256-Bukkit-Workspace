package io.github.nnubes256.minesweeperreloaded.utils;

import io.github.nnubes256.minesweeperreloaded.Minesweeper;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class ArenaBuilder {
    private final Minesweeper instance;
    private static final int CELL_NOT_SET = -2;
    private static final int CELL_IS_MINE = -1;

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

    public ArenaBuilder(Minesweeper instance) {
        this.instance = instance;
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

    public static void setupArea(final Material material, final Location location, final int[] info) {
        // place lots of the specified material below
        for (int i = 0; i < 6; i++) {
            placeLayer(location.clone(), i, info[Minesweeper.SIZE] + 4, material.getId(), (byte) 0);
        }

        // place lots of air above
        for (int i = 0; i < PALETTE.length; i++) {
            placeLayer(location.clone(), -i, info[Minesweeper.SIZE] + 2, Material.AIR.getId(), (byte) 0);
        }

        // place torches around the outside of the area
        placeLayer(location.clone(), 0, info[Minesweeper.SIZE] + 2, Material.TORCH.getId(), (byte) 5);
        placeLayer(location.clone(), 0, info[Minesweeper.SIZE], Material.AIR.getId(), (byte) 0);

        // place bottom layer of TNT
        placeLayer(location.clone(), 3, info[Minesweeper.SIZE], Material.TNT.getId(), (byte) 0);

        // generate and place random map
        final int[] map = generateMap(info[Minesweeper.MINES], info[Minesweeper.SIZE]);
        placeMap(location.clone(), 2, info[Minesweeper.SIZE], map);

        // place top layer of sand
        placeLayer(location.clone(), 1, info[Minesweeper.SIZE], Material.SAND.getId(), (byte) 0);

        // place wool blocks and signs at corners to identify colors
        placePalettes(location.clone(), info[Minesweeper.SIZE]);
    }
}
