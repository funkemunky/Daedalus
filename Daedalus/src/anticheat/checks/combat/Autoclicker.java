package anticheat.checks.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import anticheat.Daedalus;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.events.TickEvent;
import anticheat.user.User;
import anticheat.utils.Latency;
import anticheat.utils.TimerUtils;

@ChecksListener(events = TickEvent.class)
public class Autoclicker extends Checks {

	public Autoclicker() {
		super("Autoclicker", ChecksType.COMBAT, Daedalus.getAC(), 5, true, true);
	}

	@Override
	protected void onEvent(Event event) {
		if (!this.getState()) {
			return;
		}

		if (event instanceof TickEvent) {
			TickEvent e = (TickEvent) event;
			if(Daedalus.getAC().getPing().getTPS() < 18) {
				return;
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (Latency.getLag(player) > 91) {
					return;
				}
				User user = Daedalus.getAC().getUserManager().getUser(player.getUniqueId());
				if (user.getLeftClicks() > 30) {
					user.setVL(this, user.getVL(this) + 1);
					this.Alert(player, user.getLeftClicks() + " CPS");
				}
				user.setLeftClicks(0);
			}
		}
	}

}
