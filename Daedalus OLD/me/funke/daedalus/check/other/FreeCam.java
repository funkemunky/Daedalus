package me.funke.daedalus.check.other;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.Chance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class FreeCam extends Check {

    public FreeCam(me.funke.daedalus.Daedalus Daedalus) {
        super("BlockInteract", "Block Interact", Daedalus);

        this.setBannable(false);
        this.setEnabled(true);

        setMaxViolations(29);
    }

    @EventHandler(ignoreCancelled = true)
    public void checkFreecam(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        boolean isValid = false;
        Player player = e.getPlayer();
        Location scanLocation = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation();
        double x = scanLocation.getX();
        double y = scanLocation.getY();
        double z = scanLocation.getZ();
        for (double sX = x; sX < x + 2.0D; sX += 1.0D) {
            for (double sY = y; sY < y + 2.0D; sY += 1.0D) {
                for (double sZ = z; sZ < z + 2.0D; sZ += 1.0D) {
                    Location relative = new Location(scanLocation.getWorld(), sX, sY, sZ);
                    List<Location> blocks = rayTrace(player.getLocation(), relative);
                    boolean valid = true;
                    for (Location l : blocks) {
                        if (!checkPhase(l.getBlock().getType())) {
                            valid = false;
                        }
                    }
                    if (valid) {
                        isValid = true;
                    }
                }
            }
        }
        if ((!isValid) && (!player.getPlayer().getItemInHand().getType().equals(Material.ENDER_PEARL))) {
            getDaedalus().logCheat(this, player, "Block Interact is invalid!", Chance.LIKELY,
                    "Experimental");
            e.setCancelled(true);
        }
    }

    private List<Location> rayTrace(Location from, Location to) {
        List<Location> a = new ArrayList<>();
        if ((from == null) || (to == null)) {
            return a;
        }
        if (!from.getWorld().equals(to.getWorld())) {
            return a;
        }
        if (from.distance(to) > 10.0D) {
            return a;
        }
        double x1 = from.getX();
        double y1 = from.getY() + 1.62D;
        double z1 = from.getZ();
        double x2 = to.getX();
        double y2 = to.getY();
        double z2 = to.getZ();

        boolean scanning = true;
        while (scanning) {
            a.add(new Location(from.getWorld(), x1, y1, z1));
            x1 += (x2 - x1) / 10.0D;
            y1 += (y2 - y1) / 10.0D;
            z1 += (z2 - z1) / 10.0D;
            if ((Math.abs(x1 - x2) < 0.01D) && (Math.abs(y1 - y2) < 0.01D) && (Math.abs(z1 - z2) < 0.01D)) {
                scanning = false;
            }
        }
        return a;
    }

    public boolean checkPhase(Material m) {
        int[] whitelist = {355, 196, 194, 197, 195, 193, 64, 96, 187, 184, 186, 107, 185, 183, 192, 189, 139, 191, 85,
                101, 190, 113, 188, 160, 102, 163, 157, 0, 145, 49, 77, 135, 108, 67, 164, 136, 114, 156, 180, 128, 143,
                109, 134, 53, 126, 44, 416, 8, 425, 138, 26, 397, 372, 13, 135, 117, 108, 39, 81, 92, 71, 171, 141, 118,
                144, 54, 139, 67, 127, 59, 115, 330, 164, 151, 178, 32, 28, 93, 94, 175, 122, 116, 130, 119, 120, 51,
                140, 147, 154, 148, 136, 65, 10, 69, 31, 105, 114, 372, 33, 34, 36, 29, 90, 142, 27, 104, 156, 66, 40,
                330, 38, 180, 149, 150, 75, 76, 55, 128, 6, 295, 323, 63, 109, 78, 88, 134, 176, 11, 9, 44, 70, 182, 83,
                50, 146, 132, 131, 106, 177, 68, 8, 111, 30, 72, 53, 126, 37};
        for (int ids : whitelist) {
            if (m.getId() == ids) {
                return true;
            }
        }
        return false;
    }

}
