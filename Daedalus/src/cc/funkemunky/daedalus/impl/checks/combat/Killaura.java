package cc.funkemunky.daedalus.impl.checks.combat;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.math.RollingAverage;
import cc.funkemunky.daedalus.api.checks.CancelType;
import cc.funkemunky.daedalus.api.checks.Check;
import cc.funkemunky.daedalus.api.data.PlayerData;
import cc.funkemunky.daedalus.api.utils.Packets;
import org.bukkit.entity.LivingEntity;
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

    private long lastFlying;
    private int verbose;
    private RollingAverage rollingAverage = new RollingAverage(18);

    @Override
    public void onPacket(Object packet, String packetType) {
        if(packetType.equals(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket use = new WrappedInUseEntityPacket(packet, getData().getPlayer());

            /*Checks the time difference between a flying packet and a use packet. If legit, it should normally be around 50ms.
            Killaura modules tend to be made using a motion event, and client developers usually forget to make sure that the motion
            and the attack packets are being sent in separate ticks */
            long elapsed = MathUtils.elapsed(lastFlying);
            if(elapsed < 10) {
                if(verbose++ > 9) {
                    flag("t: post; " + elapsed + "<-10", true);
                }
            } else {
                verbose = 0;
            }

            if(use.getEntity() instanceof LivingEntity) { //We check if it's a LivingEntity since the MathUtils#getOffsetFromEntity requires it.
                //We only get the yaw offset from the center of the player since it simplifies the check (and vertically made auras aren't common).
                double offset = MathUtils.getOffsetFromEntity(getData().getPlayer(), (LivingEntity) use.getEntity())[0];

                //We use a RollingAverage to prevent the occasional inconsistent value returned from causing false positives.
                rollingAverage.add(offset, System.currentTimeMillis());

                if(rollingAverage.getAverage() > 32) { //The maximum yaw offset would be 30, but we put 32 for latency compensation.
                    flag("t: offset; " + rollingAverage.getAverage() + ">-30", true);
                }
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
