package funkemunky.Daedalus.check.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;

public class VClip extends Check {

	public VClip(final Daedalus Daedalus) {
		super("VClip", "VClip", Daedalus);

		this.setBannable(false);
		this.setEnabled(true);
		this.setMaxViolations(19);
		this.setViolationResetTime(10000);
	}

	public static List<Material> allowed = new ArrayList<Material>();
	public static ArrayList<Player> teleported = new ArrayList<Player>();
	public static HashMap<Player, Location> lastLocation = new HashMap<Player, Location>();

	static {
		allowed.add(Material.PISTON_EXTENSION);
		allowed.add(Material.PISTON_STICKY_BASE);
		allowed.add(Material.PISTON_BASE);
		allowed.add(Material.SIGN_POST);
		allowed.add(Material.WALL_SIGN);
		allowed.add(Material.STRING);
		allowed.add(Material.AIR);
		allowed.add(Material.FENCE_GATE);
		allowed.add(Material.CHEST);
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		if (e.getCause() != TeleportCause.UNKNOWN) {
			return;
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();

		if (p.hasPermission("daedalus.bypass")) {
			return;
		}

		Location to = e.getTo().clone();
		Location from = e.getFrom().clone();

		if (!getDaedalus().isEnabled()
				|| from.getY() == to.getY()
				|| getDaedalus().isSotwMode()
				|| p.getAllowFlight()
				|| p.getVehicle() != null
				|| teleported.remove(e.getPlayer())
				|| e.getTo().getY() <= 0 || e.getTo().getY() >= p.getWorld().getMaxHeight()
				|| !UtilCheat.blocksNear(p)
				|| (p.getLocation().getY() < 0.0D) 
				|| (p.getLocation().getY() > p.getWorld().getMaxHeight())) {
			return;
		}

		double yDist = from.getBlockY() - to.getBlockY();
		for (double y = 0; y < Math.abs(yDist); y++) {
			Location l = yDist < -0.2 ? from.getBlock().getLocation().clone().add(0.0D, y, 0.0D) : to.getBlock().getLocation().clone().add(0.0D, y, 0.0D);
			if ((yDist > 20 || yDist < -20) && l.getBlock().getType() != Material.AIR
					&& l.getBlock().getType().isSolid() && !allowed.contains(l.getBlock().getType())) {
				p.kickPlayer("No");
				getDaedalus().logCheat(this, p, "More than 20 blocks.", Chance.HIGH, new String[0]);
				p.teleport(from);
				return;
			}
			if (l.getBlock().getType() != Material.AIR && Math.abs(yDist) > 1.0 && l.getBlock().getType().isSolid()
					&& !allowed.contains(l.getBlock().getType())) {
				getDaedalus().logCheat(this, p, y + " blocks", Chance.LIKELY, new String[0]);
				p.teleport(lastLocation.get(p));
			} else {
				lastLocation.put(p, p.getLocation());
			}
		}
	}
}