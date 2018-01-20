package funkemunky.Daedalus.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class UtilMath {
	public static Random random;
	public static final String DEFAULT_ENCODING = "UTF-8";

	static {
		UtilMath.random = new Random();
	}

	public static double round(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static double getFraction(final double value) {
		return value % 1.0;
	}

	public static double trim(int degree, double d) {
		String format = "#.#";
		for (int i = 1; i < degree; ++i) {
			format = String.valueOf(format) + "#";
		}
		DecimalFormat twoDForm = new DecimalFormat(format);
		return Double.valueOf(twoDForm.format(d).replaceAll(",", "."));
	}

	public static int r(int i) {
		return UtilMath.random.nextInt(i);
	}

	public static double abs(double a) {
		return (a <= 0.0) ? (0.0 - a) : a;
	}

	public static String ArrayToString(String[] list) {
		String string = "";
		for (final String key : list) {
			string = String.valueOf(string) + key + ",";
		}
		if (string.length() != 0) {
			return string.substring(0, string.length() - 1);
		}
		return null;
	}

	public static String ArrayToString(List<String> list) {
		String string = "";
		for (final String key : list) {
			string = String.valueOf(string) + key + ",";
		}
		if (string.length() != 0) {
			return string.substring(0, string.length() - 1);
		}
		return null;
	}

	public static String[] StringToArray(String string, String split) {
		return string.split(split);
	}

	public static double offset2d(Entity a, Entity b) {
		return offset2d(a.getLocation().toVector(), b.getLocation().toVector());
	}

	public static double offset2d(Location a, Location b) {
		return offset2d(a.toVector(), b.toVector());
	}

	public static String decrypt(String strEncrypted) {
		String strData = "";

		try {
			byte[] decoded = Base64.getDecoder().decode(strEncrypted);
			strData = (new String(decoded, "UTF-8") + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return strData;
	}

	public static double offset2d(Vector a, Vector b) {
		a.setY(0);
		b.setY(0);
		return a.subtract(b).length();
	}

	public static double offset(Entity a, Entity b) {
		return offset(a.getLocation().toVector(), b.getLocation().toVector());
	}

	public static double offset(Location a, Location b) {
		return offset(a.toVector(), b.toVector());
	}

	public static double offset(final Vector a, final Vector b) {
		return a.subtract(b).length();
	}

	public static Vector getHorizontalVector(final Vector v) {
		v.setY(0);
		return v;
	}

	public static Vector getVerticalVector(final Vector v) {
		v.setX(0);
		v.setZ(0);
		return v;
	}

	public static String serializeLocation(Location location) {
		final int X = (int) location.getX();
		final int Y = (int) location.getY();
		final int Z = (int) location.getZ();
		final int P = (int) location.getPitch();
		final int Yaw = (int) location.getYaw();
		return new String(
				String.valueOf(location.getWorld().getName()) + "," + X + "," + Y + "," + Z + "," + P + "," + Yaw);
	}

	public static Location deserializeLocation(String string) {
		String[] parts = string.split(",");
		World world = Bukkit.getServer().getWorld(parts[0]);
		Double LX = Double.parseDouble(parts[1]);
		Double LY = Double.parseDouble(parts[2]);
		Double LZ = Double.parseDouble(parts[3]);
		Float P = Float.parseFloat(parts[4]);
		Float Y = Float.parseFloat(parts[5]);
		Location result = new Location(world, (double) LX, (double) LY, (double) LZ);
		result.setPitch((float) P);
		result.setYaw((float) Y);
		return result;
	}

	public static long averageLong(List<Long> list) {
		long add = 0L;
		for (final Long listlist : list) {
			add += listlist;
		}
		return add / list.size();
	}

	public static double averageDouble(List<Double> list) {
		Double add = 0.0;
		for (Double listlist : list) {
			add += listlist;
		}
		return add / list.size();
	}
}