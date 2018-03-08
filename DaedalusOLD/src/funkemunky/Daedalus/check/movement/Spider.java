package funkemunky.Daedalus.check.movement;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilBlock;
import funkemunky.Daedalus.utils.UtilCheat;
import funkemunky.Daedalus.utils.UtilMath;
import funkemunky.Daedalus.utils.UtilPlayer;

public class Spider extends Check {

	public Spider(Daedalus Daedalus) {
		super("WallClimb", "WallClimb", Daedalus);

		this.setBannable(false);
		this.setEnabled(true);

		setMaxViolations(5);
	}

	private Map<UUID, Map.Entry<Long, Double>> AscensionTicks = new HashMap<UUID, Map.Entry<Long, Double>>();

	@EventHandler(ignoreCancelled = true)
	public void CheckSpider(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (event.getFrom().getY() >= event.getTo().getY()
				|| !getDaedalus().isEnabled()
				|| getDaedalus().isSotwMode()
				|| player.getAllowFlight()
				|| player.getVehicle() != null
				|| !UtilBlock.isInAir(player)
				|| getDaedalus().LastVelocity.containsKey(player.getUniqueId())) {
			return;
		}

		long Time = System.currentTimeMillis();
		double TotalBlocks = 0.0D;
		if (this.AscensionTicks.containsKey(player.getUniqueId())) {
			Time = AscensionTicks.get(player.getUniqueId()).getKey().longValue();
			TotalBlocks = AscensionTicks.get(player.getUniqueId()).getValue().doubleValue();
		}
		long MS = System.currentTimeMillis() - Time;
		double OffsetY = UtilMath.offset(UtilMath.getVerticalVector(event.getFrom().toVector()),
				UtilMath.getVerticalVector(event.getTo().toVector()));

		boolean ya = false;
		List<Material> Types = new ArrayList<Material>();
		Types.add(player.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType());
		Types.add(player.getLocation().getBlock().getRelative(BlockFace.NORTH).getType());
		Types.add(player.getLocation().getBlock().getRelative(BlockFace.WEST).getType());
		Types.add(player.getLocation().getBlock().getRelative(BlockFace.EAST).getType());
		for (Material Type : Types) {
			if ((Type.isSolid()) && (Type != Material.LADDER) && (Type != Material.VINE)) {
				ya = true;
				break;
			}
		}
		if (OffsetY > 0.0D) {
			TotalBlocks += OffsetY;
		} else if ((!ya) || (!UtilCheat.blocksNear(player))) {
			TotalBlocks = 0.0D;
		} else if ((ya) && ((event.getFrom().getY() > event.getTo().getY()) || (UtilPlayer.isOnGround(player)))) {
			TotalBlocks = 0.0D;
		}
		double Limit = 0.5D;
		if (player.hasPotionEffect(PotionEffectType.JUMP)) {
			for (PotionEffect effect : player.getActivePotionEffects()) {
				if (effect.getType().equals(PotionEffectType.JUMP)) {
					int level = effect.getAmplifier() + 1;
					Limit += Math.pow(level + 4.2D, 2.0D) / 16.0D;
					break;
				}
			}
		}
		if ((ya) && (TotalBlocks > Limit)) {
			if (MS > 500L) {
				getDaedalus().logCheat(this, player, null, Chance.LIKELY, new String[0]);
				Time = System.currentTimeMillis();
			}
		} else {
			Time = System.currentTimeMillis();
		}
		this.AscensionTicks.put(player.getUniqueId(),
				new AbstractMap.SimpleEntry<Long, Double>(Time, TotalBlocks));
	}

}
