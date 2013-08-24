/*
 * Minesweeper Plugin v0.4 by covertbagel for CraftBukkit 1060
 * 5 September 2011
 * Licensed Under GPLv3
 */

package io.github.nnubes256.minesweeperreloaded;

import me.desht.dhutils.TerrainManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import me.desht.dhutils.cuboid.Cuboid;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static io.github.nnubes256.minesweeperreloaded.Minesweeper.*;

public class MapArea {
    public Player areaCreator = null;
	protected final double maxX, minX, maxY, minY, maxZ, minZ;
	final private int y;
	final private int[] info;
    protected final MapArea self = this;
    protected final Cuboid area;
    protected final Location pos1;
    protected Location pos2 = null;
    private int mapHash;
	private Location location = null;
	private String name = null;
    private World world = null;
	private int remainingBlocks;
    private final boolean isArena;
    private boolean lost = false;
    private TerrainManager tm;
    private String terrainFileName;

	/**
     * Creates a map instance in a <code>location</code> where the map will be generated with a size and mine numbers,
     * registering the map creator as a <code>player</code> and determining if it's an arena.
     * @param location From here, the map will spawn from there when {@link #setup(org.bukkit.Material) setup} is used.
     * @param info An array of two integrers; the first is the number of mines, and the second is the size side-to-side of the functional field(where you place the torches).
     * @param player The arena creator.
     * @param isArena Determines if it's a arena(<code>true</code>) or just a quickgame(<code>false</code>).
     */
    public MapArea(final Location location, final int[] info, final Player player, final Boolean isArena) {
        final int size = info[Minesweeper.SIZE];
        final int x = (int) Math.floor(location.getX());
        final int z = (int) Math.floor(location.getZ());
        this.location = location;
        this.areaCreator = player;
        this.isArena = isArena;
        this.world = player.getWorld();
        this.y = (int) Math.floor(location.getY());
        this.maxX = (x + size / 2) + 1;
        this.minX = (x - size / 2) - 2;
        this.maxY = this.y + 9;
        this.minY = this.y - 7;
        this.maxZ = (z + size / 2) + 1;
        this.minZ = (z - size / 2) - 2;
        this.pos1 = new Location(location.getWorld(), minX, minY, minZ);
        this.pos2 = new Location(location.getWorld(), maxX, maxY, maxZ);
        this.area = new Cuboid(pos1, pos2);
        this.info = info;
        this.mapHash = this.hashCode();
        reset();
    }
    /**
     * Creates a map instance in a <code>location</code> where the map will be generated, with a size and mine numbers,
     * registering the <code>world</code> that contains the map and determining if it's an arena.
     * @param location From here, the map will spawn from there when {@link #setup(org.bukkit.Material) setup} is used.
     * @param info An array of two integrers; the first is the number of mines, and the second is the size side-to-side of the functional field(where you place the torches).
     * @param world The world where the arena will be created.
     * @param isArena Determines if it's a arena(<code>true</code>) or just a quickgame(<code>false</code>).
     */
    public MapArea(final Location location, final int[] info, final World world, final Boolean isArena) {
        final int size = info[Minesweeper.SIZE];
        final int x = (int) Math.floor(location.getX());
        final int z = (int) Math.floor(location.getZ());
        this.location = location;
        this.isArena = isArena;
        this.world = world;
        this.y = (int) Math.floor(location.getY());
        this.maxX = (x + size / 2) + 1;
        this.minX = (x - size / 2) - 2;
        this.maxY = this.y + 9;
        this.minY = this.y - 7;
        this.maxZ = (z + size / 2) + 1;
        this.minZ = (z - size / 2) - 2;
        this.pos1 = new Location(location.getWorld(), minX, minY, minZ);
        this.pos2 = new Location(location.getWorld(), maxX, maxY, maxZ);
        this.area = new Cuboid(pos1, pos2);
        this.info = info;
        this.mapHash = this.hashCode();
        reset();
    }
    /**
     * Creates a map instance in a <code>location</code> where the map will be generated, with a size and mine numbers,
     * registering the <code>world</code> that contains the map, determining if it's an arena and setting the <code>name</code> of the map.
     * @param location From here, the map will spawn from there when {@link #setup(org.bukkit.Material) setup} is used.
     * @param info An array of two integrers; the first is the number of mines, and the second is the size side-to-side of the functional field(where you place the torches).
     * @param world The world where the arena will be created.
     * @param isArena Determines if it's a arena(<code>true</code>) or just a quickgame(<code>false</code>).
     * @param name The name of the arena.
     */
	public MapArea(final Location location, final int[] info, final World world, final Boolean isArena, final String name) {
		final int size = info[Minesweeper.SIZE];
		final int x = (int) Math.floor(location.getX());
		final int z = (int) Math.floor(location.getZ());
        this.location = location;
		if (name != null) {
			this.name = name;
		}
        this.isArena = isArena;
        this.world = world;
		this.y = (int) Math.floor(location.getY());
		this.maxX = (x + size / 2) + 1;
		this.minX = (x - size / 2) - 2;
        this.maxY = this.y + 9;
        this.minY = this.y - 7;
		this.maxZ = (z + size / 2) + 1;
		this.minZ = (z - size / 2) - 2;
        this.pos1 = new Location(location.getWorld(), minX, minY, minZ);
        this.pos2 = new Location(location.getWorld(), maxX, maxY, maxZ);
        this.area = new Cuboid(pos1, pos2);
		this.info = info;
        this.mapHash = this.hashCode();
		reset();
	}
    /**
     * Creates a map instance in a <code>location</code> where the map will be generated with a size and mine numbers,
     * registering the map creator as a <code>player</code>, determining if it's an arena and setting the <code>name</code> of the map.
     * @param location From here, the map will spawn from there when {@link #setup(org.bukkit.Material) setup} is used.
     * @param info An array of two integrers; the first is the number of mines, and the second is the size side-to-side of the functional field(where you place the torches).
     * @param player The arena creator.
     * @param isArena Determines if it's a arena(<code>true</code>) or just a quickgame(<code>false</code>).
     * @param name The name of the arena.
     */
    public MapArea(final Location location, final int[] info, final Player player, final Boolean isArena, final String name) {
        final int size = info[Minesweeper.SIZE];
        final int x = (int) Math.floor(location.getX());
        final int z = (int) Math.floor(location.getZ());
        this.location = location;
        this.areaCreator = player;
        if (name != null) {
            this.name = name;
        }
        this.isArena = isArena;
        this.world = player.getWorld();
        this.y = (int) Math.floor(location.getY());
        this.maxX = (x + size / 2) + 1;
        this.minX = (x - size / 2) - 2;
        this.maxY = this.y + 9;
        this.minY = this.y - 7;
        this.maxZ = (z + size / 2) + 1;
        this.minZ = (z - size / 2) - 2;
        this.pos1 = new Location(location.getWorld(), minX, minY, minZ);
        this.pos2 = new Location(location.getWorld(), maxX, maxY, maxZ);
        this.area = new Cuboid(pos1, pos2);
        this.info = info;
        this.mapHash = this.hashCode();
        reset();
    }

