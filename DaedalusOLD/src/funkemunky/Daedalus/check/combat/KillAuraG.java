package funkemunky.Daedalus.check.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import funkemunky.Daedalus.Daedalus;
import funkemunky.Daedalus.check.Check;
import funkemunky.Daedalus.utils.Chance;

public class KillauraG extends Check {
	
	private Map<UUID, Integer> verbose;
	
	public KillauraG(Daedalus Daedalus) {
		super("KillauraG", "Killaura (Type G)", Daedalus);
		
		setEnabled(true);
		setBannable(true);
		
		verbose = new HashMap<UUID, Integer>();
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) { 
		if(!(e.getDamager() instanceof Player)) {
			return;
		}
		
		Player player = (Player) e.getDamager();
		
		int verbose = this.verbose.getOrDefault(player.getUniqueId(), 0);
		
		if(player.isDead()) {
			verbose++;
		} else if(this.verbose.containsKey(player.getUniqueId())) {
			this.verbose.remove(player.getUniqueId());
			return;
		}
		
		if(verbose > 1) {
			verbose = 0;
			getDaedalus().logCheat(this, player, "Hit another player while dead.", Chance.HIGH, new String[0]);
		}
		
		this.verbose.put(player.getUniqueId(), verbose);
	}
	
	

}
