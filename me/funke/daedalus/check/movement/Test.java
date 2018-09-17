package me.funke.daedalus.check.movement;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.UtilCheat;
import me.funke.daedalus.utils.UtilMath;
import me.funke.daedalus.utils.UtilPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class Test extends Check {

    public static Map<UUID, Map.Entry<Integer, Long>> speedTicks = new HashMap();
    ArrayList<Double> values;
    private boolean testing;
    private Map<UUID, Long> LastMS;
    private Map<UUID, List<Long>> Clicks;
    private Map<UUID, Map.Entry<Integer, Long>> ClickTicks;

    public Test(me.funke.daedalus.Daedalus Daedalus) {
        super("Test", "Test", Daedalus);

        setEnabled(false);
        setBannable(false);

        setMaxViolations(5);

        values = new ArrayList<>();
        testing = false;
        this.LastMS = new HashMap<>();
        this.Clicks = new HashMap<>();
        this.ClickTicks = new HashMap<>();
    }

    public boolean isOnGround(Player player) {
        if (UtilPlayer.isOnClimbable(player, 0)) {
            return false;
        }
        if (player.getVehicle() != null) {
            return false;
        }
        Material type = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER)
                && (type != Material.VINE)) {
            return true;
        }
        Location a = player.getLocation().clone();
        a.setY(a.getY() - 0.5D);
        type = a.getBlock().getType();
        if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER)
                && (type != Material.VINE)) {
            return true;
        }
        a = player.getLocation().clone();
        a.setY(a.getY() + 0.5D);
        type = a.getBlock().getRelative(BlockFace.DOWN).getType();
        if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER)
                && (type != Material.VINE)) {
            return true;
        }
        return UtilCheat.isBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN),
                new Material[]{Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER});
    }

    // public void onDmg(EntityDamageByEntityEvent e) {
    // if(!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))
    // {
    // return;
    // }
    // Player damager = (Player) e.getDamager();
    // Player player = (Player) e.getEntity();
    // double Reach2 =
    // UtilPlayer.getEyeLocation(damager).distance(player.getEyeLocation());
    // if(!testing) {
    // testing = true;
    // new BukkitRunnable() {
    // public void run() {
    // double max = Collections.max(values);
    // damager.sendMessage(getDaedalus().PREFIX + C.Gray + "Highest Value: " +
    // C.Yellow + max);
    // values.clear();
    // testing = false;
    // }
    // }.runTaskLater(getDaedalus(), 100L);
    // } else {
    // values.add(Reach2);
    // }
    // }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) return;
        Player player = e.getPlayer();
        double YSpeed = UtilMath.offset(UtilMath.getHorizontalVector(e.getFrom().toVector()),
                UtilMath.getHorizontalVector(e.getTo().toVector()));
        getDaedalus().logCheat(this, player, null, null, YSpeed + " speed");

    }
}