package me.funke.daedalus.check.movement;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.UtilCheat;
import me.funke.daedalus.utils.UtilMath;
import me.funke.daedalus.utils.UtilTime;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AscensionA extends Check {
    public static Map<UUID, Map.Entry<Long, Double>> AscensionTicks;
    public static Map<UUID, Double> velocity;

    public AscensionA(me.funke.daedalus.Daedalus Daedalus) {
        super("AscensionA", "Ascension (Type A)", Daedalus);
        this.setBannable(true);
        this.setEnabled(true);
        setMaxViolations(4);
        AscensionTicks = new HashMap<>();
        velocity = new HashMap<>();
    }

    @EventHandler
    public void CheckAscension(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getY() >= event.getTo().getY()
                || !getDaedalus().isEnabled()
                || player.getAllowFlight()
                || player.getVehicle() != null
                || !UtilTime.elapsed(getDaedalus().LastVelocity.getOrDefault(player.getUniqueId(), 0L), 4200L)) return;
        long Time = System.currentTimeMillis();
        double TotalBlocks = 0.0D;
        if (AscensionTicks.containsKey(player.getUniqueId())) {
            Time = AscensionTicks.get(player.getUniqueId()).getKey();
            TotalBlocks = AscensionTicks.get(player.getUniqueId()).getValue();
        }
        long MS = System.currentTimeMillis() - Time;
        double OffsetY = UtilMath.offset(UtilMath.getVerticalVector(event.getFrom().toVector()), UtilMath.getVerticalVector(event.getTo().toVector()));
        if (OffsetY > 0.0D) {
            TotalBlocks += OffsetY;
        }
        Location a = player.getLocation().subtract(0.0D, 1.0D, 0.0D);
        if (UtilCheat.blocksNear(a)) {
            TotalBlocks = 0.0D;
        }
        double Limit = 1.05D;
        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.JUMP)) {
                    int level = effect.getAmplifier() + 1;
                    Limit += (Math.pow(level + 4.2D, 2.0D) / 16.0D) + 0.3;
                    break;
                }
            }
        }
        if (TotalBlocks > Limit) {
            if (MS > 250L) {
                if (velocity.containsKey(player.getUniqueId())) {
                    getDaedalus().logCheat(this, player, "Flew up " + UtilMath.trim(1, TotalBlocks) + " blocks", Chance.HIGH);
                }
                Time = System.currentTimeMillis();
            }
        } else {
            Time = System.currentTimeMillis();
        }
        AscensionTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(Time, TotalBlocks));
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        AscensionTicks.remove(e.getPlayer().getUniqueId());
        velocity.remove(e.getPlayer().getUniqueId());
    }
}