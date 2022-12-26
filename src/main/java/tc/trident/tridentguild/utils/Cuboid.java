package tc.trident.tridentguild.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

import java.util.*;


public class Cuboid implements Cloneable, ConfigurationSerializable, Iterable<Block>
{
    protected final Vector minimumPoint;
    protected final Vector maximumPoint;
    protected String worldName;
    
    public Cuboid(final Cuboid cuboid) {
        this(cuboid.worldName, cuboid.minimumPoint.getX(), cuboid.minimumPoint.getY(), cuboid.minimumPoint.getZ(), cuboid.maximumPoint.getX(), cuboid.maximumPoint.getY(), cuboid.maximumPoint.getZ());
    }
    
    public Cuboid(final Location loc) {
        this(loc, loc);
    }
    
    public Cuboid(final Location loc1, final Location loc2) {
        if (loc1 == null || loc2 == null) {
            throw new NullPointerException("One/both of the locations is/are null!");
        }
        if (loc1.getWorld() == null || loc2.getWorld() == null) {
            throw new NullPointerException("One/both of the worlds is/are null!");
        }
        if (!loc1.getWorld().getUID().equals(loc2.getWorld().getUID())) {
            throw new IllegalStateException("The 2 locations of the cuboid must be in the same world!");
        }
        this.worldName = loc1.getWorld().getName();
        final double xPos1 = Math.min(loc1.getX(), loc2.getX());
        final double yPos1 = Math.min(loc1.getY(), loc2.getY());
        final double zPos1 = Math.min(loc1.getZ(), loc2.getZ());
        final double xPos2 = Math.max(loc1.getX(), loc2.getX());
        final double yPos2 = Math.max(loc1.getY(), loc2.getY());
        final double zPos2 = Math.max(loc1.getZ(), loc2.getZ());
        this.minimumPoint = new Vector(xPos1, yPos1, zPos1);
        this.maximumPoint = new Vector(xPos2, yPos2, zPos2);
    }
    
    public Cuboid(final String worldName, final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        if (worldName == null || Bukkit.getServer().getWorld(worldName) == null) {
            throw new NullPointerException("One/both of the worlds is/are null!");
        }
        this.worldName = worldName;
        final double xPos1 = Math.min(x1, x2);
        final double xPos2 = Math.max(x1, x2);
        final double yPos1 = Math.min(y1, y2);
        final double yPos2 = Math.max(y1, y2);
        final double zPos1 = Math.min(z1, z2);
        final double zPos2 = Math.max(z1, z2);
        this.minimumPoint = new Vector(xPos1, yPos1, zPos1);
        this.maximumPoint = new Vector(xPos2, yPos2, zPos2);
    }
    
    public static Cuboid deserialize(final Map<String, Object> serializedCuboid) {
        try {
            final String worldName = (String) serializedCuboid.get("worldName");
            final double xPos1 = (double) serializedCuboid.get("x1");
            final double xPos2 = (double) serializedCuboid.get("x2");
            final double yPos1 = (double) serializedCuboid.get("y1");
            final double yPos2 = (double) serializedCuboid.get("y2");
            final double zPos1 = (double) serializedCuboid.get("z1");
            final double zPos2 = (double) serializedCuboid.get("z2");
            return new Cuboid(worldName, xPos1, yPos1, zPos1, xPos2, yPos2, zPos2);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public boolean containsLocation(final Location location) {
        return location != null && location.getWorld().getName().equals(this.worldName) && location.toVector().isInAABB(this.minimumPoint, this.maximumPoint);
    }
    
    public boolean containsVector(final Vector vector) {
        return vector != null && vector.isInAABB(this.minimumPoint, this.maximumPoint);
    }
    
    public List<Block> getBlocks() {
        final List<Block> blockList = new ArrayList<Block>();
        final World world = this.getWorld();
        if (world != null) {
            Utils.debug("World: "+world);
            for (int x = this.minimumPoint.getBlockX(); x <= this.maximumPoint.getBlockX(); ++x) {
                for (int y = this.minimumPoint.getBlockY(); y <= this.maximumPoint.getBlockY() && y <= world.getMaxHeight(); ++y) {
                    for (int z = this.minimumPoint.getBlockZ(); z <= this.maximumPoint.getBlockZ(); ++z) {
                        blockList.add(world.getBlockAt(x, y, z));
                    }
                }
            }
        }
        return blockList;
    }
    
    public Location getLowerLocation() {
        return this.minimumPoint.toLocation(this.getWorld());
    }
    
    public double getLowerX() {
        return this.minimumPoint.getX();
    }
    
    public double getLowerY() {
        return this.minimumPoint.getY();
    }
    
    public double getLowerZ() {
        return this.minimumPoint.getZ();
    }
    
    public Location getUpperLocation() {
        return this.maximumPoint.toLocation(this.getWorld());
    }
    
    public double getUpperX() {
        return this.maximumPoint.getX();
    }
    
    public double getUpperY() {
        return this.maximumPoint.getY();
    }
    
    public double getUpperZ() {
        return this.maximumPoint.getZ();
    }
    
    public double getVolume() {
        return (this.getUpperX() - this.getLowerX() + 1.0) * (this.getUpperY() - this.getLowerY() + 1.0) * (this.getUpperZ() - this.getLowerZ() + 1.0);
    }
    
    public World getWorld() {
        final World world = Bukkit.getServer().getWorld(this.worldName);
        if (world == null) {
            throw new NullPointerException("World '" + this.worldName + "' is not loaded.");
        }
        return world;
    }
    
    public void setWorld(final World world) {
        if (world != null) {
            this.worldName = world.getName();
            return;
        }
        throw new NullPointerException("The world cannot be null.");
    }
    
    public Location getRandomLocation() {
        final int X = new Random().nextInt(this.maximumPoint.getBlockX() - this.minimumPoint.getBlockX()) + this.minimumPoint.getBlockX();
        final int Y = this.maximumPoint.getBlockY() - 1;
        final int Z = new Random().nextInt(this.maximumPoint.getBlockZ() - this.minimumPoint.getBlockZ()) + this.minimumPoint.getBlockZ();
        return new Location(this.getWorld(), (double)X, (double)Y, (double)Z);
    }
    
    public Cuboid clone() {
        return new Cuboid(this);
    }
    
    public ListIterator<Block> iterator() {
        return this.getBlocks().listIterator();
    }
    
    public Map<String, Object> serialize() {
        final Map<String, Object> serializedCuboid = new HashMap<String, Object>();
        serializedCuboid.put("worldName", this.worldName);
        serializedCuboid.put("x1", this.minimumPoint.getX());
        serializedCuboid.put("x2", this.maximumPoint.getX());
        serializedCuboid.put("y1", this.minimumPoint.getY());
        serializedCuboid.put("y2", this.maximumPoint.getY());
        serializedCuboid.put("z1", this.minimumPoint.getZ());
        serializedCuboid.put("z2", this.maximumPoint.getZ());
        return serializedCuboid;
    }
}
