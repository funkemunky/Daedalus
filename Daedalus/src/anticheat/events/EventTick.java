package anticheat.events;

import anticheat.Daedalus;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class EventTick implements Listener {


    @EventHandler
    public void onEvent(TickEvent event) {
        Daedalus.getAC().getChecks().event(event);

    }
}