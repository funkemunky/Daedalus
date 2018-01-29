package funkemunky.Daedalus.check.other;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilTime;

public class TimerB extends Check {

	public static Map<UUID, Map.Entry<Integer, Long>> timerTicks;

	public TimerB(Daedalus Daedalus) {
		super("TimerB", "Timer (Type B)", Daedalus);

		setViolationsToNotify(1);
		setMaxViolations(9);
		setEnabled(true);
		setBannable(false);

		timerTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent e) {
		if (!getDaedalus().isEnabled()) {
			return;
		}
		Player player = e.getPlayer();
		
		if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()
				&& e.getFrom().getY() == e.getTo().getY()) {
			return;
		}
		if (getDaedalus().isSotwMode() || player.hasPermission("daedalus.bypass")
				|| Latency.getLag(player) > 500) {
			return;
		}
		int Count = 0;
		long Time = System.currentTimeMillis();
		if (timerTicks.containsKey(player.getUniqueId())) {
			Count = timerTicks.get(player.getUniqueId()).getKey().intValue();
			Time = timerTicks.get(player.getUniqueId()).getValue().longValue();
		}

		Count++;

		if ((timerTicks.containsKey(player.getUniqueId())) && (UtilTime.elapsed(Time, 1000L))) {
			if (Count > 35) {
				this.getDaedalus().logCheat(this, player, null, Chance.LIKELY, new String[] { "Experimental" });
			}
			Count = 0;
			Time = UtilTime.nowlong();
		}
		timerTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
	}

}
