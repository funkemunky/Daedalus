package me.funke.daedalus.check.combat;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.check.other.Latency;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.UtilMath;
import me.funke.daedalus.utils.UtilPlayer;
import me.funke.daedalus.utils.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.AbstractMap;
import java.util.Map;
import java.util.WeakHashMap;

public class ReachB extends Check {

    public Map<Player, Integer> count;
    public Map<Player, Map.Entry<Double, Double>> offsets;

    public ReachB(me.funke.daedalus.Daedalus Daedalus) {
        super("ReachB", "Reach (Type B)", Daedalus);

        setEnabled(true);
        setMaxViolations(7);
        setBannable(true);
        setViolationsToNotify(1);

        offsets = new WeakHashMap<>();
        count = new WeakHashMap<>();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ()) return;
        double OffsetXZ = UtilMath.offset(UtilMath.getHorizontalVector(event.getFrom().toVector()),
                UtilMath.getHorizontalVector(event.getTo().toVector()));
        double horizontal = Math.sqrt(Math.pow(event.getTo().getX() - event.getFrom().getX(), 2.0)
                + Math.pow(event.getTo().getZ() - event.getFrom().getZ(), 2.0));
        offsets.put(event.getPlayer(),
                new AbstractMap.SimpleEntry<>(OffsetXZ, horizontal));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)
                || !(e.getEntity() instanceof Player)
                || getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()) return;
        Player damager = (Player) e.getDamager();
        Player player = (Player) e.getEntity();
        double Reach = UtilMath.trim(2, UtilPlayer.getEyeLocation(damager).distance(player.getEyeLocation()) - 0.32);
        double Reach2 = UtilMath.trim(2, UtilPlayer.getEyeLocation(damager).distance(player.getEyeLocation()) - 0.32);

        double Difference;

        if (damager.getAllowFlight()
                || player.getAllowFlight()) return;

        if (!count.containsKey(damager)) {
            count.put(damager, 0);
        }

        int Count = count.get(damager);
        long Time = System.currentTimeMillis();
        double MaxReach = 3.1;
        double YawDifference = Math.abs(damager.getEyeLocation().getYaw() - player.getEyeLocation().getYaw());
        double speedToVelocityDif = 0;
        double offsets = 0.0D;

        double lastHorizontal = 0.0D;
        if (this.offsets.containsKey(damager)) {
            offsets = (this.offsets.get(damager)).getKey();
            lastHorizontal = (this.offsets.get(damager)).getValue();
        }
        if (Latency.getLag(damager) > 92 || Latency.getLag(player) > 92) return;
        speedToVelocityDif = Math.abs(offsets - player.getVelocity().length());
        MaxReach += (YawDifference * 0.001);
        MaxReach += lastHorizontal * 1.5;
        MaxReach += speedToVelocityDif * 0.08;
        if (damager.getLocation().getY() > player.getLocation().getY()) {
            Difference = damager.getLocation().getY() - player.getLocation().getY();
            MaxReach += Difference / 2.5;
        } else if (player.getLocation().getY() > damager.getLocation().getY()) {
            Difference = player.getLocation().getY() - damager.getLocation().getY();
            MaxReach += Difference / 2.5;
        }
        MaxReach += damager.getWalkSpeed() <= 0.2 ? 0 : damager.getWalkSpeed() - 0.2;

        int PingD = this.getDaedalus().getLag().getPing(damager);
        int PingP = this.getDaedalus().getLag().getPing(player);
        MaxReach += ((PingD + PingP) / 2) * 0.0024;
        if (PingD > 400) {
            MaxReach += 1.0D;
        }
        if (UtilTime.elapsed(Time, 10000)) {
            count.remove(damager);
            Time = System.currentTimeMillis();
        }
        if (Reach > MaxReach) {
            this.dumplog(damager,
                    "Count Increase (+1); Reach: " + Reach2 + ", MaxReach: " + MaxReach + ", Damager Velocity: "
                            + damager.getVelocity().length() + ", " + "Player Velocity: "
                            + player.getVelocity().length() + "; New Count: " + Count);
            count.put(damager, Count + 1);
        } else {
            if (Count >= -2) {
                count.put(damager, Count - 1);
            }
        }
        if (Reach2 > 6) {
            e.setCancelled(true);
        }
        if (Count >= 2 && Reach > MaxReach && Reach < 20.0) {
            count.remove(damager);
            if (Latency.getLag(player) < 115) {
                getDaedalus().logCheat(this, damager,
                        Reach + " > " + MaxReach + " MS: " + PingD + " Velocity Difference: " + speedToVelocityDif,
                        Chance.HIGH);

            }
            dumplog(damager, "Logged for Reach" + Reach2 + " > " + MaxReach);
            return;
        }
    }

}