package anticheat.checks.combat;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.wrappers.EnumWrappers;

import anticheat.Daedalus;
import anticheat.detections.Checks;
import anticheat.detections.ChecksListener;
import anticheat.detections.ChecksType;
import anticheat.packets.events.PacketUseEntityEvent;
import anticheat.utils.Ping;
import anticheat.utils.TimerUtils;

@ChecksListener(events = {PacketUseEntityEvent.class, PlayerQuitEvent.class, PlayerDeathEvent.class})
public class KillAuraA extends Checks {
	public static Map<UUID, Long> LastMS;
    public static Map<UUID, List<Long>> Clicks;
    public static Map<UUID, Map.Entry<Integer, Long>> ClickTicks;
    public static Map<UUID, Map.Entry<Integer, Long>> AimbotTicks;
    public static Map<UUID, Double> Differences;
    public static Map<UUID, Location> LastLocation;

    public KillAuraA() {
    	super("KillAura", "KillAura", ChecksType.COMBAT, 4, Daedalus.getAC(), true);
        this.LastMS = new HashMap<UUID, Long>();
        this.Clicks = new HashMap<UUID, List<Long>>();
        this.ClickTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
        this.AimbotTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
        this.Differences = new HashMap<UUID, Double>();
        this.LastLocation = new HashMap<UUID, Location>();
    }
    
    @Override
    protected void onEvent(Event event) {
    	if(event instanceof PacketUseEntityEvent) {
    		PacketUseEntityEvent e = (PacketUseEntityEvent) event;
    		 if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
    	            return;
    	        }
    	        if (!((e.getAttacked()) instanceof Player)) {
    	            return;
    	        }
    	        final Player damager = e.getAttacker();
    		     if(damager.hasPermission("daedalus.bypass")) {
    		         return;
    		     }
    	        int Count = 0;
    	        long Time = System.currentTimeMillis();
    	        if (this.ClickTicks.containsKey(damager.getUniqueId())) {
    	            Count = this.ClickTicks.get(damager.getUniqueId()).getKey();
    	            Time = this.ClickTicks.get(damager.getUniqueId()).getValue();
    	        }
    	        if (this.LastMS.containsKey(damager.getUniqueId())) {
    	            final long MS = TimerUtils.nowlong() - this.LastMS.get(damager.getUniqueId());
    	            if (MS > 500L || MS < 5L) {
    	                this.LastMS.put(damager.getUniqueId(), TimerUtils.nowlong());
    	                return;
    	            }
    	            if (this.Clicks.containsKey(damager.getUniqueId())) {
    	                final List<Long> Clicks = this.Clicks.get(damager.getUniqueId());
    	                if (Clicks.size() == 10) {
    	                    this.Clicks.remove(damager.getUniqueId());
    	                    Collections.sort(Clicks);
    	                    final long Range = Clicks.get(Clicks.size() - 1) - Clicks.get(0);
    	                    if (Range < 30L) {
    	                        ++Count;
    	                        Time = System.currentTimeMillis();
    	                    }
    	                }
    	                else {
    	                    Clicks.add(MS);
    	                    this.Clicks.put(damager.getUniqueId(), Clicks);
    	                }
    	            }
    	            else {
    	                final List<Long> Clicks = new ArrayList<Long>();
    	                Clicks.add(MS);
    	                this.Clicks.put(damager.getUniqueId(), Clicks);
    	            }
    	        }
    	        if (this.ClickTicks.containsKey(damager.getUniqueId()) && TimerUtils.elapsed(Time, 5000L)) {
    	            Count = 0;
    	            Time = TimerUtils.nowlong();
    	        }
    	        if ((Count > 0 && Ping.getPing(damager) < 100) || (Count > 2 && Ping.getPing(damager) < 200) 
    	        		|| (Count > 4 && Ping.getPing(damager) > 200) ) {
    	            Count = 0;
    	            this.Alert(damager, "(ClickPattern)");
    	            ClickTicks.remove(damager.getUniqueId());
    	        }
    	        this.LastMS.put(damager.getUniqueId(), TimerUtils.nowlong());
    	        this.ClickTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
    	}
    	if(event instanceof PacketUseEntityEvent) {
    		PacketUseEntityEvent e = (PacketUseEntityEvent) event;
    		if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
                return;
            }
            Player damager = e.getAttacker();
    	     if(damager.hasPermission("daedalus.bypass")) {
    	         return;
    	     }
            if (damager.getAllowFlight()) {
                return;
            }
            if (!((e.getAttacked()) instanceof Player)) {
                return;
            }
            Location from = null;
            Location to = damager.getLocation();
            if (this.LastLocation.containsKey(damager.getUniqueId())) {
                from = this.LastLocation.get(damager.getUniqueId());
            }
            this.LastLocation.put(damager.getUniqueId(), damager.getLocation());
            double Count = 0;
            long Time = System.currentTimeMillis();
            double LastDifference = -111111.0;
            if (this.Differences.containsKey(damager.getUniqueId())) {
                LastDifference = this.Differences.get(damager.getUniqueId());
            }
            if (this.AimbotTicks.containsKey(damager.getUniqueId())) {
                Count = this.AimbotTicks.get(damager.getUniqueId()).getKey();
                Time = this.AimbotTicks.get(damager.getUniqueId()).getValue();
            }
            if (from == null || (to.getX() == from.getX() && to.getZ() == from.getZ())) {
                return;
            }
            double Difference = Math.abs(to.getYaw() - from.getYaw());
            if (Difference == 0.0) {
                return;
            }
            
