package funkemunky.Daedalus.check.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.packets.events.PacketKillauraEvent;
import funkemunky.Daedalus.packets.events.PacketPlayerType;
import funkemunky.Daedalus.utils.UtilCheat;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilPlayer;

public class Test extends Check {

	ArrayList<Double> values;
	private boolean testing;
	private Map<UUID, Long> LastMS;
	private Map<UUID, List<Long>> Clicks;
	private Map<UUID, Map.Entry<Integer, Long>> ClickTicks;
	public static Map<UUID, Map.Entry<Integer, Long>> speedTicks = new HashMap();

	public Test(Daedalus Daedalus) {
		super("Test", "Test", Daedalus);

		setEnabled(false);
		setBannable(false);

		setMaxViolations(5);

		values = new ArrayList<Double>();
		testing = false;
		this.LastMS = new HashMap<UUID, Long>();
		this.Clicks = new HashMap<UUID, List<Long>>();
		this.ClickTicks = new HashMap<UUID, Map.Entry<Integer, Long>>();
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
		if (UtilCheat.isBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN),
				new Material[] { Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER })) {
			return true;
		}
		return false;
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
		if (e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
			return;
		}
		Player player = e.getPlayer();
		double YSpeed = UtilMath.offset(UtilMath.getHorizontalVector(e.getFrom().toVector()),
				UtilMath.getHorizontalVector(e.getTo().toVector()));
		getDaedalus().logCheat(this, player, null, null, new String[] { YSpeed + " speed" });

	}
}