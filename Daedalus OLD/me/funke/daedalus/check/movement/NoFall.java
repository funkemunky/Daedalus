package me.funke.daedalus.check.movement;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.UtilPlayer;
import me.funke.daedalus.utils.UtilTime;
import org.bukkit.GameMode;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.*;

public class NoFall extends Check {
    public static Map<UUID, Map.Entry<Long, Integer>> NoFallTicks;
    public static Map<UUID, Double> FallDistance;
    public static ArrayList<Player> cancel;

    public NoFall(me.funke.daedalus.Daedalus Daedalus) {
        super("NoFall", "NoFall", Daedalus);

        this.setEnabled(true);
        this.setBannable(true);

        NoFallTicks = new HashMap<>();
        FallDistance = new HashMap<>();
        cancel = new ArrayList<>();

        this.setViolationResetTime(120000);
        setMaxViolations(9);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        cancel.add(e.getEntity());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        FallDistance.remove(e.getPlayer().getUniqueId());
        if (FallDistance.containsKey(e.getPlayer().getUniqueId())) {
            FallDistance.containsKey(e.getPlayer().getUniqueId());
        }
        cancel.remove(e.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == TeleportCause.ENDER_PEARL) {
            cancel.add(e.getPlayer());
        }
    }

    @EventHandler
    public void Move(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getAllowFlight()
                || player.getGameMode().equals(GameMode.CREATIVE)
                || player.hasPermission("daedalus.bypass")
                || player.getVehicle() != null
                || cancel.remove(player)
                || UtilPlayer.isOnClimbable(player, 0)
                || UtilPlayer.isInWater(player)) return;
        Damageable dplayer = e.getPlayer();

        if (dplayer.getHealth() <= 0.0D) return;

        double Falling = 0.0D;
        if ((!UtilPlayer.isOnGround(player)) && (e.getFrom().getY() > e.getTo().getY())) {
            if (FallDistance.containsKey(player.getUniqueId())) {
                Falling = FallDistance.get(player.getUniqueId());
            }
            Falling += e.getFrom().getY() - e.getTo().getY();
        }
        FallDistance.put(player.getUniqueId(), Falling);
        if (Falling < 3.0D) return;
        long Time = System.currentTimeMillis();
        int Count = 0;
        if (NoFallTicks.containsKey(player.getUniqueId())) {
            Time = NoFallTicks.get(player.getUniqueId()).getKey();
            Count = NoFallTicks.get(player.getUniqueId()).getValue();
        }
        if ((player.isOnGround()) || (player.getFallDistance() == 0.0F)) {
            dumplog(player, "NoFall. Real Fall Distance: " + Falling);
            player.damage(5);
            Count += 2;
        } else {
            Count--;
        }
        if (NoFallTicks.containsKey(player.getUniqueId()) && UtilTime.elapsed(Time, 10000L)) {
            Count = 0;
            Time = System.currentTimeMillis();
        }
        if (Count >= 4) {
            dumplog(player, "Logged. Count: " + Count);
            Count = 0;

            FallDistance.put(player.getUniqueId(), 0.0D);
            getDaedalus().logCheat(this, player, "Packet NoFall", Chance.HIGH);
        }
        NoFallTicks.put(player.getUniqueId(),
                new AbstractMap.SimpleEntry<>(Time, Count));
        return;
    }

}