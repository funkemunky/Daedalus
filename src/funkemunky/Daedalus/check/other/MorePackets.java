package funkemunky.Daedalus.check.other;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketPlayerEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilPlayer;
import funkemunky.Daedalus.utils.UtilTime;

public class MorePackets
        extends Check
{
    public MorePackets(Daedalus Daedalus)
    {
        super("MorePackets", "MorePackets", Daedalus);

        setEnabled(false);
        setBannable(false);
    }

    public static Map<UUID, Map.Entry<Integer, Long>> packetTicks = new HashMap();
    public static Map<UUID, Long> lastPacket = new HashMap();
    public List<UUID> blacklist = new ArrayList();

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event)
    {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
    	if(packetTicks.containsKey(e.getPlayer().getUniqueId())) {
    		packetTicks.remove(e.getPlayer().getUniqueId());
    	}
    	if(lastPacket.containsKey(e.getPlayer().getUniqueId())) {
    		lastPacket.remove(e.getPlayer().getUniqueId());
    	}
    	if(blacklist.contains(e.getPlayer().getUniqueId())) {
    		blacklist.remove(e.getPlayer().getUniqueId());
    	}
    }

    @EventHandler
    public void PlayerChangedWorld(PlayerChangedWorldEvent event)
    {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void PlayerRespawn(PlayerRespawnEvent event)
    {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled=true)
    public void PacketPlayer(PacketPlayerEvent event)
    {
        Player player = event.getPlayer();
	    if(player.hasPermission("daedalus.bypass")) {
	        return;
	    }
        if (!getDaedalus().isEnabled()) {
            return;
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (getDaedalus().lag.getTPS() > 21.0D || getDaedalus().lag.getTPS() < getDaedalus().getTPSCancel()) {
            return;
        }
        
        if(getDaedalus().lag.getPing(player) > 200) {
        	return;
        }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.packetTicks.containsKey(player.getUniqueId()))
        {
            Count = ((Integer)((Map.Entry)this.packetTicks.get(player.getUniqueId())).getKey()).intValue();
            Time = ((Long)((Map.Entry)this.packetTicks.get(player.getUniqueId())).getValue()).longValue();
        }
        if (this.lastPacket.containsKey(player.getUniqueId()))
        {
            long MS = System.currentTimeMillis() - ((Long)this.lastPacket.get(player.getUniqueId())).longValue();
            if (MS >= 100L) {
                this.blacklist.add(player.getUniqueId());
            } else if ((MS > 1L) &&
                    (this.blacklist.contains(player.getUniqueId()))) {
                this.blacklist.remove(player.getUniqueId());
            }
        }
        if (!this.blacklist.contains(player.getUniqueId()))
        {
            Count++;
            if ((this.packetTicks.containsKey(player.getUniqueId())) &&
                    (UtilTime.elapsed(Time, 1000L)))
            {
                int maxPackets = 50;
                if (Count > maxPackets) {
                    if(!UtilPlayer.isFullyStuck(player) && !UtilPlayer.isPartiallyStuck(player)) {
                    	getDaedalus().logCheat(this, player, "Packets: " + Count, Chance.LIKELY, new String[0]);
                    }
                }
                if(Count > 1250) {
                	getDaedalus().logCheat(this, player, "Banned", Chance.HIGH, new String[] { "Crash" });
                	getDaedalus().banPlayer(player, this);
                }
                Count = 0;
                Time = UtilTime.nowlong();
            }
        }
        this.packetTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
        this.lastPacket.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
    }
}
