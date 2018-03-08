package funkemunky.Daedalus.check.movement;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilPlayer;
import funkemunky.Daedalus.utils.UtilTime;

public class SpeedA extends Check {
	
	public static Map<UUID, Map.Entry<Integer, Long>> speedTicks;
	public static Map<UUID, Map.Entry<Integer, Long>> tooFastTicks;
	public static Map<UUID, Long> lastHit;
	public static Map<UUID, Double> velocity;
	
	public SpeedA(Daedalus Daedalus) {
		super("SpeedA", "Speed (Type A)", Daedalus);

		setEnabled(true);
		setBannable(true);
		this.setMaxViolations(3);
		
		speedTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
		tooFastTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
		lastHit = new HashMap<UUID, Long>();
		velocity = new HashMap<UUID, Double>();
	}

	@EventHandler(ignoreCancelled = true)
	public void onHit(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();

			lastHit.put(player.getUniqueId(), System.currentTimeMillis());
		}
	}

	public boolean isOnIce(final Player player) {
		Location a = player.getLocation();
		a.setY(a.getY() - 1.0);
		if (a.getBlock().getType().equals((Object) Material.ICE)) {
			return true;
		}
		a.setY(a.getY() - 1.0);
		return a.getBlock().getType().equals((Object) Material.ICE);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLog(PlayerQuitEvent e) {
		if (speedTicks.containsKey(e.getPlayer().getUniqueId())) {
			speedTicks.remove(e.getPlayer().getUniqueId());
		}
		if (tooFastTicks.containsKey(e.getPlayer().getUniqueId())) {
			tooFastTicks.remove(e.getPlayer().getUniqueId());
		}
		if (lastHit.containsKey(e.getPlayer().getUniqueId())) {
			lastHit.remove(e.getPlayer().getUniqueId());
		}
		if (velocity.containsKey(e.getPlayer().getUniqueId())) {
			velocity.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void CheckSpeed(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if ((event.getFrom().getX() == event.getTo().getX()) && (event.getFrom().getY() == event.getTo().getY())
				&& (event.getFrom().getZ() == event.getFrom().getZ())
				|| !getDaedalus().isEnabled()
				|| player.getAllowFlight()
				|| player.getVehicle() != null
				|| player.getVelocity().length() + 0.1 < velocity.getOrDefault(player.getUniqueId(), -1.0D)
				|| (getDaedalus().LastVelocity.containsKey(player.getUniqueId())
				&& !player.hasPotionEffect(PotionEffectType.POISON)
				&& !player.hasPotionEffect(PotionEffectType.WITHER) && player.getFireTicks() == 0)) {
			return;
		}

		long lastHitDiff = lastHit.containsKey(player.getUniqueId())
				? lastHit.get(player.getUniqueId()) - System.currentTimeMillis()
				: 2001L;

		int Count = 0;
		long Time = UtilTime.nowlong();
		if (speedTicks.containsKey(player.getUniqueId())) {
			Count = speedTicks.get(player.getUniqueId()).getKey().intValue();
			Time = speedTicks.get(player.getUniqueId()).getValue().longValue();
		}
		int TooFastCount = 0;
		double percent = 0D;
		if (tooFastTicks.containsKey(player.getUniqueId())) {
			double OffsetXZ = UtilMath.offset(UtilMath.getHorizontalVector(event.getFrom().toVector()),
					UtilMath.getHorizontalVector(event.getTo().toVector()));
			double LimitXZ = 0.0D;
			if ((UtilPlayer.isOnGround(player)) && (player.getVehicle() == null)) {
				LimitXZ = 0.34D;
			} else {
				LimitXZ = 0.39D;
			}
			if (lastHitDiff < 800L) {
				++LimitXZ;
			} else if (lastHitDiff < 1600L) {
				LimitXZ += 0.4;
			} else if (lastHitDiff < 2000L) {
				LimitXZ += 0.1;
			}
			if (UtilCheat.slabsNear(player.getLocation())) {
				LimitXZ += 0.05D;
			}
			Location b = UtilPlayer.getEyeLocation(player);
			b.add(0.0D, 1.0D, 0.0D);
			if ((b.getBlock().getType() != Material.AIR) && (!UtilCheat.canStandWithin(b.getBlock()))) {
				LimitXZ = 0.69D;
			}
			Location below = event.getPlayer().getLocation().clone().add(0.0D, -1.0D, 0.0D);

			if (UtilCheat.isStair(below.getBlock())) {
				LimitXZ += 0.6;
			}

			if (isOnIce(player)) {
				if ((b.getBlock().getType() != Material.AIR) && (!UtilCheat.canStandWithin(b.getBlock()))) {
					LimitXZ = 1.0D;
				} else {
					LimitXZ = 0.75D;
				}
			}
			float speed = player.getWalkSpeed();
			LimitXZ += (speed > 0.2F ? speed * 10.0F * 0.33F : 0.0F);
			for (PotionEffect effect : player.getActivePotionEffects()) {
				if (effect.getType().equals(PotionEffectType.SPEED)) {
					if (player.isOnGround()) {
						LimitXZ += 0.061D * (effect.getAmplifier() + 1);
					} else {
						LimitXZ += 0.031D * (effect.getAmplifier() + 1);
					}
				}
			}
			if (OffsetXZ > LimitXZ && !UtilTime.elapsed(tooFastTicks.get(player.getUniqueId()).getValue().longValue(), 150L)) {
				percent = (OffsetXZ - LimitXZ) * 100;
				TooFastCount = tooFastTicks.get(player.getUniqueId()).getKey().intValue()
						+ 3;
			} else {
				TooFastCount = TooFastCount > -150 ? TooFastCount-- : -150;
			}
		}
		if (TooFastCount >= 11) {
			TooFastCount = 0;
			Count++;
			dumplog(player, "New Count: " + Count);
		}
		if (speedTicks.containsKey(player.getUniqueId()) && UtilTime.elapsed(Time, 30000L)) {
			Count = 0;
			Time = UtilTime.nowlong();
		}
		Chance prob = Chance.LIKELY;
		if (Count >= 3) {
			prob = Chance.HIGH;
			dumplog(player, "Logged for Speed. Count: " + Count);
			Count = 0;
			getDaedalus().logCheat(this, player, Math.round(percent) + "% faster than normal", prob, new String[0]);
		}
		if (!player.isOnGround()) {
			velocity.put(player.getUniqueId(), player.getVelocity().length());
		} else {
			velocity.put(player.getUniqueId(), -1.0D);
		}
		tooFastTicks.put(player.getUniqueId(),
				new AbstractMap.SimpleEntry<Integer, Long>(TooFastCount, System.currentTimeMillis()));
		speedTicks.put(player.getUniqueId(),
				new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
	}
}