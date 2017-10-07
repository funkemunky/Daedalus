package anticheat.events;

import anticheat.Daedalus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventPlayerAttack implements Listener {

	public static ConcurrentHashMap<Player, String> hasAttacked = new ConcurrentHashMap<>();

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Daedalus.getAC();
			if (!EventPlayerAttack.hasAttacked.contains(e.getDamager())) {
				Daedalus.getAC();
				EventPlayerAttack.hasAttacked.put((Player) e.getDamager(), "Has Attacked Entity");
				Bukkit.getScheduler().runTaskLater(Daedalus.getAC(), new Runnable() {
					@Override
					public void run() {
						Daedalus.getAC();
						hasAttacked.remove(e.getDamager());
					}
				}, 100);
			}
			Daedalus.getAC().getChecks().event(e);
		}
	}

}
