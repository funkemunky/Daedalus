package me.funke.daedalus.check.movement;

import me.funke.daedalus.check.Check;
import me.funke.daedalus.utils.*;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Spider extends Check {
    private Map<UUID, Map.Entry<Long, Double>> AscensionTicks = new HashMap<>();

    // TODO: SEVERE: Fix false positives with jumping next to blocks, disabled until then.
    public Spider(me.funke.daedalus.Daedalus Daedalus) {
        super("Spider", "Spider", Daedalus);
        this.setEnabled(false);
        this.setBannable(false);
        setMaxViolations(5);
    }

    @EventHandler(ignoreCancelled = true)
    public void CheckSpider(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getY() >= event.getTo().getY()
                || !getDaedalus().isEnabled()
                || getDaedalus().isSotwMode()
                || player.getAllowFlight()
                || player.getVehicle() != null
                || !UtilBlock.isInAir(player)
                || getDaedalus().LastVelocity.containsKey(player.getUniqueId())) return;
        long Time = System.currentTimeMillis();
        double TotalBlocks = 0.0D;
        if (this.AscensionTicks.containsKey(player.getUniqueId())) {
            Time = AscensionTicks.get(player.getUniqueId()).getKey();
            TotalBlocks = AscensionTicks.get(player.getUniqueId()).getValue();
        }
        long MS = System.currentTimeMillis() - Time;
        double OffsetY = UtilMath.offset(UtilMath.getVerticalVector(event.getFrom().toVector()), UtilMath.getVerticalVector(event.getTo().toVector()));
        boolean ya = false;
        List<Material> Types = new ArrayList<>();
        Types.add(player.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType());
        Types.add(player.getLocation().getBlock().getRelative(BlockFace.NORTH).getType());
        Types.add(player.getLocation().getBlock().getRelative(BlockFace.WEST).getType());
        Types.add(player.getLocation().getBlock().getRelative(BlockFace.EAST).getType());
        for (Material Type : Types) {
            if ((Type.isSolid()) && (Type != Material.LADDER) && (Type != Material.VINE) && (Type != Material.AIR)) {
                ya = true;
                break;
            }
        }
        if (OffsetY > 0.0D) {
            TotalBlocks += OffsetY;
        } else if ((!ya) || (!UtilCheat.blocksNear(player))) {
            TotalBlocks = 0.0D;
        } else if (((event.getFrom().getY() > event.getTo().getY()) || (UtilPlayer.isOnGround(player)))) {
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
                getDaedalus().logCheat(this, player, TotalBlocks + " > " + Limit, Chance.LIKELY);
                Time = System.currentTimeMillis();
            }
        } else {
            Time = System.currentTimeMillis();
        }
        this.AscensionTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(Time, TotalBlocks));
    }
}