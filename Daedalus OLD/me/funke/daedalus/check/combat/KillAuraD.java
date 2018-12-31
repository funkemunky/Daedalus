package me.funke.daedalus.check.combat;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.packets.events.PacketKillauraEvent;
import me.funke.daedalus.packets.events.PacketPlayerType;
import me.funke.daedalus.utils.Chance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraD extends Check {

    public static Map<UUID, Map.Entry<Double, Double>> packetTicks;

    public KillAuraD(me.funke.daedalus.Daedalus Daedalus) {
        super("KillAuraD", "KillAura (Packet)", Daedalus);

        setEnabled(true);
        setBannable(false);

        setMaxViolations(5);
        setViolationResetTime(60000);

        packetTicks = new HashMap<>();
    }

    @EventHandler
    public void packet(PacketKillauraEvent e) {
        if (!getDaedalus().isEnabled()
                || e.getPlayer().hasPermission("daedalus.bypass")) return;

        double Count = 0;
        double Other = 0;
        if (packetTicks.containsKey(e.getPlayer().getUniqueId())) {
            Count = packetTicks.get(e.getPlayer().getUniqueId()).getKey();
            Other = packetTicks.get(e.getPlayer().getUniqueId()).getValue();
        }

        if (e.getType() == PacketPlayerType.ARM_SWING) {
            Other++;
        }

        if (e.getType() == PacketPlayerType.USE) {
            Count++;
        }

        if (Count > Other && Other == 2) {
            getDaedalus().logCheat(this, e.getPlayer(), null, Chance.HIGH);
        }

        if (Count > 3 || Other > 3) {
            Count = 0;
            Other = 0;
        }
        packetTicks.put(e.getPlayer().getUniqueId(), new AbstractMap.SimpleEntry<>(Count, Other));
    }

    @EventHandler
    public void logout(PlayerQuitEvent e) {
        packetTicks.remove(e.getPlayer().getUniqueId());
    }

}
