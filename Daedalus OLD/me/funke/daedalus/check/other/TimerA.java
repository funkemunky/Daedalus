package me.funke.daedalus.check.other;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.packets.events.PacketPlayerEvent;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class TimerA extends Check {
    private Map<UUID, Map.Entry<Integer, Long>> packets;
    private Map<UUID, Integer> verbose;
    private Map<UUID, Long> lastPacket;
    private List<Player> toCancel;

    public TimerA(me.funke.daedalus.Daedalus Daedalus) {
        super("TimerA", "Timer (Type A)", Daedalus);

        packets = new HashMap<>();
        verbose = new HashMap<>();
        toCancel = new ArrayList<>();
        lastPacket = new HashMap<>();

        setEnabled(true);
        setBannable(false);
        setMaxViolations(5);
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        packets.remove(e.getPlayer().getUniqueId());
        verbose.remove(e.getPlayer().getUniqueId());
        lastPacket.remove(e.getPlayer().getUniqueId());
        toCancel.remove(e.getPlayer());
    }

    @EventHandler
    public void PacketPlayer(PacketPlayerEvent event) {
        Player player = event.getPlayer();
        if (!this.getDaedalus().isEnabled()) return;

        if (player.hasPermission("daedalus.bypass")) return;

        if (getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()) return;

        long lastPacket = this.lastPacket.getOrDefault(player.getUniqueId(), 0L);
        int packets = 0;
        long Time = System.currentTimeMillis();
        int verbose = this.verbose.getOrDefault(player.getUniqueId(), 0);

        if (this.packets.containsKey(player.getUniqueId())) {
            packets = this.packets.get(player.getUniqueId()).getKey();
            Time = this.packets.get(player.getUniqueId()).getValue();
        }

        if (System.currentTimeMillis() - lastPacket < 5) {
            this.lastPacket.put(player.getUniqueId(), System.currentTimeMillis());
            return;
        }
        double threshold = 21;
        if (UtilTime.elapsed(Time, 1000L)) {
            if (toCancel.remove(player) && packets <= 13) {
                return;
            }
            if (packets > threshold + getDaedalus().packet.movePackets.getOrDefault(player.getUniqueId(), 0) && getDaedalus().packet.movePackets.getOrDefault(player.getUniqueId(), 0) < 5) {
                verbose = (packets - threshold) > 10 ? verbose + 2 : verbose + 1;
            } else {
                verbose = 0;
            }

            if (verbose > 2) {
                getDaedalus().logCheat(this, player, "Packets: " + packets, Chance.HIGH);
            }
            packets = 0;
            Time = UtilTime.nowlong();
            getDaedalus().packet.movePackets.remove(player.getUniqueId());
        }
        packets++;

        this.lastPacket.put(player.getUniqueId(), System.currentTimeMillis());
        this.packets.put(player.getUniqueId(), new SimpleEntry<>(packets, Time));
        this.verbose.put(player.getUniqueId(), verbose);
    }
}