package me.funke.daedalus.check.combat;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.check.other.Latency;
import me.funke.daedalus.packets.events.PacketSwingArmEvent;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AutoClickerA extends Check {
    public Map<UUID, Integer> clicks;
    private Map<UUID, Long> recording;

    public AutoClickerA(me.funke.daedalus.Daedalus Daedalus) {
        super("AutoClickerA", "AutoClicker (Type A)", Daedalus);
        setEnabled(true);
        setBannable(true);
        setViolationsToNotify(1);
        setMaxViolations(5);
        clicks = new HashMap<>();
        recording = new HashMap<>();
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        clicks.remove(uuid);
        recording.remove(uuid);
    }

    @EventHandler
    public void onSwing(PacketSwingArmEvent e) {
        Player player = e.getPlayer();
        if (getDaedalus().isSotwMode() || getDaedalus().getLag().getTPS() < 17 || player.hasPermission("daedalus.bypass") || Latency.getLag(player) > 100)
            return;
        int clicks = this.clicks.getOrDefault(player.getUniqueId(), 0);
        long time = recording.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
        if (UtilTime.elapsed(time, 1000L)) {
            if (clicks > 30) {
                getDaedalus().logCheat(this, player, null, Chance.HIGH, clicks + " Clicks/Second");
            }
            clicks = 0;
            recording.remove(player.getUniqueId());
        } else {
            clicks++;
        }
        this.clicks.put(player.getUniqueId(), clicks);
        recording.put(player.getUniqueId(), time);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent e) {
        clicks.remove(e.getPlayer().getUniqueId());
        recording.remove(e.getPlayer().getUniqueId());
    }
}