            if (Difference > 2.4) {
                double diff = Math.abs(LastDifference - Difference);
                if(e.getAttacked().getVelocity().length() < 0.1) {
                	if(diff < 1.4) {
                        Count+= 1;
                    } else {
                    	Count= 0;
                    }
                } else {
                	if(diff < 1.8) {
                        Count+= 1;
                    } else {
                    	Count= 0;
                    }
                }
            }
            this.Differences.put(damager.getUniqueId(), Difference);
            if (this.AimbotTicks.containsKey(damager.getUniqueId()) && TimerUtils.elapsed(Time, 5000L)) {
                Count = 0;
                Time = TimerUtils.nowlong();
            }
            if (Count >= 4) {
                Count = 0;
                this.Alert(damager, "(Aimbot)");
            }
            this.AimbotTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>((int) Math.round(Count), Time));
    	}
    	if(event instanceof PlayerQuitEvent) {
    		PlayerQuitEvent e = (PlayerQuitEvent) event;
        	Player p = e.getPlayer();
        	UUID uuid = p.getUniqueId();
        	
        	if(LastMS.containsKey(uuid)) {
        		LastMS.remove(uuid);
        	}
        	if(Clicks.containsKey(uuid)) {
        		Clicks.remove(uuid);
        	}
        	if(ClickTicks.containsKey(uuid)) {
        		ClickTicks.remove(uuid);
        	}
        	if(AimbotTicks.containsKey(uuid)) {
        		AimbotTicks.remove(e.getPlayer().getUniqueId());
        	}
        	if(Differences.containsKey(uuid)) {
        		Differences.remove(e.getPlayer().getUniqueId());
        	}
        	if(LastLocation.containsKey(uuid)) {
        		LastLocation.remove(e.getPlayer().getUniqueId());
        	}
    	}
    	if(event instanceof PlayerDeathEvent) {
    		PlayerDeathEvent e = (PlayerDeathEvent) event;
        	Player p = e.getEntity();
        	UUID uuid = p.getUniqueId();
        	if(LastMS.containsKey(uuid)) {
        		LastMS.remove(uuid);
        	}
        	if(Clicks.containsKey(uuid)) {
        		Clicks.remove(uuid);
        	}
        	if(ClickTicks.containsKey(uuid)) {
        		ClickTicks.remove(uuid);
        	}
        	if(AimbotTicks.containsKey(uuid)) {
        		AimbotTicks.remove(uuid);
        	}
        	if(Differences.containsKey(uuid)) {
        		Differences.remove(uuid);
        	}
        	if(LastLocation.containsKey(uuid)) {
        		LastLocation.remove(uuid);
        	}
    	}
    }
}