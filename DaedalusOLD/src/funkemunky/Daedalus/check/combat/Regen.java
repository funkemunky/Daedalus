package funkemunky.Daedalus.check.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Difficulty;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilPlayer;
import funkemunky.Daedalus.utils.UtilTime;

public class Regen
        extends Check
{
    public Regen(Daedalus Daedalus)
    {
        super("Regen", "Regen", Daedalus);

        this.setEnabled(true);
        this.setBannable(true);
        setViolationsToNotify(3);
        setMaxViolations(12);
        setViolationResetTime(60000L);
    }

    public static Map<UUID, Long> LastHeal = new HashMap();
    public static Map<UUID, Map.Entry<Integer, Long>> FastHealTicks = new HashMap();
    
    @EventHandler
    public void onLog(PlayerQuitEvent e) {
    	Player p = e.getPlayer();
    	UUID uuid = p.getUniqueId();
    	
    	if(LastHeal.containsKey(uuid)) {
    		LastHeal.remove(uuid);
    	}
    	if(FastHealTicks.containsKey(uuid)) {
    		FastHealTicks.remove(uuid);
    	}
    }

    public boolean checkFastHeal(Player player)
    {
        if (this.LastHeal.containsKey(player.getUniqueId()))
        {
            long l = ((Long)this.LastHeal.get(player.getUniqueId())).longValue();
            this.LastHeal.remove(player.getUniqueId());
            if (System.currentTimeMillis() - l < 3000L) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onHeal(EntityRegainHealthEvent event)
    {
        if (!event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if(getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()) {
        	return;
        }
        Player player = (Player)event.getEntity();
        
	     if(player.hasPermission("daedalus.bypass")) {
	         return;
	     }
	     
        if (player.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
            return;
        }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.FastHealTicks.containsKey(player.getUniqueId()))
        {
            Count = ((Integer)((Map.Entry)this.FastHealTicks.get(player.getUniqueId())).getKey()).intValue();
            Time = ((Long)((Map.Entry)this.FastHealTicks.get(player.getUniqueId())).getValue()).longValue();
        }
        if (checkFastHeal(player)) {
            if(!UtilPlayer.isFullyStuck(player) && !UtilPlayer.isPartiallyStuck(player)) {
            	getDaedalus().logCheat(this, player, null, Chance.HIGH, new String[0]);
            }
        }
        if ((this.FastHealTicks.containsKey(player.getUniqueId())) &&
                (UtilTime.elapsed(Time, 60000L)))
        {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        this.LastHeal.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
        this.FastHealTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
    }
}
