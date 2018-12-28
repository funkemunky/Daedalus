package cc.funkemunky.daedalus.impl.listeners;

import cc.funkemunky.daedalus.Daedalus;
import cc.funkemunky.daedalus.api.data.PlayerData;
import cc.funkemunky.daedalus.api.utils.BukkitEvents;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;

public class BukkitListeners implements Listener {

    @EventHandler
    public void onEvent(PlayerMoveEvent event) {
        PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if(data != null && data.getLastMovementCancel().hasPassed(1)) {
            callChecks(data, event);
        }
    }

    @EventHandler
    public void onEvent(BlockBreakEvent event) {
        PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if(data != null) {
            callChecks(data, event);
        }
    }

    @EventHandler
    public void onEvent(BlockPlaceEvent event) {
        PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if(data != null) {
            callChecks(data, event);
        }
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if(data != null) {
            callChecks(data, event);
        }
    }

    private void callChecks(PlayerData data, Event event) {
        data.getChecks().stream()
                .filter(check -> check.getClass().isAnnotationPresent(BukkitEvents.class) && Arrays.asList(check.getClass().getAnnotation(BukkitEvents.class).events()).contains(event.getClass()))
                .forEach(check -> check.onBukkitEvent(event));
    }
}
