package funkemunky.Daedalus.check.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.wrappers.EnumWrappers;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketUseEntityEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilTime;

public class KillAuraC extends Check {
	public static Map<UUID, Map.Entry<Integer, Long>> AimbotTicks;
	public static Map<UUID, Double> Differences;
	public static Map<UUID, Location> LastLocation;

	public KillAuraC(final Daedalus Daedalus) {
		super("KillAuraC", "Kill Aura (Aimbot)", Daedalus);
		AimbotTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
		Differences = new HashMap<UUID, Double>();
		LastLocation = new HashMap<UUID, Location>();

		this.setEnabled(true);
		this.setBannable(true);

		this.setMaxViolations(11);
		this.setViolationResetTime(120000);
		this.setViolationsToNotify(2);
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		if (AimbotTicks.containsKey(e.getPlayer().getUniqueId())) {
			AimbotTicks.remove(e.getPlayer().getUniqueId());
		}
		if (Differences.containsKey(e.getPlayer().getUniqueId())) {
			Differences.remove(e.getPlayer().getUniqueId());
		}
		if (LastLocation.containsKey(e.getPlayer().getUniqueId())) {
			LastLocation.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void UseEntity(PacketUseEntityEvent e) {
		if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
			return;
		}
		Player damager = e.getAttacker();
		if (damager.hasPermission("daedalus.bypass")) {
			return;
		}
		if (damager.getAllowFlight()) {
			return;
		}
		if (!((e.getAttacked()) instanceof Player)) {
			return;
		}
		Location from = null;
		Location to = damager.getLocation();
		if (LastLocation.containsKey(damager.getUniqueId())) {
			from = LastLocation.get(damager.getUniqueId());
		}
		LastLocation.put(damager.getUniqueId(), damager.getLocation());
		double Count = 0;
		long Time = System.currentTimeMillis();
		double LastDifference = -111111.0;
		if (Differences.containsKey(damager.getUniqueId())) {
			LastDifference = Differences.get(damager.getUniqueId());
		}
		if (AimbotTicks.containsKey(damager.getUniqueId())) {
			Count = AimbotTicks.get(damager.getUniqueId()).getKey();
			Time = AimbotTicks.get(damager.getUniqueId()).getValue();
		}
		if (from == null || (to.getX() == from.getX() && to.getZ() == from.getZ())) {
			return;
		}
		double Difference = Math.abs(to.getYaw() - from.getYaw());
		if (Difference == 0.0) {
			return;
		}

		if (Difference > 2.4) {
			this.dumplog(damager, "Difference: " + Difference);
			double diff = Math.abs(LastDifference - Difference);
			if (e.getAttacked().getVelocity().length() < 0.1) {
				if (diff < 1.4) {
					Count += 1;
				} else {
					Count = 0;
				}
			} else {
				if (diff < 1.8) {
					Count += 1;
				} else {
					Count = 0;
				}
			}
		}
		Differences.put(damager.getUniqueId(), Difference);
		if (AimbotTicks.containsKey(damager.getUniqueId()) && UtilTime.elapsed(Time, 5000L)) {
			dumplog(damager, "Count Reset");
			Count = 0;
			Time = UtilTime.nowlong();
		}
		if (Count >= 4) {
			Count = 0;
			dumplog(damager,
					"Logged. Last Difference: " + Math.abs(to.getYaw() - from.getYaw()) + ", Count: " + Count);
			getDaedalus().logCheat(this, damager, null, Chance.LIKELY, new String[0]);
		}
		AimbotTicks.put(damager.getUniqueId(),
				new AbstractMap.SimpleEntry<Integer, Long>((int) Math.round(Count), Time));
	}
}