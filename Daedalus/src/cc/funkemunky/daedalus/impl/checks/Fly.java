package cc.funkemunky.daedalus.impl.checks;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.event.custom.PacketRecieveEvent;
import cc.funkemunky.api.event.custom.PacketSendEvent;
import cc.funkemunky.api.event.system.EventMethod;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import cc.funkemunky.api.utils.*;
import cc.funkemunky.daedalus.Daedalus;
import cc.funkemunky.daedalus.api.checks.CancelType;
import cc.funkemunky.daedalus.api.checks.Check;
import cc.funkemunky.daedalus.api.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Fly extends Check {
    public Fly(String name, CancelType cancelType, PlayerData data) {
        super(name, cancelType, data);
    }

    private Location from, groundLocation;
    private int verbose, cancelTicks, posTicks, flagTicks;
    private float lmotionY, lacceleration, prediction;
    private boolean lonGround;

    @EventHandler
    public void onEvent(PlayerVelocityEvent e) {
        cancelTicks = 40;
    }

    @EventMethod
    public void onPacketSend(PacketSendEvent e) {
        switch(e.getType()) {
            case Packet.Server.POSITION: {
                posTicks = 1;
                break;
            }
        }
    }

    @EventMethod
    public void onPacketReceive(PacketRecieveEvent e) {
        if(e.getType().contains("Position")) {
            WrappedInFlyingPacket packet = new WrappedInFlyingPacket(e.getPacket(), e.getPlayer());
            Location to = new Location(e.getPlayer().getWorld(), packet.getX(), packet.getY(), packet.getZ());
            if(from != null && posTicks <= 0) {
                boolean onGround = packet.isGround();
                float motionY = (float) (to.getY() - from.getY());
                float acceleration = motionY - lmotionY;

                if(onGround) {
                    prediction = 0;
                } else if(lonGround) {
                    prediction += 0.42f + PlayerUtils.getPotionEffectLevel(e.getPlayer(), PotionEffectType.JUMP) * 0.011;
                } else {
                    prediction -= 0.08f;
                    prediction *= 0.98f;
                }

                if(isOnGround(to)) {
                    groundLocation = to;
                }

                if(MathUtils.getDelta(motionY, prediction) > 0.1
                        && (acceleration > 1E-7 || prediction > -2)
                        && !onGround) {
                    if((verbose++ > 4 || (acceleration < 0.05 && motionY > 0)) && cancelTicks <= 0) {
                        flag(motionY + ">-" + prediction, true);
                        //Bukkit.broadcastMessage(Color.Red + "Flag");
                        new BukkitRunnable() {
                            public void run() {
                                e.getPlayer().teleport(groundLocation);
                            }
                        }.runTask(Daedalus.getInstance());
                    }
                } else {
                    verbose = 0;
                }

                lonGround = onGround;
                lacceleration = acceleration;
                lmotionY = motionY;
            }

            cancelTicks--;
            posTicks--;
            from = to;
        }
    }

    private boolean isOnGround(Location location) {
        BoundingBox box = new BoundingBox(location.toVector(), location.toVector().add(new Vector(0, 1.8f, 0))).grow(0.3f,0,0.3f).subtract(0, 0.4f,0,0,0,0);

        List<BoundingBox> bbs = Atlas.getInstance().getBlockBoxManager().getBlockBox().getCollidingBoxes(location.getWorld(), box);
        return bbs.size() > 0 && bbs.stream().anyMatch(bb -> bb.collides(box));
    }
}
