package anticheat.checks;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import anticheat.Daedalus;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.user.User;

@ChecksListener(events = { PlayerMoveEvent.class })
public class Fly extends Checks {

	public Fly() {
		super("Fly", ChecksType.MOVEMENT, Daedalus.getAC(), true);

	}

	@Override
	protected void onEvent(Event event) {
		if (this.getState() == false)
			return;

		if (event instanceof PlayerMoveEvent) {

			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player p = e.getPlayer();

			if (p.getNoDamageTicks() > 3) {
				return;
			}

			if (p.getVehicle() != null) {
				return;
			}

			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				return;
			}

			if (p.getAllowFlight()) {
				return;
			}
			
			User user = Daedalus.getUserManager().getUser(p.getUniqueId());

		}
	}
}
