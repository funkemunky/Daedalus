package me.funke.daedalus.check.combat;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.Chance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class KillAuraE extends Check {
    public static Map<Player, Map.Entry<Integer, Long>> lastAttack;

    public KillAuraE(me.funke.daedalus.Daedalus Daedalus) {
        super("KillAuraE", "KillAura (MultiAura)", Daedalus);

        lastAttack = new HashMap<>();

        setEnabled(true);
        setBannable(false);

        setViolationsToNotify(2);
        setMaxViolations(7);
        setViolationResetTime(1800000L);
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        lastAttack.remove(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void Damage(EntityDamageByEntityEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || !((e.getEntity()) instanceof Player)
                || !(e.getDamager() instanceof Player)
                || getDaedalus().isSotwMode()) return;

        Player player = (Player) e.getDamager();
        if (lastAttack.containsKey(player)) {
            Integer entityid = lastAttack.get(player).getKey();
            Long time = lastAttack.get(player).getValue();
            if (entityid != e.getEntity().getEntityId() && System.currentTimeMillis() - time < 6L) {
                this.getDaedalus().logCheat(this, player, null, Chance.LIKELY);
            }
            lastAttack.remove(player);
        } else {
            lastAttack.put(player, new AbstractMap.SimpleEntry<>(e.getEntity().getEntityId(),
                    System.currentTimeMillis()));
        }
    }
}