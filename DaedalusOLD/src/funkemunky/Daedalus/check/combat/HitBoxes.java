package funkemunky.Daedalus.check.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import funkemunky.Daedalus.packets.events.PacketUseEntityEvent;
import funkemunky.Daedalus.utils.UtilMath;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.check.other.Latency;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;

public class HitBoxes extends Check {

	public HitBoxes(Daedalus Daedalus) {
		super("HitBoxes", "Hitboxes", Daedalus);

		setEnabled(true);
		setBannable(false);

		setMaxViolations(5);
	}

	public static Map<UUID, Integer> count = new HashMap<>();
	public static Map<UUID, Player> lastHit = new HashMap<>();
	public static Map<UUID, Double> yawDif = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent e) {
		if (count.containsKey(e.getPlayer().getUniqueId())) {
			count.remove(e.getPlayer().getUniqueId());
		}
		if (yawDif.containsKey(e.getPlayer().getUniqueId())) {
			yawDif.remove(e.getPlayer().getUniqueId());
		}
		if (lastHit.containsKey(e.getPlayer().getUniqueId())) {
			lastHit.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUse(PacketUseEntityEvent e) {

		Player player = e.getAttacker();
		Player attacked = (Player) e.getAttacked();
		if (player.hasPermission("daedalus.bypass")
				|| player.getAllowFlight()) {
			return;
		}

		int verbose = count.getOrDefault(player.getUniqueId(), 0);

		double offset = UtilCheat.getOffsetOffCursor(player, attacked);

		if(offset > 30) {
			if((verbose+= 2) > 25) {
				getDaedalus().logCheat(this, player, UtilMath.round(offset, 4) + ">-30", Chance.HIGH);
			}
		} else if(verbose > 0) {
			verbose--;
		}

		count.put(player.getUniqueId(), verbose);
	}

}
