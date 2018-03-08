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
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;

public class Glide extends Check {
	public static Map<UUID, Long> flyTicks;

	public Glide(Daedalus Daedalus) {
		super("FlyB", "Fly (Type B)", Daedalus);
		
		flyTicks = new HashMap<UUID, Long>();

		this.setEnabled(false);
		this.setBannable(true);
		setMaxViolations(5);
	}

	@EventHandler
	public void onLog(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();

		if (flyTicks.containsKey(uuid)) {
			flyTicks.remove(uuid);
		}
	}

	@EventHandler
	public void CheckGlide(PlayerMoveEvent event) {
		if (!this.getDaedalus().isEnabled()) {
			return;
		}
		Player player = event.getPlayer();
		
		/** False positive/optimization check **/
		if (event.isCancelled()
				|| !(event.getTo().getX() == event.getFrom().getX() && event.getTo().getZ() == event.getFrom().getZ())
				|| getDaedalus().isSotwMode()
				|| player.getVehicle() != null
				|| player.hasPermission("daedalus.bypass")
				|| player.getAllowFlight()
				|| getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()
				|| UtilCheat.isInWeb(player)) {
			return;
		}

		if (UtilCheat.blocksNear(player)) {
			if (flyTicks.containsKey(player.getUniqueId())) {
				flyTicks.remove(player.getUniqueId());
			}
			return;
		}

		double OffsetY = event.getFrom().getY() - event.getTo().getY();
		if (OffsetY <= 0.0 || OffsetY > 0.16) {
			if (flyTicks.containsKey(player.getUniqueId())) {
				flyTicks.remove(player.getUniqueId());
			}
			return;
		}
		long Time = System.currentTimeMillis();
		if (flyTicks.containsKey(player.getUniqueId())) {
			Time = flyTicks.get(player.getUniqueId());
		}
		long MS = System.currentTimeMillis() - Time;
		if (MS > 1000L) {
			this.dumplog(player, "Logged. MS: " + MS);
			flyTicks.remove(player.getUniqueId());
			if (getDaedalus().getLag().getPing(player) < 275) {
				this.getDaedalus().logCheat(this, player, null, Chance.LIKELY, new String[0]);
			}
			return;
		}
		flyTicks.put(player.getUniqueId(), Time);
	}
}