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

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilTime;

public class AscensionA extends Check {
	
	public static Map<UUID, Map.Entry<Long, Double>> AscensionTicks;
	public static Map<UUID, Double> velocity;
	
	public AscensionA(Daedalus Daedalus) {
		super("AscensionA", "Ascension (Type A)", Daedalus);

		this.setBannable(true);
		this.setEnabled(true);
		setMaxViolations(4);
		
		AscensionTicks = new HashMap<UUID, Map.Entry<Long, Double>>();
		velocity = new HashMap<UUID, Double>();
	}

	@EventHandler
	public void CheckAscension(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (event.getFrom().getY() >= event.getTo().getY()
				|| !getDaedalus().isEnabled()
				|| player.getAllowFlight()
				|| player.getVehicle() != null
				|| !UtilTime.elapsed(getDaedalus().LastVelocity.getOrDefault(player.getUniqueId(), 0L), 4200L)) {
			return;
		}
		
		long Time = System.currentTimeMillis();
		double TotalBlocks = 0.0D;
		if (AscensionTicks.containsKey(player.getUniqueId())) {
			Time = AscensionTicks.get(player.getUniqueId()).getKey().longValue();
			TotalBlocks = AscensionTicks.get(player.getUniqueId()).getValue().doubleValue();
		}
		long MS = System.currentTimeMillis() - Time;
		double OffsetY = UtilMath.offset(UtilMath.getVerticalVector(event.getFrom().toVector()),
				UtilMath.getVerticalVector(event.getTo().toVector()));
		if (OffsetY > 0.0D) {
			TotalBlocks += OffsetY;
		}
		Location a = player.getLocation().subtract(0.0D, 1.0D, 0.0D);
		if (UtilCheat.blocksNear(a)) {
			TotalBlocks = 0.0D;
		}
		double Limit = 1.05D;
		if (player.hasPotionEffect(PotionEffectType.JUMP)) {
			for (PotionEffect effect : player.getActivePotionEffects()) {
				if (effect.getType().equals(PotionEffectType.JUMP)) {
					int level = effect.getAmplifier() + 1;
					Limit += (Math.pow(level + 4.2D, 2.0D) / 16.0D) + 0.3;
					break;
				}
			}
		}
		if (TotalBlocks > Limit) {
			if (MS > 250L) {
				if (velocity.containsKey(player.getUniqueId())) {
					getDaedalus().logCheat(this, player, "Flew up " + UtilMath.trim(1, TotalBlocks) + " blocks",
							Chance.HIGH, new String[0]);
				}
				Time = System.currentTimeMillis();
			}
		} else {
			Time = System.currentTimeMillis();
		}
		AscensionTicks.put(player.getUniqueId(),
				new AbstractMap.SimpleEntry<Long, Double>(Time, TotalBlocks));
	}

	@EventHandler
	public void onLog(PlayerQuitEvent e) {
		if (AscensionTicks.containsKey(e.getPlayer().getUniqueId())) {
			AscensionTicks.remove(e.getPlayer().getUniqueId());
		}
		if (velocity.containsKey(e.getPlayer().getUniqueId())) {
			velocity.remove(e.getPlayer().getUniqueId());
		}
	}
}