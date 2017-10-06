package funkemunky.Daedalus.check.movement;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;
import funkemunky.Daedalus.utils.UtilPlayer;
import funkemunky.Daedalus.utils.UtilTime;

public class NormalMovements extends Check {
	
	public static Map<Player, Map.Entry<Integer, Long>> count;

	public NormalMovements(Daedalus Daedalus) {
		super("NormalMovements", "NormalMovements", Daedalus);
		
		this.setEnabled(true);
		this.setBannable(true);
		
		setMaxViolations(7);
		
		count = new HashMap<Player, Map.Entry<Integer, Long>>();
	}
	
	@EventHandler
	public void onLog(PlayerQuitEvent e) {
		if(count.containsKey(e.getPlayer())) {
			count.remove(e.getPlayer());
		}
	}
	
    public static boolean isOnGround(Player player) {
    	Location l = player.getLocation();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        Location b = new Location(player.getWorld(), x, y - 1, z);

        if (player.isOnGround() && b.getBlock().getType() != Material.AIR && b.getBlock().getType() != Material.WEB
                && !b.getBlock().isLiquid()) {
            return true;
        } else {
            return false;
        }
    }
	
	@EventHandler
	public void checkMovements(PlayerMoveEvent e) {
		Player player = e.getPlayer();
        if ((e.getFrom().getYaw() == e.getTo().getYaw())) {
            return;
        }
        
        if (!getDaedalus().isEnabled()) {
            return;
        }
        
        if(player.hasPermission("daedalus.bypass")) {
        	return;
        }
        
    	if(getDaedalus().isSotwMode()) {
    		return;
    	}

        if ((e.getTo().getX() == e.getFrom().getX()) && (e.getTo().getZ() == e.getFrom().getZ())
                && (e.getTo().getY() == e.getFrom().getY())) {
            return;
        }
        if(getDaedalus().getLag().getTPS() < getDaedalus().getTPSCancel()) {
        	return;
        }
        
        if (player.getAllowFlight()) {
            return;
        }
        
        if (player.getNoDamageTicks() > 3) {
            return;
        }
        
        if (player.getVehicle() != null) {
            return;
        }
        
        int Count = 0;
        long Time = System.currentTimeMillis();
		if(count.containsKey(player.getUniqueId())) {
			Count = count.get(player.getUniqueId()).getKey();
			Time = count.get(player.getUniqueId()).getValue();
		}
        Location l = player.getLocation();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        
        Location to = e.getTo();
        Location from = e.getFrom();
        Location loc2 = new Location(player.getWorld(), x, y + 1, z);
        Location above = new Location(player.getWorld(), x, y + 2, z);
        
        double ongroundDiff = (to.getY() - from.getY());
        
        if (isOnGround(player) && !player.hasPotionEffect(PotionEffectType.JUMP)
                && above.getBlock().getType() == Material.AIR && loc2.getBlock().getType() == Material.AIR
                && ongroundDiff > 0 && ongroundDiff != 0 && ongroundDiff != 0.41999998688697815
                && ongroundDiff != 0.33319999363422426 && ongroundDiff != 0.1568672884460831
                && ongroundDiff != 0.4044491418477924 && ongroundDiff != 0.4044449141847757
                && ongroundDiff != 0.40444491418477746 && ongroundDiff != 0.24813599859094637
                && ongroundDiff != 0.1647732812606676 && ongroundDiff != 0.24006865856430082
                && ongroundDiff != 0.20000004768370516 && ongroundDiff != 0.19123230896968835
                && ongroundDiff != 0.10900766491188207 && ongroundDiff != 0.20000004768371227
                && ongroundDiff != 0.40444491418477924 && ongroundDiff != 0.0030162615090425504
                && ongroundDiff != 0.05999999821186108 && ongroundDiff != 0.05199999886751172
                && ongroundDiff != 0.06159999881982792 && ongroundDiff != 0.06927999889612124
                && ongroundDiff != 0.07542399904870933 && ongroundDiff != 0.07532994414328797
                && ongroundDiff != 0.08033919924402255 && ongroundDiff != 0.5 && ongroundDiff != 0.08427135945886555
                && ongroundDiff != 0.340000110268593 && ongroundDiff != 0.30000001192092896
                && ongroundDiff != 0.3955758986732967 && ongroundDiff != 0.019999999105930755
                && ongroundDiff != 0.21560001587867816 && ongroundDiff != 0.13283301814746876
                && ongroundDiff != 0.05193025879327907 && ongroundDiff != 0.1875 && ongroundDiff != 0.375
                && ongroundDiff != 0.08307781780646728 && ongroundDiff != 0.125 && ongroundDiff != 0.25
                && ongroundDiff != 0.01250004768371582 && ongroundDiff != 0.1176000022888175
                && ongroundDiff != 0.0625 && ongroundDiff != 0.20000004768371582
                && ongroundDiff != 0.4044448882341385 && ongroundDiff != 0.40444491418477835) {
            Count+= 1;
            if (Count >= 25) {
                getDaedalus().logCheat(this, player, null, Chance.HIGH, new String[0]);
                Count = 0;
            } 
        }
        
        if(UtilTime.elapsed(Time, 25000L)) {
        	Count = 0;
        	Time = UtilTime.nowlong();
        }
        
        count.put(e.getPlayer(), new AbstractMap.SimpleEntry<Integer, Long>(Count, Time));
	}
	
	@EventHandler
	public void onInvMove(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player player = (Player) e.getWhoClicked();
		if(player.hasPermission("daedalus.bypass")) {
			return;
		}
		
		if(!player.isSprinting()) {
			return;
		}
		
		getDaedalus().logCheat(this, player, "Clicked inventory while sprinting.", Chance.LIKELY, new String[0]);
	}

}
