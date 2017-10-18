package anticheat.checks.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import anticheat.Daedalus;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.MiscUtils;

@ChecksListener(events = { PlayerMoveEvent.class })
public class Jesus extends Checks {

	public Map<Player, Integer> onWater;
	public ArrayList<Player> placedBlockOnWater;
	public Map<Player, Integer> count;
	public Map<UUID, Double> velocity;

	public Jesus() {
		super("Jesus", ChecksType.MOVEMENT, Daedalus.getAC(), 10, true, true);

		this.onWater = new HashMap<Player, Integer>();
		this.placedBlockOnWater = new ArrayList<Player>();
		this.count = new HashMap<Player, Integer>();
		this.velocity = new HashMap<UUID, Double>();
	}

	@Override
	protected void onEvent(Event event) {
		if(!this.getState()) {
			return;
		}
		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			if ((e.getFrom().getX() == e.getTo().getX()) && (e.getFrom().getZ() == e.getTo().getZ())) {
				return;
			}
			Player p = e.getPlayer();
			if (p.getVelocity().length() < velocity.getOrDefault(p.getUniqueId(), -1.0D)) {
				return;
			}
			if (p.hasPermission("daedalus.bypass")) {
				return;
			}
			if (p.getAllowFlight()) {
				return;
			}
			if (!p.getNearbyEntities(1.0D, 1.0D, 1.0D).isEmpty()) {
				return;
			}
			if (MiscUtils.isOnLilyPad(p)) {
				return;
			}

			if (this.placedBlockOnWater.remove(p)) {
				return;
			}
			int Count = 0;
			if (count.containsKey(p)) {
				Count = count.get(p);
			}
			if ((MiscUtils.cantStandAtWater(p.getWorld().getBlockAt(p.getLocation())))
					&& (MiscUtils.isHoveringOverWater(p.getLocation()))
					&& (!MiscUtils.isFullyInWater(p.getLocation()))) {
				count.put(p, Count + 1);
			}

			if (Count >= 20) {
				User user = Daedalus.getAC().getUserManager().getUser(p.getUniqueId());
				count.remove(p);
				user.setVL(this, user.getVL(this) + 1);
				this.Alert(p, null);
				
				
			}
			if (!p.isOnGround()) {
				this.velocity.put(p.getUniqueId(), p.getVelocity().length());
			} else {
				this.velocity.put(p.getUniqueId(), -1.0D);
			}
		}
	}

}
