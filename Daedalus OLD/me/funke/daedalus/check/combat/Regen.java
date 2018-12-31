package me.funke.daedalus.check.combat;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.UtilPlayer;
import me.funke.daedalus.utils.UtilTime;
import org.bukkit.Difficulty;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Regen extends Check {
    public static Map<UUID, Long> LastHeal = new HashMap<>();
    public static Map<UUID, Map.Entry<Integer, Long>> FastHealTicks = new HashMap<>();

    public Regen(me.funke.daedalus.Daedalus Daedalus) {
        super("Regen", "Regen", Daedalus);

        this.setEnabled(true);
        this.setBannable(true);
        setViolationsToNotify(3);
        setMaxViolations(12);
        setViolationResetTime(60000L);
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        LastHeal.remove(uuid);
        FastHealTicks.remove(uuid);
    }

    public boolean checkFastHeal(Player player) {
        if (LastHeal.containsKey(player.getUniqueId())) {
            long l = LastHeal.get(player.getUniqueId());
            LastHeal.remove(player.getUniqueId());
            return System.currentTimeMillis() - l < 3000L;
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onHeal(EntityRegainHealthEvent event) {
        if (!event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)
                || !(event.getEntity() instanceof Player)
                || getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()) return;

        Player player = (Player) event.getEntity();

        if (player.hasPermission("daedalus.bypass")
                || player.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) return;
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (FastHealTicks.containsKey(player.getUniqueId())) {
            Count = FastHealTicks.get(player.getUniqueId()).getKey();
            Time = FastHealTicks.get(player.getUniqueId()).getValue();
        }
        if (checkFastHeal(player) && !UtilPlayer.isFullyStuck(player) && !UtilPlayer.isPartiallyStuck(player)) {
            Count++;
        } else {
            Count = Count > 0 ? Count - 1 : Count;
        }

        if (Count > 2) {
            getDaedalus().logCheat(this, player, null, Chance.HIGH);
        }
        if (FastHealTicks.containsKey(player.getUniqueId()) && UtilTime.elapsed(Time, 60000L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        LastHeal.put(player.getUniqueId(), System.currentTimeMillis());
        FastHealTicks.put(player.getUniqueId(),
                new AbstractMap.SimpleEntry<>(Count, Time));
    }
}
