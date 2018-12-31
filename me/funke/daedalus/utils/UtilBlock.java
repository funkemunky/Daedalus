package me.funke.daedalus.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UtilBlock {
    public static HashSet<Byte> blockPassSet = new HashSet();
    public static HashSet<Byte> blockAirFoliageSet = new HashSet();
    public static HashSet<Byte> fullSolid = new HashSet();
    public static HashSet<Byte> blockUseSet = new HashSet();

    public static Block getLowestBlockAt(Location Location) {
        Block Block = Location.getWorld().getBlockAt((int) Location.getX(), 0, (int) Location.getZ());
        if ((Block == null) || (Block.getType().equals(Material.AIR))) {
            Block = Location.getBlock();
            for (int y = (int) Location.getY(); y > 0; y--) {
                Block Current = Location.getWorld().getBlockAt((int) Location.getX(), y, (int) Location.getZ());
                Block Below = Current.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
                if ((Below == null) || (Below.getType().equals(Material.AIR))) {
                    Block = Current;
                }
            }
        }
        return Block;
    }

    public static boolean isLiquid(Block block) {
        return block != null && (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER
                || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA);
    }

    public static boolean isSolid(Block material) {
        return material != null && isSolid(material.getTypeId());
    }

    public static boolean isCarpet(Block material) {
        return material.getType().equals(Material.CARPET);
    }

    public static boolean isSolid(int block) {
        return isSolid((byte) block);
    }

    public static boolean isSolid(final byte block) {
        if (UtilBlock.blockPassSet.isEmpty()) {
            UtilBlock.blockPassSet.add((byte) 0);
            UtilBlock.blockPassSet.add((byte) 6);
            UtilBlock.blockPassSet.add((byte) 8);
            UtilBlock.blockPassSet.add((byte) 9);
            UtilBlock.blockPassSet.add((byte) 10);
            UtilBlock.blockPassSet.add((byte) 11);
            UtilBlock.blockPassSet.add((byte) 27);
            UtilBlock.blockPassSet.add((byte) 28);
            UtilBlock.blockPassSet.add((byte) 30);
            UtilBlock.blockPassSet.add((byte) 31);
            UtilBlock.blockPassSet.add((byte) 32);
            UtilBlock.blockPassSet.add((byte) 37);
            UtilBlock.blockPassSet.add((byte) 38);
            UtilBlock.blockPassSet.add((byte) 39);
            UtilBlock.blockPassSet.add((byte) 40);
            UtilBlock.blockPassSet.add((byte) 50);
            UtilBlock.blockPassSet.add((byte) 51);
            UtilBlock.blockPassSet.add((byte) 55);
            UtilBlock.blockPassSet.add((byte) 59);
            UtilBlock.blockPassSet.add((byte) 63);
            UtilBlock.blockPassSet.add((byte) 66);
            UtilBlock.blockPassSet.add((byte) 68);
            UtilBlock.blockPassSet.add((byte) 69);
            UtilBlock.blockPassSet.add((byte) 70);
            UtilBlock.blockPassSet.add((byte) 72);
            UtilBlock.blockPassSet.add((byte) 75);
            UtilBlock.blockPassSet.add((byte) 76);
            UtilBlock.blockPassSet.add((byte) 77);
            UtilBlock.blockPassSet.add((byte) 78);
            UtilBlock.blockPassSet.add((byte) 83);
            UtilBlock.blockPassSet.add((byte) 90);
            UtilBlock.blockPassSet.add((byte) 104);
            UtilBlock.blockPassSet.add((byte) 105);
            UtilBlock.blockPassSet.add((byte) 115);
            UtilBlock.blockPassSet.add((byte) 119);
            UtilBlock.blockPassSet.add((byte) (-124));
            UtilBlock.blockPassSet.add((byte) (-113));
            UtilBlock.blockPassSet.add((byte) (-81));
            UtilBlock.blockPassSet.add((byte) (-85));
        }
        return !UtilBlock.blockPassSet.contains(block);
    }

    public static boolean containsBlock(Location Location, Material Material) {
        for (int y = 0; y < 256; y++) {
            Block Current = Location.getWorld().getBlockAt((int) Location.getX(), y, (int) Location.getZ());
            if ((Current != null) && (Current.getType().equals(Material))) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsBlock(Location Location) {
        for (int y = 0; y < 256; y++) {
            Block Current = Location.getWorld().getBlockAt((int) Location.getX(), y, (int) Location.getZ());
            if ((Current != null) && (!Current.getType().equals(Material.AIR))) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsBlockBelow(Location Location) {
        for (int y = 0; y < (int) Location.getY(); y++) {
            Block Current = Location.getWorld().getBlockAt((int) Location.getX(), y, (int) Location.getZ());
            if ((Current != null) && (!Current.getType().equals(Material.AIR))) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Block> getBlocksAroundCenter(Location loc, int radius) {
        ArrayList<Block> blocks = new ArrayList();
        for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
            for (int y = loc.getBlockY() - radius; y <= loc.getBlockY() + radius; y++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    Location l = new Location(loc.getWorld(), x, y, z);
                    if (l.distance(loc) <= radius) {
                        blocks.add(l.getBlock());
                    }
                }
            }
        }
        return blocks;
    }

    public static Location StringToLocation(String Key) {
        String[] Args = Key.split(",");
        World World = Bukkit.getWorld(Args[0]);
        double X = Double.parseDouble(Args[1]);
        double Y = Double.parseDouble(Args[2]);
        double Z = Double.parseDouble(Args[3]);
        float Pitch = Float.parseFloat(Args[4]);
        float Yaw = Float.parseFloat(Args[5]);
        return new Location(World, X, Y, Z, Pitch, Yaw);
    }

    public static String LocationToString(Location Location) {
        return Location.getWorld().getName() + "," + Location.getX() + "," + Location.getY() + "," + Location.getZ()
                + "," + Location.getPitch() + "," + Location.getYaw();
    }

    public static boolean solid(Block block) {
        if (block == null) {
            return false;
        }
        return solid(block.getTypeId());
    }

    public static boolean solid(int block) {
        return solid((byte) block);
    }

    public static boolean solid(byte block) {
        if (blockPassSet.isEmpty()) {
            blockPassSet.add((byte) 0);
            blockPassSet.add((byte) 6);
            blockPassSet.add((byte) 8);
            blockPassSet.add((byte) 9);
            blockPassSet.add((byte) 10);
            blockPassSet.add((byte) 11);
            blockPassSet.add((byte) 26);
            blockPassSet.add((byte) 27);
            blockPassSet.add((byte) 28);
            blockPassSet.add((byte) 30);
            blockPassSet.add((byte) 31);
            blockPassSet.add((byte) 32);
            blockPassSet.add((byte) 37);
            blockPassSet.add((byte) 38);
            blockPassSet.add((byte) 39);
            blockPassSet.add((byte) 40);
            blockPassSet.add((byte) 50);
            blockPassSet.add((byte) 51);
            blockPassSet.add((byte) 55);
            blockPassSet.add((byte) 59);
            blockPassSet.add((byte) 63);
            blockPassSet.add((byte) 64);
            blockPassSet.add((byte) 65);
            blockPassSet.add((byte) 66);
            blockPassSet.add((byte) 68);
            blockPassSet.add((byte) 69);
            blockPassSet.add((byte) 70);
            blockPassSet.add((byte) 71);
            blockPassSet.add((byte) 72);
            blockPassSet.add((byte) 75);
            blockPassSet.add((byte) 76);
            blockPassSet.add((byte) 77);
            blockPassSet.add((byte) 78);
            blockPassSet.add((byte) 83);
            blockPassSet.add((byte) 90);
            blockPassSet.add((byte) 92);
            blockPassSet.add((byte) 93);
            blockPassSet.add((byte) 94);
            blockPassSet.add((byte) 96);
            blockPassSet.add((byte) 101);
            blockPassSet.add((byte) 102);
            blockPassSet.add((byte) 104);
            blockPassSet.add((byte) 105);
            blockPassSet.add((byte) 106);
            blockPassSet.add((byte) 107);
            blockPassSet.add((byte) 111);
            blockPassSet.add((byte) 115);
            blockPassSet.add((byte) 116);
            blockPassSet.add((byte) 117);
            blockPassSet.add((byte) 118);
            blockPassSet.add((byte) 119);
            blockPassSet.add((byte) 120);
            blockPassSet.add((byte) -85);
        }
        return !blockPassSet.contains(block);
    }

    public static boolean airFoliage(Block block) {
        if (block == null) {
            return false;
        }
        return airFoliage(block.getTypeId());
    }

    public static boolean airFoliage(int block) {
        return airFoliage((byte) block);
    }

    public static boolean airFoliage(byte block) {
        if (blockAirFoliageSet.isEmpty()) {
            blockAirFoliageSet.add((byte) 0);
            blockAirFoliageSet.add((byte) 6);
            blockAirFoliageSet.add((byte) 31);
            blockAirFoliageSet.add((byte) 32);
            blockAirFoliageSet.add((byte) 37);
            blockAirFoliageSet.add((byte) 38);
            blockAirFoliageSet.add((byte) 39);
            blockAirFoliageSet.add((byte) 40);
            blockAirFoliageSet.add((byte) 51);
            blockAirFoliageSet.add((byte) 59);
            blockAirFoliageSet.add((byte) 104);
            blockAirFoliageSet.add((byte) 105);
            blockAirFoliageSet.add((byte) 115);
            blockAirFoliageSet.add((byte) -115);
            blockAirFoliageSet.add((byte) -114);
        }
        return blockAirFoliageSet.contains(block);
    }

    public static boolean fullSolid(Block block) {
        if (block == null) {
            return false;
        }
        return fullSolid(block.getTypeId());
    }

    public static boolean fullSolid(int block) {
        return fullSolid((byte) block);
    }

    public static boolean fullSolid(byte block) {
        if (fullSolid.isEmpty()) {
            fullSolid.add((byte) 1);
            fullSolid.add((byte) 2);
            fullSolid.add((byte) 3);
            fullSolid.add((byte) 4);
            fullSolid.add((byte) 5);
            fullSolid.add((byte) 7);
            fullSolid.add((byte) 12);
            fullSolid.add((byte) 13);
            fullSolid.add((byte) 14);
            fullSolid.add((byte) 15);
            fullSolid.add((byte) 16);
            fullSolid.add((byte) 17);
            fullSolid.add((byte) 19);
            fullSolid.add((byte) 20);
            fullSolid.add((byte) 21);
            fullSolid.add((byte) 22);
            fullSolid.add((byte) 23);
            fullSolid.add((byte) 24);
            fullSolid.add((byte) 25);
            fullSolid.add((byte) 29);
            fullSolid.add((byte) 33);
            fullSolid.add((byte) 35);
            fullSolid.add((byte) 41);
            fullSolid.add((byte) 42);
            fullSolid.add((byte) 43);
            fullSolid.add((byte) 44);
            fullSolid.add((byte) 45);
            fullSolid.add((byte) 46);
            fullSolid.add((byte) 47);
            fullSolid.add((byte) 48);
            fullSolid.add((byte) 49);
            fullSolid.add((byte) 56);
            fullSolid.add((byte) 57);
            fullSolid.add((byte) 58);
            fullSolid.add((byte) 60);
            fullSolid.add((byte) 61);
            fullSolid.add((byte) 62);
            fullSolid.add((byte) 73);
            fullSolid.add((byte) 74);
            fullSolid.add((byte) 79);
            fullSolid.add((byte) 80);
            fullSolid.add((byte) 82);
            fullSolid.add((byte) 84);
            fullSolid.add((byte) 86);
            fullSolid.add((byte) 87);
            fullSolid.add((byte) 88);
            fullSolid.add((byte) 89);
            fullSolid.add((byte) 91);
            fullSolid.add((byte) 95);
            fullSolid.add((byte) 97);
            fullSolid.add((byte) 98);
            fullSolid.add((byte) 99);
            fullSolid.add((byte) 100);
            fullSolid.add((byte) 103);
            fullSolid.add((byte) 110);
            fullSolid.add((byte) 112);
            fullSolid.add((byte) 121);
            fullSolid.add((byte) 123);
            fullSolid.add((byte) 124);
            fullSolid.add((byte) 125);
            fullSolid.add((byte) 126);
            fullSolid.add((byte) -127);
            fullSolid.add((byte) -123);
            fullSolid.add((byte) -119);
            fullSolid.add((byte) -118);
            fullSolid.add((byte) -104);
            fullSolid.add((byte) -103);
            fullSolid.add((byte) -101);
            fullSolid.add((byte) -98);
        }
        return fullSolid.contains(block);
    }

    public static boolean usable(Block block) {
        if (block == null) {
            return false;
        }
        return usable(block.getTypeId());
    }

    public static boolean usable(int block) {
        return usable((byte) block);
    }

    public static boolean usable(byte block) {
        if (blockUseSet.isEmpty()) {
            blockUseSet.add((byte) 23);
            blockUseSet.add((byte) 26);
            blockUseSet.add((byte) 33);
            blockUseSet.add((byte) 47);
            blockUseSet.add((byte) 54);
            blockUseSet.add((byte) 58);
            blockUseSet.add((byte) 61);
            blockUseSet.add((byte) 62);
            blockUseSet.add((byte) 64);
            blockUseSet.add((byte) 69);
            blockUseSet.add((byte) 71);
            blockUseSet.add((byte) 77);
            blockUseSet.add((byte) 93);
            blockUseSet.add((byte) 94);
            blockUseSet.add((byte) 96);
            blockUseSet.add((byte) 107);
            blockUseSet.add((byte) 116);
            blockUseSet.add((byte) 117);
            blockUseSet.add((byte) -126);
            blockUseSet.add((byte) -111);
            blockUseSet.add((byte) -110);
            blockUseSet.add((byte) -102);
            blockUseSet.add((byte) -98);
        }
        return blockUseSet.contains(block);
    }

    public static HashMap<Block, Double> getInRadius(Location loc, double dR) {
        return getInRadius(loc, dR, 999.0D);
    }

    public static HashMap<Block, Double> getInRadius(Location loc, double dR, double heightLimit) {
        HashMap<Block, Double> blockList = new HashMap();
        int iR = (int) dR + 1;
        for (int x = -iR; x <= iR; x++) {
            for (int z = -iR; z <= iR; z++) {
                for (int y = -iR; y <= iR; y++) {
                    if (Math.abs(y) <= heightLimit) {
                        Block curBlock = loc.getWorld().getBlockAt((int) (loc.getX() + x), (int) (loc.getY() + y),
                                (int) (loc.getZ() + z));

                        double offset = UtilMath.offset(loc, curBlock.getLocation().add(0.5D, 0.5D, 0.5D));
                        if (offset <= dR) {
                            blockList.put(curBlock, 1.0D - offset / dR);
                        }
                    }
                }
            }
        }
        return blockList;
    }

    public static HashMap<Block, Double> getInRadius(Block block, double dR) {
        HashMap<Block, Double> blockList = new HashMap();
        int iR = (int) dR + 1;
        for (int x = -iR; x <= iR; x++) {
            for (int z = -iR; z <= iR; z++) {
                for (int y = -iR; y <= iR; y++) {
                    Block curBlock = block.getRelative(x, y, z);

                    double offset = UtilMath.offset(block.getLocation(), curBlock.getLocation());
                    if (offset <= dR) {
                        blockList.put(curBlock, 1.0D - offset / dR);
                    }
                }
            }
        }
        return blockList;
    }

    public static boolean isBlock(ItemStack item) {
        if (item == null) {
            return false;
        }
        return (item.getTypeId() > 0) && (item.getTypeId() < 256);
    }

    public static Block getHighest(Location locaton) {
        return getHighest(locaton, null);
    }

    public static Block getHighest(Location location, HashSet<Material> ignore) {
        Location loc = location;
        loc.setY(0);
        for (int i = 0; i < 256; i++) {
            loc.setY(256 - i);
            if (solid(loc.getBlock())) {
                break;
            }
        }
        return loc.getBlock().getRelative(BlockFace.UP);
    }

    public static boolean isInAir(Player player) {
        boolean nearBlocks = false;
        for (Block block : getSurrounding(player.getLocation().getBlock(), true)) {
            if (block.getType() != Material.AIR) {
                nearBlocks = true;
                break;
            }
        }
        return nearBlocks;
    }

    public static ArrayList<Block> getSurrounding(Block block, boolean diagonals) {
        ArrayList<Block> blocks = new ArrayList();
        if (diagonals) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if ((x != 0) || (y != 0) || (z != 0)) {
                            blocks.add(block.getRelative(x, y, z));
                        }
                    }
                }
            }
        } else {
            blocks.add(block.getRelative(BlockFace.UP));
            blocks.add(block.getRelative(BlockFace.DOWN));
            blocks.add(block.getRelative(BlockFace.NORTH));
            blocks.add(block.getRelative(BlockFace.SOUTH));
            blocks.add(block.getRelative(BlockFace.EAST));
            blocks.add(block.getRelative(BlockFace.WEST));
        }
        return blocks;
    }

    public static ArrayList<Block> getSurroundingB(Block block) {
        ArrayList<Block> blocks = new ArrayList();
        for (double x = -0.5; x <= 0.5; x += 0.5) {
            for (double y = -0.5; y <= 0.5; y += 0.5) {
                for (double z = -0.5; z <= 0.5; z += 0.5) {
                    if ((x != 0) || (y != 0) || (z != 0)) {
                        blocks.add(block.getLocation().add(x, y, z).getBlock());
                    }
                }
            }
        }
        return blocks;
    }

    public static ArrayList<Block> getSurroundingXZ(Block block) {
        ArrayList<Block> blocks = new ArrayList();
        blocks.add(block.getRelative(BlockFace.NORTH));
        blocks.add(block.getRelative(BlockFace.NORTH_EAST));
        blocks.add(block.getRelative(BlockFace.NORTH_WEST));
        blocks.add(block.getRelative(BlockFace.SOUTH));
        blocks.add(block.getRelative(BlockFace.SOUTH_EAST));
        blocks.add(block.getRelative(BlockFace.SOUTH_WEST));
        blocks.add(block.getRelative(BlockFace.EAST));
        blocks.add(block.getRelative(BlockFace.WEST));

        return blocks;
    }

    public static ArrayList<Block> getSurroundingXZ(Block block, boolean diagonals) {
        ArrayList<Block> blocks = new ArrayList();
        if (diagonals) {
            blocks.add(block.getRelative(BlockFace.NORTH));
            blocks.add(block.getRelative(BlockFace.NORTH_EAST));
            blocks.add(block.getRelative(BlockFace.NORTH_WEST));
            blocks.add(block.getRelative(BlockFace.SOUTH));
            blocks.add(block.getRelative(BlockFace.SOUTH_EAST));
            blocks.add(block.getRelative(BlockFace.SOUTH_WEST));
            blocks.add(block.getRelative(BlockFace.EAST));
            blocks.add(block.getRelative(BlockFace.WEST));
        } else {
            blocks.add(block.getRelative(BlockFace.NORTH));
            blocks.add(block.getRelative(BlockFace.SOUTH));
            blocks.add(block.getRelative(BlockFace.EAST));
            blocks.add(block.getRelative(BlockFace.WEST));
        }

        return blocks;
    }

    public static String serializeLocation(Location location) {
        int X = (int) location.getX();
        int Y = (int) location.getY();
        int Z = (int) location.getZ();
        int P = (int) location.getPitch();
        int Yaw = (int) location.getYaw();
        return new String(location.getWorld().getName() + "," + X + "," + Y + "," + Z + "," + P + "," + Yaw);
    }

    public static Location deserializeLocation(String string) {
        if (string == null) {
            return null;
        }
        String[] parts = string.split(",");
        World world = Bukkit.getServer().getWorld(parts[0]);
        Double LX = Double.parseDouble(parts[1]);
        Double LY = Double.parseDouble(parts[2]);
        Double LZ = Double.parseDouble(parts[3]);
        Float P = Float.parseFloat(parts[4]);
        Float Y = Float.parseFloat(parts[5]);
        Location result = new Location(world, LX, LY, LZ);
        result.setPitch(P);
        result.setYaw(Y);
        return result;
    }

    public static boolean isVisible(Block block) {
        for (Block other : getSurrounding(block, false)) {
            if (!other.getType().isOccluding()) {
                return true;
            }
        }
        return false;
    }
}