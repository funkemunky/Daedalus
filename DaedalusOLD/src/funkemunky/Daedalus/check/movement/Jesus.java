package funkemunky.Daedalus.check.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;

public class Jesus extends Check {
	public static Map<Player, Integer> onWater;
	public static List<Player> placedBlockOnWater;
	public static Map<Player, Integer> count;

	public Jesus(Daedalus Daedalus) {
		super("Jesus", "Jesus", Daedalus);

		this.setEnabled(true);
		this.setBannable(true);
		setViolationsToNotify(1);
		setMaxViolations(5);
		
		count = new WeakHashMap<Player, Integer>();
		placedBlockOnWater = new ArrayList<Player>();
		onWater = new WeakHashMap<Player, Integer>();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent e) {
		if (onWater.containsKey(e.getPlayer())) {
			onWater.remove(e.getPlayer());
		}
		if (placedBlockOnWater.contains(e.getPlayer())) {
			placedBlockOnWater.remove(e.getPlayer());
		}
		if (count.containsKey(e.getPlayer())) {
			count.remove(e.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent e) {
		if (onWater.containsKey(e.getEntity())) {
			onWater.remove(e.getEntity());
		}
		if (placedBlockOnWater.contains(e.getEntity())) {
			placedBlockOnWater.remove(e.getEntity());
		}
		if (count.containsKey(e.getEntity())) {
			count.remove(e.getEntity());
		}
	}

	@EventHandler
	public void OnPlace(BlockPlaceEvent e) {
		if (e.getBlockReplacedState().getBlock().getType() == Material.WATER) {
			placedBlockOnWater.add(e.getPlayer());
		}
	}

	@EventHandler
	public void CheckJesus(PlayerMoveEvent event) {
		if ((event.getFrom().getX() == event.getTo().getX()) && (event.getFrom().getZ() == event.getTo().getZ())) {
			return;
		}
		Player p = event.getPlayer();
		if (p.hasPermission("daedalus.bypass")) {
			return;
		}
		if (p.getAllowFlight()) {
			return;
		}
		if (!p.getNearbyEntities(1.0D, 1.0D, 1.0D).isEmpty()) {
			return;
		}
		if (UtilCheat.isOnLilyPad(p)) {
			return;
		}

		if (placedBlockOnWater.remove(p)) {
			return;
		}
		int Count = 0;
		if (count.containsKey(p)) {
			Count = count.get(p);
		}
		if ((UtilCheat.cantStandAtWater(p.getWorld().getBlockAt(p.getLocation())))
				&& (UtilCheat.isHoveringOverWater(p.getLocation())) && (!UtilCheat.isFullyInWater(p.getLocation()))) {
			count.put(p, Count + 2);
		} else {
			count.put(p, Count > 0 ? -1 : 0);
		}

		if (Count >= 20) {
			count.remove(p);
			getDaedalus().logCheat(this, p, null, Chance.HIGH, new String[0]);
		}
	}

}