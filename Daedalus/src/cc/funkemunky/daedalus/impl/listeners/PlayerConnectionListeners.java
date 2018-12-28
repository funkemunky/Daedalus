package cc.funkemunky.daedalus.impl.listeners;

import cc.funkemunky.daedalus.Daedalus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectionListeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Daedalus.getInstance().getDataManager().addData(event.getPlayer().getUniqueId());
    }
}
