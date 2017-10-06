package funkemunky.Daedalus.check.combat;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.check.other.Latency;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilPlayer;
import funkemunky.Daedalus.utils.UtilTime;

public class ReachB extends Check {
    
    public static Map<Player, Integer> count = new HashMap();
    public static Map<Player, Map.Entry<Double, Double>> offsets = new HashMap();
    public static Map<Player, Long> reachTicks = new HashMap();
    
    public ReachB(Daedalus Daedalus) {
        super("ReachB", "Reach (Type B)", Daedalus);
        
        this.setEnabled(true);
        this.setMaxViolations(7);
        this.setBannable(true);
        this.setViolationsToNotify(1);
        
    }
     
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
    	if(event.getFrom().getX() == event.getTo().getX() &&
    			event.getFrom().getZ() == event.getTo().getZ()) {
    		return;
    	}
    	double OffsetXZ = UtilMath.offset(UtilMath.getHorizontalVector(event.getFrom().toVector()), UtilMath.getHorizontalVector(event.getTo().toVector()));
    	double horizontal = Math.sqrt(Math.pow(event.getTo().getX() - event.getFrom().getX(), 2.0) + Math.pow(event.getTo().getZ() - event.getFrom().getZ(), 2.0));
    	this.offsets.put(event.getPlayer(), new AbstractMap.SimpleEntry(Double.valueOf(OffsetXZ), Double.valueOf(horizontal)));
    }
    
    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
    	if(offsets.containsKey(e.getPlayer())) {
    		offsets.remove(e.getPlayer());
    	}
    	if(count.containsKey(e.getPlayer())) {
    		count.remove(e.getPlayer());
    	}
    	if(reachTicks.containsKey(e.getPlayer())) {
    		reachTicks.remove(e.getPlayer());
    	}
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player damager = (Player)e.getDamager();
        Player player = (Player)e.getEntity();
        double Reach = UtilMath.trim(2, UtilPlayer.getEyeLocation(damager).distance(player.getEyeLocation()) - 0.32);
        double Reach2 = UtilMath.trim(2, UtilPlayer.getEyeLocation(damager).distance(player.getEyeLocation()) - 0.32);
        
        double Difference;
        
        if(getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()) {
        	return;
        }
        if (damager.getAllowFlight()) {
            return;
        }
        if (player.getAllowFlight()) {
            return;
        }
        
        if(!count.containsKey(damager)) {
        	count.put(damager, 0);
        }
        
        int Count = count.get(damager);
        long Time = System.currentTimeMillis();
        double MaxReach = 3.1;
        double YawDifference = Math.abs(damager.getEyeLocation().getYaw() - player.getEyeLocation().getYaw());
        double speedToVelocityDif = 0;
        double velocityDifference2 = Math.abs(damager.getVelocity().length() + player.getVelocity().length());
        double offsets = 0.0D;
        double offsetsp = 0.0D;
        double lastHorizontal = 0.0D;
        if(this.offsets.containsKey(damager)) {
        	offsets = ((Double)((Map.Entry)this.offsets.get(damager)).getKey()).doubleValue();
        	lastHorizontal = ((Double)((Map.Entry)this.offsets.get(damager)).getValue()).doubleValue();
        }
        if(this.offsets.containsKey(player)) {
        	offsetsp = ((Double)((Map.Entry)this.offsets.get(player)).getKey()).doubleValue();
        }
        if(Latency.getLag(damager) > 100 || Latency.getLag(player) > 100) {
        	return;
        }
        speedToVelocityDif = Math.abs(offsets - player.getVelocity().length());
        MaxReach += (YawDifference * 0.001);
         MaxReach += lastHorizontal * 1.5;
         MaxReach += speedToVelocityDif * 0.09;
        if (damager.getLocation().getY() > player.getLocation().getY()) {
            Difference = damager.getLocation().getY() - player.getLocation().getY();
            MaxReach += Difference / 2.5;
        } else if (player.getLocation().getY() > damager.getLocation().getY()) {
            Difference = player.getLocation().getY() - damager.getLocation().getY();
            MaxReach += Difference / 2.5;
        }
        MaxReach += damager.getWalkSpeed() <= 0.2 ? 0 : damager.getWalkSpeed() - 0.2;
        for (PotionEffect effect : damager.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                 MaxReach += 0.3D * (effect.getAmplifier() + 1);
            }
        }
        int PingD = this.getDaedalus().getLag().getPing(damager);
        int PingP = this.getDaedalus().getLag().getPing(player);
        MaxReach += ((PingD + PingP) / 2) * 0.0024;
        Reach2 -= UtilMath.trim(2, velocityDifference2);
        Reach2 -= UtilMath.trim(2, offsetsp);
        if (UtilTime.elapsed(Time, 10000)) {
            count.remove(damager);
            Time = System.currentTimeMillis();
        }
        if (Reach > MaxReach) {
        	 this.dumplog(damager, "Count Increase (+1); Reach: " + Reach2 + ", MaxReach: " + MaxReach + ", Damager Velocity: " + damager.getVelocity().length() + ", " + "Player Velocity: " + player.getVelocity().length() + "; New Count: " + Count);
            count.put(damager, Count + 1);
        } else {
        	if(Count >= -2) {
        		count.put(damager, Count - 1);
        	}
        }
        if(Reach2 > 6) {
        	e.setCancelled(true);
        }
        if(Count >= 2 && Reach > MaxReach && Reach < 20.0) {
        	count.remove(damager);
        	if(Latency.getLag(player) < 115) {
        		this.getDaedalus().logCheat(this, damager, Reach  + " > " + MaxReach + " MS: " + PingD + " Velocity Difference: " + speedToVelocityDif, Chance.HIGH, new String[0]);
        		
        	}
        	this.dumplog(damager, "Logged for Reach" + Reach2 +  " > " + MaxReach);
        	return;
        }
        long attackTime = System.currentTimeMillis();
        if (this.reachTicks.containsKey(damager)) {
            attackTime = reachTicks.get(damager);
        }
    }

    public class ReachEntry {
        public Long LastTime;
        public List<Double> Reachs;

        public ReachEntry(Long LastTime, List<Double> Reachs) {
            this.Reachs = new ArrayList<Double>();
            this.LastTime = LastTime;
            this.Reachs = Reachs;
        }

        public Long getLastTime() {
            return this.LastTime;
        }

        public List<Double> getReachs() {
            return this.Reachs;
        }

        public void setLastTime(Long LastTime) {
            this.LastTime = LastTime;
        }

        public void setReachs(List<Double> Reachs) {
            this.Reachs = Reachs;
        }

        public void addReach(Double Reach2) {
            this.Reachs.add(Reach2);
        }
    }

}