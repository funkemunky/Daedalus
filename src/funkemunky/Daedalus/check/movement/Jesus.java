package funkemunky.Daedalus.check.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;

public class Jesus extends Check
{
	public static Map<Player, Integer> onWater = new HashMap();
	public static ArrayList<Player> placedBlockOnWater = new ArrayList();
	public static Map<Player, Integer> count = new HashMap();

    public Jesus(Daedalus Daedalus) {
        super("Jesus", "Jesus", Daedalus);
        
        this.setEnabled(true);
        this.setBannable(true);
        setViolationsToNotify(1);
    }
    
    private boolean isBhopping(Player player) {
    	for(double y = 0 ; y < 1 ; y+= 0.5) {
        	if(player.getLocation().clone().subtract(0.0D, y, 0.0).getBlock().getType().equals(Material.AIR)
        			&& (player.getLocation().clone().subtract(0.0D, y + 1D, 0.0D).getBlock().getType().equals(Material.WATER) || player.getLocation().clone().subtract(0.0D, y + 1D, 0.0D).getBlock().getType().equals(Material.STATIONARY_WATER))) {
        		return true;
        	}
    	}
    	return false;
    }
    
    @EventHandler
    public void OnPlace(BlockPlaceEvent e)
    {
      if (e.getBlockReplacedState().getBlock().getType() == Material.WATER) {
        this.placedBlockOnWater.add(e.getPlayer());
      }
    }
    
    @EventHandler
    public void CheckJesus(PlayerMoveEvent event)
    {
      if ((event.getFrom().getX() == event.getTo().getX()) && (event.getFrom().getZ() == event.getTo().getZ())) {
        return;
      }
      Player p = event.getPlayer();
	     if(p.hasPermission("daedalus.bypass")) {
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
      
      if(p.getVelocity().getZ() > 0.0 || p.getVelocity().getX() > 0.0) {
    	  return;
      }
      
      if (this.placedBlockOnWater.remove(p)) {
        return;
      }
      int Count = 0;
      if(count.containsKey(p)) {
    	  Count = count.get(p);
      }
      if ((UtilCheat.cantStandAtWater(p.getWorld().getBlockAt(p.getLocation()))) && 
        (UtilCheat.isHoveringOverWater(p.getLocation())) && 
        (!UtilCheat.isFullyInWater(p.getLocation()))) {
        count.put(p, Count + 1);
      }
      
      if(Count >= 20) {
    	  count.remove(p);
    	  getDaedalus().logCheat(this, p, null, Chance.HIGH, new String[0]);
      }
    }




}