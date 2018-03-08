package funkemunky.Daedalus.check.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.wrappers.EnumWrappers;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketUseEntityEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;
import funkemunky.Daedalus.utils.UtilTime;

public class KillAuraB extends Check {
	public static Map<UUID, Map.Entry<Integer, Long>> AuraTicks;

	public KillAuraB(Daedalus Daedalus) {
		super("KillAuraB", "Kill Aura (Hit Miss Ratio)", Daedalus);
		
		AuraTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();

		setEnabled(false);
		setBannable(true);
		setMaxViolations(150);
		setViolationsToNotify(140);
	}

	@EventHandler
	public void onLog(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();

		if (AuraTicks.containsKey(uuid)) {
			AuraTicks.remove(uuid);
		}
	}

	@EventHandler
	public void UseEntity(PacketUseEntityEvent e) {
		if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK
				|| !((e.getAttacked()) instanceof Player)
				|| getDaedalus().isSotwMode()) {
			return;
		}

		Player damager = e.getAttacker();
		Player player = (Player) e.getAttacked();
		
		if (damager.hasPermission("daedalus.bypass")
				|| damager.getAllowFlight()
				|| player.getAllowFlight()) {
			return;
		}
		
		int Count = 0;
		long Time = System.currentTimeMillis();
		if (AuraTicks.containsKey(damager.getUniqueId())) {
			Count = AuraTicks.get(damager.getUniqueId()).getKey();
			Time = AuraTicks.get(damager.getUniqueId()).getValue();
		}
		double OffsetXZ = UtilCheat.getAimbotoffset(damager.getLocation(), damager.getEyeHeight(),
				player);
		double LimitOffset = 200.0;
		if (damager.getVelocity().length() > 0.08
				|| this.getDaedalus().LastVelocity.containsKey(damager.getUniqueId())) {
			LimitOffset += 200.0;
		}
		int Ping = this.getDaedalus().getLag().getPing(damager);
		if (Ping >= 100 && Ping < 200) {
			LimitOffset += 50.0;
		} else if (Ping >= 200 && Ping < 250) {
			LimitOffset += 75.0;
		} else if (Ping >= 250 && Ping < 300) {
			LimitOffset += 150.0;
		} else if (Ping >= 300 && Ping < 350) {
			LimitOffset += 300.0;
		} else if (Ping >= 350 && Ping < 400) {
			LimitOffset += 400.0;
		} else if (Ping > 400) {
			return;
		}
		if (OffsetXZ > LimitOffset * 4.0) {
			Count += 12;
		} else if (OffsetXZ > LimitOffset * 3.0) {
			Count += 10;
		} else if (OffsetXZ > LimitOffset * 2.0) {
			Count += 8;
		} else if (OffsetXZ > LimitOffset) {
			Count += 4;
		}
		if (AuraTicks.containsKey(damager.getUniqueId()) && UtilTime.elapsed(Time, 60000L)) {
			Count = 0;
			Time = UtilTime.nowlong();
		}
		if (Count >= 16) {
			this.dumplog(damager, "Offset: " + OffsetXZ + ", Ping: " + Ping + ", Max Offset: " + LimitOffset);
			this.dumplog(damager, "Logged. Count: " + Count + ", Ping: " + Ping);
			Count = 0;
			this.getDaedalus().logCheat(this, damager, null, Chance.LIKELY, "Experimental");
		}
		AuraTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
	}
}