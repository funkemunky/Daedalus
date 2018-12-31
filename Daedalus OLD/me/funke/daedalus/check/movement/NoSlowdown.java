package me.funke.daedalus.check.movement;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.UtilMath;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoSlowdown extends Check {

    public static Map<UUID, Map.Entry<Integer, Long>> speedTicks;

    public NoSlowdown(me.funke.daedalus.Daedalus Daedalus) {
        super("NoSlowdown", "NoSlowdown", Daedalus);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(5);
        speedTicks = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        speedTicks.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void BowShoot(final EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();
        if (!this.isEnabled()
                || player.isInsideVehicle()
                || !player.isSprinting()) return;
        getDaedalus().logCheat(this, player, "Sprinting while bowing.", Chance.LIKELY);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        double OffsetXZ = UtilMath.offset(UtilMath.getHorizontalVector(e.getFrom().toVector()), UtilMath.getHorizontalVector(e.getTo().toVector()));
        if (e.getTo().getX() == e.getFrom().getX()
                && e.getFrom().getY() == e.getTo().getY()
                && e.getTo().getZ() == e.getFrom().getZ()) return;
        if (player.getAllowFlight()
                || !player.getLocation().getBlock().getType().equals(Material.WEB)
                || OffsetXZ < 0.2) return;
        getDaedalus().logCheat(this, player, null, Chance.LIKELY);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem() != null) {
            Player player = event.getPlayer();
            if (event.getItem().getType().equals(Material.EXP_BOTTLE)
                    || event.getItem().getType().equals(Material.GLASS_BOTTLE)
                    || event.getItem().getType().equals(Material.POTION)
                    || player.getAllowFlight()
                    || player.hasPermission("daedalus.bypass")) {
                return;
            }
            long Time = System.currentTimeMillis();
            int level = 0;
            if (speedTicks.containsKey(player.getUniqueId())) {
                level = speedTicks.get(player.getUniqueId()).getKey();
                Time = speedTicks.get(player.getUniqueId()).getValue();
            }
            double diff = System.currentTimeMillis() - Time;
            level = diff >= 2.0 ? (diff <= 51.0 ? (level += 2) : (diff <= 100.0 ? (level += 0) : (diff <= 500.0 ? (level -= 6) : (level -= 12)))) : ++level;
            int max = 50;
            if (level > max * 0.9D && diff <= 100.0D) {
                getDaedalus().logCheat(this, player, "Level: " + level + " Ping: " + getDaedalus().lag.getPing(player), Chance.HIGH);
                if (level > max) {
                    level = max / 4;
                }
            } else if (level < 0) {
                level = 0;
            }
            speedTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(level, System.currentTimeMillis()));
        }
    }
}