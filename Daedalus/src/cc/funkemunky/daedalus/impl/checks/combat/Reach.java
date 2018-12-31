package cc.funkemunky.daedalus.impl.checks.combat;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import cc.funkemunky.daedalus.Daedalus;
import cc.funkemunky.daedalus.api.checks.CancelType;
import cc.funkemunky.daedalus.api.checks.Check;
import cc.funkemunky.daedalus.api.data.PlayerData;
import cc.funkemunky.daedalus.api.utils.Packets;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@Packets(packets = {Packet.Client.USE_ENTITY})
public class Reach extends Check {
    public Reach(String name, CancelType cancelType, PlayerData data) {
        super(name, cancelType, data);
    }

    private int verbose;

    @Override
    public void onPacket(Object packet, String packetType) {
        WrappedInUseEntityPacket use = new WrappedInUseEntityPacket(packet, getData().getPlayer());

        Player player = getData().getPlayer();

        if(use.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) use.getEntity();

            double delta = player.getEyeLocation().distance(((LivingEntity) use.getEntity()).getEyeLocation()) - 0.3;

            double threshold = 3.6f;

            double combinedPing = getData().getPing();

            if(entity instanceof Player) {
                PlayerData dumbassData = Daedalus.getInstance().getDataManager().getPlayerData(entity.getUniqueId());

                if(dumbassData != null) combinedPing+= dumbassData.getPing();
            }

            threshold+= (player.getVelocity().lengthSquared() + use.getEntity().getVelocity().lengthSquared()) * 3.0;
            threshold+= combinedPing * 0.004;

            if(delta > threshold) {
                if(verbose++ > 9) {
                    flag(delta + ">-" + threshold, true);
                }
            } else {
                verbose = 0;
            }
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
