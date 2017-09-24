package funkemunky.Daedalus.check.movement;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilPlayer;
import funkemunky.Daedalus.utils.UtilTime;

public class SpeedB
        extends Check
{
    public SpeedB(Daedalus Daedalus)
    {
        super("SpeedB", "Speed (Type B)", Daedalus);

        setEnabled(false);
        setBannable(true);
        this.setMaxViolations(4);
    }
    
    public boolean isOnIce(final Player player) {
        final Location a = player.getLocation();
        a.setY(a.getY() - 1.0);
        if (a.getBlock().getType().equals((Object)Material.ICE)) {
            return true;
        }
        a.setY(a.getY() - 1.0);
        return a.getBlock().getType().equals((Object)Material.ICE);
    }
    
    public static Map<UUID, Map.Entry<Integer, Long>> speedTicks = new HashMap();
    public static Map<UUID, Long> lastHit = new HashMap();
    
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
    	if(e.getEntity() instanceof Player) {
    		Player player = (Player) e.getEntity();
    		
    		lastHit.put(player.getUniqueId(), System.currentTimeMillis());
    	}
    }
    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
    	if(speedTicks.containsKey(e.getPlayer().getUniqueId())) {
    		speedTicks.remove(e.getPlayer().getUniqueId());
    	}
    	if(lastHit.containsKey(e.getPlayer().getUniqueId())) {
    		lastHit.remove(e.getPlayer().getUniqueId());
    	}
    }

    @SuppressWarnings("rawtypes")
	@EventHandler
    public void CheckSpeed(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if ((event.getFrom().getX() == event.getTo().getX()) && (event.getFrom().getY() == event.getTo().getY()) &&
                (event.getFrom().getZ() == event.getFrom().getZ())) {
            return;
        }
	    if(player.hasPermission("daedalus.bypass")) {
	        return;
	    }
        if (!getDaedalus().isEnabled()) {
            return;
        }
        if (player.getAllowFlight()) {
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        long lastHitDiff = this.lastHit.containsKey(player.getUniqueId()) ? this.lastHit.get(player.getUniqueId()) - System.currentTimeMillis() : 2001L;
    	if(getDaedalus().isSotwMode()) {
    		return;
    	}
        
        if (getDaedalus().LastVelocity.containsKey(player.getUniqueId()) && !player.getActivePotionEffects().contains(PotionEffectType.POISON) && !player.getActivePotionEffects().contains(PotionEffectType.WITHER) && player.getFireTicks() == 0) {
            return;
        }
        
        int Count = 0;
        long Time = UtilTime.nowlong();
        if (this.speedTicks.containsKey(player.getUniqueId()))
        {
            Count = ((Integer)((Map.Entry)this.speedTicks.get(player.getUniqueId())).getKey()).intValue();
            Time = ((Long)((Map.Entry)this.speedTicks.get(player.getUniqueId())).getValue()).longValue();
        }
            double OffsetXZ = UtilMath.offset(UtilMath.getHorizontalVector(event.getFrom().toVector()), UtilMath.getHorizontalVector(event.getTo().toVector()));
            double LimitXZ = 0.0D;
            if (player.getVehicle() == null) {
                LimitXZ = 0.62D;
            } else {
            	LimitXZ = 2D;
            }
            if (lastHitDiff < 800L) {
                LimitXZ += 2;
            }
            else if (lastHitDiff < 1600L) {
                LimitXZ += 1.0;
            }
            else if (lastHitDiff < 2000L) {
                LimitXZ += 0.8;
            }
            LimitXZ += player.getVelocity().length() * 1.35;
            if (UtilCheat.slabsNear(player.getLocation())) {
                LimitXZ += 0.08D;
            }
            Location b = UtilPlayer.getEyeLocation(player);b.add(0.0D, 1.0D, 0.0D);
            
            if (isOnIce(player)) {
                if ((b.getBlock().getType() != Material.AIR) && (!UtilCheat.canStandWithin(b.getBlock()))) {
                    LimitXZ = 1.0D;
                } else {
                    LimitXZ = 0.7D;
                }
            }
            float speed = player.getWalkSpeed();LimitXZ += (speed > 0.2F ? speed * 10.0F * 0.33F : 0.0F);
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED)) {
                    if (player.isOnGround()) {
                        LimitXZ += 0.059D * (effect.getAmplifier() + 1);
                    } else {
                        LimitXZ += 0.04D * (effect.getAmplifier() + 1);
                    }
                }
            }
            if ((OffsetXZ > LimitXZ))
            {
            	Count++;
                dumplog(player, "Speed XZ: " + OffsetXZ);
                dumplog(player, "New Count: " + Count);
            }
        if ((this.speedTicks.containsKey(player.getUniqueId())) &&
                (UtilTime.elapsed(Time, 30000L)))
        {
            dumplog(player, "Count Reset after 30 seconds.");
            Count = 0;
            Time = UtilTime.nowlong();
        }
        if(Count >= 2) {
        	getDaedalus().logCheat(this, player, OffsetXZ +  " > " + LimitXZ, Chance.HIGH, new String[0]);
        	Count = 0;
        }
        this.speedTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
    }
}