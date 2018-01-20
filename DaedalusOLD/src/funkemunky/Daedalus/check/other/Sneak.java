package funkemunky.Daedalus.check.other;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketEntityActionEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilTime;

public class Sneak extends Check {
	public static Map<UUID, Map.Entry<Integer, Long>> sneakTicks;

	public Sneak(Daedalus daedalus) {
		super("Sneak", "Sneak", daedalus);

		setEnabled(true);
		setBannable(true);
		setMaxViolations(5);

		sneakTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
	}

	@EventHandler
	public void onLog(PlayerQuitEvent e) {
		if (sneakTicks.containsKey(e.getPlayer().getUniqueId())) {
			sneakTicks.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void EntityAction(PacketEntityActionEvent event) {
		if (event.getAction() != 1) {
			return;
		}
		Player player = event.getPlayer();

		if (player.hasPermission("daedalus.bypass")) {
			return;
		}
		if (getDaedalus().isSotwMode()) {
			return;
		}

		int Count = 0;
		long Time = -1L;
		if (sneakTicks.containsKey(player.getUniqueId())) {
			Count = sneakTicks.get(player.getUniqueId()).getKey().intValue();
			Time = sneakTicks.get(player.getUniqueId()).getValue().longValue();
		}
		Count++;
		if (sneakTicks.containsKey(player.getUniqueId())) {
			if (UtilTime.elapsed(Time, 100L)) {
				Count = 0;
				Time = System.currentTimeMillis();
			} else {
				Time = System.currentTimeMillis();
			}
		}
		if (Count > 50) {
			Count = 0;

			getDaedalus().logCheat(this, player, null, Chance.HIGH, new String[0]);
		}
		sneakTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
	}
}
