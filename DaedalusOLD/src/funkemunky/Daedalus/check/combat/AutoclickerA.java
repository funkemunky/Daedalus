package funkemunky.Daedalus.check.combat;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.wrappers.EnumWrappers;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketUseEntityEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilTime;

public class AutoclickerA
        extends Check
{
    public static Map<UUID, Map.Entry<Integer, Long>> attackTicks = new HashMap();

    public AutoclickerA(Daedalus Daedalus)
    {
        super("AutoclickerA", "Autoclicker (Type A)", Daedalus);
        
        setEnabled(true);
        setBannable(false);
        setViolationsToNotify(1);
        setMaxViolations(5);
    }
    
    @EventHandler
    public void onLog(PlayerQuitEvent e) {
    	Player p = e.getPlayer();
    	UUID uuid = p.getUniqueId();
    	
    	if(attackTicks.containsKey(uuid)) {
    		attackTicks.remove(uuid);
    	}
    }

    @EventHandler
    public void UseEntity(PacketUseEntityEvent e)
    {
        if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
            return;
        }
    	if(getDaedalus().isSotwMode()) {
    		return;
    	}
        if (!(e.getAttacked() instanceof Player)) {
            return;
        }
        Player player = e.getAttacker();
	     if(player.hasPermission("daedalus.bypass")) {
	         return;
	     }

        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.attackTicks.containsKey(player.getUniqueId()))
        {
            Count = ((Integer)((Map.Entry)this.attackTicks.get(player.getUniqueId())).getKey()).intValue();
            Time = ((Long)((Map.Entry)this.attackTicks.get(player.getUniqueId())).getValue()).longValue();
        }
        Count++;
        if ((this.attackTicks.containsKey(player.getUniqueId())) &&
                (UtilTime.elapsed(Time, 1000L)))
        {
            if (Count >= 20) {
                this.getDaedalus().logCheat(this, player, "FastClick (Not Bannable)", Chance.LIKELY, new String[] { Count + " cps" });
            }
            Count = 0;
            Time = UtilTime.nowlong();
        }
        this.attackTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
    }
}
