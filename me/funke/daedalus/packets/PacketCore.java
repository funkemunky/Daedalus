package me.funke.daedalus.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.funke.daedalus.Daedalus;
import me.funke.daedalus.packets.events.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class PacketCore {
    private static final PacketType[] ENTITY_PACKETS = new PacketType[]{PacketType.Play.Server.SPAWN_ENTITY_LIVING,
            PacketType.Play.Server.NAMED_ENTITY_SPAWN, PacketType.Play.Server.ENTITY_METADATA};
    public me.funke.daedalus.Daedalus Daedalus;
    public Map<UUID, Integer> movePackets;
    private HashSet<EntityType> enabled;

    public PacketCore(Daedalus Daedalus) {
        super();
        this.Daedalus = Daedalus;
        enabled = new HashSet<>();
        enabled.add(EntityType.valueOf("PLAYER"));
        movePackets = new HashMap<>();

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.Daedalus,
                PacketType.Play.Client.USE_ENTITY) {
            public void onPacketReceiving(final PacketEvent event) {
                final PacketContainer packet = event.getPacket();
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                try {
                    Object playEntity = getNMSClass("PacketPlayInUseEntity");
                    String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                    if (version.contains("1_7")) {
                        if (packet.getHandle() == playEntity) {
                            if (playEntity.getClass().getMethod("c") == null) {
                                return;
                            }
                        }
                    } else {
                        if (packet.getHandle() == playEntity) {
                            if (playEntity.getClass().getMethod("a") == null) {
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EntityUseAction type;
                try {
                    type = packet.getEntityUseActions().read(0);
                } catch (Exception ex) {
                    return;
                }

                Entity entity = event.getPacket().getEntityModifier(player.getWorld()).read(0);

                if (entity == null) {
                    return;
                }

                Bukkit.getServer().getPluginManager().callEvent(new PacketUseEntityEvent(type, player, entity));
                if (type == EntityUseAction.ATTACK) {
                    Bukkit.getServer().getPluginManager()
                            .callEvent(new PacketKillauraEvent(player, PacketPlayerType.USE));
                }
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Daedalus, ENTITY_PACKETS) {

            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Entity e = packet.getEntityModifier(event).read(0);
                if (e instanceof LivingEntity && enabled.contains(e.getType())
                        && packet.getWatchableCollectionModifier().read(0) != null
                        && e.getUniqueId() != event.getPlayer().getUniqueId()) {
                    packet = packet.deepClone();
                    event.setPacket(packet);
                    if (event.getPacket().getType() == PacketType.Play.Server.ENTITY_METADATA) {
                        WrappedDataWatcher watcher = new WrappedDataWatcher(
                                packet.getWatchableCollectionModifier().read(0));
                        this.processDataWatcher(watcher);
                        packet.getWatchableCollectionModifier().write(0,
                                watcher.getWatchableObjects());
                    }
                }
            }

            private void processDataWatcher(WrappedDataWatcher watcher) {
                if (watcher != null && watcher.getObject(6) != null && watcher.getFloat(6) != 0.0F) {
                    watcher.setObject(6, 1.0f);
                }
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.Daedalus,
                PacketType.Play.Client.POSITION_LOOK) {
            public void onPacketReceiving(final PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent(new PacketPlayerEvent(player,
                        event.getPacket().getDoubles().read(0),
                        event.getPacket().getDoubles().read(1),
                        event.getPacket().getDoubles().read(2), event.getPacket().getFloat().read(0),
                        event.getPacket().getFloat().read(1), PacketPlayerType.POSLOOK));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this.Daedalus, PacketType.Play.Client.LOOK) {
                    public void onPacketReceiving(final PacketEvent event) {
                        Player player = event.getPlayer();

                        if (player == null) {
                            return;
                        }

                        Bukkit.getServer().getPluginManager()
                                .callEvent(new PacketPlayerEvent(player, event.getPacket().getDoubles().read(0),
                                        event.getPacket().getDoubles().read(1), event.getPacket().getDoubles().read(2),
                                        event.getPacket().getFloat().read(0), event.getPacket().getFloat().read(1),
                                        PacketPlayerType.POSLOOK));
                    }
                });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.Daedalus,
                PacketType.Play.Client.POSITION) {
            public void onPacketReceiving(final PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent(
                        new PacketPlayerEvent(player, event.getPacket().getDoubles().read(0),
                                event.getPacket().getDoubles().read(1),
                                event.getPacket().getDoubles().read(2), player.getLocation().getYaw(),
                                player.getLocation().getPitch(), PacketPlayerType.POSITION));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.Daedalus,
                PacketType.Play.Server.POSITION) {
            public void onPacketSending(final PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) {
                    return;
                }

                int i = movePackets.getOrDefault(player.getUniqueId(), 0);
                i++;
                movePackets.put(player.getUniqueId(), i);
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.Daedalus,
                PacketType.Play.Client.ENTITY_ACTION) {
            public void onPacketReceiving(final PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager()
                        .callEvent(new PacketEntityActionEvent(player, packet.getIntegers().read(1)));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.Daedalus,
                PacketType.Play.Client.KEEP_ALIVE) {
            public void onPacketReceiving(final PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent(new PacketKeepAliveEvent(player));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.Daedalus,
                PacketType.Play.Client.ARM_ANIMATION) {
            public void onPacketReceiving(final PacketEvent event) {
                final Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager()
                        .callEvent(new PacketKillauraEvent(player, PacketPlayerType.ARM_SWING));
                Bukkit.getServer().getPluginManager().callEvent(new PacketSwingArmEvent(event, player));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.Daedalus,
                PacketType.Play.Client.HELD_ITEM_SLOT) {
            public void onPacketReceiving(final PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent(new PacketHeldItemChangeEvent(event, player));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.Daedalus,
                PacketType.Play.Client.BLOCK_PLACE) {
            public void onPacketReceiving(final PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                Bukkit.getServer().getPluginManager().callEvent(new PacketBlockPlacementEvent(event, player));
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this.Daedalus, PacketType.Play.Client.FLYING) {
                    public void onPacketReceiving(final PacketEvent event) {
                        final Player player = event.getPlayer();
                        if (player == null) {
                            return;
                        }
                        Bukkit.getServer().getPluginManager()
                                .callEvent(new PacketPlayerEvent(player, player.getLocation().getX(),
                                        player.getLocation().getY(), player.getLocation().getZ(),
                                        player.getLocation().getYaw(), player.getLocation().getPitch(),
                                        PacketPlayerType.FLYING));
                    }
                });
    }

    public Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}