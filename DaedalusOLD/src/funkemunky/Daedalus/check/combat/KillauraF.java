package funkemunky.Daedalus.check.combat;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.check.movement.PhaseA;
import funkemunky.Daedalus.packets.events.PacketUseEntityEvent;
import funkemunky.Daedalus.utils.Chance;
import funkemunky.Daedalus.utils.UtilCheat;

public class KillauraF extends Check {

	public KillauraF(Daedalus Daedalus) {
		super("KillAuraF", "KillAura (Wall)", Daedalus);

		setEnabled(true);
		setBannable(false);

		setMaxViolations(7);
	}

	public static HashMap<Player, Integer> counts = new HashMap();
	private ArrayList<Player> blockGlitched = new ArrayList();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogout(PlayerQuitEvent e) {
		if (counts.containsKey(e.getPlayer())) {
			counts.remove(e.getPlayer());
		}
		if (blockGlitched.contains(e.getPlayer())) {
			blockGlitched.remove(e.getPlayer());
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (e.isCancelled()) {
			blockGlitched.add(e.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void checkKillaura(PacketUseEntityEvent e) {
		if(e.getAction() != EntityUseAction.ATTACK) {
			return;
		}
		if(!getDaedalus().isEnabled()) {
			return;
		}
		
		if(!(e.getAttacked() instanceof Player)) {
			return;
		}
		
		Player p = e.getAttacker();
	    if(p.hasPermission("daedalus.bypass")) {
	        return;
	    }
	    
	    if(UtilCheat.slabsNear(p.getEyeLocation()) || UtilCheat.slabsNear(p.getEyeLocation().clone().add(0.0D, 0.5D, 0.0D))) {
	       	return;
	    }
	    int Count = 0;
	    
	    if(counts.containsKey(p)) {
	     	Count = counts.get(p);
	    }
	    
		Player attacked = (Player) e.getAttacked();
		Location dloc = p.getLocation();
		Location aloc = attacked.getLocation();
		double zdif = Math.abs(dloc.getZ() - aloc.getZ());
		double xdif = Math.abs(dloc.getX() - aloc.getX());
		
		if(xdif == 0 || zdif == 0) {
			return;
		}
		
		for(int y=0; y < 1 ; y+= 1) {
			Location zBlock = zdif < -0.2 ? dloc.clone().add(0.0D, y, zdif) : aloc.clone().add(0.0D, y, zdif);
			if(!PhaseA.allowed.contains(zBlock.getBlock().getType()) && zBlock.getBlock().getType().isSolid() && !p.hasLineOfSight(attacked)
					&& !UtilCheat.isSlab(zBlock.getBlock())) {
				Count++;
			}
			Location xBlock = xdif < -0.2 ? dloc.clone().add(xdif, y, 0.0D) : aloc.clone().add(xdif, y, 0.0D);
			if(!PhaseA.allowed.contains(xBlock.getBlock().getType()) && xBlock.getBlock().getType().isSolid() && !p.hasLineOfSight(attacked)
					&& !UtilCheat.isSlab(xBlock.getBlock())) {
				Count++;
			}
			
		}
		if(Count >= 6) {
			getDaedalus().logCheat(this, p, null, Chance.LIKELY, new String[] {"Experimental"});
			Count = 0;
		}
		counts.put(p, Count);
	}
}
