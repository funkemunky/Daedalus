package anticheat.checks.movement;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import anticheat.Daedalus;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.utils.Latency;
import anticheat.utils.MathUtils;
import anticheat.utils.MiscUtils;
import anticheat.utils.PlayerUtils;

@ChecksListener(events = { PlayerMoveEvent.class })
public class Fly extends Checks {
	
	public Map<UUID, Map.Entry<Long, Double>> AscensionTicks;
	public Map<UUID, Double> velocity;
	public Map<UUID, Long> flyTicksA;

	public Fly() {
		super("Fly", ChecksType.MOVEMENT, Daedalus.getAC(), 9, true, true);
		this.AscensionTicks = new HashMap<UUID, Map.Entry<Long, Double>>();
		this.velocity = new HashMap<UUID, Double>();
		this.flyTicksA = new HashMap<UUID, Long>();
	}
	


	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}

		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player player = e.getPlayer();
			if (e.getFrom().getY() >= e.getTo().getY()) {
				return;
			}
			if (player.getAllowFlight()) {
				return;
			}
			if (player.getVehicle() != null) {
				return;
			}

			if (player.getVelocity().length() < velocity.getOrDefault(player.getUniqueId(), -1.0D)) {
				return;
			}

			long Time = System.currentTimeMillis();
			double TotalBlocks = 0.0D;
			if (this.AscensionTicks.containsKey(player.getUniqueId())) {
				Time = ((Long) ((Map.Entry) this.AscensionTicks.get(player.getUniqueId())).getKey()).longValue();
				TotalBlocks = Double.valueOf(
						((Double) ((Map.Entry) this.AscensionTicks.get(player.getUniqueId())).getValue()).doubleValue())
						.doubleValue();
			}
			long MS = System.currentTimeMillis() - Time;
			double OffsetY = MathUtils.offset(MathUtils.getVerticalVector(e.getFrom().toVector()),
					MathUtils.getVerticalVector(e.getTo().toVector()));
			if (OffsetY > 0.0D) {
				TotalBlocks += OffsetY;
			}
			if (MiscUtils.blocksNear(player)) {
				TotalBlocks = 0.0D;
			}
			Location a = player.getLocation().subtract(0.0D, 1.0D, 0.0D);
			if (MiscUtils.blocksNear(a)) {
				TotalBlocks = 0.0D;
			}
			double Limit = 0.5D;
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
				if (MS > 150L) {
					if (velocity.containsKey(player.getUniqueId())) {
						this.Alert(player, "Type A");
					}
					Time = System.currentTimeMillis();
				}
			} else {
				Time = System.currentTimeMillis();
			}
			this.AscensionTicks.put(player.getUniqueId(),
					new AbstractMap.SimpleEntry(Long.valueOf(Time), Double.valueOf(TotalBlocks)));
			if (!player.isOnGround()) {
				this.velocity.put(player.getUniqueId(), player.getVelocity().length());
			} else {
				this.velocity.put(player.getUniqueId(), -1.0D);
			}
		}

		if (event instanceof PlayerMoveEvent) {

			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player player = e.getPlayer();
			if (e.isCancelled()) {
				return;
			}
			if (player.getAllowFlight()) {
				return;
			}
			if (player.getVehicle() != null) {
				return;
			}
			if (player.hasPermission("daedalus.bypass")) {
				return;
			}
			if (Latency.getLag(player) > 92) {
				return;
			}
			if (Daedalus.getAC().getPing().getTPS() < 17) {
				return;
			}
			if (PlayerUtils.isInWater(player)) {
				return;
			}
			if (MiscUtils.isInWeb(player)) {
				return;
			}
			if (MiscUtils.blocksNear(player.getLocation())) {
				if (this.flyTicksA.containsKey(player.getUniqueId())) {
					this.flyTicksA.remove(player.getUniqueId());
				}
				return;
			}
			if ((e.getTo().getX() == e.getFrom().getX()) && (e.getTo().getZ() == e.getFrom().getZ())) {
				return;
			}
			if (Math.abs(e.getTo().getY() - e.getFrom().getY()) > 0.1) {
				if (this.flyTicksA.containsKey(player.getUniqueId())) {
					this.flyTicksA.remove(player.getUniqueId());
				}
				return;
			}
			long Time = System.currentTimeMillis();
			if (this.flyTicksA.containsKey(player.getUniqueId())) {
				Time = ((Long) this.flyTicksA.get(player.getUniqueId())).longValue();
			}
			long MS = System.currentTimeMillis() - Time;
			if (MS > 500L) {
				this.Alert(player, "(Type B)");
				this.flyTicksA.remove(player.getUniqueId());
				return;
			}
			this.flyTicksA.put(player.getUniqueId(), Time);

		}
	}
}