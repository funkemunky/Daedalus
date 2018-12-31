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

    private float lastMotionY, lastAccelerationPacket;
    private int verbose;
    @Override
    public void onPacket(Object packet, String packetType) {
        float motionY = (float) (getData().getTo().getY() - getData().getFrom().getY()), acceleration = motionY - lastMotionY;

        /* This checks for the acceleration of the player being too low. The average acceleration for a legitimate player is around 0.08.
           We check if it's less than 1E-4 for some compensation of inconsistencies that happen very often due to netty.
         */
        if(getData().airTicks > 1
                && Math.abs(acceleration) < 1E-4
                && Math.abs(lastAccelerationPacket) < 1E-4
                && !getData().isOnGround()
                && !getData().isGeneralCancel()
                && !getData().isOnClimbable()
                && !getData().isInLiquid()
                && !getData().isInWeb()) {
            flag("t: low; " + motionY + "≈" + lastMotionY, true);
        }

        /* This is to check for large amounts of instant acceleration to counter any fly which tries bypass in this manner  */
        if(Math.abs(acceleration) > 0.1
                && !getData().isBlocksOnTop()
                && !getData().isOnHalfBlock()
                && Math.abs(lastAccelerationPacket) > 0.1
                && !getData().isGeneralCancel()) {
            //We have to add a verbose since this check isn't 100% accurate and therefore can have issues.
            //However, we can instantly flag if they are already in the air since a large delta between velocities is impossible.
            if(verbose++ > 2 || (acceleration > 0.5 && getData().getAirTicks() > 1)) {
                flag("t: high; " + motionY + ">-" + lastMotionY, true);
            }
        } else {
            verbose = 0;
        }

        lastAccelerationPacket = acceleration;
        lastMotionY = motionY;
    }

    @Override
    public void onBukkitEvent(Event event) {
    }
}
