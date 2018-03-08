package funkemunky.Daedalus.check.combat;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.wrappers.EnumWrappers;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.check.other.Latency;
import funkemunky.Daedalus.packets.events.PacketUseEntityEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilPlayer;
import funkemunky.Daedalus.utils.UtilTime;

public class ReachC extends Check {

	private Map<Player, Map.Entry<Double, Double>> offsets;
	private Map<Player, Long> reachTicks;
	private ArrayList<Player> projectileHit;

	public ReachC(Daedalus Daedalus) {
		super("ReachC", "Reach (Type C)", Daedalus);
		this.offsets = new HashMap<Player, Map.Entry<Double, Double>>();
		this.reachTicks = new HashMap<Player, Long>();
		this.projectileHit = new ArrayList<Player>();

		this.setEnabled(true);
		this.setBannable(true);
		this.setMaxViolations(5);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ()) {
			return;
		}
		if (getDaedalus().isSotwMode()) {
			return;
		}
		double OffsetXZ = UtilMath.offset(UtilMath.getHorizontalVector(event.getFrom().toVector()),
				UtilMath.getHorizontalVector(event.getTo().toVector()));
		double horizontal = Math.sqrt(Math.pow(event.getTo().getX() - event.getFrom().getX(), 2.0)
				+ Math.pow(event.getTo().getZ() - event.getFrom().getZ(), 2.0));
		this.offsets.put(event.getPlayer(),
				new AbstractMap.SimpleEntry<Double, Double>(Double.valueOf(OffsetXZ), Double.valueOf(horizontal)));
	}

	@EventHandler
	public void onDmg(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)
				|| e.getCause() != DamageCause.PROJECTILE
				|| getDaedalus().isSotwMode()) {
			return;
		}

		Player player = (Player) e.getDamager();

		this.projectileHit.add(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLogout(PlayerQuitEvent e) {
		if (offsets.containsKey(e.getPlayer())) {
			offsets.remove(e.getPlayer());
		}
		if (reachTicks.containsKey(e.getPlayer())) {
			reachTicks.remove(e.getPlayer());
		}
		if (projectileHit.contains(e.getPlayer())) {
			projectileHit.remove(e.getPlayer());
		}
	}

	@EventHandler
	public void onDamage(PacketUseEntityEvent e) {
		if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK
				|| !(e.getAttacked() instanceof Player)
				|| getDaedalus().isSotwMode()
				|| e.getAttacker().getAllowFlight()
				|| getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()) {
			return;
		}

		Player damager = e.getAttacker();
		Player player = (Player) e.getAttacked();
		double ydist = Math.abs(damager.getEyeLocation().getY() - player.getEyeLocation().getY());
		double Reach = UtilMath.trim(2,
				(UtilPlayer.getEyeLocation(damager).distance(player.getEyeLocation()) - ydist) - 0.32);
		int PingD = this.getDaedalus().getLag().getPing(damager);
		int PingP = this.getDaedalus().getLag().getPing(player);

		long attackTime = System.currentTimeMillis();
		if (this.reachTicks.containsKey(damager)) {
			attackTime = reachTicks.get(damager);
		}
		double yawdif = Math.abs(180 - Math.abs(damager.getLocation().getYaw() - player.getLocation().getYaw()));
		if (Latency.getLag(damager) > 92 || Latency.getLag(player) > 92) {
			return;
		}
		double offsetsp = 0.0D;
		double lastHorizontal = 0.0D;
		double offsetsd = 0.0D;
		if (this.offsets.containsKey(damager)) {
			offsetsd = ((this.offsets.get(damager)).getKey()).doubleValue();
			lastHorizontal = ((this.offsets.get(damager)).getValue()).doubleValue();
		}
		if (this.offsets.containsKey(player)) {
			offsetsp = ((this.offsets.get(player)).getKey()).doubleValue();
			lastHorizontal = ((this.offsets.get(player)).getValue()).doubleValue();
		}
		Reach -= UtilMath.trim(2, offsetsd);
		Reach -= UtilMath.trim(2, offsetsp);
		double maxReach2 = 3.1;
		if (yawdif > 90) {
			maxReach2 += 0.38;
		}
		maxReach2 += lastHorizontal * 0.87;

		maxReach2 += ((PingD + PingP) / 2) * 0.0024;
		if (Reach > maxReach2 && UtilTime.elapsed(attackTime, 1100) && !projectileHit.contains(player)) {
			Chance chance = Chance.LIKELY;
			if ((Reach - maxReach2) > 0.4) {
				chance = Chance.HIGH;
			}
			this.dumplog(damager,
					"Logged for Reach Type C (First Hit Reach) " + Reach + " > " + maxReach2 + " blocks. Ping: "
							+ getDaedalus().getLag().getPing(damager) + " TPS: " + getDaedalus().getLag().getTPS()
							+ " Elapsed: " + UtilTime.elapsed(attackTime));
			getDaedalus().logCheat(this, damager, "(First Hit Reach) Range: " + Reach + " > " + maxReach2 + " Ping: "
					+ getDaedalus().getLag().getPing(damager), chance, new String[0]);
		}
		reachTicks.put(damager, UtilTime.nowlong());
		projectileHit.remove(player);
	}

}
