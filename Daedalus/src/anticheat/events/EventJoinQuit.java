package anticheat.events;


import anticheat.Daedalus;
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
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Daedalus.getAC();
        Daedalus.getData().saveProfil(p);

    }

}
