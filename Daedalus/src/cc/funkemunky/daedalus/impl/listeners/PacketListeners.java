package cc.funkemunky.daedalus.impl.listeners;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.event.custom.PacketRecieveEvent;
import cc.funkemunky.api.event.custom.PacketSendEvent;
import cc.funkemunky.api.event.system.EventMethod;
import cc.funkemunky.api.event.system.Listener;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInAbilitiesPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInTransactionPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutEntityMetadata;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutTransaction;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.daedalus.Daedalus;
import cc.funkemunky.daedalus.api.data.PlayerData;
import cc.funkemunky.daedalus.api.utils.CollisionAssessment;
import cc.funkemunky.daedalus.api.utils.Packets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class PacketListeners implements Listener {

    @EventMethod
    public void onEvent(PacketSendEvent event) {
        if(event.getPlayer() == null) return;
        PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if(data != null) {
            switch(event.getType()) {
                case Packet.Server.POSITION: {
                    data.getLastServerPos().reset();
                    break;
                }
                case Packet.Server.KEEP_ALIVE: {
                    TinyProtocolHandler.sendPacket(event.getPlayer(), new WrappedOutTransaction(event.getPlayer().getEntityId() + 32, (short) 69, false));
                    data.setLastTransaction(System.currentTimeMillis());
                    break;
                }
                case Packet.Server.ENTITY_VELOCITY: {
                    WrappedOutVelocityPacket packet = new WrappedOutVelocityPacket(event.getPacket(), event.getPlayer());

                    if(packet.getId() == event.getPlayer().getEntityId()) {
                        Vector vector = new Vector(packet.getX(), packet.getY(), packet.getZ());

                        if(MathUtils.round(vector.getY(), 2) != -0.08) {
                            data.getLastVelocity().reset();
                            data.setLastVelocityVector(vector);
                        }
                    }
                    break;
                }
                case Packet.Server.ENTITY_METADATA: {
                    WrappedOutEntityMetadata packet = new WrappedOutEntityMetadata(event.getPacket(), event.getPlayer());

                    if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13) && packet.getObjects().size() > 6) {
                        int rt = ((Byte)packet.getObjects().get(6).getObject() & 0x04);
                        data.setRiptiding(rt == 1);
                    }
                    break;
                }
            }
            data.getChecks().stream().filter(check -> check.getClass().isAnnotationPresent(Packets.class) && Arrays.asList(check.getClass().getAnnotation(Packets.class).packets()).contains(event.getType())).forEach(check -> check.onPacket(event.getPacket(), event.getType()));
        }
    }
    @EventMethod
    public void onEvent(PacketRecieveEvent event) {
        if(event.getPlayer() == null) return;
        PlayerData data = Daedalus.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if(data != null) {
            switch(event.getType()) {
                case Packet.Client.TRANSACTION: {
                    WrappedInTransactionPacket packet = new WrappedInTransactionPacket(event.getPacket(), event.getPlayer());

                    if(packet.getId() == 32 + event.getPlayer().getEntityId()) {
                        data.setPing(MathUtils.elapsed(data.getLastTransaction()));
                    }
                    break;
                }
                case Packet.Client.ABILITIES: {
                    WrappedInAbilitiesPacket packet = new WrappedInAbilitiesPacket(event.getPacket(), event.getPlayer());

                    data.setAbleToFly(packet.isAllowedFlight());
                    data.setCreativeMode(packet.isCreativeMode());
                    data.setInvulnerable(packet.isInvulnerable());
                    data.setFlying(packet.isFlying());
                    data.setWalkSpeed(packet.getWalkSpeed());
                    data.setFlySpeed(packet.getFlySpeed());
                    break;
                }
                case Packet.Client.POSITION:
                case Packet.Client.POSITION_LOOK:
                case Packet.Client.LOOK:
                case Packet.Client.LEGACY_POSITION:
                case Packet.Client.LEGACY_POSITION_LOOK:
                case Packet.Client.LEGACY_LOOK:{
                    WrappedInFlyingPacket packet = new WrappedInFlyingPacket(event.getPacket(), event.getPlayer());

                    if(data.getFrom() == null || data.getTo() == null) {
                        data.setFrom(new Location(event.getPlayer().getWorld(), 0,0,0));
                        data.setTo(new Location(event.getPlayer().getWorld(), 0,0,0));
                    }

                    data.setFrom(data.getTo().clone());
                    if(packet.isPos()) {
                        data.getTo().setX(packet.getX());
                        data.getTo().setY(packet.getY());
                        data.getTo().setZ(packet.getZ());
                        data.setBoundingBox(new BoundingBox(data.getTo().toVector(), data.getTo().toVector().add(new Vector(0, 1.8, 0))).grow(0.3f,0,0.3f));

                        List<BoundingBox> box = Atlas.getInstance().getBlockBoxManager().getBlockBox().getCollidingBoxes(data.getTo().getWorld(), data.getBoundingBox().grow(0.5f, 0.1f, 0.5f).subtract(0, 0.5f, 0, 0, 0, 0));

                        CollisionAssessment assessment = new CollisionAssessment(data.getBoundingBox(), data);
                        box.forEach(bb -> assessment.assessBox(bb, data.getTo().getWorld()));

                        data.setOnGround(assessment.isOnGround());
                        data.setBlocksOnTop(assessment.isBlocksOnTop());
                        data.setCollidesHorizontally(assessment.isCollidesHorizontally());
                        data.setInLiquid(assessment.isInLiquid());
                        data.setOnHalfBlock(assessment.isOnHalfBlock());
                        data.setOnIce(assessment.isOnIce());
                        data.setPistonsNear(assessment.isPistonsNear());
                        data.setInWeb(assessment.isInWeb());
                        data.setOnClimbable(assessment.isOnClimbable());
                        data.setFullyInAir(assessment.isFullyInAir());

                        event.setCancelled(true);
                        if(data.isOnGround()) {
                            data.groundTicks++;
                            data.airTicks = 0;

                            data.setOnSlimeBefore(assessment.isOnSlime());
                        } else {
                            data.airTicks++;
                            data.groundTicks = 0;
                        }

                        data.iceTicks = data.isOnIce() ? Math.min(40, data.iceTicks + 1) : Math.max(0, data.iceTicks - 1);

                    }

                    if(packet.isLook()) {
                        data.getTo().setYaw(packet.getYaw());
                        data.getTo().setPitch(packet.getPitch());
                    }

                    data.setGeneralCancel(data.isAbleToFly() || data.isRiptiding() || data.getLastVelocity().hasNotPassed(40));
                    break;
                }
            }

            data.getChecks().stream().filter(check -> check.getClass().isAnnotationPresent(Packets.class) && Arrays.asList(check.getClass().getAnnotation(Packets.class).packets()).contains(event.getType())).forEach(check -> check.onPacket(event.getPacket(), event.getType()));
        }
    }
}
