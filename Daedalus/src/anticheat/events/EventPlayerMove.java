package anticheat.events;

import anticheat.Daedalus;
import anticheat.user.User;
import anticheat.utils.PlayerUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventPlayerMove implements Listener {

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Daedalus.getAC().getchecksmanager().event(event);
		Player p = event.getPlayer();
		User user = Daedalus.getUserManager().getUser(p.getUniqueId());

		if (PlayerUtils.isReallyOnground(p)) {
			user.setGroundTicks(user.getGroundTicks() + 1);
			user.setAirTicks(0);
		} else {
			user.setGroundTicks(0);
			user.setAirTicks(user.getAirTicks() + 1);
		}
	}
}