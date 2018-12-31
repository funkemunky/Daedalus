package me.funke.daedalus.check.other;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.packets.events.PacketPlayerEvent;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.UtilPlayer;
import me.funke.daedalus.utils.UtilTime;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;

public class MorePackets extends Check {
    public static Map<UUID, Map.Entry<Integer, Long>> packetTicks;
    public static Map<UUID, Long> lastPacket;
    public List<UUID> blacklist;

    public MorePackets(me.funke.daedalus.Daedalus Daedalus) {
        super("MorePackets", "MorePackets", Daedalus);

        setEnabled(true);
        setBannable(false);
        setMaxViolations(10);

        blacklist = new ArrayList<>();
        lastPacket = new HashMap<>();
        packetTicks = new HashMap<>();
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
    }

    @EventHandler
    public void PlayerChangedWorld(PlayerChangedWorldEvent event) {
        blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void PlayerRespawn(PlayerRespawnEvent event) {
        blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PacketPlayer(PacketPlayerEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("daedalus.bypass")) return;
        if (!getDaedalus().isEnabled()) return;
        if (getDaedalus().isSotwMode()) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        if (getDaedalus().lag.getTPS() > 21.0D || getDaedalus().lag.getTPS() < getDaedalus().getTPSCancel()) return;

        if (getDaedalus().lag.getPing(player) > 200) return;
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (packetTicks.containsKey(player.getUniqueId())) {
            Count = packetTicks.get(player.getUniqueId()).getKey();
            Time = packetTicks.get(player.getUniqueId()).getValue();
        }
        if (lastPacket.containsKey(player.getUniqueId())) {
            long MS = System.currentTimeMillis() - lastPacket.get(player.getUniqueId());
            if (MS >= 100L) {
                blacklist.add(player.getUniqueId());
            } else if ((MS > 1L) && (this.blacklist.contains(player.getUniqueId()))) {
                blacklist.remove(player.getUniqueId());
            }
        }
        if (!blacklist.contains(player.getUniqueId())) {
            Count++;
            if ((packetTicks.containsKey(player.getUniqueId())) && (UtilTime.elapsed(Time, 1000L))) {
                int maxPackets = 50;
                if (Count > maxPackets) {
                    if (!UtilPlayer.isFullyStuck(player) && !UtilPlayer.isPartiallyStuck(player)) {
                        getDaedalus().logCheat(this, player, "Packets: " + Count, Chance.LIKELY);
                    }
                }
                if (Count > 400) {
                    getDaedalus().logCheat(this, player, null, Chance.HIGH, "Kicked");
                    player.kickPlayer("Too many packets.");
                }
                Count = 0;
                Time = UtilTime.nowlong();
            }
        }
        packetTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(Count, Time));
        lastPacket.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
