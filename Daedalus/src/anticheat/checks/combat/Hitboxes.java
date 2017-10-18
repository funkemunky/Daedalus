package anticheat.checks.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import anticheat.Daedalus;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;
import anticheat.utils.Latency;
import anticheat.utils.MiscUtils;

@ChecksListener(events = {PlayerMoveEvent.class, EntityDamageByEntityEvent.class, PlayerQuitEvent.class})
public class Hitboxes extends Checks {
	
	public static Map<UUID, Integer> count = new HashMap();
	public static Map<UUID, Player> lastHit;
	public static Map<UUID, Double> yawDif;
	
	public Hitboxes() {
		super("Hitboxes", ChecksType.COMBAT, Daedalus.getAC(), 6, true, false);
		
		this.yawDif = new HashMap<UUID, Double>();
		this.lastHit = new HashMap<UUID, Player>();
		this.count = new HashMap<UUID, Integer>();
	}
	
	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}
		if(event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			double yawDif = Math.abs(e.getFrom().getYaw() - e.getTo().getYaw()); 
			this.yawDif.put(e.getPlayer().getUniqueId(), yawDif);
		}
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (e.getCause() != DamageCause.ENTITY_ATTACK) {
				return;
			}
			if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
				return;
			}
			Player player = (Player) e.getDamager();
			Player attacked = (Player) e.getEntity();
			if (player.hasPermission("daedalus.bypass")) {
				return;
			}

			int Count = 0;
			double yawDif = 0;
			Player lastPlayer = attacked;

			if (this.lastHit.containsKey(player.getUniqueId())) {
				lastPlayer = this.lastHit.get(player.getUniqueId());
			}

			if (count.containsKey(player.getUniqueId())) {
				Count = count.get(player.getUniqueId());
			}
			if (this.yawDif.containsKey(player.getUniqueId())) {
				yawDif = this.yawDif.get(player.getUniqueId());
			}

			if (lastPlayer != attacked) {
				this.lastHit.put(player.getUniqueId(), attacked);
				return;
			}

			double offset = MiscUtils.getOffsetOffCursor(player, attacked);
			double Limit = 55D;
			double distance = MiscUtils.getHorizontalDistance(player.getLocation(), attacked.getLocation());
			Limit += distance * 14;
			Limit += (attacked.getVelocity().length() + player.getVelocity().length()) * 48;
			Limit += yawDif * 1.51;

			if (Latency.getLag(player) > 80 || Latency.getLag(attacked) > 80) {
				return;
			}

			if (offset > Limit) {
				Count++;
			} else {
				Count = 0;
			}

			if (Count > 1) {
				this.Alert(player, "Type A");
				User user = Daedalus.getAC().getUserManager().getUser(player.getUniqueId());
				user.setVL(this, user.getVL(this) + 1);
				Count = 0;
			}

			this.count.put(player.getUniqueId(), Count);
			this.lastHit.put(player.getUniqueId(), attacked);
		}
	}

}