    public void init() {
        Minesweeper.getMapAreas().add(self);
        if(isArena) {
            Minesweeper.getArenas().put(name, self);
        }
        if(Minesweeper.wep != null && Minesweeper.isWEEnabled) {
            terrainFileName = getName();
            TerrainBackup.save(self);
        }
    }

    public void setup(final Material material) {
        // place lots of the specified material below
        for (int i = 0; i < 6; i++) {
            placeLayer(getLocation().clone(), i, info[Minesweeper.SIZE] + 4, material.getId(), (byte) 0);
        }

        // place lots of air above
        for (int i = 0; i < PALETTE.length; i++) {
            placeLayer(getLocation().clone(), -i, info[Minesweeper.SIZE] + 2, Material.AIR.getId(), (byte) 0);
        }

        // place torches around the outside of the area
        placeLayer(getLocation().clone(), 0, info[Minesweeper.SIZE] + 2, Material.TORCH.getId(), (byte) 5);
        placeLayer(getLocation().clone(), 0, info[Minesweeper.SIZE], Material.AIR.getId(), (byte) 0);

        // place bottom layer of TNT
        placeLayer(getLocation().clone(), 3, info[Minesweeper.SIZE], Material.TNT.getId(), (byte) 0);

        // generate and place random map
        final int[] map = generateMap(info[Minesweeper.MINES], info[Minesweeper.SIZE]);
        placeMap(getLocation().clone(), 2, info[Minesweeper.SIZE], map);

        // place top layer of sand
        placeLayer(getLocation().clone(), 1, info[Minesweeper.SIZE], Material.SAND.getId(), (byte) 0);

        // place wool blocks and signs at corners to identify colors
        placePalettes(getLocation().clone(), info[Minesweeper.SIZE]);
    }

