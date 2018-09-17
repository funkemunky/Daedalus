package me.funke.daedalus.check.movement;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import me.funke.daedalus.check.Check;
import me.funke.daedalus.listeners.PearlGlitchEvent;
import me.funke.daedalus.utils.C;
import me.funke.daedalus.utils.Chance;
import me.funke.daedalus.utils.PearlGlitchType;
import me.funke.daedalus.utils.UtilCheat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Directional;
import org.bukkit.material.Door;
import org.bukkit.material.Gate;
import org.bukkit.material.TrapDoor;

import java.util.*;

public class Phase extends Check {
    public static final Map<UUID, Location> lastLocation = new HashMap<>();
    public static List<Material> allowed = new ArrayList<>();
    public static List<Material> semi = new ArrayList<>();
    public static Set<UUID> teleported = new HashSet<>();

    static {
        allowed.add(Material.SIGN);
        allowed.add(Material.SIGN_POST);
        allowed.add(Material.WALL_SIGN);
        allowed.add(Material.SUGAR_CANE_BLOCK);
        allowed.add(Material.WHEAT);
        allowed.add(Material.POTATO);
        allowed.add(Material.CARROT);
        allowed.add(Material.STEP);
        allowed.add(Material.AIR);
        allowed.add(Material.WOOD_STEP);
        allowed.add(Material.SOUL_SAND);
        allowed.add(Material.CARPET);
        allowed.add(Material.STONE_PLATE);
        allowed.add(Material.WOOD_PLATE);
        allowed.add(Material.LADDER);
        allowed.add(Material.CHEST);
        allowed.add(Material.WATER);
        allowed.add(Material.STATIONARY_WATER);
        allowed.add(Material.LAVA);
        allowed.add(Material.STATIONARY_LAVA);
        allowed.add(Material.REDSTONE_COMPARATOR);
        allowed.add(Material.REDSTONE_COMPARATOR_OFF);
        allowed.add(Material.REDSTONE_COMPARATOR_ON);
        allowed.add(Material.IRON_PLATE);
        allowed.add(Material.GOLD_PLATE);
        allowed.add(Material.DAYLIGHT_DETECTOR);
        allowed.add(Material.STONE_BUTTON);
        allowed.add(Material.WOOD_BUTTON);
        allowed.add(Material.HOPPER);
        allowed.add(Material.RAILS);
        allowed.add(Material.ACTIVATOR_RAIL);
        allowed.add(Material.DETECTOR_RAIL);
        allowed.add(Material.POWERED_RAIL);
        allowed.add(Material.TRIPWIRE_HOOK);
        allowed.add(Material.TRIPWIRE);
        allowed.add(Material.SNOW_BLOCK);
        allowed.add(Material.REDSTONE_TORCH_OFF);
        allowed.add(Material.REDSTONE_TORCH_ON);
        allowed.add(Material.DIODE_BLOCK_OFF);
        allowed.add(Material.DIODE_BLOCK_ON);
        allowed.add(Material.DIODE);
        allowed.add(Material.SEEDS);
        allowed.add(Material.MELON_SEEDS);
        allowed.add(Material.PUMPKIN_SEEDS);
        allowed.add(Material.DOUBLE_PLANT);
        allowed.add(Material.LONG_GRASS);
        allowed.add(Material.WEB);
        allowed.add(Material.SNOW);
        allowed.add(Material.FLOWER_POT);
        allowed.add(Material.BREWING_STAND);
        allowed.add(Material.CAULDRON);
        allowed.add(Material.CACTUS);
        allowed.add(Material.WATER_LILY);
        allowed.add(Material.RED_ROSE);
        allowed.add(Material.ENCHANTMENT_TABLE);
        allowed.add(Material.ENDER_PORTAL_FRAME);
        allowed.add(Material.PORTAL);
        allowed.add(Material.ENDER_PORTAL);
        allowed.add(Material.ENDER_CHEST);
        allowed.add(Material.NETHER_FENCE);
        allowed.add(Material.NETHER_WARTS);
        allowed.add(Material.REDSTONE_WIRE);
        allowed.add(Material.LEVER);
        allowed.add(Material.YELLOW_FLOWER);
        allowed.add(Material.CROPS);
        allowed.add(Material.WATER);
        allowed.add(Material.LAVA);
        allowed.add(Material.SKULL);
        allowed.add(Material.TRAPPED_CHEST);
        allowed.add(Material.FIRE);
        allowed.add(Material.BROWN_MUSHROOM);
        allowed.add(Material.RED_MUSHROOM);
        allowed.add(Material.DEAD_BUSH);
        allowed.add(Material.SAPLING);
        allowed.add(Material.TORCH);
        allowed.add(Material.MELON_STEM);
        allowed.add(Material.PUMPKIN_STEM);
        allowed.add(Material.COCOA);
        allowed.add(Material.BED);
        allowed.add(Material.BED_BLOCK);
        allowed.add(Material.PISTON_EXTENSION);
        allowed.add(Material.PISTON_MOVING_PIECE);
        semi.add(Material.IRON_FENCE);
        semi.add(Material.THIN_GLASS);
        semi.add(Material.STAINED_GLASS_PANE);
        semi.add(Material.COBBLE_WALL);
    }

