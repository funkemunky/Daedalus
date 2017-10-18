package anticheat.checks.movement;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import anticheat.Daedalus;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.utils.AdvancedLicense;
import anticheat.utils.AdvancedLicense.ValidationType;
import anticheat.utils.PlayerUtils;
import anticheat.utils.TimerUtils;

@ChecksListener(events = { PlayerMoveEvent.class })
public class Vclip extends Checks {
	public Map<Player, Location> flag = new HashMap<Player, Location>();
	public Location location;
	public TimerUtils t = new TimerUtils();

	public Vclip() {
		super("Vclip", ChecksType.MOVEMENT, Daedalus.getAC(), 16, true, true);
	}

	@Override
	protected void onEvent(Event event) {
		if (this.getState() == false)
			return;

		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			final Player p = e.getPlayer();
			if (p.getGameMode().equals(GameMode.CREATIVE) || p.getVehicle() != null) {
				return;
			}
			if (PlayerUtils.isReallyOnground(p) && t.hasReached(location == null ? 500L : 2500L)) {
				flag.put(p, p.getLocation());
				location = p.getLocation();
				t.reset();
			}
			final double diff = Math.abs(e.getTo().getY() - e.getFrom().getY());
			if (diff >= 2.0 && !PlayerUtils.isAir(p)) {
				Alert(p, "Type A");
				kick(p);
				flag(p, flag.get(p));
			}
		}
	}


	public void flag(Player p, Location l) {
		if (l != null) {
			p.teleport(l);
		}
	}
}
