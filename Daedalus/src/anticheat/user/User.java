package anticheat.user;

import java.util.UUID;

import org.bukkit.entity.Player;

public class User {

	private Player player;
	private UUID uuid;
	private int vl;
	private int AirTicks = 0;
	private int GroundTicks = 0;
	private int IceTicks = 0;
	private boolean hasAlerts = false;

	private int leftClicks;
	private int rightClicks;

	public User(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId();
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

	public int getVL() {
		return vl;
	}

	public int setVL(int vl) {
		return this.vl = vl;
	}

	public boolean needBan() {
		return getVL() >= 30;
	}

	public int clearVL() {
		return setVL(0);
	}

	public void clearData() {
		this.player = null;
		this.uuid = null;
		vl = 0;
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
