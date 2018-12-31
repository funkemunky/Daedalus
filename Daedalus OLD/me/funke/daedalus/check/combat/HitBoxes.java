package me.funke.daedalus.check.combat;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.packets.events.PacketUseEntityEvent;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.UtilCheat;
import me.funke.daedalus.utils.UtilMath;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HitBoxes extends Check {

    public static Map<UUID, Integer> count = new HashMap<>();
    public static Map<UUID, Player> lastHit = new HashMap<>();
    public static Map<UUID, Double> yawDif = new HashMap<>();

    public HitBoxes(me.funke.daedalus.Daedalus Daedalus) {
        super("HitBoxes", "HitBoxes", Daedalus);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(5);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        count.remove(e.getPlayer().getUniqueId());
        yawDif.remove(e.getPlayer().getUniqueId());
        lastHit.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUse(PacketUseEntityEvent e) {
        if (!(e.getAttacked() instanceof Player)) return;
        Player player = e.getAttacker();
        Player attacked = (Player) e.getAttacked();
        if (player.hasPermission("daedalus.bypass")
                || player.getAllowFlight()) return;
        int verbose = count.getOrDefault(player.getUniqueId(), 0);
        double offset = UtilCheat.getOffsetOffCursor(player, attacked);
        if (offset > 30) {
            if ((verbose += 2) > 25) {
                getDaedalus().logCheat(this, player, UtilMath.round(offset, 4) + ">-30", Chance.HIGH);
            }
        } else if (verbose > 0) {
            verbose--;
        }
        count.put(player.getUniqueId(), verbose);
    }
}