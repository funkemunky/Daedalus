package funkemunky.Daedalus.check.movement;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.check.other.Latency;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;
import funkemunky.Daedalus.utils.UtilTime;

public class AscensionB extends Check {

	public AscensionB(funkemunky.Daedalus.Daedalus Daedalus) {
		super("AscensionB", "Ascension (Type B)", Daedalus);

		setBannable(true);
		setEnabled(true);
		setMaxViolations(5);
	}

	public static Map<UUID, Map.Entry<Integer, Long>> flyTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
	public static Map<UUID, Double> velocity = new HashMap<UUID, Double>();

	@EventHandler
	public void onLog(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();

		if (flyTicks.containsKey(uuid)) {
			flyTicks.remove(uuid);
		}
	}

	@EventHandler
	public void CheckAscension(PlayerMoveEvent e) {
		Player p = e.getPlayer();

		/** Shit I spent 20 minutes fixing this. Remember me **/
		if (e.isCancelled()
				|| !getDaedalus().isEnabled()
				|| p.getVehicle() != null
				|| getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()
				|| e.getFrom().getY() >= e.getTo().getY()
				|| p.getAllowFlight()
				|| !UtilTime.elapsed(getDaedalus().LastVelocity.getOrDefault(p.getUniqueId(), 0L), 4200L)
				|| p.hasPermission("daedalus.bypass")
				|| getDaedalus().isSotwMode()
				|| Latency.getLag(p) > 75
				|| this.getDaedalus().getLastVelocity().containsKey(p.getUniqueId())) {
			return;
		}
		
		Location to = e.getTo();
		Location from = e.getFrom();
		int Count = 0;
		long Time = UtilTime.nowlong();
		if (flyTicks.containsKey(p.getUniqueId())) {
			Count = flyTicks.get(p.getUniqueId()).getKey().intValue();
			Time = flyTicks.get(p.getUniqueId()).getValue().longValue();
		}
		if (flyTicks.containsKey(p.getUniqueId())) {
			double Offset = to.getY() - from.getY();
			double Limit = 0.5D;
			double TotalBlocks = Offset;

			if (UtilCheat.blocksNear(p)) {
				TotalBlocks = 0.0D;
			}
			Location a = p.getLocation().subtract(0.0D, 1.0D, 0.0D);
			if (UtilCheat.blocksNear(a)) {
				TotalBlocks = 0.0D;
			}
			if (p.hasPotionEffect(PotionEffectType.JUMP)) {
				for (PotionEffect effect : p.getActivePotionEffects()) {
					if (effect.getType().equals(PotionEffectType.JUMP)) {
						int level = effect.getAmplifier() + 1;
						Limit += Math.pow(level + 4.1D, 2.0D) / 16.0D;
						break;
					}
				}
			}

			if (TotalBlocks >= Limit) {
				Count += 2;
			} else {
				if (Count > 0) {
					Count--;
				}
			}
		}
		if ((flyTicks.containsKey(p.getUniqueId())) && (UtilTime.elapsed(Time, 30000L))) {
			Count = 0;
			Time = UtilTime.nowlong();
		}
		if (Count >= 4) {
			Count = 0;
			dumplog(p, "Logged for Ascension Type B");
			this.getDaedalus().logCheat(this, p, null, Chance.HIGH, new String[0]);
		}
		flyTicks.put(p.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
	}

}
