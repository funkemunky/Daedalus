package funkemunky.Daedalus.check.movement;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilPlayer;
import funkemunky.Daedalus.utils.UtilTime;

public class NoFall
        extends Check
{
    public static Map<UUID, Map.Entry<Long, Integer>> NoFallTicks = new HashMap();
    public static Map<UUID, Double> FallDistance = new HashMap();
    public static ArrayList<Player> cancel = new ArrayList();

    public NoFall(Daedalus Daedalus)
    {
        super("NoFall", "NoFall", Daedalus);

        setBannable(true);
        setMaxViolations(10);
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
    	cancel.add(e.getEntity());
    }
    
    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
    	if(FallDistance.containsKey(e.getPlayer().getUniqueId())) {
    		FallDistance.remove(e.getPlayer().getUniqueId());
    	}
    	if(FallDistance.containsKey(e.getPlayer().getUniqueId())) {
    		FallDistance.containsKey(e.getPlayer().getUniqueId());
    	}
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
    	if(e.getCause() == TeleportCause.ENDER_PEARL) {
    		cancel.add(e.getPlayer());
    	}
    }
    

    @EventHandler
    public void Move(PlayerMoveEvent e)
    {
        Player player = e.getPlayer();
	     if(player.hasPermission("daedalus.bypass")) {
	         return;
	     }
        Damageable dplayer = (Damageable) e.getPlayer();
        if(this.cancel.contains(player)) {
        	cancel.remove(player);
        	return;
        }
        if (player.getAllowFlight()) {
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        if (dplayer.getHealth() <= 0.0D) {
            return;
        }
        if (UtilPlayer.isOnClimbable(player, 0)) {
            return;
        }
        if (UtilPlayer.isInWater(player)) {
            return;
        }
        double Falling = 0.0D;
        if ((!UtilPlayer.isOnGround(player)) && (e.getFrom().getY() > e.getTo().getY()))
        {
            if (this.FallDistance.containsKey(player.getUniqueId())) {
                Falling = ((Double)this.FallDistance.get(player.getUniqueId())).doubleValue();
            }
            Falling += e.getFrom().getY() - e.getTo().getY();
        }
        this.FallDistance.put(player.getUniqueId(), Double.valueOf(Falling));
        if (Falling < 3.0D) {
            return;
        }
        long Time = System.currentTimeMillis();
        int Count = 0;
        if (this.NoFallTicks.containsKey(player.getUniqueId()))
        {
            Time = ((Long)((Map.Entry)this.NoFallTicks.get(player.getUniqueId())).getKey()).longValue();
            Count = Integer.valueOf(((Integer)((Map.Entry)this.NoFallTicks.get(player.getUniqueId())).getValue()).intValue()).intValue();
        }
        if ((player.isOnGround()) || (player.getFallDistance() == 0.0F))
        {
            dumplog(player, "NoFall. Real Fall Distance: " + Falling);
            player.damage(5);
            Count+= 2;
        }
        else
        {
            Count--;
        }
        if ((this.NoFallTicks.containsKey(player.getUniqueId())) &&
                (UtilTime.elapsed(Time, 10000L)))
        {
            dumplog(player, "Count Reset");
            Count = 0;
            Time = System.currentTimeMillis();
        }
        if (Count >= 4)
        {
            dumplog(player, "Logged. Count: " + Count);
            Count = 0;

            this.FallDistance.put(player.getUniqueId(), Double.valueOf(0.0D));
            getDaedalus().logCheat(this, player, "Packet NoFall", Chance.HIGH, new String[0]);
        }
        this.NoFallTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Long.valueOf(Time), Integer.valueOf(Count)));
        return;
    }

}