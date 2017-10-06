package anticheat.events;

import anticheat.Daedalus;
import anticheat.user.User;
import anticheat.utils.PlayerUtils;

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
		if (p != null) {
			if (!Daedalus.getUserManager().allUsers.contains(p)) {
				Daedalus.getUserManager().add(new User(p));
				PlayerUtils.DevAlerts("New user join by the name " + p.getName());
				PlayerUtils.DevAlerts(p.getName() + " Was added to the Users list!");
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Daedalus.getAC();
		Daedalus.getData().saveProfil(p);
		if (Daedalus.getUserManager().allUsers.contains(p)) {
			Daedalus.getUserManager().remove(new User(p));
			PlayerUtils.DevAlerts("A user has left by the name " + p.getName());
			PlayerUtils.DevAlerts(p.getName() + " Was removed from the Users list!");
		}

	}

}
