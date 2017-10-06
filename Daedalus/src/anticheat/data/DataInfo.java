package anticheat.data;

import org.bukkit.entity.Player;


/**
 * Created by XtasyCode on 11/08/2017.
 */

public class DataInfo {

	private float yaw, pitch;
	private int x, y, z;
	private boolean onground;

	@SuppressWarnings("deprecation")
	public DataInfo(Player p) {
		this.onground = p.isOnGround();
		this.yaw = p.getLocation().getYaw();
		this.pitch = p.getLocation().getPitch();
		this.x = p.getLocation().getBlockX();
		this.y = p.getLocation().getBlockY();
		this.z = p.getLocation().getBlockZ();
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public boolean isOnground() {
		return onground;
	}

}
