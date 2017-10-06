package anticheat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import anticheat.Daedalus;
import anticheat.user.User;

public class EventPlayerInteractEvent implements Listener {

	@EventHandler
	public void onMove(PlayerInteractEvent event) {
		Daedalus.getAC().getchecksmanager().event(event);
		Player p = (Player) event.getPlayer();
		User user = Daedalus.getUserManager().getUser(p.getUniqueId());

		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			user.setLeftClicks(user.getLeftClicks() + 1);

		} else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			user.setRightClicks(user.getRightClicks() + 1);
		}
	}
}
