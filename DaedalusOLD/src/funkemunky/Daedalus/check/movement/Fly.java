package funkemunky.Daedalus.check.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.check.other.Latency;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilPlayer;

public class Fly extends Check {
	
	public static Map<UUID, Long> flyTicksA;

	public Fly(Daedalus Daedalus) {
		super("FlyA", "Fly (Type A)", Daedalus);

		this.setEnabled(true);
		this.setBannable(true);
		setMaxViolations(5);
		
		flyTicksA = new HashMap<UUID, Long>();
	}

	@EventHandler
	public void onLog(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();

		if (flyTicksA.containsKey(uuid)) {
			flyTicksA.remove(uuid);
		}
	}

	@EventHandler
	public void CheckFlyA(PlayerMoveEvent event) {
		if (!getDaedalus().isEnabled()) {
			return;
		}
		Player player = event.getPlayer();
		
		/** False positive/optimization check **/
		if (event.isCancelled()
				|| (event.getTo().getX() == event.getFrom().getX()) && (event.getTo().getZ() == event.getFrom().getZ())
				|| getDaedalus().isSotwMode()
				|| player.getAllowFlight()
				|| player.getVehicle() != null
				|| player.hasPermission("daedalus.bypass")
				|| getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()
				|| UtilPlayer.isInWater(player)
				|| UtilCheat.isInWeb(player)
				|| Latency.getLag(player) > 92) {
			return;
		}
		
		if (UtilCheat.blocksNear(player.getLocation())) {
			if (flyTicksA.containsKey(player.getUniqueId())) {
				flyTicksA.remove(player.getUniqueId());
			}
			return;
		} 
		if (Math.abs(event.getTo().getY() - event.getFrom().getY()) > 0.06) {
			if (flyTicksA.containsKey(player.getUniqueId())) {
				flyTicksA.remove(player.getUniqueId());
			}
			return;
		}
		
		long Time = System.currentTimeMillis();
		if (flyTicksA.containsKey(player.getUniqueId())) {
			Time = flyTicksA.get(player.getUniqueId()).longValue();
		}
		long MS = System.currentTimeMillis() - Time;
		if (MS > 200L) {
			dumplog(player, "Logged Fly. MS: " + MS);
			getDaedalus().logCheat(this, player,
					"Hovering for " + UtilMath.trim(1, Double.valueOf((MS / 1000))) + " second(s)", Chance.HIGH,
					new String[0]);
			flyTicksA.remove(player.getUniqueId());
			return;
		}
		flyTicksA.put(player.getUniqueId(), Time);
	}
}