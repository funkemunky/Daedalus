package cc.funkemunky.daedalus.impl.checks.combat;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.daedalus.api.checks.CancelType;
import cc.funkemunky.daedalus.api.checks.Check;
import cc.funkemunky.daedalus.api.data.PlayerData;
import cc.funkemunky.daedalus.api.utils.Packets;
import org.bukkit.event.Event;

@Packets(packets = {
        Packet.Client.USE_ENTITY,
        Packet.Client.FLYING,
        Packet.Client.POSITION,
        Packet.Client.POSITION_LOOK,
        Packet.Client.LOOK,
        Packet.Client.LEGACY_POSITION,
        Packet.Client.LEGACY_POSITION_LOOK,
        Packet.Client.LEGACY_LOOK})
public class Killaura extends Check {
    public Killaura(String name, CancelType cancelType, PlayerData data) {
        super(name, cancelType, data);
    }

    long lastFlying;
    int verbose;

    @Override
    public void onPacket(Object packet, String packetType) {
        if(packetType.equals(Packet.Client.USE_ENTITY)) {
            long elapsed = MathUtils.elapsed(lastFlying);
            if(elapsed < 10) {
                if(verbose++ > 9) {
                    flag(elapsed + "<-10", true);
                }
            } else {
                verbose = 0;
            }
        } else {
            if(MathUtils.elapsed(lastFlying) < 5) {
                return;
            }
            lastFlying = System.currentTimeMillis();
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
