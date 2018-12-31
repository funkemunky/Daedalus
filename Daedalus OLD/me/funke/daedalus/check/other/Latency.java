package me.funke.daedalus.check.other;

import me.funke.daedalus.Daedalus;
import me.funke.daedalus.packets.events.PacketPlayerEvent;
import me.funke.daedalus.packets.events.PacketPlayerType;
import me.funke.daedalus.utils.UtilTime;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;

public class Latency implements Listener {

    public static Map<UUID, Map.Entry<Integer, Long>> packetTicks;
    public static Map<UUID, Long> lastPacket;
    private static Map<UUID, Integer> packets;
    public List<UUID> blacklist;
    private me.funke.daedalus.Daedalus Daedalus;

    public Latency(Daedalus Daedalus) {
        this.Daedalus = Daedalus;

        packetTicks = new HashMap<>();
        lastPacket = new HashMap<>();
        blacklist = new ArrayList<>();
        packets = new HashMap<>();
    }

    public static Integer getLag(Player player) {
        if (packets.containsKey(player.getUniqueId())) {
            return packets.get(player.getUniqueId());
        }
        return 0;
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        packetTicks.remove(e.getPlayer().getUniqueId());
        lastPacket.remove(e.getPlayer().getUniqueId());
        blacklist.remove(e.getPlayer().getUniqueId());
        packets.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void PlayerChangedWorld(PlayerChangedWorldEvent event) {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void PlayerRespawn(PlayerRespawnEvent event) {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PacketPlayer(PacketPlayerEvent event) {
        Player player = event.getPlayer();
        if (!Daedalus.isEnabled()) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        if (Daedalus.lag.getTPS() > 21.0D || Daedalus.lag.getTPS() < Daedalus.getTPSCancel()) return;
        if (event.getType() != PacketPlayerType.FLYING) return;
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (Latency.packetTicks.containsKey(player.getUniqueId())) {
            Count = Latency.packetTicks.get(player.getUniqueId()).getKey();
            Time = Latency.packetTicks.get(player.getUniqueId()).getValue();
        }
        if (Latency.lastPacket.containsKey(player.getUniqueId())) {
            long MS = System.currentTimeMillis() - Latency.lastPacket.get(player.getUniqueId());
            if (MS >= 100L) {
                this.blacklist.add(player.getUniqueId());
            } else if ((MS > 1L)) {
                this.blacklist.remove(player.getUniqueId());
            }
        }
        if (!this.blacklist.contains(player.getUniqueId())) {
            Count++;
            if ((Latency.packetTicks.containsKey(player.getUniqueId())) && (UtilTime.elapsed(Time, 1000L))) {
                packets.put(player.getUniqueId(), Count);
                Count = 0;
                Time = UtilTime.nowlong();
            }
        }
        Latency.packetTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(Count, Time));
        Latency.lastPacket.put(player.getUniqueId(), System.currentTimeMillis());
    }

}