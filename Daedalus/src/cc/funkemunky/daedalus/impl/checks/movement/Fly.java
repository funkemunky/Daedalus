package cc.funkemunky.daedalus.impl.checks.movement;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.daedalus.api.checks.CancelType;
import cc.funkemunky.daedalus.api.checks.Check;
import cc.funkemunky.daedalus.api.data.PlayerData;
import cc.funkemunky.daedalus.api.utils.BukkitEvents;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

@BukkitEvents(events = {PlayerMoveEvent.class})
public class Fly extends Check {
    public Fly(String name, CancelType cancelType, PlayerData data) {
        super(name, cancelType, data);
    }

    private float lastMotion;
    @Override
    public void onPacket(Object packet, String packetType) {

    }

    @Override
    public void onBukkitEvent(Event event) {
        PlayerMoveEvent e = (PlayerMoveEvent) event;

        float motionY = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8) ? lastMotion : (float) (e.getTo().getY() - e.getFrom().getY());
        float predictedY = (float) e.getPlayer().getVelocity().getY();
        float acceleration = (float) (e.getTo().getY() - e.getFrom().getY()) - lastMotion;


        if(getData().isFullyInAir()
                && getData().getLastVelocity().hasPassed(40)
                && MathUtils.getDelta(motionY, predictedY) > 0.1
                && (acceleration < -0.07 || acceleration > -0.084)) {
            flag(motionY + ">-" + predictedY, true);
        }

        Bukkit.broadcastMessage(motionY + ", " + predictedY + ", " + acceleration);

        lastMotion = (float) (e.getTo().getY() - e.getFrom().getY());
    }
}
