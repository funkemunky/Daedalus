package me.funke.daedalus.check.combat;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.UtilMath;
import me.funke.daedalus.utils.UtilPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class ReachA extends Check {

    public static HashMap<UUID, Integer> toBan;

    public ReachA(me.funke.daedalus.Daedalus Daedalus) {
        super("ReachA", "Reach (Type A)", Daedalus);

        this.setEnabled(true);
        this.setBannable(false);

        this.setViolationsToNotify(1);
        this.setMaxViolations(9);

        toBan = new HashMap<>();
    }

    @EventHandler
    public void onATTACK(EntityDamageByEntityEvent e) {
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                || !(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)
                || getDaedalus().isSotwMode()
                || getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()) return;

        Player player = (Player) e.getDamager();
        Player damaged = (Player) e.getEntity();

        if (player.hasPermission("daedalus.bypass")
                || player.getAllowFlight()) return;

        double YawDifference = Math.abs(180 - Math.abs(damaged.getLocation().getYaw() - player.getLocation().getYaw()));
        double Difference = UtilPlayer.getEyeLocation(player).distance(damaged.getEyeLocation()) - 0.35;

        int Ping = getDaedalus().getLag().getPing(player);
        double TPS = getDaedalus().getLag().getTPS();
        double MaxReach = 4.0 + damaged.getVelocity().length();

        if (player.isSprinting()) {
            MaxReach += 0.2;
        }

        if (player.getLocation().getY() > damaged.getLocation().getY()) {
            Difference = player.getLocation().getY() - player.getLocation().getY();
            MaxReach += Difference / 2.5;
        } else if (player.getLocation().getY() > player.getLocation().getY()) {
            Difference = player.getLocation().getY() - player.getLocation().getY();
            MaxReach += Difference / 2.5;
        }
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                MaxReach += 0.2D * (effect.getAmplifier() + 1);
            }
        }

        double velocity = player.getVelocity().length() + damaged.getVelocity().length();

        MaxReach += velocity * 1.5;
        MaxReach += Ping < 250 ? Ping * 0.00212 : Ping * 0.031;
        MaxReach += YawDifference * 0.008;

        double ChanceVal = Math.round(Math.abs((Difference - MaxReach) * 100));

        if (ChanceVal > 100) {
            ChanceVal = 100;
        }

        if (MaxReach < Difference) {
            this.dumplog(player, "Logged for Reach Type A; Check is Bannable (so no special bans); Reach: " + Difference
                    + "; MaxReach; " + MaxReach + "; Chance: " + ChanceVal + "%" + "; Ping: " + Ping + "; TPS: " + TPS);
            Chance chance = Chance.LIKELY;
            if (ChanceVal >= 60) {
                chance = Chance.HIGH;
            }
            getDaedalus().logCheat(this, player, UtilMath.trim(4, Difference) + " > " + MaxReach + " Ping:" + Ping
                    + " Yaw Difference: " + YawDifference, chance, "Experimental");
        }
    }

}