    public void setup(final Material material, final HashMap<String,Boolean> args) {
        Boolean withWalls = false;

        try {
            if(args.containsKey("withWalls")) {
                if(args.get("withWalls")) withWalls = true;
            }
        } catch(ClassCastException e) {// No problem, because we catched it.
        } catch(NullPointerException e) {// Same as above.
        }

        // We want obsidian walls?
        if(withWalls) {
            for (int i = -PALETTE.length + 1; i < 7; i++) {
                placeLayer(location.clone(), i, info[SIZE] + 4, Material.OBSIDIAN.getId(), (byte) 0);
            }
        }

        // place lots of the specified material below
        for (int i = 0; i < 6; i++) {
            placeLayer(getLocation().clone(), i, info[Minesweeper.SIZE] + 4, material.getId(), (byte) 0);
        }

        // place lots of air above
        for (int i = 0; i < PALETTE.length; i++) {
            placeLayer(getLocation().clone(), -i, info[Minesweeper.SIZE] + 2, Material.AIR.getId(), (byte) 0);
        }

        // place torches around the outside of the area
        placeLayer(getLocation().clone(), 0, info[Minesweeper.SIZE] + 2, Material.TORCH.getId(), (byte) 5);
        placeLayer(getLocation().clone(), 0, info[Minesweeper.SIZE], Material.AIR.getId(), (byte) 0);

        // place bottom layer of TNT
        placeLayer(getLocation().clone(), 3, info[Minesweeper.SIZE], Material.TNT.getId(), (byte) 0);

        // generate and place random map
        final int[] map = generateMap(info[Minesweeper.MINES], info[Minesweeper.SIZE]);
        placeMap(getLocation().clone(), 2, info[Minesweeper.SIZE], map);

        // place top layer of sand
        placeLayer(getLocation().clone(), 1, info[Minesweeper.SIZE], Material.SAND.getId(), (byte) 0);

        // place wool blocks and signs at corners to identify colors
        placePalettes(getLocation().clone(), info[Minesweeper.SIZE]);
    }

   /**
    * Checks for the given arena if a block is inside it.
    * @param block The block checked.
    * @return True in case it's inside, false otherwise.
    */
	public boolean inArea(final Block block) {
		final int x = (int) Math.floor(block.getX());
		final int y = (int) Math.floor(block.getY());
		final int z = (int) Math.floor(block.getZ());
		return (
			this.y + 9 > y && // this is the 'height above' distance
			this.y - 7 < y && // this is the 'height below' distance
			this.maxX >= x &&
			this.minX <= x &&
			this.maxZ >= z &&
			this.minZ <= z
		);
	}

    public boolean inAreaTotal(final Block block) {
        return area.contains(block);
    }
	/**
	 * @return Location of the arena.
	 */
	public Location getLocation() {
		return this.location;
	}

    /**
     * @return World where the arena is.
     */
    public World getWorld() {
        return this.world;
    }
	/**
	 * @return Name of the arena.
	 */
	public String getName() {
		if (name == null) {
            return "quickgame" + mapHash;
        } else return this.name;
	}
	
	/**
	 * @return Info about the arena.
	 */
	public int[] getInfo() {
		return this.info;
	}
	/**
	 * Vitrifies the arena, deleting the location and the name of it, making it to be nothing.
	 */
	public void vitrify() {
		this.location = null;
		this.name = null;
	}
	// TODO Understand these methods XD
	public void reset() {
        lost = false;
		this.remainingBlocks = info[Minesweeper.SIZE] * info[Minesweeper.SIZE] - info[Minesweeper.MINES];
	}
	
	public int blockCleared() {
		return --remainingBlocks;
	}
	
	public boolean isClear() {
		return remainingBlocks == 0;
	}
	
	public boolean isArena() {
		return this.isArena;
	}

    @SuppressWarnings("unused")
    public boolean hasExploded() {
        return lost;
    }

    protected void setExploded(Boolean exploded) {
        lost = exploded;
    }
}