package cc.funkemunky.daedalus.impl.checks.movement;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.daedalus.api.checks.CancelType;
import cc.funkemunky.daedalus.api.checks.Check;
import cc.funkemunky.daedalus.api.data.PlayerData;
import cc.funkemunky.daedalus.api.utils.Packets;
import org.bukkit.event.Event;

@Packets(packets = {Packet.Client.POSITION_LOOK, Packet.Client.POSITION, Packet.Client.LEGACY_POSITION_LOOK, Packet.Client.LEGACY_POSITION})
public class Fly extends Check {
    public Fly(String name, CancelType cancelType, PlayerData data) {
        super(name, cancelType, data);
    }

    private float lastMotionPacket, lastAccelerationPacket;
    private int verbose;
    @Override
    public void onPacket(Object packet, String packetType) {
        float acceleration = (float) (getData().getTo().getY() - getData().getFrom().getY()) - lastMotionPacket;

        if(getData().airTicks > 1
                && Math.abs(acceleration) < 1E-4
                && Math.abs(lastAccelerationPacket) < 1E-4
                && !getData().isOnGround()
                && !getData().isGeneralCancel()
                && !getData().isOnClimbable()
                && !getData().isInLiquid()
                && !getData().isInWeb()) {
            flag(acceleration + "-<1E-4", true);
        }

        if(Math.abs(acceleration) > 0.1
                && !getData().isBlocksOnTop()
                && !getData().isOnHalfBlock()
                && Math.abs(lastAccelerationPacket) > 0.1
                && !getData().isGeneralCancel()) {
            if(verbose++ > 2) {
                flag(acceleration + ">-0.1", true);
            }
        } else {
            verbose = 0;
        }

        //Bukkit.broadcastMessage(motionY + ", " + predictedY + ", " + acceleration);

        lastAccelerationPacket = acceleration;
        lastMotionPacket = (float) (getData().getTo().getY() - getData().getFrom().getY());
    }

    @Override
    public void onBukkitEvent(Event event) {
    }
}
