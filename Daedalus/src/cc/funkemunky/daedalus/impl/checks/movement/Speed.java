package cc.funkemunky.daedalus.impl.checks.movement;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.PlayerUtils;
import cc.funkemunky.daedalus.api.checks.CancelType;
import cc.funkemunky.daedalus.api.checks.Check;
import cc.funkemunky.daedalus.api.data.PlayerData;
import cc.funkemunky.daedalus.api.utils.Packets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;

@Packets(packets = {Packet.Client.POSITION_LOOK, Packet.Client.POSITION, Packet.Client.LEGACY_POSITION_LOOK, Packet.Client.LEGACY_POSITION})
public class Speed extends Check {
    public Speed(String name, CancelType cancelType, PlayerData data) {
        super(name, cancelType, data);
    }

    private int verbose;

    @Override
    public void onPacket(Object packet, String packetType) {
        Location to = getData().getTo();
        Location from = getData().getFrom();

        float motionXZ = (float) Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

        float baseSpeed = getData().isOnGround() ? 0.3f : 0.34f;

        Player player = getData().getPlayer();

        baseSpeed += PlayerUtils.getPotionEffectLevel(player, PotionEffectType.SPEED) * (getData().isOnGround() ? 0.06f : 0.045f);
        baseSpeed *= getData().isOnHalfBlock() ? 2.5 : 1;
        baseSpeed *= getData().isBlocksOnTop() ? 3.4 : 1;
        baseSpeed *= getData().getIceTicks() > 0 && getData().getGroundTicks() < 6 ? 2.5f : 1.0;
        baseSpeed += getData().isOnSlimeBefore() ? 0.1 : 0;
        baseSpeed += (player.getWalkSpeed() - 0.2) * 1.45f;

        if (motionXZ > baseSpeed && !getData().isGeneralCancel()) {
            if ((verbose+= 2) > 40) {
                flag(MathUtils.round(motionXZ, 4) + ">-" + MathUtils.round(baseSpeed, 4),  true);
                verbose = 20;
            }
        } else {
            verbose = Math.max(0, verbose - 1);
        }

        //Bukkit.broadcastMessage(verbose + ": " + motionXZ + ">-" + baseSpeed + ", " + getData().getTo().getX() + ", " + getData().getFrom().getX()) ;
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
