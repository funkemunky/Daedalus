package anticheat.events;

import anticheat.Daedalus;
import anticheat.utils.PlayerUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventPlayerMove implements Listener {

	public static int AirTicks = 0;
	public static int GroundTicks = 0;

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Daedalus.getAC().getchecksmanager().event(event);
		Player p = event.getPlayer();
		if (p == null) {
			AirTicks = 0;
			GroundTicks = 0;
		}
		if (PlayerUtils.isReallyOnground(p)) {
			GroundTicks++;
			AirTicks = 0;
		} else {
			GroundTicks = 0;
			AirTicks++;
		}
	}
}