package cc.funkemunky.daedalus.impl.listeners;

import cc.funkemunky.daedalus.Daedalus;
import cc.funkemunky.daedalus.api.checks.CancelType;
import cc.funkemunky.daedalus.api.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class CancelEvents implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(PlayerMoveEvent event) {
        PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if(data.getCancelType() == CancelType.MOTION) {
            if(data.getSetbackLocation() != null) {
                event.getPlayer().teleport(data.getSetbackLocation());
            }
            data.getLastMovementCancel().reset();
            data.setCancelType(CancelType.NONE);
        } else if(data.isOnGround() && data.getLastMovementCancel().hasPassed(20)) {
            data.setSetbackLocation(event.getTo());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getDamager().getUniqueId());

            if(data.getCancelType() == CancelType.COMBAT) {
                event.setCancelled(true);
                data.setCancelType(CancelType.NONE);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(EntityRegainHealthEvent event) {
        if(event.getEntity() instanceof Player) {
            PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getEntity().getUniqueId());

            if(data.getCancelType() == CancelType.HEALTH) {
                event.setCancelled(true);
                data.setCancelType(CancelType.NONE);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(PlayerInteractEvent event) {
        PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if(data.getCancelType().equals(CancelType.INTERACT)) {
            event.setCancelled(true);
            data.setCancelType(CancelType.NONE);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(BlockBreakEvent event) {
        PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if(data.getCancelType().equals(CancelType.BREAK)) {
            event.setCancelled(true);
            data.setCancelType(CancelType.NONE);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(BlockPlaceEvent event) {
        PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if(data.getCancelType().equals(CancelType.PLACE)) {
            event.setCancelled(true);
            data.setCancelType(CancelType.NONE);
        }
    }


}
