package me.funke.daedalus.check.movement;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Step extends Check {
    double stepHeight;

    public Step(me.funke.daedalus.Daedalus Daedalus) {
        super("Step", "Step", Daedalus);

        setEnabled(true);
        setBannable(false);

        this.setMaxViolations(7);
        this.setViolationsToNotify(1);
        this.setViolationResetTime(90000);
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

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!getDaedalus().isEnabled()
                || !isOnGround(player)
                || player.getAllowFlight()
                || getDaedalus().isSotwMode()
                || player.hasPotionEffect(PotionEffectType.JUMP)
                || getDaedalus().LastVelocity.containsKey(player.getUniqueId())
                || UtilPlayer.isOnClimbable(player, 0)
                || UtilCheat.slabsNear(player.getLocation())
                || player.getLocation().getBlock().getType().equals(Material.WATER)
                || player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER)) return;

        double yDist = event.getTo().getY() - event.getFrom().getY();
        if (yDist < 0) return;
        double YSpeed = UtilMath.offset(UtilMath.getVerticalVector(event.getFrom().toVector()),
                UtilMath.getVerticalVector(event.getTo().toVector()));
        if (yDist > 0.95) {
            this.dumplog(player, "Height (Logged): " + yDist);
            this.getDaedalus().logCheat(this, player, Math.round(yDist) + " blocks", Chance.HIGH,
                    "Type A");
            return;
        }
        if (((YSpeed == 0.25D || (YSpeed >= 0.58D && YSpeed < 0.581D)) && yDist > 0.0D
                || (YSpeed > 0.2457D && YSpeed < 0.24582D) || (YSpeed > 0.329 && YSpeed < 0.33))
                && !player.getLocation().clone().subtract(0.0D, 0.1, 0.0D).getBlock().getType().equals(Material.SNOW)) {
            this.getDaedalus().logCheat(this, player,
                    "Speed: " + YSpeed + " Block: "
                            + player.getLocation().clone().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().toString(),
                    Chance.LIKELY, "Type C");
            return;
        }
        ArrayList<Block> blocks = UtilBlock.getBlocksAroundCenter(player.getLocation(), 1);
        for (Block block : blocks) {
            if (block.getType().isSolid()) {
                if ((YSpeed >= 0.321 && YSpeed < 0.322)) {
                    this.getDaedalus().logCheat(this, player, "Speed: " + YSpeed, Chance.HIGH,
                            "Type D");
                    return;
                }
            }
        }
    }
}