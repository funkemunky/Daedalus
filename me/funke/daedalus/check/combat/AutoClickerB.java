package me.funke.daedalus.check.combat;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.funke.daedalus.check.Check;
import me.funke.daedalus.packets.events.PacketUseEntityEvent;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class AutoClickerB extends Check {
    public static Map<UUID, Long> LastMS;
    public static Map<UUID, List<Long>> Clicks;
    public static Map<UUID, Map.Entry<Integer, Long>> ClickTicks;

    public AutoClickerB(me.funke.daedalus.Daedalus Daedalus) {
        super("AutoClickerB", "AutoClicker (Type B)", Daedalus);
        LastMS = new HashMap<>();
        Clicks = new HashMap<>();
        ClickTicks = new HashMap<>();
        setEnabled(true);
        setBannable(false);
        setMaxViolations(5);
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        LastMS.remove(uuid);
        Clicks.remove(uuid);
        if (ClickTicks.containsKey(uuid)) {
            Clicks.remove(uuid);
        }
    }

    @EventHandler
    public void UseEntity(PacketUseEntityEvent e) {
        if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK || !((e.getAttacked()) instanceof Player)) return;
        Player damager = e.getAttacker();
        if (damager.hasPermission("daedalus.bypass")) return;
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (ClickTicks.containsKey(damager.getUniqueId())) {
            Count = ClickTicks.get(damager.getUniqueId()).getKey();
            Time = ClickTicks.get(damager.getUniqueId()).getValue();
        }
        if (LastMS.containsKey(damager.getUniqueId())) {
            long MS = UtilTime.nowlong() - LastMS.get(damager.getUniqueId());
            if (MS > 500L || MS < 5L) {
                LastMS.put(damager.getUniqueId(), UtilTime.nowlong());
                return;
            }
            if (Clicks.containsKey(damager.getUniqueId())) {
                List<Long> Clicks = AutoClickerB.Clicks.get(damager.getUniqueId());
                if (Clicks.size() == 3) {
                    AutoClickerB.Clicks.remove(damager.getUniqueId());
                    Collections.sort(Clicks);
                    long Range = Clicks.get(Clicks.size() - 1) - Clicks.get(0);
                    if (Range >= 0 && Range <= 2) {
                        ++Count;
                        Time = System.currentTimeMillis();
                        this.dumplog(damager, "New Count: " + Count + "; Range: " + Range + "; Ping: " + getDaedalus().getLag().getPing(damager) + "; TPS: " + getDaedalus().getLag().getTPS());
                    }
                } else {
                    Clicks.add(MS);
                    AutoClickerB.Clicks.put(damager.getUniqueId(), Clicks);
                }
            } else {
                List<Long> Clicks = new ArrayList<>();
                Clicks.add(MS);
                AutoClickerB.Clicks.put(damager.getUniqueId(), Clicks);
            }
        }
        if (ClickTicks.containsKey(damager.getUniqueId()) && UtilTime.elapsed(Time, 5000L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        if ((Count > 4 && this.getDaedalus().getLag().getPing(damager) < 100) || (Count > 6 && this.getDaedalus().getLag().getPing(damager) < 200)) {
            this.dumplog(damager, "Logged. Count: " + Count);
            Count = 0;
            this.getDaedalus().logCheat(this, damager, "Continuous/Repeating Patterns", Chance.HIGH);
            ClickTicks.remove(damager.getUniqueId());
        } else if (this.getDaedalus().getLag().getPing(damager) > 250) {
            this.dumplog(damager, "Would set off Autoclicker (Constant) but latency is too high!");
        }
        LastMS.put(damager.getUniqueId(), UtilTime.nowlong());
        ClickTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<>(Count, Time));
    }
}