    private final ImmutableSet<Material> blockedPearlTypes = Sets.immutableEnumSet(Material.THIN_GLASS,
            Material.IRON_FENCE, Material.FENCE, Material.NETHER_FENCE, Material.FENCE_GATE, Material.ACACIA_STAIRS,
            Material.BIRCH_WOOD_STAIRS, Material.BRICK_STAIRS, Material.COBBLESTONE_STAIRS, Material.DARK_OAK_STAIRS,
            Material.JUNGLE_WOOD_STAIRS, Material.NETHER_BRICK_STAIRS, Material.QUARTZ_STAIRS,
            Material.SANDSTONE_STAIRS, Material.SMOOTH_STAIRS, Material.SPRUCE_WOOD_STAIRS, Material.WOOD_STAIRS);

    public Phase(me.funke.daedalus.Daedalus Daedalus) {
        super("Phase", "Phase", Daedalus);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(40);
        setViolationsToNotify(2);
    }

    @EventHandler(ignoreCancelled = true)
    public void teleport(PlayerTeleportEvent e) {
        if (e.getCause() != TeleportCause.UNKNOWN) {
            teleported.add(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        teleported.add(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        teleported.add(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void update(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.isDead()) return;

        UUID playerId = player.getUniqueId();
        Location loc1 = lastLocation.containsKey(playerId) ? lastLocation.get(playerId)
                : player.getLocation();
        Location loc2 = player.getLocation();
        if (player.getAllowFlight()) {
            teleported.add(player.getUniqueId());
        }
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            teleported.add(player.getUniqueId());
        }
        if ((loc1.getWorld() == loc2.getWorld()) && (!teleported.contains(playerId))
                && (loc1.distance(loc2) > 10.0D)) {
            player.teleport(lastLocation.get(playerId), PlayerTeleportEvent.TeleportCause.PLUGIN);
            if ((player.getLocation().getBlock().getType().isSolid())
                    || (player.getLocation().clone().add(0.0D, 1.0D, 0.0D).getBlock().getType().isSolid())
                    && player.getVehicle() == null && !player.hasPermission("daedalus.bypass")) {
                player.teleport(lastLocation.get(playerId), PlayerTeleportEvent.TeleportCause.PLUGIN);
                return;
            }
            getDaedalus().logCheat(this, player, null, Chance.LIKELY);
        } else if (isLegit(playerId, loc1, loc2)) {
            lastLocation.put(playerId, loc2);
        } else if ((player.hasPermission("daedalus.admin")) || (lastLocation.containsKey(playerId))) {
            player.teleport(lastLocation.get(playerId), PlayerTeleportEvent.TeleportCause.PLUGIN);
            if ((player.getLocation().getBlock().getType().isSolid())
                    || (player.getLocation().clone().add(0.0D, 1.0D, 0.0D).getBlock().getType().isSolid())
                    && player.getVehicle() == null && !player.hasPermission("daedalus.bypass")) {
                player.teleport(lastLocation.get(playerId), PlayerTeleportEvent.TeleportCause.PLUGIN);
                return;
            }
            getDaedalus().logCheat(this, player, null, Chance.LIKELY);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!getDaedalus().getConfig().getBoolean("checks.Phase.pearlFix")) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem()
                && event.getItem().getType() == Material.ENDER_PEARL) {
            Block block = event.getClickedBlock();
            if (block.getType().isSolid() && this.blockedPearlTypes.contains(block.getType())
                    && !(block.getState() instanceof InventoryHolder)) {
                final PearlGlitchEvent event2 = new PearlGlitchEvent(event.getPlayer(), event.getPlayer().getLocation(),
                        event.getPlayer().getLocation(), event.getPlayer().getItemInHand(), PearlGlitchType.INTERACT);
                Bukkit.getPluginManager().callEvent(event2);

                if (!event2.isCancelled()) {
                    event.setCancelled(true);
                    Player player = event.getPlayer();
                    player.setItemInHand(event.getItem());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPearlClip(PlayerTeleportEvent event) {
        if (!getDaedalus().getConfig().getBoolean("checks.Phase.pearlFix")) return;
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location to = event.getTo();
            if (blockedPearlTypes.contains(to.getBlock().getType()) && to.getBlock().getType() != Material.FENCE_GATE
                    && to.getBlock().getType() != Material.TRAP_DOOR) {
                final PearlGlitchEvent event2 = new PearlGlitchEvent(event.getPlayer(), event.getFrom(), event.getTo(),
                        event.getPlayer().getItemInHand(), PearlGlitchType.TELEPORT);
                Bukkit.getPluginManager().callEvent(event2);
                if (!event2.isCancelled()) {
                    Player player = event.getPlayer();
                    player.sendMessage(getDaedalus().PREFIX + C.Red
                            + "You have been detected trying to pearl glitch, therefore your pearl was cancelled.");
                    event.setCancelled(true);
                }
                return;
            }
            to.setX(to.getBlockX() + 0.5);
            to.setZ(to.getBlockZ() + 0.5);
            if ((!allowed.contains(to.getBlock().getType()) || !allowed.contains(to.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType()))
                    && (to.getBlock().getType().isSolid()
                    || to.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType().isSolid())
                    && to.clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType().isSolid()
                    & !UtilCheat.isSlab(to.getBlock())) {
                Player player = event.getPlayer();
                final PearlGlitchEvent event2 = new PearlGlitchEvent(player, event.getFrom(), event.getTo(),
                        event.getPlayer().getItemInHand(), PearlGlitchType.SAFE_LOCATION);
                Bukkit.getPluginManager().callEvent(event2);
                if (!event2.isCancelled()) {
                    event.setCancelled(true);
                    player.sendMessage(getDaedalus().PREFIX + C.Red
                            + "Could not find a safe location, therefore your pearl was cancelled.");
                }
                return;
            } else if (!allowed.contains(to.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType())
                    && to.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType().isSolid()
                    && !to.getBlock().getType().isSolid()) {
                to.setY(to.getY() - 0.7);
            }

            event.setTo(to);
        }
    }

    public boolean isLegit(UUID playerId, Location loc1, Location loc2) {
        if (loc1.getWorld() != loc2.getWorld()) {
            return true;
        }
        if (teleported.remove(playerId)) {
            return true;
        }
        int moveMaxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int moveMinX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int moveMaxY = Math.max(loc1.getBlockY(), loc2.getBlockY()) + 1;
        int moveMinY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int moveMaxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int moveMinZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        if (moveMaxY > 256) {
            moveMaxX = 256;
        }
        if (moveMinY > 256) {
            moveMinY = 256;
        }
        for (int x = moveMinX; x <= moveMaxX; x++) {
            for (int z = moveMinZ; z <= moveMaxZ; z++) {
                for (int y = moveMinY; y <= moveMaxY; y++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    if (((y != moveMinY) || (loc1.getBlockY() == loc2.getBlockY()))
                            && (hasPhased(block, loc1, loc2, Bukkit.getPlayer(playerId)))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean hasPhased(Block block, Location loc1, Location loc2, Player p) {
        if (((allowed.contains(block.getType())) || (UtilCheat.isStair(block)) || (UtilCheat.isSlab(block))
                || (UtilCheat.isClimbableBlock(block)) || (block.isLiquid()))) {
            return false;
        }
        double moveMaxX = Math.max(loc1.getX(), loc2.getX());
        double moveMinX = Math.min(loc1.getX(), loc2.getX());
        double moveMaxY = Math.max(loc1.getY(), loc2.getY()) + 1.8D;
        double moveMinY = Math.min(loc1.getY(), loc2.getY());
        double moveMaxZ = Math.max(loc1.getZ(), loc2.getZ());
        double moveMinZ = Math.min(loc1.getZ(), loc2.getZ());
        double blockMaxX = block.getLocation().getBlockX() + 1;
        double blockMinX = block.getLocation().getBlockX();
        double blockMaxY = block.getLocation().getBlockY() + 2;
        double blockMinY = block.getLocation().getBlockY();
        double blockMaxZ = block.getLocation().getBlockZ() + 1;
        double blockMinZ = block.getLocation().getBlockZ();
        if (blockMinY > moveMinY) {
            blockMaxY -= 1.0D;
        }
        if ((block.getType().equals(Material.IRON_DOOR_BLOCK)) || (block.getType().equals(Material.WOODEN_DOOR))) {
            Door door = (Door) block.getType().getNewData(block.getData());
            if (door.isTopHalf()) {
                return false;
            }
            BlockFace facing = door.getFacing();
            if (door.isOpen()) {
                Block up = block.getRelative(BlockFace.UP);
                boolean hinge;
                if ((up.getType().equals(Material.IRON_DOOR_BLOCK)) || (up.getType().equals(Material.WOODEN_DOOR))) {
                    hinge = (up.getData() & 0x1) == 1;
                } else {
                    return false;
                }
                if (facing == BlockFace.NORTH) {
                    facing = hinge ? BlockFace.WEST : BlockFace.EAST;
                } else if (facing == BlockFace.EAST) {
                    facing = hinge ? BlockFace.NORTH : BlockFace.SOUTH;
                } else if (facing == BlockFace.SOUTH) {
                    facing = hinge ? BlockFace.EAST : BlockFace.WEST;
                } else {
                    facing = hinge ? BlockFace.SOUTH : BlockFace.NORTH;
                }
            }
            if (facing == BlockFace.WEST) {
                blockMaxX -= 0.8D;
            }
            if (facing == BlockFace.EAST) {
                blockMinX += 0.8D;
            }
            if (facing == BlockFace.NORTH) {
                blockMaxZ -= 0.8D;
            }
            if (facing == BlockFace.SOUTH) {
                blockMinZ += 0.8D;
            }
        } else if (block.getType().equals(Material.FENCE_GATE)) {
            if (((Gate) block.getType().getNewData(block.getData())).isOpen()) {
                return false;
            }
            BlockFace face = ((Directional) block.getType().getNewData(block.getData())).getFacing();
            if ((face == BlockFace.NORTH) || (face == BlockFace.SOUTH)) {
                blockMaxX -= 0.2D;
                blockMinX += 0.2D;
            } else {
                blockMaxZ -= 0.2D;
                blockMinZ += 0.2D;
            }
        } else if (block.getType().equals(Material.TRAP_DOOR)) {
            TrapDoor door = (TrapDoor) block.getType().getNewData(block.getData());
            if (door.isOpen()) {
                return false;
            }
            if (door.isInverted()) {
                blockMinY += 0.85D;
            } else {
                blockMaxY -= (blockMinY > moveMinY ? 0.85D : 1.85D);
            }
        } else if (block.getType().equals(Material.FENCE) || semi.contains(block.getType())) {
            blockMaxX -= 0.2D;
            blockMinX += 0.2D;
            blockMaxZ -= 0.2D;
            blockMinZ += 0.2D;
            if (((moveMaxX > blockMaxX) && (moveMinX > blockMaxX) && (moveMaxZ > blockMaxZ) && (moveMinZ > blockMaxZ))
                    || ((moveMaxX < blockMinX) && (moveMinX < blockMinX) && (moveMaxZ > blockMaxZ)
                    && (moveMinZ > blockMaxZ))
                    || ((moveMaxX > blockMaxX) && (moveMinX > blockMaxX) && (moveMaxZ < blockMinZ)
                    && (moveMinZ < blockMinZ))
                    || ((moveMaxX < blockMinX) && (moveMinX < blockMinX) && (moveMaxZ < blockMinZ)
                    && (moveMinZ < blockMinZ))) {
                return false;
            }
            if (block.getRelative(BlockFace.EAST).getType() == block.getType()) {
                blockMaxX += 0.2D;
            }
            if (block.getRelative(BlockFace.WEST).getType() == block.getType()) {
                blockMinX -= 0.2D;
            }
            if (block.getRelative(BlockFace.SOUTH).getType() == block.getType()) {
                blockMaxZ += 0.2D;
            }
            if (block.getRelative(BlockFace.NORTH).getType() == block.getType()) {
                blockMinZ -= 0.2D;
            }
        }
        boolean x = loc1.getX() < loc2.getX();
        boolean y = loc1.getY() < loc2.getY();
        boolean z = loc1.getZ() < loc2.getZ();

        double distance = loc1.distance(loc2) - Math.abs(loc1.getY() - loc2.getY());

        if (distance > 0.5 && block.getType().isSolid()) {
            return true;
        }

        return ((moveMinX != moveMaxX) && (moveMinY <= blockMaxY) && (moveMaxY >= blockMinY) && (moveMinZ <= blockMaxZ)
                && (moveMaxZ >= blockMinZ)
                && (((x) && (moveMinX <= blockMinX) && (moveMaxX >= blockMinX))
                || ((!x) && (moveMinX <= blockMaxX) && (moveMaxX >= blockMaxX))))
                || ((moveMinY != moveMaxY) && (moveMinX <= blockMaxX) && (moveMaxX >= blockMinX)
                && (moveMinZ <= blockMaxZ) && (moveMaxZ >= blockMinZ)
                && (((y) && (moveMinY <= blockMinY) && (moveMaxY >= blockMinY))
                || ((!y) && (moveMinY <= blockMaxY) && (moveMaxY >= blockMaxY))))
                || ((moveMinZ != moveMaxZ) && (moveMinX <= blockMaxX) && (moveMaxX >= blockMinX)
                && (moveMinY <= blockMaxY) && (moveMaxY >= blockMinY)
                && (((z) && (moveMinZ <= blockMinZ) && (moveMaxZ >= blockMinZ))
                || ((!z) && (moveMinZ <= blockMaxZ) && (moveMaxZ >= blockMaxZ))));
    }
}