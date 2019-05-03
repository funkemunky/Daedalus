package cc.funkemunky.anticheat.impl.listeners;

import cc.funkemunky.anticheat.Daedalus;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.Init;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Init
public class PlayerConnectionListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Atlas.getInstance().getThreadPool().execute(() -> {
            Daedalus.getInstance().getDataManager().addData(event.getPlayer().getUniqueId());
            Daedalus.getInstance().getCheckManager().getBannedPlayers().remove(event.getPlayer().getUniqueId());
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Daedalus.getInstance().getDataManager().removeData(event.getPlayer().getUniqueId());
    }
}
