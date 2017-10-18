package anticheat.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import anticheat.detections.Checks;

public class User {

	private Player player;
	private UUID uuid;
	private Map<Checks, Integer> vl;
	private int AirTicks = 0;
	private int GroundTicks = 0;
	private int IceTicks = 0;
	private boolean hasAlerts = false;

	private int leftClicks;
	private int rightClicks;

	public User(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId();
		this.vl = new HashMap<Checks, Integer>();
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isStaff() {
		if (this.player.hasPermission("Daedalus.staff")) {
			return true;
		}
		return false;

	}

	public UUID getUUID() {
		return uuid;
	}

	public int getVL(Checks check) {
		return vl.getOrDefault(check, 0);
	}

	public int setVL(Checks check, int vl) {
		return this.vl.put(check, vl);
	}
	
	public Map<Checks, Integer> getVLs() {
		return this.vl;
	}

	public boolean needBan(Checks check) {
		return getVL(check) >= check.getWeight();
	}

	public int clearVL(Checks check) {
		return getVLs().put(check, 0);
	}

	public void clearData() {
		this.player = null;
		this.uuid = null;
		this.vl.clear();;
		setAirTicks(0);
		setGroundTicks(0);
		setIceTicks(0);
		setRightClicks(0);
		setLeftClicks(0);
	}

	/**
	 * @return the airTicks
	 */
	public int getAirTicks() {
		return AirTicks;
	}

	/**
	 * @param airTicks
	 *            the airTicks to set
	 */
	public void setAirTicks(int airTicks) {
		AirTicks = airTicks;
	}

	/**
	 * @return the groundTicks
	 */
	public int getGroundTicks() {
		return GroundTicks;
	}

	/**
	 * @param groundTicks
	 *            the groundTicks to set
	 */
	public void setGroundTicks(int groundTicks) {
		GroundTicks = groundTicks;
	}

	/**
	 * @return the iceTicks
	 */
	public int getIceTicks() {
		return IceTicks;
	}

	/**
	 * @param iceTicks
	 *            the iceTicks to set
	 */
	public void setIceTicks(int iceTicks) {
		IceTicks = iceTicks;
	}

	/**
	 * @return the hasAlerts
	 */
	public boolean isHasAlerts() {
		return hasAlerts;
	}

	/**
	 * @param hasAlerts
	 *            the hasAlerts to set
	 */
	public void setHasAlerts(boolean hasAlerts) {
		this.hasAlerts = hasAlerts;
	}

	/**
	 * @return the leftClicks
	 */
	public int getLeftClicks() {
		return leftClicks;
	}

	/**
	 * @param leftClicks
	 *            the leftClicks to set
	 */
	public void setLeftClicks(int leftClicks) {
		this.leftClicks = leftClicks;
	}

	/**
	 * @return the rightClicks
	 */
	public int getRightClicks() {
		return rightClicks;
	}

	/**
	 * @param rightClicks
	 *            the rightClicks to set
	 */
	public void setRightClicks(int rightClicks) {
		this.rightClicks = rightClicks;
	}

}
