package funkemunky.Daedalus.check.combat;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketUseEntityEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilTime;

public class KillAuraA extends Check {
	public static Map<UUID, Long> LastMS;
	public static Map<UUID, List<Long>> Clicks;
	public static Map<UUID, Map.Entry<Integer, Long>> ClickTicks;

	public KillAuraA(final Daedalus Daedalus) {
		super("KillAuraA", "Kill Aura (Click Pattern)", Daedalus);
		this.LastMS = new HashMap<>();
		this.Clicks = new HashMap<>();
		this.ClickTicks = new HashMap<>();

		this.setEnabled(true);
		this.setBannable(true);
		this.setViolationResetTime(300000);
		this.setMaxViolations(10);
	}

	@EventHandler
	public void onLog(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();

		if (LastMS.containsKey(uuid)) {
			LastMS.remove(uuid);
		}
		if (Clicks.containsKey(uuid)) {
			Clicks.remove(uuid);
		}
		if (ClickTicks.containsKey(uuid)) {
			ClickTicks.remove(uuid);
		}
	}

	@EventHandler
	public void UseEntity(PacketUseEntityEvent e) {
		if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK
				|| !((e.getAttacked()) instanceof Player)) {
			return;
		}

		Player damager = e.getAttacker();
		if (damager.hasPermission("daedalus.bypass")) {
			return;
		}
		int Count = 0;
		long Time = System.currentTimeMillis();
		if (ClickTicks.containsKey(damager.getUniqueId())) {
			Count = ClickTicks.get(damager.getUniqueId()).getKey();
			Time = ClickTicks.get(damager.getUniqueId()).getValue();
		}
		if (LastMS.containsKey(damager.getUniqueId())) {
			long MS = UtilTime.nowlong() - LastMS.get(damager.getUniqueId());
			if (MS > 500L || MS < 5L) {
				LastMS.put(damager.getUniqueId(), UtilTime.nowlong());
				return;
			}
			if (Clicks.containsKey(damager.getUniqueId())) {
				List<Long> Clicks = this.Clicks.get(damager.getUniqueId());
				if (Clicks.size() == 10) {
					this.Clicks.remove(damager.getUniqueId());
					Collections.sort(Clicks);
					final long Range = Clicks.get(Clicks.size() - 1) - Clicks.get(0);
					if (Range < 30L) {
						++Count;
						Time = System.currentTimeMillis();
						this.dumplog(damager, "New Range: " + Range);
						this.dumplog(damager, "New Count: " + Count);
					}
				} else {
					Clicks.add(MS);
					this.Clicks.put(damager.getUniqueId(), Clicks);
				}
			} else {
				final List<Long> Clicks = new ArrayList<Long>();
				Clicks.add(MS);
				this.Clicks.put(damager.getUniqueId(), Clicks);
			}
		}
		if (ClickTicks.containsKey(damager.getUniqueId()) && UtilTime.elapsed(Time, 5000L)) {
			Count = 0;
			Time = UtilTime.nowlong();
		}
		if ((Count > 2 && this.getDaedalus().getLag().getPing(damager) < 100)
				|| (Count > 4 && this.getDaedalus().getLag().getPing(damager) <= 400)) {
			this.dumplog(damager, "Logged. Count: " + Count);
			Count = 0;
			this.getDaedalus().logCheat(this, damager, null, Chance.HIGH, new String[0]);
			ClickTicks.remove(damager.getUniqueId());
		} else if (this.getDaedalus().getLag().getPing(damager) > 400) {
			this.dumplog(damager, "Would set off Killaura (Click Pattern) but latency is too high!");
		}
		LastMS.put(damager.getUniqueId(), UtilTime.nowlong());
		ClickTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
	}
}