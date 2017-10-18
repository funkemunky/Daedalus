package anticheat.events;

import anticheat.Daedalus;
import anticheat.user.User;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventJoinQuit implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Daedalus.getAC();
		Daedalus.getData().loadProfil(p);
		Daedalus.getUserManager().add(new User(p));
		Daedalus.getAC().getchecksmanager().event(e);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Daedalus.getAC();
		Daedalus.getData().saveProfil(p);
		Daedalus.getUserManager().remove(Daedalus.getUserManager().getUser(p.getUniqueId()));
		Daedalus.getAC().getchecksmanager().event(e);
	}

}
