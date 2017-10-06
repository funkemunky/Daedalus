package anticheat.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class PlayerUtils {

	public static final double PLAYER_WIDTH = .6;

	public static void kick(Player p, String reason) {
		if (p.isOnline()) {
			p.kickPlayer("§8[§4" + "Daedalus" + "§8]:\n§c" + reason);
			Bukkit.broadcastMessage(
					"§8[§4" + "Daedalus" + "§8]" + " §7kicked §0" + p.getDisplayName() + " §7for §a" + reason);
		}
	}

	public static void DevAlerts(String reason) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase("ItsMeBabe")) {
				p.sendMessage("§8[§b" + "DevAlerts" + "§8] " + "§7" + ChatColor.RED + reason);
			}
		}
	}

	public static double frac(double value) {
		return value % 1.0;
	}

	public static double getDistanceBetweenAngles(final float angle1, final float angle2) {
		float distance = Math.abs(angle1 - angle2) % 360.0f;
		if (distance > 180.0f) {
			distance = 360.0f - distance;
		}
		return distance;
	}

	public static Location getEyeLocation(final Player player) {
		final Location eye = player.getLocation();
		eye.setY(eye.getY() + player.getEyeHeight());
		return eye;
	}

	public static double offset(Vector a, Vector b) {
		return a.subtract(b).length();
	}

	@SuppressWarnings("deprecation")
	public static boolean isReallyOnground(Player p) {
		Location l = p.getLocation();
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		Location b = new Location(p.getWorld(), x, y - 1, z);

		if (p.isOnGround() && b.getBlock().getType() != Material.AIR && b.getBlock().getType() != Material.WEB
				&& !b.getBlock().isLiquid()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean flaggyStuffNear(Location loc) {
		boolean nearBlocks = false;
		for (Block bl : getSurrounding(loc.getBlock(), true)) {
			if ((bl.getType().equals(Material.STEP)) || (bl.getType().equals(Material.DOUBLE_STEP))
					|| (bl.getType().equals(Material.BED)) || (bl.getType().equals(Material.WOOD_DOUBLE_STEP))
					|| (bl.getType().equals(Material.WOOD_STEP))) {
				nearBlocks = true;
				break;
			}
		}
		for (Block bl : getSurrounding(loc.getBlock(), false)) {
			if ((bl.getType().equals(Material.STEP)) || (bl.getType().equals(Material.DOUBLE_STEP))
					|| (bl.getType().equals(Material.BED)) || (bl.getType().equals(Material.WOOD_DOUBLE_STEP))
					|| (bl.getType().equals(Material.WOOD_STEP))) {
				nearBlocks = true;
				break;
			}
		}
		if (isBlock(loc.getBlock().getRelative(BlockFace.DOWN), new Material[] { Material.STEP, Material.BED,
				Material.DOUBLE_STEP, Material.WOOD_DOUBLE_STEP, Material.WOOD_STEP })) {
			nearBlocks = true;
		}
		return nearBlocks;
	}

	public static ArrayList<Block> getSurrounding(Block block, boolean diagonals) {
		ArrayList<Block> blocks = new ArrayList<Block>();
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

	public static boolean isBlock(Block block, Material[] materials) {
		Material type = block.getType();
		Material[] arrayOfMaterial;
		int j = (arrayOfMaterial = materials).length;
		for (int i = 0; i < j; i++) {
			Material m = arrayOfMaterial[i];
			if (m == type) {
				return true;
			}
		}
		return false;
	}

	public static boolean shouldNotFlag(Location loc) {
		return isMaterialGlideable(loc.getBlock().getType())
				|| isMaterialGlideable(loc.clone().add(PLAYER_WIDTH / 2, 0, 0).getBlock().getType())
				|| isMaterialGlideable(loc.clone().add(PLAYER_WIDTH / 2, 0, PLAYER_WIDTH / 2).getBlock().getType())
				|| isMaterialGlideable(loc.clone().add(PLAYER_WIDTH / 2, 0, -PLAYER_WIDTH / 2).getBlock().getType())
				|| isMaterialGlideable(loc.clone().add(-PLAYER_WIDTH / 2, 0, 0).getBlock().getType())
				|| isMaterialGlideable(loc.clone().add(-PLAYER_WIDTH / 2, 0, -PLAYER_WIDTH / 2).getBlock().getType())
				|| isMaterialGlideable(loc.clone().add(0, 0, -PLAYER_WIDTH / 2).getBlock().getType())
				|| isMaterialGlideable(loc.clone().add(0, 0, PLAYER_WIDTH / 2).getBlock().getType())
				|| isOnGround(loc.clone().add(0, 0, 0));

	}

	public static boolean isOnGround(Location loc) {
		return loc.getBlock().getLocation().clone().add(0, -1, 0).getBlock().getType().isSolid()
				|| loc.clone().add(PLAYER_WIDTH / 2, -1, 0).getBlock().getType().isSolid()
				|| loc.clone().add(PLAYER_WIDTH / 2, -1, PLAYER_WIDTH / 2).getBlock().getType().isSolid()
				|| loc.clone().add(PLAYER_WIDTH / 2, -1, -PLAYER_WIDTH / 2).getBlock().getType().isSolid()
				|| loc.clone().add(-PLAYER_WIDTH / 2, -1, 0).getBlock().getType().isSolid()
				|| loc.clone().add(-PLAYER_WIDTH / 2, -1, -PLAYER_WIDTH / 2).getBlock().getType().isSolid()
				|| loc.clone().add(0, -1, -PLAYER_WIDTH / 2).getBlock().getType().isSolid()
				|| loc.clone().add(0, -1, PLAYER_WIDTH / 2).getBlock().getType().isSolid();
	}

	public static boolean isMaterialGlideable(Material mat) {
		// TODO Add liquids
		switch (mat) {
		case STATIONARY_WATER:
		case WATER:
		case LADDER:
		case VINE:
			return true;
		default:
			return false;
		}
	}

	public static double[] getOffsets(Player player, LivingEntity livingEntity) {
		Location add = livingEntity.getLocation().add(0.0, livingEntity.getEyeHeight(), 0.0);
		Location add2 = player.getLocation().add(0.0, player.getEyeHeight(), 0.0);
		Vector vector = new Vector(add2.getYaw(), add2.getPitch(), 0.0f);
		Vector rotation = getRotation(add2, add);
		double fix180 = fix180(vector.getX() - rotation.getX());
		double fix181 = fix180(vector.getY() - rotation.getY());
		double horizontalDistance = getHorizontalDistance(add2, add);
		double distance3D = getDistance3D(add2, add);
		return new double[] { Math.abs(fix180 * horizontalDistance * distance3D),
				Math.abs(fix181 * Math.abs(Math.sqrt(add.getY() - add2.getY())) * distance3D) };
	}

	public static Vector getRotation(Location location, Location location2) {
		double n = location2.getX() - location.getX();
		double n2 = location2.getY() - location.getY();
		double n3 = location2.getZ() - location.getZ();
		return new Vector((float) (Math.atan2(n3, n) * 180.0 / 3.141592653589793) - 90.0f,
				(float) (-(Math.atan2(n2, Math.sqrt(n * n + n3 * n3)) * 180.0 / 3.141592653589793)), 0.0f);
	}

	public static double getVerticalDistance(Location location, Location location2) {
		return Math.abs(Math.sqrt((location2.getY() - location.getY()) * (location2.getY() - location.getY())));
	}

	public static double getHorizontalDistance(Location location, Location location2) {
		return Math.abs(Math.sqrt((location2.getX() - location.getX()) * (location2.getX() - location.getX())
				+ (location2.getZ() - location.getZ()) * (location2.getZ() - location.getZ())));
	}

	public static double fix180(double n) {
		n %= 360.0;
		if (n >= 180.0) {
			n -= 360.0;
		}
		if (n < -180.0) {
			n += 360.0;
		}
		return n;
	}

	public static double getDistance3D(Location location, Location location2) {
		return Math.abs(Math.sqrt((location2.getX() - location.getX()) * (location2.getX() - location.getX())
				+ (location2.getY() - location.getY()) * (location2.getY() - location.getY())
				+ (location2.getZ() - location.getZ()) * (location2.getZ() - location.getZ())));
	}

	public static boolean isAir(Player player) {
		Block b = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		return b.getType().equals((Object) Material.AIR)
				&& b.getRelative(BlockFace.WEST).getType().equals((Object) Material.AIR)
				&& b.getRelative(BlockFace.NORTH).getType().equals((Object) Material.AIR)
				&& b.getRelative(BlockFace.EAST).getType().equals((Object) Material.AIR)
				&& b.getRelative(BlockFace.SOUTH).getType().equals((Object) Material.AIR);
	}

	public static boolean isInCobWeb(Player player) {
		return player.getLocation().getBlock().getType() == Material.WEB
				|| player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WEB
				|| player.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.WEB;
	}
}
